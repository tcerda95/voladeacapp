package hci.voladeacapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

//TODO: ARREGLAR TODOS LOS MAGIC NUMBERS
public class ResenaCardAdapter extends BaseAdapter{
    private ArrayList<Resena> cardsData;
    private LayoutInflater inflater;
    private ViewHolder holder;
    RequestQueue rq;

    public ResenaCardAdapter(Context aContext, ArrayList<Resena> listData) {
        this.cardsData = listData;
        inflater = LayoutInflater.from(aContext);
//        rq = Volley.newRequestQueue(aContext);
    }
    @Override
    public int getCount() {
        return cardsData.size();
    }

    @Override
    public Object getItem(int i) {
        return cardsData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.review_card, parent, false);
            holder = new ResenaCardAdapter.ViewHolder();
            holder.airlineView = (TextView) convertView.findViewById(R.id.flight_airline_text);
            holder.numberView = (TextView) convertView.findViewById(R.id.flight_number_text);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar_resena);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon_recommend);
            holder.percentageView = (TextView) convertView.findViewById(R.id.percentage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TextView numberTextView = holder.numberView;
        TextView airlineTextView = holder.airlineView;
        RatingBar ratingBarView = holder.ratingBar;
        TextView percentageView = holder.percentageView;
        ImageView iconView = holder.icon;
        ratingBarView.setClickable(false);


        Resena resena = (Resena) getItem(position);

        numberTextView.setText(resena.getFlightNumber());
        airlineTextView.setText(resena.getFlightAirline());
        ratingBarView.setRating(resena.getPuntuacion()/2);
        //TODO:arreglar el porcentaje de personas que lo recomiendan (no se como se guarda eso o si lo devuelve la api)
        percentageView.setText(resena.getRecomendado().toString());

        if ( resena.getPuntuacion() > 7){
        iconView.setImageResource(R.drawable.ic_sentiment_satisfied_black_24px);
        }
        else {
            iconView.setImageResource(R.drawable.ic_sentiment_dissatisfied_black_24px);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView airlineView;
        TextView numberView;
        TextView percentageView;
        ImageView icon;
        RatingBar ratingBar;
    }
}
