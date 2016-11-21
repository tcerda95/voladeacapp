package hci.voladeacapp;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static hci.voladeacapp.ApiService.DATA_DEAL_LIST;

public class PromocionesFragment extends Fragment {

    public final static String INSTANCE_TAG = "hci.voladeacapp.Promociones.INSTANCE_TAG";
    private final static String RECEIVER_TAG = "_GET_DEALS_RECEIVE_";

    private ListView cardListView;
    private Calendar fromCalendar;
    private TextView fromDateText;
    private RequestQueue requestQueue;
    private ArrayList<DealGson> deals;
    private HashMap<DealGson, String> imageURLs;
    private PromoCardAdapter promoAdapter;
    private BroadcastReceiver dealsReceiver;
    private boolean registeredReceiver = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_promociones, parent, false);

        fromCalendar = Calendar.getInstance();
        fromDateText = (TextView) rootView.findViewById(R.id.from_date_edit_text);
        updateLabel();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                fromCalendar.set(Calendar.YEAR, year);
                fromCalendar.set(Calendar.MONTH, monthOfYear);
                fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicked edit text" + fromDateText.getText());
                new DatePickerDialog(getActivity(), date, fromCalendar
                        .get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                        fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        deals = new ArrayList<>();
        imageURLs = new HashMap<>();

        cardListView = (ListView) rootView.findViewById(R.id.promo_card_list);
        promoAdapter = new PromoCardAdapter(getActivity(), deals, imageURLs);
        cardListView.setAdapter(promoAdapter);



        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                //TODO: Sacar el vuelo en base al deal para hacer la pantalla de detalles
//                Object o = cardListView.getItemAtPosition(position);
//                Flight flightData = (Flight) o;
//
//                Intent detailIntent = new Intent(getActivity(), FlightDetails.class);
//                detailIntent.putExtra("Flight",flightData);
//
//                startActivity(detailIntent);
                System.out.println("CLICKED: " + position);
            }
        });

        dealsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("Received with " + RECEIVER_TAG);
                List<DealGson> list = (List<DealGson>)intent.getSerializableExtra(DATA_DEAL_LIST);
                for (DealGson d : list) {
                    System.out.println("" + d.city.name + ": " + d.price);
                    deals.add(d);
                    new getCityImageURLTask().execute(d);
                }
                promoAdapter.notifyDataSetChanged();
            }
        };

        if (!registeredReceiver) {
            getActivity().registerReceiver(dealsReceiver, new IntentFilter(RECEIVER_TAG));
            registeredReceiver = true;
        }

        ApiService.startActionGetDeals(rootView.getContext(), "BUE", RECEIVER_TAG);

        return rootView;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.promo_map_menu_item, menu);
        MenuItem mapIcon = menu.findItem(R.id.go_to_map_view);

        mapIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateLabel() {
        String myFormat = getResources().getString(R.string.formato_fecha); //TODO: Localizar formato
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        fromDateText.setText(sdf.format(fromCalendar.getTime()));
    }


    private class getCityImageURLTask extends AsyncTask<DealGson, Void, String> {
        protected String doInBackground(final DealGson... deal) {

            StringRequest sr = new StringRequest(Request.Method.GET, getAPIPetition(deal[0]),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println(response);
                                imageURLs.put(deal[0], getImageURL(new JSONObject(response)));
                                promoAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });

            requestQueue.add(sr);
            return null;
        }

        private String getImageURL(JSONObject obj) {
            try {
                JSONObject photo = obj.getJSONObject("photos").getJSONArray("photo").getJSONObject(0);
                String url = "https://farm"
                        + photo.getString("farm") + ".staticflickr.com/"
                        + photo.getString("server") + "/"
                        + photo.getString("id") + "_"
                        + photo.getString("secret") + ".jpg";

                return url;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String getAPIPetition(DealGson deal) {
            String urlstr =
                    "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                    "&api_key=3fc73140f600953c1eea5e534bac4670&"
                    + "&tags=city" + "&text=" + deal.city.name.split(",")[0].split(" ")[0]
                    + "&sort=interestingness-desc" + "&format=json&nojsoncallback=1";
            //TODO: Hacer bien
            return urlstr;
        }
    }

    @Override
    public void onPause() {
        if (registeredReceiver) {
            getActivity().unregisterReceiver(dealsReceiver);
            registeredReceiver = false;
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (registeredReceiver) {
            getActivity().unregisterReceiver(dealsReceiver);
            registeredReceiver = false;
        }
        super.onDestroy();
    }
}
