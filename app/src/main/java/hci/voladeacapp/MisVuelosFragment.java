package hci.voladeacapp;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.MODE_PRIVATE;

public class MisVuelosFragment extends Fragment {

    public final static String INSTANCE_TAG = "hci.voladeacapp.MisVuelos.INSTANCE_TAG";
    public final static String FLIGHT_LIST = "hci.voladeacapp.MisVuelos.FLIGHT_LIST";
    public final static int GET_FLIGHT = 1;

    private ListView flightsListView;

    ArrayList<Flight> flight_details;
    FlightListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_misvuelos, parent, false);

        flightsListView = (ListView) rootView.findViewById(R.id.text_mis_vuelos);

        //Lleno la lista con lo que esta en shared preferences
        SharedPreferences sp = getActivity().getPreferences(MODE_PRIVATE);
        String list = sp.getString(FLIGHT_LIST, null); //Si no hay nada devuelve null

        if(list == null){
            flight_details = getListData(); //TODO: Borrar
        }
        else {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Flight>>() {
            }.getType();

            flight_details = gson.fromJson(list, type);
        }

        adapter = new FlightListAdapter(getActivity(),flight_details);
        flightsListView.setAdapter(adapter);



        flightsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //ACA A LO QUE PASA CUANDO HACE CLICK
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = flightsListView.getItemAtPosition(position);
                Flight flightData = (Flight) o;

                Intent detailIntent = new Intent(getActivity(), FlightDetails.class);
                detailIntent.putExtra("Flight",flightData);

                startActivity(detailIntent);

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

    /**
     * Realiza la lógica del refresh. En este caso refreshear el estado de los vuelos.
     */
    private void updateFlightsStatus() {
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

            FlightStatusGson resultado = (FlightStatusGson)data.getSerializableExtra("RESPONSE");
            if(requestCode == GET_FLIGHT){
                addToList(new Flight(resultado));

            }
        }

    }

    @Override
    public void onPause() {
        // Save the user's current game state
        super.onPause();

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
