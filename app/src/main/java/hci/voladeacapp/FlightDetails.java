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

public class FlightDetails extends AppCompatActivity {
    private ConfiguredFlight flight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_details);
        this.flight = (ConfiguredFlight) this.getIntent().getSerializableExtra("Flight");
        setTitle(flight.getAirline() + " " + flight.getNumber());
        fillDetails(flight);
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

    public static class removeFlightDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Dejar de seguir este vuelo?")
                    .setTitle("Borrar")
                    .setPositiveButton("Dejar de seguir", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getActivity(), "Vuelo Borrado", Toast.LENGTH_LONG).show();
                            // Remover Vuelo de seguidos y todo eso
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            return builder.create();
        }
    }

    private class removeFlightListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            DialogFragment dialogFragment = new removeFlightDialog();
            dialogFragment.show(getFragmentManager(), "removeQuestion");
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.flight_details_menu, menu);
        MenuItem notificationsButton = menu.findItem(R.id.action_flight_config);
        MenuItem removeButton = menu.findItem(R.id.action_remove_flight);
        MenuItem addButton = menu.findItem(R.id.action_add_flight);

        //TODO
        if (true) { /* flightIsInMyFlights */
            notificationsButton.setVisible(true);
            removeButton.setVisible(true);

            notificationsButton.setOnMenuItemClickListener(new goToConfigurationActivityListener());
            removeButton.setOnMenuItemClickListener(new removeFlightListener());
        }
        else {
            addButton.setVisible(true);
        }

        return true;
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


}
