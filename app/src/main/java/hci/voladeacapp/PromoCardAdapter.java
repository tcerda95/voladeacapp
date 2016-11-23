package hci.voladeacapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Bianchi on 16/11/16.
 */

public class PromoCardAdapter extends BaseAdapter {
    private ArrayList<DealGson> cardsData;
    private Map<DealGson, String> flightImages;
    private LayoutInflater inflater;
    private ViewHolder holder;

    public PromoCardAdapter(Context aContext, ArrayList<DealGson> flights, Map<DealGson, String> flightImages) {
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
            fillViewHolder(convertView, (DealGson) getItem(position));
        } else {
            holder = (PromoCardAdapter.ViewHolder) convertView.getTag();
        }

        DealGson deal = (DealGson) getItem(position);
        holder.cityView.setText(deal.city.name.split(",")[0]);
        holder.priceView.setText("U$D " + deal.price);

        // IMAGEN
        setImageView(deal, holder.photoView);

        return convertView;
    }

    private void fillViewHolder(View convertView, final DealGson deal) {
        holder = new ViewHolder();
        holder.cityView = (TextView) convertView.findViewById(R.id.city_info_text);
        holder.priceView = (TextView) convertView.findViewById(R.id.promo_price);
        holder.overflowbtn = (ImageView) convertView.findViewById(R.id.promo_card_overflow);
        holder.photoView = (ImageView) convertView.findViewById(R.id.city_photo);

        holder.overflowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view.findViewById(R.id.promo_card_overflow), deal);
            }
        });

        convertView.setTag(holder);
    }

    private void showPopupMenu(final View btn, final DealGson deal) {
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

    private void setImageView(DealGson deal, ImageView imgView) {
        if (flightImages.containsKey(deal)) {
            Glide.with(imgView.getContext())
                    .load(flightImages.get(deal))
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
    }
}

