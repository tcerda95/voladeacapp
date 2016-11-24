package hci.voladeacapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static hci.voladeacapp.ApiService.DATA_FLIGHT_GSON;
import static hci.voladeacapp.MisVuelosFragment.ACTION_GET_FLIGHT;
import static hci.voladeacapp.MisVuelosFragment.DETAILS_REQUEST_CODE;
import static hci.voladeacapp.MisVuelosFragment.FLIGHT_IDENTIFIER;
import static hci.voladeacapp.MisVuelosFragment.FLIGHT_REMOVED;

public class AddFlightActivity extends AppCompatActivity implements Validator.ValidationListener{


    public final static String PARENT_ADD_FLIGHT_ACTIVITY = "hci.voladeacapp.AddFlightActivity.parent";
    public static final String NEW_FLIGHT_ADDED = "hci.voladeacapp.AddFlightActivity.NEW_FLIGHT_ADDED";

    @NotEmpty
    private EditText flightNumberEdit;

    @NotEmpty
    private AutoCompleteTextView airline;

    private Validator validator; // Valida los campos

    private ProgressDialog pDialog;
    private AdderReceiver adder;

    private TextInputLayout numberInputLayout;
    private TextInputLayout airlineInputLayout;


    @Override
    public void onValidationSucceeded() {
        String airlineData = airline.getText().toString();
        String numberData = flightNumberEdit.getText().toString();
        String airlineId = StorageHelper.getAirlineIdMap(this).get(airlineData);
        /* Ver si se valida esto */
        if(airlineId == null)
            Toast.makeText(this,"No existe esa aerolinea",Toast.LENGTH_SHORT);

        airlineInputLayout.setErrorEnabled(false);
        numberInputLayout.setErrorEnabled(false);

        pDialog.show();
        ApiService.startActionGetFlightStatus(this, airlineId, numberData, ACTION_GET_FLIGHT);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        Toast.makeText(this, "Hay errores", Toast.LENGTH_SHORT).show();
        CardView resultCardView = (CardView) findViewById(R.id.card_view);
        TextView notExists = (TextView) findViewById(R.id.not_exists_result);
        resultCardView.setVisibility(View.GONE);
        notExists.setVisibility(View.GONE);

        for(ValidationError error : errors){
            View v = error.getView();
            Log.d("test", String.valueOf(v.getId()) + String.valueOf(R.id.fl_num_data));
            if(v.getId() == R.id.fl_num_data){
                //Es el numero
                numberInputLayout.setErrorEnabled(true);
                numberInputLayout.setError(getString(R.string.error_required_flight_number));
            } else {
                //es la aerolinea
                airlineInputLayout.setErrorEnabled(true);
                airlineInputLayout.setError(getString(R.string.error_required_airline));
            }
        }
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
        /* ACA SE LLENAN LOS DATOS DE LA TARJETA */
        //TextView text = (TextView) findViewById(R.id.flight_info);
        Button add = (Button) findViewById(R.id.add_btn);
        Button detailsButton = (Button) findViewById(R.id.details_btn);
        ((LinearLayout)findViewById(R.id.buttons)).setVisibility(View.VISIBLE);
        LinearLayout resultCardView = (LinearLayout) findViewById(R.id.result_card);
        TextView notExists = (TextView) findViewById(R.id.not_exists_result);

        if(flGson != null) {
            notExists.setVisibility(View.GONE);
            //text.setText(flGson.toString());
            fillData(flGson);
            resultCardView.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setResult(MisVuelosFragment.GET_FLIGHT, new Intent().putExtra(ApiService.DATA_FLIGHT_GSON, flGson));
                    finish();
                }
            });
            detailsButton.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view) {

                    Flight flight = new Flight(flGson);
                    Intent intent = new Intent(getApplication(),FlightDetails.class);
                    intent.putExtra("Flight",flight);
                    intent.putExtra(FLIGHT_IDENTIFIER, flight.getIdentifier());
                    intent.putExtra(PARENT_ADD_FLIGHT_ACTIVITY, true); // Indica a FlightDetails que esta actividad es la padre
                    startActivityForResult(intent, DETAILS_REQUEST_CODE);
                    //TODO: Ver lo de FlightDetails por que aparece como si tuvie

                }
            });

        }else{
            resultCardView.setVisibility(View.GONE);
            notExists.setVisibility(View.VISIBLE);
        }
        // finish();
    }

    private void fillData(FlightStatusGson flGson) {
        TextView origAirView = (TextView) findViewById(R.id.card_departure_airport_id);
        TextView origCityView = (TextView) findViewById(R.id.card_depart_city);
        TextView destAirView = (TextView) findViewById(R.id.card_arrival_airport_id);
        TextView destCityView = (TextView)findViewById(R.id.card_arrival_city);
        ImageView stateView = (ImageView) findViewById(R.id.card_status_badge);
        TextView flnumberView = (TextView) findViewById(R.id.card_flight_number);
        TextView departDateView = (TextView) findViewById(R.id.card_depart_date);

        flnumberView.setText(flGson.airline.id + " " + flGson.number);
        origAirView.setText(flGson.departure.airport.id);
        destAirView.setText(flGson.arrival.airport.id);
        origCityView.setText(flGson.departure.airport.city.name.split(",")[0]);
        destCityView.setText(flGson.arrival.airport.city.name.split(",")[0]);
        //TODO: imagen del estado
        Flight.FlightDate date = new Flight.FlightDate(flGson.departure.scheduled_time);
        departDateView.setText(new SimpleDateFormat("dd-MM-yyyy").format(date.date));
        //stateTextView.setText(flight.getState());


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pDialog = new ProgressDialog(this);
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.search_flight_title));
        setContentView(R.layout.activity_add_flight);
        Map<String,String> airlineMap = StorageHelper.getAirlineIdMap(this);

        flightNumberEdit = (EditText) findViewById(R.id.fl_num_data);
        airline = (AutoCompleteTextView) findViewById(R.id.airline_id_data);

        airlineInputLayout = (TextInputLayout) findViewById(R.id.airline_inputLayout);
        numberInputLayout = (TextInputLayout) findViewById(R.id.number_inputLayout);

        validator = new Validator(this);
        validator.setValidationListener(this);

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.select_dialog_item,airlineMap.keySet().toArray());
        airline.setAdapter(adapter);
        airline.setThreshold(1);

        Button search = (Button)findViewById(R.id.fl_search_btn);

        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                validator.validate();
                hideKeyboard(v);
            }
        });

        setFocusChangeListeners();

        
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
    }
    /**
     * En cambio de foco se validan los campos y se pone el mensaje de error correspondiente
     */
    private void setFocusChangeListeners() {
        flightNumberEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                numberInputLayout.setErrorEnabled(false);
                if(hasFocus){
                    numberInputLayout.setErrorEnabled(false);
                }else{
                    numberInputLayout.setErrorEnabled(false);
                    if (!validateEditText(((EditText) v).getText())){
                        numberInputLayout.setErrorEnabled(true);
                        numberInputLayout.setError(getString(R.string.error_required_flight_number));
                    } else {
                        numberInputLayout.setErrorEnabled(false);
                    }
                }

            }
        });

        airline.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                airlineInputLayout.setErrorEnabled(false);
                if(hasFocus){
                    airlineInputLayout.setErrorEnabled(false);
                } else {
                    airlineInputLayout.setErrorEnabled(false);
                    if(!validateEditText(((EditText) v).getText())){
                        airlineInputLayout.setErrorEnabled(true);
                        airlineInputLayout.setError(getString(R.string.error_required_airline));
                    } else {
                        airlineInputLayout.setErrorEnabled(false);
                    }
                }
            }
        });
    }

    /**
     * Retorna true si el campo está lleno y false si el campo está vacío
     * @param text
     * @return
     */
    private boolean validateEditText(Editable text) {
        if (TextUtils.isEmpty(text))
            return false;
        return true;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == DETAILS_REQUEST_CODE) {
            boolean addedNew = data.getBooleanExtra(NEW_FLIGHT_ADDED, false);
            boolean deleted = data.getBooleanExtra(FLIGHT_REMOVED, false);
            if(deleted) {
                //Borró y hay que borrarlo de la lista
                FlightIdentifier identifier = (FlightIdentifier)data.getSerializableExtra(FLIGHT_IDENTIFIER);
                StorageHelper.deleteFlight(this, identifier);
                Toast.makeText(this, "Vuelo borrado", Toast.LENGTH_SHORT).show();
            }
            else if(addedNew){
                //Agrego desde aca y se fue, hay que cambiar la tarjeta aca
                Toast.makeText(this, "Se agrego", Toast.LENGTH_SHORT).show();

            }
            else{
                //Aca no hizo nada
                Toast.makeText(this, "No toco nada", Toast.LENGTH_SHORT).show();
            }
        }

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
