package hci.voladeacapp;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static hci.voladeacapp.ApiService.DATA_DEAL_LIST;

public class PromocionesFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public final static String INSTANCE_TAG = "hci.voladeacapp.Promociones.INSTANCE_TAG";
    private final static String RECEIVER_TAG = "_GET_DEALS_RECEIVE_";

    private Calendar fromCalendar;

    private ListView cardListView;
    private TextView fromDateTextView;
    private AutoCompleteTextView fromCityTextView;

    private RequestQueue requestQueue;

    private ArrayList<DealGson> deals;
    private Map<DealGson, String> imageURLs;
    private BroadcastReceiver dealsReceiver;
    private boolean registeredReceiver = false;

    private PromoCardAdapter promoAdapter;
    private ArrayAdapter<String> cityAutocompleteAdapter;

    private GoogleApiClient client;

    private static final String[] CITIES_DUMMY = new String[]{
            "Buenos Aires", "Londres", "Neuquen", "Nueva York",
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        deals = new ArrayList<>();
        imageURLs = new HashMap<>();
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        client = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onStart() {
        client.connect();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_promociones, parent, false);

        fromCalendar = Calendar.getInstance();
        fromDateTextView = (TextView) rootView.findViewById(R.id.from_date_edit_text);
        fromCityTextView = (AutoCompleteTextView) rootView.findViewById(R.id.promo_from_city_autocomplete);
        updateLabel();

        // Datepicker para fecha de salida
        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                fromCalendar.set(Calendar.YEAR, year);
                fromCalendar.set(Calendar.MONTH, monthOfYear);
                fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                refreshResults();
            }
        };

        // Listener fecha salida
        fromDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicked edit text" + fromDateTextView.getText());
                new DatePickerDialog(getActivity(), dateListener, fromCalendar
                        .get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                        fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        cityAutocompleteAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                android.R.layout.select_dialog_item, CITIES_DUMMY);

        fromCityTextView.setAdapter(cityAutocompleteAdapter);

        fromCityTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard(rootView);
                System.out.println("Clicked " + CITIES_DUMMY[position]);
            }
        });

        cardListView = (ListView) rootView.findViewById(R.id.promo_card_list);
        promoAdapter = new PromoCardAdapter(getActivity(), deals, imageURLs);
        cardListView.setAdapter(promoAdapter);

        // Listener para actualización de ciudad de salida
        fromCityTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;
                    hideKeyboard(rootView);
                    refreshResults();
                }
                return handled;
            }
        });

        // Listener para ver detalles del vuelo
        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                //TODO: Sacar el vuelo en base al deal para hacer la pantalla de detalles
                System.out.println("CLICKED: " + position);
            }
        });

        // Receiver para el ApiService
        dealsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<DealGson> list = (List<DealGson>) intent.getSerializableExtra(DATA_DEAL_LIST);
                if (list == null) {
                    System.out.println("NULL LIST");
                } else {
                    for (DealGson d : list) {
                        deals.add(d);
                        getCityImageURL(d);
                    }
                    saveDealsData();
                    promoAdapter.notifyDataSetChanged();
                }
            }
        };

        rootView.findViewById(R.id.dummy_focus_layout).requestFocus(); // Para que el cityView no tenga focus

        // Realiza la búsqueda puesta por defecto después de settear las cosas del view.
        refreshResults();

        return rootView;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            System.out.println("Rejected location permissions");
           //TODO: No me dieron permisos para usar localizacion. Hacer algo. Hay que preguntarlos en algún momento.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        if (mLastLocation != null) {
            System.out.println("Latitude: " + mLastLocation.getLatitude());
            System.out.println("Longitude: " + mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Connection suspended");
        return;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection failed");
    }

    private void saveDealsData() {
        if (getActivity() != null) { //por si se muere el fragment y queda la petición corriendo.
            System.out.println("saving");
            Context context = getActivity().getApplicationContext();
            StorageHelper.saveDeals(context, imageURLs);
            StorageHelper.saveDealSearchCity(context, fromCityTextView.getText().toString());
            StorageHelper.saveDealSearchCalendar(context, fromCalendar);
        }
    }

    private void refreshResults() {
        Context context = getActivity().getApplicationContext();
        Calendar prevSearchCal = StorageHelper.getDealSearchCalendar(context);
        String prevSearchcity = StorageHelper.getDealSearchCity(context);

        boolean sameDay = prevSearchCal != null && prevSearchCal.get(Calendar.DAY_OF_YEAR) != fromCalendar.get(Calendar.DAY_OF_YEAR);
        boolean sameCity = prevSearchcity != null && prevSearchcity.equals(fromCityTextView.getText().toString());

        if (prevSearchCal != null && prevSearchcity != null) {
            System.out.println("Prev city: " + prevSearchcity);
            System.out.println("Prev cal" + prevSearchCal.get(Calendar.DAY_OF_MONTH) + "/" + prevSearchCal.get(Calendar.MONTH));
        }

        if (false) { //(sameCity && sameDay) TODO: se rompe todo
            System.out.println("Using cache!");
            imageURLs = StorageHelper.getDeals(context);
            deals = new ArrayList<>(imageURLs.keySet());
            promoAdapter.notifyDataSetChanged();
        }
        else {
            System.out.println("NOT using cache!");
            deals.clear();
            if (!registeredReceiver) {
                getActivity().registerReceiver(dealsReceiver, new IntentFilter(RECEIVER_TAG));
                registeredReceiver = true;
            }
            ApiService.startActionGetDeals(getActivity().getApplicationContext(), "BUE",RECEIVER_TAG);
        }

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
        String myFormat = getResources().getString(R.string.formato_fecha);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        fromDateTextView.setText(sdf.format(fromCalendar.getTime()));
    }

    private void getCityImageURL(final DealGson deal) {
        StringRequest sr = new StringRequest(Request.Method.GET, getAPIPetition(deal),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            imageURLs.put(deal, getImageURL(new JSONObject(response)));
                            promoAdapter.notifyDataSetChanged();

                            // Ineficiente pero bue
                            saveDealsData();
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

    @Override
    public void onPause() {
        if (registeredReceiver) {
            getActivity().unregisterReceiver(dealsReceiver);
            registeredReceiver = false;
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        client.disconnect();
        super.onStop();
    }

    private void destroyPendingRequests() {
        System.out.println("Destroying pending volley requests");
        // TODO
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.findViewById(R.id.dummy_focus_layout).requestFocus();
    }


}
