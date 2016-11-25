package hci.voladeacapp;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static hci.voladeacapp.ApiService.DATA_GLOBAL_REVIEW;

public class ResenasFragment extends Fragment {
    public final static String INSTANCE_TAG = "hci.voladeacapp.Resenas.INSTANCE_TAG";
    private static final String ACTION_FILL_REVIEWS = "hci.voladeacapp.Resenas.FILL_REVIEWS";

    private GridView cardListView;
    private ResenaCardAdapter adapter;

    private int refreshCount;

    private ArrayList<GlobalReview> reviewList;
    private BroadcastReceiver receiver;
    private ArrayList<Flight> flight_list;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        flight_list = StorageHelper.getFlights(getActivity().getApplicationContext());

        reviewList = new ArrayList<>();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getBooleanExtra(ApiService.API_REQUEST_ERROR, false)){
                    ErrorHelper.connectionErrorShow(context);
                }

                else {
                    GlobalReview review = (GlobalReview) intent.getSerializableExtra(DATA_GLOBAL_REVIEW);
                    Flight corresponding = new Flight();
                    corresponding.setIdentifier(new FlightIdentifier(review.airline(), review.flightNumber()));
                    review.setIndex(flight_list.indexOf(corresponding));
                    reviewList.add(review);
                    refreshCount--;
                    if(refreshCount == 0) {
                        Collections.sort(reviewList, new Comparator<GlobalReview>() {
                            @Override
                            public int compare(GlobalReview r1, GlobalReview r2) {
                                return r1.getIndex() - r2.getIndex();
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                    System.out.println("RECEIVED: " + review.airline() + "  " + review.flightNumber());
                }
            }
        };


        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    public void onResume(){
        super.onResume();
        flight_list = StorageHelper.getFlights(getActivity().getApplicationContext());
    }

    @Override
    public void onStart(){
        super.onStart();
        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_FILL_REVIEWS));
    }

    public void onStop(){
        super.onStop();
        getActivity().unregisterReceiver(receiver);
    }


    private void fillList() {
        refreshCount = 0;
        for(Flight f: flight_list){
            ApiService.startActionGetReviews(getActivity(), f.getAerolinea(), f.getNumber(), ACTION_FILL_REVIEWS);
            refreshCount++;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_item_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_resenas, container, false);

        FloatingActionButton addButton = (FloatingActionButton)rootView.findViewById(R.id.add_review_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddReviewActivity.class);
                startActivity(intent);
            }
        });

        reviewList = new ArrayList<>();

        cardListView = (GridView) rootView.findViewById(R.id.resenas_list);
        adapter = new ResenaCardAdapter(getActivity(), reviewList);
        cardListView.setAdapter(adapter);
        fillList();

        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = cardListView.getItemAtPosition(position);
                GlobalReview review = (GlobalReview) o;

                Intent detailIntent = new Intent(getActivity(), ReviewDetail.class);
                detailIntent.putExtra("review",review);

                startActivity(detailIntent);

            }
        });
        System.out.println("onCreateView");
        return rootView;
    }

}
