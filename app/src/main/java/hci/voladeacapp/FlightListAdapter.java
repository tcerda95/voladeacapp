package hci.voladeacapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.ViewHolder> {

    private ArrayList<Flight> listData;
    private LayoutInflater layoutInflater;

    public FlightListAdapter(Context aContext, ArrayList<Flight> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.simplerow, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Flight flight = listData.get(position);

        holder.numberView.setText(flight.getNumber());
        holder.originView.setText(flight.getDepartureCity());
        holder.destinationView.setText(flight.getArrivalCity());
        //holder.stateView.setText(flight.getState());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void onItemDismiss(int position) {
        listData.remove(position);
        notifyItemRemoved(position);
    }

    /*
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.simplerow, null);
            holder = new ViewHolder();
            holder.originView = (TextView) convertView.findViewById(R.id.origin_city);
            holder.destinationView = (TextView) convertView.findViewById(R.id.destination_city);
            //holder.stateView = (TextView) convertView.findViewById(R.id.flight_state);
            holder.numberView = (TextView) convertView.findViewById(R.id.flight_number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TextView numberTextView = holder.numberView;
        TextView originTextView = holder.originView;
        TextView destinationTextView = holder.destinationView;
        //TextView stateTextView = holder.stateView;

        Flight flight = (Flight) getItem(position);

        numberTextView.setText(flight.getNumber());
        originTextView.setText(flight.getDepartureCity());
        destinationTextView.setText(flight.getArrivalCity());
        //stateTextView.setText(flight.getState());

        return convertView;

    }
    */

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView numberView;
        TextView originView;
        TextView destinationView;
        TextView stateView;

        public ViewHolder(View itemView) {
            super(itemView);
            originView = (TextView) itemView.findViewById(R.id.origin_city);
            destinationView = (TextView) itemView.findViewById(R.id.destination_city);
            //stateView = (TextView) itemView.findViewById(R.id.flight_state);
            numberView = (TextView) itemView.findViewById(R.id.flight_number);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Flight flight = listData.get(getLayoutPosition());
            Context context = view.getContext();
            Intent detailIntent = new Intent(context, FlightDetails.class);
            detailIntent.putExtra("Flight", flight);

            context.startActivity(detailIntent);
        }
    }

}
