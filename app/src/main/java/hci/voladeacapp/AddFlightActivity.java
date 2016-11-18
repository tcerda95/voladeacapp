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

public class AddFlightActivity extends AppCompatActivity {

    private class AdderReceiver extends BroadcastReceiver{

        AddFlightActivity parent;

        public AdderReceiver(AddFlightActivity parent){
            super();
            this.parent = parent;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            FlightStatusGson flGson = (FlightStatusGson)intent.getSerializableExtra("RESPONSE");
            parent.addFlight(flGson);
        }
    }

    private void addFlight(FlightStatusGson flGson) {
        setResult(MisVuelosFragment.GET_FLIGHT, new Intent().putExtra("RESPONSE", flGson));
        finish();
    }


    ProgressDialog pDialog;
    AdderReceiver adder;
    MisVuelosFragment misVuelos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flight);

        Button add = (Button)findViewById(R.id.add_btn);

        add.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                pDialog = new ProgressDialog(AddFlightActivity.this);
                pDialog.setMessage("Loading!");
                pDialog.show();
                ApiService.startActionGetFlightStatus(view.getContext(), "8R", "8700");
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
