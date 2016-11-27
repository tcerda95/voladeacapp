package hci.voladeacapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import static hci.voladeacapp.MisVuelosFragment.FLIGHT_IDENTIFIER;
import static hci.voladeacapp.MisVuelosFragment.IS_PROMO_DETAIL;
import static hci.voladeacapp.MisVuelosFragment.PROMO_DETAIL_PRICE;


public class BestFlightReceiver extends BroadcastReceiver {

    Activity activity;
    ProgressDialog pDialog;
    ArrayList<DealGson> deals;

    public BestFlightReceiver(Activity activity, ProgressDialog pDialog, ArrayList<DealGson> deals) {
        this.activity = activity;
        this.pDialog = pDialog;
        this.deals = deals;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(pDialog != null){
            pDialog.hide();
        }

        if(intent.getBooleanExtra(ApiService.API_REQUEST_ERROR, false)) {
            return;
        }
        else {
            FlightStatusGson flGson = (FlightStatusGson) intent.getSerializableExtra(ApiService.DATA_FLIGHT_GSON);
            Flight flight = new Flight(flGson);
            Intent detailIntent = new Intent(this.activity, FlightDetails.class);
            detailIntent.putExtra("Flight", flight);
            detailIntent.putExtra(FLIGHT_IDENTIFIER, flight.getIdentifier());
            detailIntent.putExtra(IS_PROMO_DETAIL, true);
            DealGson asDeal = getDealFromCity(flight.getArrivalCity());
            if (asDeal != null)
                detailIntent.putExtra(PROMO_DETAIL_PRICE, asDeal.price);
            activity.startActivityForResult(detailIntent, MisVuelosFragment.DETAILS_REQUEST_CODE);
        }
    }

    private DealGson getDealFromCity(String arrivalCity) {
        for (DealGson d: deals) {
            System.out.println("ArrivalCity: " + arrivalCity);
            System.out.println("DealCity: " + d.city.name);
            if (d.city.name.equals(arrivalCity))
                return d;
        }
        return null;
    }
}
