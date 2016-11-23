package hci.voladeacapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.w3c.dom.Text;

import java.util.List;

import static hci.voladeacapp.MisVuelosFragment.ACTION_GET_FLIGHT;
import static hci.voladeacapp.MisVuelosFragment.ACTION_GET_REFRESH;

public class AddFlightActivity extends AppCompatActivity implements Validator.ValidationListener{


    @NotEmpty
    private EditText flightNumberEdit;

    @NotEmpty
    private EditText airline;

    private Validator validator; // Valida los campos

    private ProgressDialog pDialog;
    private AdderReceiver adder;

    @Override
    public void onValidationSucceeded() {
        String airline = ((EditText)findViewById(R.id.ch_airline_id)).getText().toString().toUpperCase();
        String number = ((EditText)findViewById(R.id.fl_num)).getText().toString();
        pDialog.show();
        ApiService.startActionGetFlightStatus(this, airline, number, ACTION_GET_FLIGHT);
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
        TextView text = (TextView) findViewById(R.id.chelo_flight_info);
        Button add = (Button) findViewById(R.id.add_btn);

        if(flGson != null) {

            text.setText(flGson.toString());
            add.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setResult(MisVuelosFragment.GET_FLIGHT, new Intent().putExtra(ApiService.DATA_FLIGHT_GSON, flGson));
                    finish();
                }
            });

            add.setVisibility(View.VISIBLE);

        }else{
            text.setText("No existe ese vuelo");
            add.setVisibility(View.GONE);
        }
        // finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pDialog = new ProgressDialog(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flight);

        flightNumberEdit = (EditText) findViewById(R.id.fl_num);
        airline = (EditText) findViewById(R.id.ch_airline_id);

        validator = new Validator(this);
        validator.setValidationListener(this);

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
