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
import static hci.voladeacapp.MisVuelosFragment.FLIGHT_LIST;

public class PullRequestReceiver extends BroadcastReceiver {
    public PullRequestReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<Flight> flight_details = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences("FLIGHTS", MODE_PRIVATE);
        String list = sp.getString(FLIGHT_LIST, null); //Si no hay nada devuelve null

        if (list != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Flight>>() {
            }.getType();

            flight_details = gson.fromJson(list, type);

            for (Flight f : flight_details) {
                ApiService.startActionGetFlightStatus(context.getApplicationContext(), f.getAerolinea(), f.getNumber(), ACTION_GET_REFRESH);
            }
        }

    }
}