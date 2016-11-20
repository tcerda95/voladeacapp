package hci.voladeacapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

import static hci.voladeacapp.ApiService.DATA_FLIGHT_GSON;
import static hci.voladeacapp.MisVuelosFragment.ACTION_GET_REFRESH;


public class BackgroundRefreshReceiver extends BroadcastReceiver {

    public static final String TIME_TO_PULL = "hci.voladeacapp.TIME_TO_PULL";

    public BackgroundRefreshReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ArrayList<ConfiguredFlight> flight_details = StorageHelper.getFlights(context.getApplicationContext());

        if(intent.getAction().equals(ACTION_GET_REFRESH)){
                FlightStatusGson updatedGson = (FlightStatusGson)intent.getSerializableExtra(DATA_FLIGHT_GSON);
                if(updatedGson == null)
                     return;
                 int idx = flight_details.indexOf(new Flight(updatedGson));
                 if(idx == -1){
                    return;
                 }
                ConfiguredFlight toUpdate = flight_details.get(idx);
                toUpdate.update(updatedGson);
                NotificationCreator.createNotification(context, toUpdate, NotificationType.DELAYED);
        }

    }

}



