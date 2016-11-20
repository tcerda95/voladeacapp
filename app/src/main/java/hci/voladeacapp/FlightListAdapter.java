package hci.voladeacapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import java.util.ArrayList;

public class FlightListAdapter extends BaseAdapter implements UndoAdapter {

    private ArrayList<Flight> listData;
    private LayoutInflater layoutInflater;

    public FlightListAdapter(Context aContext, ArrayList<Flight> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.my_flight_card, parent, false);
            holder = new ViewHolder();
            holder.originView = (TextView) convertView.findViewById(R.id.card_departure_airport_id);
            holder.destinationView = (TextView) convertView.findViewById(R.id.card_arrival_airport_id);
            holder.stateView = (ImageView) convertView.findViewById(R.id.card_status_badge);
            holder.flnumberView = (TextView) convertView.findViewById(R.id.card_flight_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TextView numberTextView = holder.flnumberView;
        TextView originTextView = holder.originView;
        TextView destinationTextView = holder.destinationView;
        //TextView stateTextView = holder.stateView;

        Flight flight = (Flight) getItem(position);

        numberTextView.setText(flight.getNumber());
        originTextView.setText(flight.getDepartureAirport());
        destinationTextView.setText(flight.getArrivalAirport());
        //stateTextView.setText(flight.getState());

        return convertView;
    }

    @NonNull
    @Override
    public View getUndoView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return layoutInflater.inflate(R.layout.undo_view, parent, false);
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull View view) {
        return view.findViewById(R.id.undo_button);
    }


    static class ViewHolder {
        TextView flnumberView;
        TextView originView;
        TextView destinationView;
        ImageView stateView;
    }

}
