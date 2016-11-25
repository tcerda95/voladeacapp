package hci.voladeacapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static hci.voladeacapp.AddFlightActivity.NEW_FLIGHT_ADDED;
import static hci.voladeacapp.ApiService.API_REQUEST_ERROR;
import static hci.voladeacapp.ApiService.BEST_FLIGHT_RESPONSE;
import static hci.voladeacapp.ApiService.DATA_BEST_FLIGHT_FOUND;
import static hci.voladeacapp.ApiService.DATA_DEAL_LIST;
import static hci.voladeacapp.MisVuelosFragment.DETAILS_REQUEST_CODE;
import static hci.voladeacapp.MisVuelosFragment.FLIGHT_IDENTIFIER;
import static hci.voladeacapp.MisVuelosFragment.FLIGHT_REMOVED;

public class PromocionesFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public final static String INSTANCE_TAG = "hci.voladeacapp.Promociones.INSTANCE_TAG";
    private final static String RECEIVER_TAG = "_GET_DEALS_RECEIVE_";

    private final static String DEFAULT_CITY = "Nueva York, New York, Estados Unidos"; // Para probar. Tendria que ser BsAs
    private static final String START_DETAIL_CALLBACK = "hci.voladeacapp.START_DETAIL_CALLBACK";

    private View rootView;
    private LayoutInflater layoutInflater;

    private Calendar fromCalendar;

    private TextView fromDateTextView;
    private AutoCompleteTextView fromCityTextView;

    private RequestQueue requestQueue;

    private ArrayList<DealGson> deals;
    private Map<DealGson, String> imageURLs;
    private BroadcastReceiver dealsReceiver;
    private boolean registeredReceiver = false;

    private PromoCardAdapter promoAdapter;
    private Map<String, CityGson> citiesMap;

    private GoogleApiClient client;
    MapViewFragment mapfragment;

    private boolean inListView ;

    private BroadcastReceiver dealIdReceiver;
    private BroadcastReceiver detailStarterReceiver;

    private ProgressDialog pDialog;
    private boolean notifiedConnectionError;

    private CityGson currentCity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Context context = getActivity().getApplication();
        deals = new ArrayList<>();
        imageURLs = new HashMap<>();
        requestQueue = Volley.newRequestQueue(context);
        client = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        citiesMap = StorageHelper.getCitiesMap(context);
        inListView = true;
        notifiedConnectionError = false;

        dealIdReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if(intent.getBooleanExtra(ApiService.API_REQUEST_ERROR, false)){
                            if(!notifiedConnectionError) {
                                System.out.println("1");
                                ErrorHelper.connectionErrorShow(context);
                                notifiedConnectionError = true;
                            }
                        }
                      else{
                        boolean found = intent.getBooleanExtra(DATA_BEST_FLIGHT_FOUND, false);
                        if(found){
                            ApiService.startActionGetFlightStatus(context, (FlightIdentifier)intent.getSerializableExtra("identifier"), START_DETAIL_CALLBACK);
                        }else {
                            ErrorHelper.alert(context, "Se produjo un error", "Intente de nuevo más tarde");
                        }
                    }

                    }
                };

        detailStarterReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(pDialog != null){
                    pDialog.hide();
                }

                if(intent.getBooleanExtra(ApiService.API_REQUEST_ERROR, false)) {
                    if(!notifiedConnectionError) {
                        System.out.println("2");
                        ErrorHelper.connectionErrorShow(context);
                        notifiedConnectionError = true;
                    }
                }
                else {
                    FlightStatusGson flGson = (FlightStatusGson) intent.getSerializableExtra(ApiService.DATA_FLIGHT_GSON);
                    Flight flight = new Flight(flGson);
                    Intent detailIntent = new Intent(getActivity(), FlightDetails.class);
                    detailIntent.putExtra("Flight", flight);
                    detailIntent.putExtra(FLIGHT_IDENTIFIER, flight.getIdentifier());
                    startActivityForResult(detailIntent, MisVuelosFragment.DETAILS_REQUEST_CODE);
                }
            }
        };

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAILS_REQUEST_CODE) {

            boolean addedNew = data.getBooleanExtra(NEW_FLIGHT_ADDED, false);
            boolean deleted = data.getBooleanExtra(FLIGHT_REMOVED, false);
            if(deleted) {
                //Borró y hay que borrarlo de la lista
                FlightIdentifier identifier = (FlightIdentifier)data.getSerializableExtra(FLIGHT_IDENTIFIER);
                StorageHelper.deleteFlight(getActivity(), identifier);
            }
        }
    }

    @Override
    public void onStart() {
        client.connect();
        super.onStart();
    }


    @Override
    public void onResume(){
        super.onResume();
        getActivity().registerReceiver(dealIdReceiver,  new IntentFilter(BEST_FLIGHT_RESPONSE));
        getActivity().registerReceiver(detailStarterReceiver, new IntentFilter(START_DETAIL_CALLBACK));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        layoutInflater = inflater;
        rootView = layoutInflater.inflate(R.layout.fragment_promociones, parent, false);

        pDialog = new ProgressDialog(getActivity());
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
        rootView.findViewById(R.id.date_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), dateListener, fromCalendar
                        .get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                        fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ArrayAdapter<String> cityAutocompleteAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                android.R.layout.select_dialog_item, new ArrayList<>(citiesMap.keySet()));

        fromCityTextView.setAdapter(cityAutocompleteAdapter);

        fromCityTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hideKeyboard(rootView);
                refreshResults();
            }
        });

        ListView cardListView = (ListView) rootView.findViewById(R.id.promo_card_list);
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
                String originId = currentCity.id;
                String destId = deals.get(position).city.id;
                Double price = deals.get(position).price;

                pDialog.setMessage("Cargando el vuelo para vos mami");
                pDialog.show();

                ApiService.startActionGetBestFlight(v.getContext(), originId, destId, price);

            }
        });

        // Receiver para el ApiService
        dealsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getBooleanExtra(ApiService.API_REQUEST_ERROR, false)){
                    if(!notifiedConnectionError) {
                        System.out.println("3");
                        ErrorHelper.connectionErrorShow(context);
                        notifiedConnectionError = true;
                    }
                    return; //Me voy
                }


                List<DealGson> list = (List<DealGson>) intent.getSerializableExtra(DATA_DEAL_LIST);
                if (list == null) {
                    System.out.println("NULL LIST");
                } else {
                    for (DealGson d : list) {
                        deals.add(d);
                        getCityImageURL(d);
                    }
                    saveDealsData();
                    if (inListView)
                        promoAdapter.notifyDataSetChanged();
                    else
                        mapfragment.updateMap(deals, fromCityTextView.getText().toString(), fromCalendar);
                }
            }
        };

        fromCityTextView.addTextChangedListener(new CityTextWatcher());

        rootView.findViewById(R.id.dummy_focus_layout).requestFocus(); // Para que el cityView no tenga focus

        // Realiza la búsqueda puesta por defecto después de settear las cosas del view.
        refreshResults();

        /* MAP */
//        android.app.FragmentManager fragmentManager = getFragmentManager();
//        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);
//        fragmentManager.beginTransaction().add(mapFragment, "MAPAAAAAAAAAA");
//        mapFragment.getMapAsync(new PromoMapCallback());

        return rootView;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (!getLocationAndSearch()) {
            //No permissions
            requestLocationPermissions();
        }
    }

    private boolean requestLocationPermissions() {
        if (!hasLocationPermissions()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Voladeacapp.LOCATION_PERMISSION_REQUEST_CODE);

                Toast.makeText(getActivity().getApplicationContext(), "Supuesta explicacion", Toast.LENGTH_LONG).show();
            } else {
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Localización");
                alertDialog.setMessage("Permitinos usar tu localización para mostrarte promociones saliendo desde un lugar cerca tuyo.");

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                Voladeacapp.LOCATION_PERMISSION_REQUEST_CODE);
                    }
                });

                alertDialog.show();
                Toast.makeText(getActivity().getApplicationContext(), "De una corte", Toast.LENGTH_LONG);
            }
        }
        return true;
    }

    private boolean getLocationAndSearch() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
            if (mLastLocation == null) {
                locationError();
            } else {
                fromCityTextView.setText(getClosestCity(mLastLocation));
                refreshResults();
            }
            return true;
        }
        return false;
    }

    public boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private String getClosestCity(Location userLocation) {
        Set<Map.Entry<String, CityGson>> entries = citiesMap.entrySet();
        Location cityLocation = new Location("CityLocation");
        double minDistance = Double.MAX_VALUE;
        String closestCity  = null;

        for (Map.Entry<String, CityGson> e: entries) {
            cityLocation.setLatitude(e.getValue().latitude);
            cityLocation.setLongitude(e.getValue().longitude);
            double auxDistance = userLocation.distanceTo(cityLocation);
            if (auxDistance < minDistance) {
                closestCity = e.getKey();
                minDistance = auxDistance;
                System.out.println(closestCity);
            }
        }

        return closestCity;
    }

    @Override
    public void onConnectionSuspended(@NonNull int i) {
        rootView.findViewById(R.id.promos_no_connection_layout).setVisibility(View.VISIBLE);
        System.out.println("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        rootView.findViewById(R.id.promos_no_connection_layout).setVisibility(View.VISIBLE);
        System.out.println("Connection failed");
    }

    private void saveDealsData() {
        if (getActivity() != null) { //por si se muere el fragment y queda la petición corriendo.
//            System.out.println("saving");
            Context context = getActivity().getApplicationContext();
            StorageHelper.saveDeals(context, imageURLs);
            StorageHelper.saveDealSearchCity(context, fromCityTextView.getText().toString());
            StorageHelper.saveDealSearchCalendar(context, fromCalendar);
        }
    }

    private void refreshResults() {
        Context context = getActivity().getApplicationContext();
        if (!isValidCity(fromCityTextView.getText().toString()))
            return; // Deja la búsqueda como la última realizada

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
                System.out.println("Registered");
                registeredReceiver = true;
            }
            CityGson city = citiesMap.get(fromCityTextView.getText().toString());

            if (city == null) {
                System.out.println("INVALID CITY");
            } else {
                currentCity = city;
                ApiService.startActionGetDeals(getActivity().getApplicationContext(), city.id, RECEIVER_TAG);
            }
        }

    }

    boolean mapAdded = false;

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.promo_map_menu_item, menu);
        final MenuItem mapIcon = menu.findItem(R.id.go_to_map_view);
        final MenuItem listIcon = menu.findItem(R.id.go_to_list_view);

        mapfragment = MapViewFragment.newInstance(deals, fromCityTextView.getText().toString(), fromCalendar);

        final View listView = rootView.findViewById(R.id.promo_card_list);
        mapIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                inListView = false;

                if (!mapAdded) {
                    final FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.promos_map_parent, mapfragment, "MAPAAAAAA").commit();
                    mapAdded = true;
                }

                View mapView = rootView.findViewById(R.id.mapView);
                listView.setVisibility(View.GONE);
                if (mapView != null)
                    mapView.setVisibility(View.VISIBLE);
                mapIcon.setVisible(false);
                listIcon.setVisible(true);
                mapfragment.updateMap(deals, fromCityTextView.getText().toString(), fromCalendar);
                return true;
            }
        });

        listIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                inListView = true;
                listView.setVisibility(View.VISIBLE);
                View mapView = rootView.findViewById(R.id.mapView);
                if (mapView != null)
                    mapView.setVisibility(View.GONE);
                mapIcon.setVisible(true);
                listIcon.setVisible(false);
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
            return "https://farm"
                    + photo.getString("farm") + ".staticflickr.com/"
                    + photo.getString("server") + "/"
                    + photo.getString("id") + "_"
                    + photo.getString("secret") + ".jpg";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAPIPetition(DealGson deal) {
        return
                "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                        "&api_key=3fc73140f600953c1eea5e534bac4670&"
                        + "&tags=city" + "&text=" + deal.city.name.replace(',', ' ').replace(' ', '+')
                        + "&sort=interestingness-desc" + "&format=json&nojsoncallback=1";
    }

    @Override
    public void onPause() {
        if (registeredReceiver) {
            getActivity().unregisterReceiver(dealsReceiver);
            registeredReceiver = false;
            System.out.println("Unregistered");
        }
        getActivity().unregisterReceiver(dealIdReceiver);
        getActivity().unregisterReceiver(detailStarterReceiver);

        super.onPause();
    }

    @Override
    public void onStop() {
        client.disconnect();
        super.onStop();
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        view.findViewById(R.id.dummy_focus_layout).requestFocus();
    }

    private boolean isValidCity(String name) {
        return citiesMap.containsKey(name);
    }


    private class CityTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length() > 2 && !fromCityTextView.isPopupShowing() && !isValidCity(editable.toString())) {
                fromCityTextView.setError(getResources().getString(R.string.invalid_city_message));
            }
        }

    }
    public void notifyLocationPermission(boolean granted) {
        if (granted) {
            getLocationAndSearch();
        } else {
            locationError();
        }

    }

    private void locationError() {
        Toast.makeText(getActivity().getApplicationContext(),
                getResources().getString(R.string.couldnt_determine_position), Toast.LENGTH_SHORT).show();
        fromCityTextView.setText(DEFAULT_CITY);
        refreshResults();
    }

    @Override
    public void onDestroy() {
        mapAdded = false;
        super.onDestroy();
    }
}

