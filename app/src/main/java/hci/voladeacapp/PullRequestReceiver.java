package hci.voladeacapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static hci.voladeacapp.MisVuelosFragment.ACTION_GET_REFRESH;

public class PullRequestReceiver extends BroadcastReceiver {
    public PullRequestReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<Flight> flight_details = new ArrayList<>();
        System.out.println("TIME TO PULL!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        flight_details = StorageHelper.getFlights(context.getApplicationContext());

        for (Flight f : flight_details) {
                ApiService.startActionGetFlightStatus(context.getApplicationContext(), f.getIdentifier(), ACTION_GET_REFRESH);
            }
        }

}
