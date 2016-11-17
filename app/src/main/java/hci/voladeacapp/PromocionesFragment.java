package hci.voladeacapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Date;

public class PromocionesFragment extends Fragment {

    public final static String INSTANCE_TAG = "hci.voladeacapp.Promociones.INSTANCE_TAG";

    private ListView cardListView;

    ImageLoader imageLoader;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_promociones, parent, false);
        Button mapButton = (Button) rootView.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }
        });

        cardListView = (ListView) rootView.findViewById(R.id.promo_card_list);
        cardListView.setAdapter(new PromoCardAdapter(getActivity(), dummyList()));

        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = cardListView.getItemAtPosition(position);
                Flight flightData = (Flight) o;

                Intent detailIntent = new Intent(getActivity(), FlightDetails.class);
                detailIntent.putExtra("Flight",flightData);

                startActivity(detailIntent);
            }
        });




        return rootView;

    }

    private ArrayList<Flight> dummyList() {
        ArrayList<Flight> ar = new ArrayList<Flight>();
        Flight f = new Flight();
        f.setArrivalCity("Cordoba");
        f.setDepartureDate(new Date());
        f.setPrice(400.43);
        f.setNumber("1234");
        ar.add(f);
        f = new Flight();
        f.setArrivalCity("Paris");
        f.setDepartureDate(new Date());
        f.setPrice(1990.43);
        f.setNumber("12355");
        ar.add(f);
        f = new Flight();
        f.setArrivalCity("Cubolandia");
        f.setDepartureDate(new Date());
        f.setPrice(2000.43);
        f.setNumber("955");
        ar.add(f);
        f = new Flight();
        f.setArrivalCity("Bs As");
        f.setDepartureDate(new Date());
        f.setPrice(40.43);
        f.setNumber("575");
        ar.add(f);
        f = new Flight();
        f.setArrivalCity("Guatemala");
        f.setDepartureDate(new Date());
        f.setPrice(4000.43);
        f.setNumber("0129");
        ar.add(f);
        return ar;
    }
}
