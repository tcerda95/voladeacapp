package hci.voladeacapp;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.SimpleShowcaseEventListener;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static hci.voladeacapp.ApiService.DATA_GLOBAL_REVIEW;

public class ResenasFragment extends Fragment implements ShowCase{
    public final static String INSTANCE_TAG = "hci.voladeacapp.Resenas.INSTANCE_TAG";
    private static final String ACTION_FILL_REVIEWS = "hci.voladeacapp.Resenas.FILL_REVIEWS";

    private GridView cardListView;
    private ResenaCardAdapter adapter;

    private int refreshCount;

    private ArrayList<GlobalReview> reviewList;
    private BroadcastReceiver receiver;
    private View rootView;

    private ErrConnReceiver errConnReceiver;
    private ArrayList<Flight> flight_list;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        flight_list = StorageHelper.getFlights(getActivity().getApplicationContext());



        reviewList = new ArrayList<>();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getBooleanExtra(ApiService.API_REQUEST_ERROR, false)){
                        return;
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
        ErrorHelper.checkConnection(getActivity());
        flight_list = StorageHelper.getFlights(getActivity().getApplicationContext());
    }



    @Override
    public void onStart(){
        super.onStart();
        errConnReceiver = new ErrConnReceiver(getView());

        getActivity().registerReceiver(receiver, new IntentFilter(ACTION_FILL_REVIEWS));
        getActivity().registerReceiver(errConnReceiver, new IntentFilter(ErrorHelper.NO_CONNECTION_ERROR));
        getActivity().registerReceiver(errConnReceiver, new IntentFilter(ErrorHelper.RECONNECTION_NOTICE));

    }

    public void onStop(){
        super.onStop();
        getActivity().unregisterReceiver(receiver);
        getActivity().registerReceiver(errConnReceiver, new IntentFilter(ErrorHelper.NO_CONNECTION_ERROR));
        getActivity().registerReceiver(errConnReceiver, new IntentFilter(ErrorHelper.RECONNECTION_NOTICE));
    }


    private void fillList() {
        refreshCount = 0;
        for(Flight f: flight_list){
            ApiService.startActionGetReviews(getActivity(), f.getAerolinea(), f.getNumber(), ACTION_FILL_REVIEWS);
            refreshCount++;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_resenas, container, false);

        FloatingActionButton addButton = (FloatingActionButton)rootView.findViewById(R.id.add_review_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddReviewActivity.class);
                startActivity(intent);
            }
        });

        reviewList = new ArrayList<>();
        adapter = new ResenaCardAdapter(getActivity(), reviewList);
        cardListView = (GridView) rootView.findViewById(R.id.resenas_list);
        cardListView.setAdapter(adapter);
        fillList();
        cardListView.setEmptyView(rootView.findViewById(R.id.emptyElement));


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


    @Override
    public void setShowcase() {
        final RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // This aligns button to the bottom left side of screen
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lps.addRule(RelativeLayout.CENTER_VERTICAL);
        // Set margins to the button, we add 16dp margins here
        int margin = ((Number) (getResources().getDisplayMetrics().density * 16)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        ShowcaseView sv = new ShowcaseView.Builder(getActivity())
                .setStyle(R.style.CustomShowcaseTheme)
                .setTarget(new ViewTarget(getActivity().findViewById(R.id.action_resenas)))
                .hideOnTouchOutside()
                .blockAllTouches()
                .setContentTitle("Reseñas")
                .setContentText("Acá podes ver las reseñas de los vuelos que seguís")
                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        ShowcaseView sv = new ShowcaseView.Builder(getActivity())
                                .setStyle(R.style.CustomShowcaseTheme)
                                .setTarget(new ViewTarget(getActivity().findViewById(R.id.add_review_button)))
                                .hideOnTouchOutside()
                                .setContentTitle("Dejar una reseña")
                                .setContentText("Haciendo click en este botón podes opinar sobre un vuelo")
                                .blockAllTouches()
                                .setShowcaseEventListener(new SimpleShowcaseEventListener() {

                                    @Override
                                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                        ((Voladeacapp)getActivity()).onHiddenReviewShowcase();
                                    }

                                })
                                .build();
                                sv.setButtonPosition(lps);
                                sv.setButtonText("Finalizar");
                    }

                })
                .build();
        sv.setButtonPosition(lps);
    }
}
