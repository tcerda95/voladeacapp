package hci.voladeacapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;
import java.util.Map;

import static hci.voladeacapp.MisVuelosFragment.ACTION_GET_FLIGHT;

public class AddFlightActivity extends AppCompatActivity implements Validator.ValidationListener{


    @NotEmpty
    private AutoCompleteTextView flightNumberEdit;

    @NotEmpty
    private AutoCompleteTextView airline;

    private Validator validator; // Valida los campos

    private ProgressDialog pDialog;
    private AdderReceiver adder;


    @Override
    public void onValidationSucceeded() {
        String airlineData = airline.getText().toString();
        String numberData = flightNumberEdit.getText().toString();
        String airlineId = StorageHelper.getAirlineIdMap(this).get(airlineData);
        /* Ver si se valida esto */
        if(airlineId == null)
            Toast.makeText(this,"No existe esa aerolinea",Toast.LENGTH_SHORT);

        pDialog.show();
        ApiService.startActionGetFlightStatus(this, airlineId, numberData, ACTION_GET_FLIGHT);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Toast.makeText(this, "Hay errores", Toast.LENGTH_SHORT).show();
    }

    private class AdderReceiver extends BroadcastReceiver{

        AddFlightActivity parent;

        public AdderReceiver(AddFlightActivity parent){
            super();
            this.parent = parent;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            pDialog.hide();
            FlightStatusGson flGson = (FlightStatusGson)intent.getSerializableExtra(ApiService.DATA_FLIGHT_GSON);
            parent.addFlight(flGson);
        }
    }

    private void addFlight(final FlightStatusGson flGson) {
        TextView text = (TextView) findViewById(R.id.flight_info);
        Button add = (Button) findViewById(R.id.add_btn);
        Button detailsButton = (Button) findViewById(R.id.details_btn);
        CardView resultCardView = (CardView) findViewById(R.id.result_card_view);
        TextView notExists = (TextView) findViewById(R.id.not_exists_result);

        if(flGson != null) {
            notExists.setVisibility(View.GONE);
            text.setText(flGson.toString());
            resultCardView.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setResult(MisVuelosFragment.GET_FLIGHT, new Intent().putExtra(ApiService.DATA_FLIGHT_GSON, flGson));
                    finish();
                }
            });
            detailsButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    /*
                    Flight flight = new Flight(flGson);
                    Intent intent = new Intent(getApplication(),FlightDetails.class);
                    intent.putExtra(FLIGHT_IDENTIFIER,flight);
                    startActivity(intent);
                    */
                }
            });


        }else{
            resultCardView.setVisibility(View.GONE);
            notExists.setVisibility(View.VISIBLE);
        }
        // finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pDialog = new ProgressDialog(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_flight);
        Map<String,String> airlineMap = StorageHelper.getAirlineIdMap(this);

        flightNumberEdit = (AutoCompleteTextView) findViewById(R.id.fl_num_data);
        airline = (AutoCompleteTextView) findViewById(R.id.airline_id_data);

        validator = new Validator(this);
        validator.setValidationListener(this);

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.select_dialog_item,airlineMap.keySet().toArray());
        airline.setAdapter(adapter);
        airline.setThreshold(1);

        Button search = (Button)findViewById(R.id.fl_search_btn);
        Button add = (Button)findViewById(R.id.add_btn);

        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                validator.validate();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(adder == null) {
            adder = new AdderReceiver(this);
        }
        registerReceiver(adder, new IntentFilter(ACTION_GET_FLIGHT));
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(adder);
    }
}
