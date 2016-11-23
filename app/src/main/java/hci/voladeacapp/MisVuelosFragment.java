package hci.voladeacapp;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.TimedUndoAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static hci.voladeacapp.ApiService.DATA_FLIGHT_GSON;

public class MisVuelosFragment extends Fragment {


    private class RefreshReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            FlightStatusGson updatedGson = (FlightStatusGson)intent.getSerializableExtra(DATA_FLIGHT_GSON);
            if(updatedGson == null)
                return;
            int idx = flight_details.indexOf(new ConfiguredFlight(updatedGson));
            if(idx == -1){
                return;
            }
            ConfiguredFlight toUpdate = flight_details.get(idx);
            toUpdate.update(updatedGson);
            System.out.println("Updated!");
            abortBroadcast();
        }
    }

    public final static String INSTANCE_TAG = "hci.voladeacapp.MisVuelos.INSTANCE_TAG";

    public final static String ACTION_GET_FLIGHT = "hci.voladeacapp.MisVuelos.ACTION_GET_FLIGHT";
    public final static String ACTION_GET_REFRESH = "hci.voladeacapp.MisVuelos.ACTION_GET_REFRESH";
    public static final String FLIGHT_IDENTIFIER = "hci.voladeacapp.extra.FLIGHT_IDENTIFIER";


    public final static String FLIGHT_REMOVED = "hci.voladeacapp.MisVuelos.FLIGHT_REMOVED";

    private final static int DETAILS_REQUEST_CODE = 2;

    public final static int GET_FLIGHT = 1;
    private final static long UNDO_TIMEOUT = 3000;

    private DynamicListView flightsListView;

    RefreshReceiver receiver;
    private ArrayList<ConfiguredFlight> flight_details;

    private TimedUndoAdapter adapter;

    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        System.out.println("ON CREATE VIEW!!!!!!1111111111111111111");
        flight_details = StorageHelper.getFlights(getActivity().getApplicationContext());
        rootView = inflater.inflate(R.layout.fragment_misvuelos, parent, false);


        receiver = new RefreshReceiver();

        FlightListAdapter flightListAdapter = new FlightListAdapter(getActivity(), flight_details);


        flightsListView = (DynamicListView) rootView.findViewById(R.id.text_mis_vuelos);

        adapter = new TimedUndoAdapter(flightListAdapter, getActivity(), new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions)
                    flight_details.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        adapter.setTimeoutMs(UNDO_TIMEOUT);

        adapter.setAbsListView(flightsListView);
        flightsListView.setAdapter(adapter);
        flightsListView.enableSimpleSwipeUndo();

        flightsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //ACA VA LO QUE PASA CUANDO HACE CLICK
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = flightsListView.getItemAtPosition(position);
                ConfiguredFlight flightData = (ConfiguredFlight) o;


                Intent detailIntent = new Intent(getActivity(), FlightDetails.class);
                detailIntent.putExtra(FLIGHT_IDENTIFIER, new FlightIdentifier(flightData));

                startActivityForResult(detailIntent, DETAILS_REQUEST_CODE);
            }
        });

        flightsListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position,
                                                  long lid, boolean checked) {
                int checkedCount = flightsListView.getCheckedItemCount();
                actionMode.setTitle(getResources().getQuantityString(R.plurals.selected_flights, checkedCount, checkedCount));
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.delete_menu, menu);
                hideActions();
                
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        Resources res = getResources();
                        String feedback = res.getQuantityString(R.plurals.deleted_flights, flightsListView.getCheckedItemCount());
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
            public void onDestroyActionMode(ActionMode actionMode) {
                showActions();
            }
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

    private void hideActions() {
        Voladeacapp activity = (Voladeacapp) getActivity();
        if (activity != null)
            activity.hideActions();

        FloatingActionButton addButton = (FloatingActionButton)rootView.findViewById(R.id.add_button);
        addButton.setVisibility(View.GONE);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh_mis_vuelos);
        swipeRefreshLayout.setEnabled(false);
    }

    private void showActions() {
        Voladeacapp activity = (Voladeacapp) getActivity();
        if (activity != null)
            activity.showActions();

        FloatingActionButton addButton = (FloatingActionButton)rootView.findViewById(R.id.add_button);
        addButton.setVisibility(View.VISIBLE);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh_mis_vuelos);
        swipeRefreshLayout.setEnabled(true);
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

        for(ConfiguredFlight f: flight_details){
            ApiService.startActionGetFlightStatus(getActivity(), f.getAerolinea(), f.getNumber(), ACTION_GET_REFRESH);
        }

        Toast.makeText(getActivity(), getResources().getString(R.string.refreshed), Toast.LENGTH_SHORT).show();
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swiperefresh_mis_vuelos);
        swipeRefreshLayout.setRefreshing(false); // Quita el ícono del refresh
    }

    protected void addToList(ConfiguredFlight f){
        flight_details.add(f);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        resetList();

        if (requestCode == DETAILS_REQUEST_CODE && resultCode == AddFlightActivity.RESULT_OK) {
          //  flight_details = StorageHelper.getFlights(getActivity());
        }

        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Resultado cancelado", Toast.LENGTH_SHORT)
                    .show();
        }
        else {
            Toast.makeText(getActivity(), "Recibi resultado", Toast.LENGTH_SHORT)
                    .show();

            FlightStatusGson resultado = (FlightStatusGson)data.getSerializableExtra(DATA_FLIGHT_GSON);
            if(requestCode == GET_FLIGHT){
                addToList(new ConfiguredFlight(resultado));

            }
        }
    }

    private void resetList() {
        flight_details.clear();
        List<ConfiguredFlight> list = StorageHelper.getFlights(getActivity());
        for(ConfiguredFlight f : list){
            flight_details.add(f);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter ifilter = new IntentFilter(ACTION_GET_REFRESH);
        ifilter.setPriority(10);
        getActivity().registerReceiver(receiver, ifilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(receiver);
        StorageHelper.saveFlights(getActivity().getApplicationContext(), flight_details);
    }
}
