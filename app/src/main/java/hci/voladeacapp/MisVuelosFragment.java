package hci.voladeacapp;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.MODE_PRIVATE;
import static hci.voladeacapp.ApiService.DATA_FLIGHT_GSON;

public class MisVuelosFragment extends Fragment {

    private class RefreshReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            FlightStatusGson updatedGson = (FlightStatusGson)intent.getSerializableExtra(DATA_FLIGHT_GSON);
            if(updatedGson == null)
                return;
            int idx = flight_details.indexOf(new Flight(updatedGson));
            if(idx == -1){
                return;
            }
            Flight toUpdate = flight_details.get(idx);
            toUpdate.update(updatedGson);
            System.out.println("Updated!");
        }
    }


    public final static String INSTANCE_TAG = "hci.voladeacapp.MisVuelos.INSTANCE_TAG";
    public final static String FLIGHT_LIST = "hci.voladeacapp.MisVuelos.FLIGHT_LIST";

    public final static String ACTION_GET_FLIGHT = "hci.voladeacapp.MisVuelos.ACTION_GET_FLIGHT";
    public final static String ACTION_GET_REFRESH = "hci.voladeacapp.MisVuelos.ACTION_GET_REFRESH";

    public final static int GET_FLIGHT = 1;

    private ListView flightsListView;

    ArrayList<Flight> flight_details;
    FlightListAdapter adapter;
    Set<Flight> refresh_bag =  new HashSet<>();
    RefreshReceiver receiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        //Lleno la lista con lo que esta en shared app_preferences
        SharedPreferences sp = getActivity().getPreferences(MODE_PRIVATE);
        String list = sp.getString(FLIGHT_LIST, null); //Si no hay nada devuelve null

        if(list == null || list.length() < 1){
            flight_details = getListData(); //TODO: Borrar
        }
        else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Flight>>() {
            }.getType();

            flight_details = gson.fromJson(list, type);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_misvuelos, parent, false);

        receiver = new RefreshReceiver();
        flightsListView = (ListView) rootView.findViewById(R.id.text_mis_vuelos);

        adapter = new FlightListAdapter(getActivity(),flight_details);
        flightsListView.setAdapter(adapter);

        flightsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //ACA VA LO QUE PASA CUANDO HACE CLICK
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = flightsListView.getItemAtPosition(position);
                Flight flightData = (Flight) o;

                Intent detailIntent = new Intent(getActivity(), FlightDetails.class);
                detailIntent.putExtra("Flight",flightData);

                startActivity(detailIntent);

            }
        });

        flightsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position,
                                                  long lid, boolean checked) {}

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.delete_menu, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        Resources res = getResources();
                        String feedback = flightsListView.getCheckedItemCount() > 1 ?
                                res.getString(R.string.plural_eliminado) : res.getString(R.string.singular_eliminado);
                        deleteChecked();
                        Toast.makeText(getActivity(), feedback, Toast.LENGTH_SHORT).show();
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {}
        });

        FloatingActionButton addButton = (FloatingActionButton)rootView.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // Toast.makeText(getActivity(),"Aca se deberia agregar un vuelo", Toast.LENGTH_LONG).show();
                startActivityForResult(new Intent(v.getContext(), AddFlightActivity.class), GET_FLIGHT);
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh_mis_vuelos);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFlightsStatus();
            }
        });

        return rootView;
    }

    private void deleteChecked() {
        SparseBooleanArray checked = flightsListView.getCheckedItemPositions();
        int size = flight_details.size();
        int removed = 0;

        for (int i = 0; i < size; i++)
            if (checked.get(i))
                flight_details.remove(i-removed++);

        adapter.notifyDataSetChanged();
    }

    /**
     * Realiza la lógica del refresh. En este caso refreshear el estado de los vuelos.
     */
    private void updateFlightsStatus() {

        for(Flight f: flight_details){
            ApiService.startActionGetFlightStatus(getActivity(), f.getAerolinea(), f.getNumber(), ACTION_GET_REFRESH);
        }

        Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swiperefresh_mis_vuelos);
        swipeRefreshLayout.setRefreshing(false); // Quita el ícono del refresh
    }

    protected void addToList(Flight f){
        flight_details.add(f);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Resultado cancelado", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(getActivity(), "Recibi resultado", Toast.LENGTH_SHORT)
                    .show();

            FlightStatusGson resultado = (FlightStatusGson)data.getSerializableExtra(DATA_FLIGHT_GSON);
            if(requestCode == GET_FLIGHT){
                addToList(new Flight(resultado));

            }
        }

    }


    @Override
    public void onResume(){
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_GET_REFRESH));

    }


    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onStop() {
        // Save the user's current game state
        super.onStop();

        Gson gson = new Gson();
        String s = gson.toJson(flight_details);

        SharedPreferences.Editor editor = getActivity().getPreferences(MODE_PRIVATE).edit();
        editor.putString(FLIGHT_LIST, s);
        editor.commit();
    }

    /* PROBANDO UNA ARRAYLIST CUALQUIERA */
    public ArrayList getListData() {
        ArrayList<Flight> results = new ArrayList<Flight>();
        Flight flight1 = new Flight();
        flight1.setArrivalCity("BUENOS AIRES");
        flight1.setNumber("12345");
        flight1.setDepartureCity("NUEVA YORK");
        flight1.setState("EXPLOTADO");
        results.add(flight1);

        Flight flight2 = new Flight();
        flight2.setArrivalCity("EL INFINITO");
        flight2.setNumber("00000");
        flight2.setDepartureCity("MAS ALLA");
        flight2.setState("PERDIDO");
        results.add(flight2);

        Flight flight3 = new Flight();
        flight3.setArrivalCity("MADRID");
        flight3.setNumber("00002");
        flight3.setDepartureCity("BARILOCHE");
        flight3.setState("RESTRASADO");
        results.add(flight3);

        Flight flight4 = new Flight();
        flight4.setArrivalCity("LALA");
        flight4.setNumber("000123");
        flight4.setDepartureCity("ERWER");
        flight4.setState("RESTRASADO");
        results.add(flight4);


        return results;
    }


}
