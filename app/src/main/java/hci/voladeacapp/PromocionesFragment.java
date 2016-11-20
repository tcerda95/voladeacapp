package hci.voladeacapp;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PromocionesFragment extends Fragment {

    public final static String INSTANCE_TAG = "hci.voladeacapp.Promociones.INSTANCE_TAG";

    private ListView cardListView;
    private ImageLoader imageLoader;
    private ImageView imageView;
    private Calendar fromCalendar;
    private TextView fromDateText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_promociones, parent, false);
//        Button mapButton = (Button) rootView.findViewById(R.id.map_button);
//        mapButton.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), MapActivity.class);
//                startActivity(intent);
//            }
//        });

        Button cheloDebug = (Button) rootView.findViewById(R.id.chelo_dbg_btn);
        cheloDebug.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(), CheloActivity.class));
            }
        });


        fromCalendar = Calendar.getInstance();
        fromDateText = (TextView) rootView.findViewById(R.id.from_date_edit_text);
        updateLabel();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                fromCalendar.set(Calendar.YEAR, year);
                fromCalendar.set(Calendar.MONTH, monthOfYear);
                fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        fromDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicked edit text" + fromDateText.getText());
                new DatePickerDialog(getActivity(), date, fromCalendar
                        .get(Calendar.YEAR), fromCalendar.get(Calendar.MONTH),
                        fromCalendar.get(Calendar.DAY_OF_MONTH)).show();
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.promo_map_menu_item, menu);
        MenuItem mapIcon = menu.findItem(R.id.go_to_map_view);

        mapIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //TODO: Localizar formato
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        fromDateText.setText("Fecha de salida: " + sdf.format(fromCalendar.getTime()));
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
