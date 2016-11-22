package hci.voladeacapp;

import android.content.Context;
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

/**
 * Created by chelo on 11/20/16.
 */

public class StorageHelper {
    public final static String FLIGHT_LIST = "hci.voladeacapp.MisVuelos.FLIGHT_LIST";
    public final static String FLIGHTS = "hci.voladeacapp.data.FLIGHTS";
    public final static String DEALS_WITH_IMG = "hci.voladeacapp.data.DEALS_WITH_IMG";
    public final static String DEALS_CITY_ID = "hci.voladeacapp.data.DEALS_CITY_ID";
    public final static String DEALS_CALENDAR = "hci.voladeacapp.data.DEALS_CALENDAR";



    // TODO: modularizarlas. No se como.

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

        System.out.println("DEALSMAP: " + s);
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

    public static void saveFlights(Context context, List<ConfiguredFlight> flight_details){
        saveData(context, flight_details, FLIGHT_LIST);
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
}
