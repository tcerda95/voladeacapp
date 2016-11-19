package hci.voladeacapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.internal.PlaceOpeningHoursEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class ApiService extends IntentService {

    public static final String DATA_FLIGHT_GSON = "hci.voladeacapp.data.DATA_FLIGHT_GSON";

    private static final String ACTION_GET_STATUS = "hci.voladeacapp.action.GET";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "hci.voladeacapp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "hci.voladeacapp.extra.PARAM2";
    private static final String CALLBACK_INTENT = "hci.voladeacapp.extra.CALLBACK";

    public ApiService() {
        super("ApiService");
    }


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionGetFlightStatus(Context context, String airline, String num, String callback) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_GET_STATUS);
        intent.putExtra(EXTRA_PARAM1, airline);
        intent.putExtra(EXTRA_PARAM2, num);
        intent.putExtra(CALLBACK_INTENT, callback);
        context.startService(intent);
    }

    public static void startActionGetFlightStatus(Context context, Flight flight, String callbackAction) {
        startActionGetFlightStatus(context, flight.getAirline(), flight.getNumber(), callbackAction);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_STATUS.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                final String callback = intent.getStringExtra(CALLBACK_INTENT);
                handleActionGetStatus(param1, param2, callback);
            }
        }
    }


    private RequestQueue requestQueue;

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetStatus(String airline, String number, final String callback) {
        requestQueue = Volley.newRequestQueue(this);

        String url = "http://hci.it.itba.edu.ar/v1/api/status.groovy?method=getflightstatus&airline_id="
                + airline + "&flight_number=" + number;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            Gson gson = new Gson();
                            Type type = new TypeToken<FlightStatusGson>() {
                            }.getType();

                            FlightStatusGson status;
                            if(obj.has("status")) {
                                status = gson.fromJson(obj.getString("status"), type);
                            } else{
                                //No existe el vuelo
                                status = null;
                            }

                            sendOrderedBroadcast(new Intent(callback).putExtra(DATA_FLIGHT_GSON, status), null);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);

            }
        });
        // Add the request to the RequestQueue;
        requestQueue.add(stringRequest);


    }

}
