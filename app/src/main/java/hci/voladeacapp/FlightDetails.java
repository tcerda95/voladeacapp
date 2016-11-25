package hci.voladeacapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static hci.voladeacapp.MisVuelosFragment.FLIGHT_IDENTIFIER;
import static hci.voladeacapp.MisVuelosFragment.FLIGHT_REMOVED;
import static hci.voladeacapp.MisVuelosFragment.IS_PROMO_DETAIL;
import static hci.voladeacapp.MisVuelosFragment.PROMO_DETAIL_PRICE;

public class FlightDetails extends AppCompatActivity {
    private Menu menu;
    private Flight flight;
    private FlightIdentifier identifier;
    private boolean isPromoDetail;
    private double promoPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_details);

        this.flight = (Flight) this.getIntent().getSerializableExtra("Flight");

        this.identifier = (FlightIdentifier) getIntent().getSerializableExtra(FLIGHT_IDENTIFIER);
        this.isPromoDetail = getIntent().getBooleanExtra(IS_PROMO_DETAIL, false);
        this.promoPrice = getIntent().getDoubleExtra(PROMO_DETAIL_PRICE, -1);

        setTitle(flight.getAirlineID() + " " + flight.getNumber());
        fillDetails(flight);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class goToConfigurationActivityListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            Intent configIntent = new Intent(getApplicationContext(), FlightSettingsActivity.class);
            configIntent.putExtra(FLIGHT_IDENTIFIER, identifier);
            startActivity(configIntent);
            return true;
        }
    }

    private class removeFlightListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(final MenuItem menuItem) {
            if (getIntent().getBooleanExtra(FLIGHT_REMOVED, false)
                || !StorageHelper.flightExists(getApplicationContext(), flight.getIdentifier())) {
                throw new IllegalStateException("Not following this flight!");
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(FlightDetails.this);
            builder.setMessage("Dejar de seguir este vuelo?")
                    .setTitle("Borrar")
                    .setPositiveButton("Dejar de seguir", new Dialog.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(getApplicationContext(), "Dejado de seguir", Toast.LENGTH_LONG).show();
                    setRemovedFlightResult(flight, true);

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

    private void setRemovedFlightResult(Flight flight, boolean isRemoved) {
        Intent intent = getIntent();
        intent.putExtra(MisVuelosFragment.FLIGHT_REMOVED, isRemoved);
        intent.putExtra(MisVuelosFragment.FLIGHT_IDENTIFIER, identifier);
    }


    private class addFlightListener implements MenuItem.OnMenuItemClickListener {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(!getIntent().getBooleanExtra(FLIGHT_REMOVED, false)
                    && StorageHelper.flightExists(getApplicationContext(), flight.getIdentifier())) {
                throw new IllegalStateException("Already followed flight!");
            }

            if(!StorageHelper.flightExists(getApplicationContext(), flight.getIdentifier())){
                //Esta agregando desde ver detalle
                StorageHelper.saveFlight(getApplicationContext(), flight);
                StorageHelper.saveSettings(getApplicationContext(), flight.getIdentifier(), new FlightSettings());
                getIntent().putExtra(AddFlightActivity.NEW_FLIGHT_ADDED, true);
            }

            Toast.makeText(getApplicationContext(), "Agregado a Mis Vuelos", Toast.LENGTH_LONG).show();
            setRemovedFlightResult(flight, false);
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

        boolean added = StorageHelper.flightExists(getApplicationContext(), flight.getIdentifier())
                            &&  !getIntent().getBooleanExtra(FLIGHT_REMOVED , false);

        System.out.println("FLIGHT IS ADDED: "+ added);

        notificationsButton.setVisible(added);
        removeButton.setVisible(added);
        addButton.setVisible(!added);
    }

    private void fillDetails(Flight flight) {
        Resources res = getResources();
        FragmentManager manager = getFragmentManager();
        String baggageClaim = flight.getBaggageClaim() == null ? res.getString(R.string.a_confirmar) : flight.getBaggageClaim();

        ScheduleFragment departureFragment = (ScheduleFragment) manager.findFragmentById(R.id.fragment_salida);
        ScheduleFragment arrivalFragment = (ScheduleFragment) manager.findFragmentById(R.id.fragment_llegada);

        departureFragment.setSchedule(res.getString(R.string.salida), flight.getDepartureSchedule());
        arrivalFragment.setSchedule(res.getString(R.string.llegada), flight.getArrivalSchedule(), baggageClaim);

        getTextView(R.id.airline_name).setText(flight.getFullAirlineName());

        // Estado
        ((ImageView)findViewById(R.id.state_badge)).setImageResource(StatusInterpreter.getStateImage(flight.getState()));
        getTextView(R.id.state_name).setText(StatusInterpreter.getStatusName(getApplicationContext(),flight.getState()));
        getTextView(R.id.state_name).setTextColor(StatusInterpreter.getStatusColor(flight.getState()));

        if (isPromoDetail) {
            findViewById(R.id.promo_details_layout).setVisibility(View.VISIBLE);
            getTextView(R.id.promo_details_price).setText("U$D " + new Double(promoPrice).intValue());
        }
    }

    private TextView getTextView(int id) {
        return (TextView) findViewById(id);
    }

    protected void onResume(){
        super.onResume();
        setResult(Activity.RESULT_OK, getIntent());

    }

    // Se sobreescriben estos dos métodos para definir la Activity padre de forma dinámica.

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        Intent parentIntent;
        boolean isAddFlightParent = getIntent().getBooleanExtra(AddFlightActivity.PARENT_ADD_FLIGHT_ACTIVITY, false);

        if (isAddFlightParent) {
            parentIntent = new Intent(this, AddFlightActivity.class);
            parentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Se reusa la instancia anterior
        }
        else {  // El otro único padre es Mis Vuelos. Considerar pasar un String por el Intent en lugar de un boolean si hubiesen más padres.
            parentIntent = new Intent(this, Voladeacapp.class);
            parentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Se reusa la instancia anterior
        }

        return parentIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this); // Vuelve al padre que fue definido dinámicamente.
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
