package hci.voladeacapp;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.synnapps.carouselview.ViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PromoDetailsActivity extends AppCompatActivity {

    CarouselView carouselView;
    RequestQueue requestQueue;
    Flight flight;

    private static int CAROUSEL_SIZE = 7;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_details);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(CAROUSEL_SIZE);

        flight = new Flight();
        flight.setArrivalCity("Buenos Aires Argentina");

        ViewListener viewListener = new ViewListener() {
            @Override
            public View setViewForPosition(int position) {
                View customView = getLayoutInflater().inflate(R.layout.carousel_image_layout, null);
                ImageView image = (ImageView) customView.findViewById(R.id.carousel_image);
                setImageInPosition(image, position);

                return customView;
            }
        };

        carouselView.setViewListener(viewListener);
    }

    private void setImageInPosition(final ImageView img, final int position) {
        final ProgressBar progressBar = (ProgressBar) ((View) img.getParent()).findViewById(R.id.img_loading_indicator);
        progressBar.setVisibility(View.VISIBLE);
        StringRequest sr = new StringRequest(Request.Method.GET, getAPIPetition(flight.getArrivalCity()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Se hace una peticion a Flickr por cada imagen pero anda.
                            setGlideImage(new JSONObject(response), img, position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        requestQueue.add(sr);
    }

    private void setGlideImage(JSONObject resp, final ImageView img, int position) {
        final ProgressBar progressBar = (ProgressBar) ((View) img.getParent()).findViewById(R.id.img_loading_indicator);

        Glide.with(img.getContext())
                .load(getImageURL(resp, position))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            img.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                .centerCrop()
                .crossFade()
                .into(img);
    }

    private String getImageURL(JSONObject obj, int index) {
        try {
            JSONObject photo = obj.getJSONObject("photos").getJSONArray("photo").getJSONObject(index);
            if (photo == null)
                return null;
            return "https://farm"
                    + photo.getString("farm") + ".staticflickr.com/"
                    + photo.getString("server") + "/"
                    + photo.getString("id") + "_"
                    + photo.getString("secret") + ".jpg";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAPIPetition(String city) {
        return
                "https://api.flickr.com/services/rest/?method=flickr.photos.search" +
                        "&api_key=3fc73140f600953c1eea5e534bac4670&"
                        + "&tags=city" + "&text=" + city.replace(',', ' ').replace(' ', '+')
                        + "&sort=interestingness-desc" + "&format=json&nojsoncallback=1";
    }
}
