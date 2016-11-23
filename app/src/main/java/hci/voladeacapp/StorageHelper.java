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
import java.util.Calendar;
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

    public final static String DEALS_WITH_IMG = "hci.voladeacapp.data.DEALS_WITH_IMG";
    public final static String DEALS_CITY_ID = "hci.voladeacapp.data.DEALS_CITY_ID";
    public final static String DEALS_CALENDAR = "hci.voladeacapp.data.DEALS_CALENDAR";


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

    public static Map<DealGson, String> getDeals(Context context) {
        Map<DealGson, String> dealsMap;

        SharedPreferences sp = context.getSharedPreferences(FLIGHTS, MODE_PRIVATE);
        String s = sp.getString(DEALS_WITH_IMG, null); //Si no hay nada devuelve null

        if (s == null) {
            dealsMap = new HashMap<>();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<DealGson, String>>(){} .getType();

            dealsMap = gson.fromJson(s, type);
        }

        return dealsMap;
    }

    public static String getDealSearchCity(Context context) {
        String city;

        SharedPreferences sp = context.getSharedPreferences(FLIGHTS, MODE_PRIVATE);
        String s = sp.getString(DEALS_CITY_ID, null); //Si no hay nada devuelve null

        if (s == null) {
            city = null;
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<String>(){} .getType();

            city = gson.fromJson(s, type);
        }

        return city;
    }

    public static Calendar getDealSearchCalendar(Context context) {
        Calendar calendar;

        SharedPreferences sp = context.getSharedPreferences(FLIGHTS, MODE_PRIVATE);
        String s = sp.getString(DEALS_CALENDAR, null); //Si no hay nada devuelve null

        if (s == null) {
            calendar = null;
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<Calendar>(){} .getType();

            calendar = gson.fromJson(s, type);
        }
        return calendar;
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



    public static boolean flightExists(Context context, FlightIdentifier identifier){
        List<ConfiguredFlight> list = getFlights(context);

        ConfiguredFlight searcher = new ConfiguredFlight();
        searcher.setIdentifier(identifier);
        return list.indexOf(searcher) >= 0;
    }

    public static ConfiguredFlight getFlight(Context context, FlightIdentifier identifier){
        ConfiguredFlight searcher = new ConfiguredFlight();

        searcher.setIdentifier(identifier);

        List<ConfiguredFlight> list = getFlights(context);

        int idx = list.indexOf(searcher);
        if(idx < 0){
            return null;
        }


        System.out.println("FINDING: FOUND AT IDX " + list.indexOf(searcher));
        return list.get(idx);

    }

    public static void deleteFlight(Context context, FlightIdentifier identifier){
        ConfiguredFlight searcher = new ConfiguredFlight();
        searcher.setIdentifier(identifier);

        List<ConfiguredFlight> list = getFlights(context);
        System.out.println("DELETING: FOUND AT IDX " + list.indexOf(searcher));
        list.remove(searcher);

        saveFlights(context, list);
    }


    public static void saveFlight(Context context, ConfiguredFlight flight){
        List<ConfiguredFlight> list = getFlights(context);
        int idx = list.indexOf(flight);

        if(idx < 0) {
            list.add(flight);
        } else {
            list.remove(flight);
            list.add(idx, flight);
        }
        saveFlights(context, list);
    }


    public static void saveSettings(Context context, FlightIdentifier identifier, FlightSettings settings){
        ConfiguredFlight searcher = new ConfiguredFlight();
        searcher.setIdentifier(identifier);

        List<ConfiguredFlight> list = getFlights(context);
        int idx = list.indexOf(searcher);
        if(idx >= 0) {
            list.get(idx).setSettings(settings);
            System.out.println("SAVING SETTINGS");
            System.out.println("Landing: " + settings.isActive(NotificationCategory.LANDING));
            System.out.println("Delay: " + settings.isActive(NotificationCategory.DELAY));
            System.out.println("Cancelation: " + settings.isActive(NotificationCategory.CANCELATION));
            System.out.println("Takeoff: " + settings.isActive(NotificationCategory.TAKEOFF));
            System.out.println("Deviation: " + settings.isActive(NotificationCategory.DEVIATION));
            System.out.println("ALL: " + settings.notificationsActive());
        }else{
            System.out.println("NOT SAVING SETTINGS B/C NOT FOUND");
        }

        saveFlights(context, list);
    }

    public static void saveFlights(Context context, List<ConfiguredFlight> flight_details){
        saveData(context, flight_details, FLIGHT_LIST);
    }



    public static void fillListWithFlights(Context context, List<ConfiguredFlight> list){
        List<ConfiguredFlight> saved = getFlights(context);

        if(list == null || saved == null)
            return;

        list.clear();
        for(ConfiguredFlight f: saved){
            list.add(f);
        }
    }


    public static void saveDeals(Context context, Map<DealGson, String> deals) {
        saveData(context, deals, DEALS_WITH_IMG);
    }

    public static void saveDealSearchCity(Context context, String cityID) {
        saveData(context, cityID, DEALS_CITY_ID);
    }

    public static void saveDealSearchCalendar(Context context, Calendar cal) {
        saveData(context, cal, DEALS_CALENDAR);
    }

    private static void saveData(Context context, Object obj, String key) {
        Gson gson = new Gson();
        String s = gson.toJson(obj);
        SharedPreferences.Editor editor = context.getSharedPreferences(FLIGHTS, MODE_PRIVATE).edit();
        editor.putString(key, s);
        editor.commit();
    }

    //Carga recursos estaticos
    public static void initialize(Context context) {
        final SharedPreferences sp = context.getSharedPreferences(DATA, MODE_PRIVATE);
        final String airlines = sp.getString(AIRLINE_LIST, null);

        if(airlines == null){
            //Solo si no lo tengo lo voy a buscar
            String callback = "hci.voladeacapp.initialize.AIRLINE_SAVER";
            ApiService.startActionGetAirlines(context, callback);

            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    HashMap<String, String> idMap = (HashMap<String,String>)intent.getSerializableExtra(DATA_AIRLINE_ID_MAP);
                    if(idMap != null) {
                        Gson gson = new Gson();
                        String map = gson.toJson(idMap);
                        System.out.println(map);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(AIRLINE_LIST, map);
                        editor.commit();
                    } else{
                    }

                    //Automaticamente me desuscribo
                    context.unregisterReceiver(this);
                }
            }, new IntentFilter(callback));
        }
    }
}
