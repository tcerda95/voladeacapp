package hci.voladeacapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    private Flight flight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_details);
        this.flight = (Flight) this.getIntent().getSerializableExtra("Flight");
        setTitle(flight.getNumber());
//        fillDetails(flight);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
            // Create the AlertDialog object and return it
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

    private void fillDetails(Flight flight) {
        getTextView(R.id.from_date_data).setText(flight.getDepartureDate().toString());
        getTextView(R.id.to_date_data).setText(flight.getArrivalDate().toString());
        getTextView(R.id.from_boarding_data).setText("");
        getTextView(R.id.to_boarding_data).setText("");
        getTextView(R.id.from_airport_data).setText(flight.getDepartureAirport());
        getTextView(R.id.to_airport_data).setText(flight.getArrivalAirport());
        getTextView(R.id.from_terminal_data).setText("");
        getTextView(R.id.to_terminal_data).setText("");
        getTextView(R.id.from_gate_data).setText("");
        getTextView(R.id.to_gate_data).setText("");
        getTextView(R.id.baggage_claim_data).setText("");
        getTextView(R.id.state_data).setText(flight.getState());
    }

    private TextView getTextView(int id) {
        return (TextView) findViewById(id);
    }


}
