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
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ApiService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GET_STATUS = "hci.voladeacapp.action.GET";
    private static final String ACTION_BAZ = "hci.voladeacapp.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "hci.voladeacapp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "hci.voladeacapp.extra.PARAM2";

    public ApiService() {
        super("ApiService");
    }


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetFlightStatus(Context context, String airline, String num) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_GET_STATUS);
        intent.putExtra(EXTRA_PARAM1, airline);
        intent.putExtra(EXTRA_PARAM2, num);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_STATUS.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionGetStatus(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }
    }
    private RequestQueue requestQueue;
    ;

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetStatus(String airline, String number) {
        requestQueue = Volley.newRequestQueue(this);

        String url = "http://hci.it.itba.edu.ar/v1/api/status.groovy?method=getflightstatus&airline_id="
                + airline + "&flight_number=" + number;


        System.out.println("HOLa");

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
                            status = gson.fromJson(obj.getString("status"), type);

                            sendBroadcast(new Intent(Intent.ACTION_ANSWER).putExtra("RESPONSE", status));
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

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
