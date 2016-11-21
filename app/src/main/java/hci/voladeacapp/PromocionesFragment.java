package hci.voladeacapp;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PromocionesFragment extends Fragment {

    public final static String INSTANCE_TAG = "hci.voladeacapp.Promociones.INSTANCE_TAG";

    private ListView cardListView;
    private Calendar fromCalendar;
    private TextView fromDateText;
    private RequestQueue requestQueue;
    private ArrayList<Flight> flights;
    private HashMap<Flight, String> imageURLs;
    PromoCardAdapter promoAdapter;

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
//        Button cheloDebug = (Button) rootView.findViewById(R.id.chelo_dbg_btn);
//        cheloDebug.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                view.getContext().startActivity(new Intent(view.getContext(), CheloActivity.class));
//            }
//        });


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


//        ArrayAdapter<String> autocompleteAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
//                android.R.layout.simple_list_item_1, new String[] {"Buenos Aires", "Sao Paulo", "Calafate"});
//        AutoCompleteTextView textView = (AutoCompleteTextView)
//                rootView.findViewById(R.id.promo_from_city_autocomplete);
//        textView.setAdapter(autocompleteAdapter);

        flights = dummyList();
        imageURLs = new HashMap<>();

        cardListView = (ListView) rootView.findViewById(R.id.promo_card_list);
        promoAdapter = new PromoCardAdapter(getActivity(), flights, imageURLs);
        cardListView.setAdapter(promoAdapter);

        for (Flight fl: flights) {
            new getCityImageURLTask().execute(fl);
        }

        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = cardListView.getItemAtPosition(position);
                Flight flightData = (Flight) o;

                Intent detailIntent = new Intent(getActivity(), FlightDetails.class);
                detailIntent.putExtra("Flight",flightData);

                startActivity(detailIntent);
            }
        });

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


    private class getCityImageURLTask extends AsyncTask<Flight, Void, String> {
        protected String doInBackground(final Flight... fl) {

            StringRequest sr = new StringRequest(Request.Method.GET, getAPIPetition(fl[0]),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                System.out.println(response);
                                imageURLs.put(fl[0], getImageURL(new JSONObject(response)));
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

        private String getAPIPetition(Flight flight) {
            String urlstr = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=3fc73140f600953c1eea5e534bac4670&"
                    + "&tags=city" + "&text=" + flight.getArrivalCity() + "&sort=interestingness-desc" + "&format=json&nojsoncallback=1";

            return urlstr;
        }
    }

    private ArrayList<Flight> dummyList() {
        ArrayList<Flight> ar = new ArrayList<Flight>();
        Flight f = new Flight();
        f.setArrivalCity("Cordoba");
        f.setDepartureDate(new Date());
        f.setPrice(400.43);
        f.setNumber("1234");
        f.setAirline("Hola");
        ar.add(f);


        f = new Flight();
        f.setArrivalCity("Tucuman");
        f.setDepartureDate(new Date());
        f.setPrice(41230.43);
        f.setNumber("AR0129");
        f.setAirline("Hola");
        ar.add(f);

        f = new Flight();
        f.setArrivalCity("Paris");
        f.setDepartureDate(new Date());
        f.setPrice(1990.43);
        f.setNumber("12355");
        f.setAirline("Hola");

        ar.add(f);
        f = new Flight();
        f.setArrivalCity("Berlin");
        f.setDepartureDate(new Date());
        f.setPrice(2000.43);
        f.setNumber("955");
        f.setAirline("Hola");

        ar.add(f);
        f = new Flight();
        f.setArrivalCity("Guatemala");
        f.setDepartureDate(new Date());
        f.setPrice(4000.43);
        f.setNumber("0129");
        f.setAirline("Hola");


        ar.add(f);

        f = new Flight();
        f.setArrivalCity("Springfield");
        f.setDepartureDate(new Date());
        f.setPrice(409.43);
        f.setNumber("0119");
        f.setAirline("Hola");

        ar.add(f);
        return ar;
    }
}
