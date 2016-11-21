package hci.voladeacapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Bianchi on 16/11/16.
 */

public class PromoCardAdapter extends BaseAdapter {
    private ArrayList<Flight> cardsData;
    private HashMap<Flight, String> flightImages;
    private LayoutInflater inflater;
    private ViewHolder holder;

    public PromoCardAdapter(Context aContext, ArrayList<Flight> flights, HashMap<Flight, String> flightImages) {
        inflater = LayoutInflater.from(aContext);
        this.flightImages = flightImages;
        this.cardsData = flights;
    }

    @Override
    public int getCount() {
        return cardsData.size();
    }

    @Override
    public Object getItem(int position) {
        return cardsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.promo_card, null);
            fillViewHolder(convertView, (Flight) getItem(position));
        } else {
            holder = (PromoCardAdapter.ViewHolder) convertView.getTag();
        }

        Flight flight = (Flight) getItem(position);
        holder.cityView.setText(flight.getArrivalCity());
        System.out.println(flight.getDepartureDate());
        holder.priceView.setText("U$D " + String.valueOf(flight.getPrice()));

        // IMAGEN
        setImageView(flight, holder.photoView);

        return convertView;
    }

    private void fillViewHolder(View convertView, final Flight flight) {
        holder = new ViewHolder();
        holder.cityView = (TextView) convertView.findViewById(R.id.city_info_text);
        holder.priceView = (TextView) convertView.findViewById(R.id.promo_price);
        holder.overflowbtn = (ImageView) convertView.findViewById(R.id.promo_card_overflow);
        holder.photoView = (ImageView) convertView.findViewById(R.id.city_photo);

        holder.overflowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view.findViewById(R.id.promo_card_overflow), flight);
            }
        });

        convertView.setTag(holder);
    }

    private void showPopupMenu(final View btn, final Flight fl) {
        PopupMenu popup = new PopupMenu(btn.getContext(), btn);
        popup.getMenuInflater().inflate(R.menu.promo_item_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.overflow_more_details:
                        System.out.println("More Details");
                        return true;
                    case R.id.overflow_add_flight:
                        System.out.println("Agregar a mis vuelos");
                        return true;
                }
                return false;
            }
        });

        popup.show();
    }

    private void setImageView(Flight flight, ImageView imgView) {
        if (flightImages.containsKey(flight)) {
            Glide.with(imgView.getContext())
                    .load(flightImages.get(flight))
                    .centerCrop()
                    .crossFade()
                    .into(imgView);
        }
    }

    private static class ViewHolder {
        TextView cityView;
        TextView priceView;
        ImageView overflowbtn;
        ImageView photoView;
        //MAS
    }
}

