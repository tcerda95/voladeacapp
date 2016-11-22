package hci.voladeacapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static hci.voladeacapp.ApiService.DATA_AIRLINE_ID_MAP;

/**
 * Created by chelo on 11/20/16.
 */

public class StorageHelper {
    public final static String FLIGHT_LIST = "hci.voladeacapp.StorageHelper.FLIGHT_LIST";
    public final static String FLIGHTS = "hci.voladeacapp.data.StorageHelper.FLIGHTS";
    public final static String DATA = "hci.voladeacapp.StorageHelper.DATA";
    private static final String AIRLINE_LIST = "hci.voladeacapp.StorageHelper.AIRLINE_LIST";

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



    public static Map<String, String> getAirlineIdMap(Context context){
        Map<String, String> map;

        SharedPreferences sp = context.getSharedPreferences(DATA, MODE_PRIVATE);
        String mapString = sp.getString(AIRLINE_LIST, null); //Si no hay nada devuelve null

        if(mapString != null){
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String,String>>(){}.getType();
            map = gson.fromJson(mapString, type);
        } else {
            map = null;
            initialize(context);
        }

        return map;
    }



    public static void saveFlights(Context context, List<ConfiguredFlight> flight_details){
        Gson gson = new Gson();
        String s = gson.toJson(flight_details);
        SharedPreferences.Editor editor = context.getSharedPreferences(FLIGHTS, MODE_PRIVATE).edit();
        editor.putString(FLIGHT_LIST, s);
        editor.commit();
    }

    //Carga recursos estaticos
    public static void initialize(Context context) {
        System.out.println("???????????????????????");
        final SharedPreferences sp = context.getSharedPreferences(DATA, MODE_PRIVATE);
        final String airlines = sp.getString(AIRLINE_LIST, null);
        if(airlines == null){
            ApiService.startActionGetAirlines(context, "AIRLINE_SAVER");

            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    HashMap<String, String> idMap = (HashMap<String,String>)intent.getSerializableExtra(DATA_AIRLINE_ID_MAP);
                    if(idMap != null) {
                        Gson gson = new Gson();
                        String map = gson.toJson(idMap);
                        System.out.println("MAAAAAAAAAAAAAAAAAAAAAP");
                        System.out.println(map);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(AIRLINE_LIST, map);
                        editor.commit();
                    } else{
                        System.out.println("NULLLLLLLLLLLLLLLLLLLLLLLLLLLL");
                    }
                    context.unregisterReceiver(this);
                }
            }, new IntentFilter("AIRLINE_SAVER"));
        }else{
            System.out.println(airlines);
        }
    }


}
