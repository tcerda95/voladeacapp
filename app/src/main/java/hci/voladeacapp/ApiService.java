package hci.voladeacapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.internal.PlaceOpeningHoursEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class ApiService extends IntentService {

    public static final String DATA_FLIGHT_GSON = "hci.voladeacapp.data.DATA_FLIGHT_GSON";

    private static final String ACTION_GET_STATUS = "hci.voladeacapp.action.GET_STATUS";
    private static final String ACTION_SEND_REVIEW = "hci.voladeacapp.action.SEND_REVIEW";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "hci.voladeacapp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "hci.voladeacapp.extra.PARAM2";
    private static final String CALLBACK_INTENT = "hci.voladeacapp.extra.CALLBACK";


    private RequestQueue requestQueue;


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


    public static void startActionSendReview(Context context, ReviewGson review) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(ACTION_SEND_REVIEW);
        intent.putExtra(EXTRA_PARAM1, review); //SerializableExtra
        context.startService(intent);
    }




    private void handleActionSendReview(ReviewGson review) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }


        String url = "http://hci.it.itba.edu.ar/v1/api/review.groovy?method=reviewairline";

        Gson gson = new Gson();
        Type type = new TypeToken<ReviewGson>() {
        }.getType();

        final String jsonReview = gson.toJson(review ,type);
        System.out.println("AAAAAAAAAAAAAAAAAA ESTOY ACA!");
        System.out.println(jsonReview);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println(response);
                            if(obj.has("review") && obj.getBoolean("review")) {
                                System.out.println("SENT REVIEW SUCCESSFULLY!");
                            } else{
                                //No existe el vuelo
                            }

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR SENDING REVIEW:");
                System.out.println(error);

            }

        }){
            //Clase anonima StringRequest
            @Override
            public byte[] getBody(){
                String s = "{" +
                        " \"flight\": {" +
                        " \"airline\": {" +
                        " \"id\": \"AR\"" +
                        " }," +
                        " \"number\": 5260" +
                        " }," +
                        " \"rating\": {" +
                        " \"friendliness\": 9," +
                        " \"food\": 9," +
                        " \"punctuality\": 9," +
                        " \"mileage_program\": 9," +
                        " \"comfort\": 9," +
                        " \"quality_price\": 9" +
                        " }," +
                        " \"yes_recommend\": true," +
                        " \"comments\": \"Best flight ever!\"" +
                        "}";
                System.out.println(s);
                return s.getBytes();
                //return "{\"comments\":\"Reseñando desde VoladeAcapp\",\"flight\":{\"airline\":{\"id\":\"AA\"},\"number\":236},\"rating\":{\"comfort\":1,\"food\":1,\"friendliness\":1,\"mileage_program\":1,\"punctuality\":1,\"quality_price\":1},\"yes_recommend\":true}".getBytes()
            }

        };

        requestQueue.add(stringRequest);

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
            if(ACTION_SEND_REVIEW.equals(intent.getAction())){
                final ReviewGson review = (ReviewGson)intent.getSerializableExtra(EXTRA_PARAM1);
                handleActionSendReview(review);
            }
        }
    }







    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetStatus(String airline, String number, final String callback) {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }

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
