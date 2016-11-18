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

import org.w3c.dom.Text;

public class AddFlightActivity extends AppCompatActivity {


    private class AdderReceiver extends BroadcastReceiver{

        AddFlightActivity parent;

        public AdderReceiver(AddFlightActivity parent){
            super();
            this.parent = parent;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            pDialog.hide();
            FlightStatusGson flGson = (FlightStatusGson)intent.getSerializableExtra("RESPONSE");
            parent.addFlight(flGson);
        }
    }

    private void addFlight(final FlightStatusGson flGson) {
        TextView text = (TextView) findViewById(R.id.chelo_flight_info);
        if(flGson != null) {

            text.setText(flGson.toString());
            Button add = (Button) findViewById(R.id.add_btn);
            add.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    setResult(MisVuelosFragment.GET_FLIGHT, new Intent().putExtra("RESPONSE", flGson));
                    finish();
                }
            });

            add.setVisibility(View.VISIBLE);

        }else{
            text.setText("No existe ese vuelo");
        }
        // finish();
    }


    ProgressDialog pDialog;
    AdderReceiver adder;
    MisVuelosFragment misVuelos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pDialog = new ProgressDialog(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flight);

        Button search = (Button)findViewById(R.id.fl_search_btn);
        Button add = (Button)findViewById(R.id.add_btn);

        search.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String airline = ((EditText)findViewById(R.id.ch_airline_id)).getText().toString();
                String number = ((EditText)findViewById(R.id.fl_num)).getText().toString();
                pDialog.show();
                ApiService.startActionGetFlightStatus(v.getContext(), airline, number);
            }
        });


        add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

            }
        });

    }


    @Override
    protected void onResume(){
        super.onResume();
        adder = new AdderReceiver(this);

          registerReceiver(adder, new IntentFilter(Intent.ACTION_ANSWER));
    }


    protected void onPause(){
        super.onPause();
        unregisterReceiver(adder);
    }


}