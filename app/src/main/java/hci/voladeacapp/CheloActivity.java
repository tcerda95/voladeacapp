package hci.voladeacapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import static hci.voladeacapp.ApiService.DATA_FLIGHT_GSON;
import static hci.voladeacapp.ApiService.DATA_REVIEW_LIST;
import static hci.voladeacapp.BackgroundRefreshReceiver.TIME_TO_PULL;
import static hci.voladeacapp.MisVuelosFragment.ACTION_GET_FLIGHT;
import static hci.voladeacapp.MisVuelosFragment.ACTION_GET_REFRESH;

public class CheloActivity extends AppCompatActivity {

    private class MyReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if(pDialog != null){
                pDialog.hide();
            }

            TextView txt = (TextView)findViewById(R.id.dbg_text);
            final FlightStatusGson response = (FlightStatusGson)intent.getSerializableExtra(DATA_FLIGHT_GSON);
            txt.setText("Recibi intent de Service: " + response);

        }
    }

    MyReciever rcv;
    BroadcastReceiver reviewrcv;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chelo);

        rcv = rcv == null ? new MyReciever() : rcv;

    }

    @Override
    protected void onStart() {
        super.onStart();

        Button cheloDebug = (Button)findViewById(R.id.chelo_btn);

        reviewrcv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ReviewGson> list = (List<ReviewGson>)intent.getSerializableExtra(DATA_REVIEW_LIST);
                for(ReviewGson r : list){
                    System.out.println(r.comments);
                }

                GlobalReview global = new GlobalReview(list);
                System.out.println("GLOBAL:");
                System.out.println(global.getRating());
                System.out.println(global.comfort());
                System.out.println(global.getRecommendedPercentage());

            }
        };

        registerReceiver(reviewrcv, new IntentFilter("GET_REVIEWS"));

        cheloDebug.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                pDialog = new ProgressDialog(CheloActivity.this);
                pDialog.setMessage("Loading!");
                pDialog.show();


                TextView txt = (TextView)findViewById(R.id.dbg_text);
                txt.setText("OK");

                ApiService.startActionGetFlightStatus(view.getContext(), "8R", "8700", ACTION_GET_FLIGHT);
                ApiService.startActionGetReviews(view.getContext(), "AR", "5620", "GET_REVIEWS");

             /*   AlarmManager alarmMgr;
                PendingIntent alarmIntent;

                alarmMgr = (AlarmManager)view.getContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(view.getContext(), PullRequestReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(view.getContext(), 0, intent, 0);

               alarmMgr.cancel(alarmIntent);
                //    sendBroadcast(new Intent(TIME_TO_PULL)); */
          }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(rcv, new IntentFilter(ACTION_GET_FLIGHT));
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(rcv);
        unregisterReceiver(reviewrcv);
    }
}
