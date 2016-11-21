package hci.voladeacapp;

import android.app.Service;
import android.content.ContentProvider;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by chelo on 11/20/16.
 */

public class StorageHelper {
    public final static String FLIGHT_LIST = "hci.voladeacapp.MisVuelos.FLIGHT_LIST";
    public final static String FLIGHTS = "hci.voladeacapp.data.FLIGHTS";

    public static ArrayList<ConfiguredFlight> getFlights(Context context) {
        ArrayList<ConfiguredFlight> flight_details;

        SharedPreferences sp = context.getSharedPreferences(FLIGHTS, MODE_PRIVATE);
        String list = sp.getString(FLIGHT_LIST, null); //Si no hay nada devuelve null

        if (list == null) {
            flight_details = new ArrayList<>();

        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ConfiguredFlight>>() {
            }.getType();

            flight_details = gson.fromJson(list, type);
        }

        return flight_details;
    }



    public static void saveFlights(Context context, List<ConfiguredFlight> flight_details){
        Gson gson = new Gson();
        String s = gson.toJson(flight_details);
        SharedPreferences.Editor editor = context.getSharedPreferences(FLIGHTS, MODE_PRIVATE).edit();
        editor.putString(FLIGHT_LIST, s);
        editor.commit();
    }

}
