package hci.voladeacapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FlightDetails extends AppCompatActivity {
    private ArrayList<ConfiguredFlight> saved_flights;
    private Menu menu;
    private ConfiguredFlight flight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_details);
        this.flight = (ConfiguredFlight) this.getIntent().getSerializableExtra("Flight");
        setTitle(flight.getAirline() + " " + flight.getNumber());
        fillDetails(flight);

        saved_flights = StorageHelper.getFlights(getApplicationContext());
    }

    private class goToConfigurationActivityListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent configIntent = new Intent(getApplicationContext(), FlightSettingsActivity.class);
            configIntent.putExtra("Flight", flight);
            startActivity(configIntent);
            return true;
        }
    }

    private class removeFlightListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(final MenuItem menuItem) {
            if (!saved_flights.contains(flight)) {
                throw new IllegalStateException("Not following this flight!");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(FlightDetails.this);
            builder.setMessage("Dejar de seguir este vuelo?")
                    .setTitle("Borrar")
                    .setPositiveButton("Dejar de seguir", new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saved_flights.remove(flight);
                    Toast.makeText(getApplicationContext(), "Dejado de seguir", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                    updateOptionsMenuVisibility();
                }

            });

            builder.setNegativeButton("Cancelar", new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "No paso nada", Toast.LENGTH_LONG).show();
                    dialog.cancel();
                }

            });

            builder.show();
            return true;
        }
    }

    private class addFlightListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (saved_flights.contains(flight)) {
                throw new IllegalStateException("Already followed flight!");
            }

            saved_flights.add(flight);
            Toast.makeText(getApplicationContext(), "Agregado a Mis Vuelos", Toast.LENGTH_LONG).show();
            updateOptionsMenuVisibility();
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu m) {
        this.menu = m;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.flight_details_menu, menu);

        MenuItem notificationsButton = menu.findItem(R.id.action_flight_config);
        MenuItem removeButton = menu.findItem(R.id.action_remove_flight);
        MenuItem addButton = menu.findItem(R.id.action_add_flight);

        notificationsButton.setOnMenuItemClickListener(new goToConfigurationActivityListener());
        removeButton.setOnMenuItemClickListener(new removeFlightListener());
        addButton.setOnMenuItemClickListener(new addFlightListener());

        updateOptionsMenuVisibility();
        return true;
    }

    private void updateOptionsMenuVisibility() {
        MenuItem notificationsButton = menu.findItem(R.id.action_flight_config);
        MenuItem removeButton = menu.findItem(R.id.action_remove_flight);
        MenuItem addButton = menu.findItem(R.id.action_add_flight);

        boolean added = saved_flights.contains(flight);

        notificationsButton.setVisible(added);
        removeButton.setVisible(added);
        addButton.setVisible(!added);
    }

    private void fillDetails(ConfiguredFlight flight) {
        Resources res = getResources();
        FragmentManager manager = getFragmentManager();
        String baggageClaim = flight.getBaggageClaim() == null ? res.getString(R.string.a_confirmar) : flight.getBaggageClaim();

        ScheduleFragment departureFragment = (ScheduleFragment) manager.findFragmentById(R.id.fragment_salida);
        ScheduleFragment arrivalFragment = (ScheduleFragment) manager.findFragmentById(R.id.fragment_llegada);

        departureFragment.setSchedule(res.getString(R.string.salida), flight.getDepartureSchedule());
        arrivalFragment.setSchedule(res.getString(R.string.llegada), flight.getArrivalSchedule(), baggageClaim);

        getTextView(R.id.state_data).setText(flight.getState());
    }

    private TextView getTextView(int id) {
        return (TextView) findViewById(id);
    }

    @Override
    protected void onDestroy() {
        StorageHelper.saveFlights(getApplicationContext(), saved_flights);
        super.onDestroy();
    }
}
