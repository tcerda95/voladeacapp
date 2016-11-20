package hci.voladeacapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class ResenasFragment extends Fragment {
    private ListView cardListView;
    public final static String INSTANCE_TAG = "hci.voladeacapp.Resenas.INSTANCE_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

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

        cardListView = (ListView) rootView.findViewById(R.id.resenas_list);
        cardListView.setAdapter(new ResenaCardAdapter(getActivity(), dummyList()));

        return rootView;
    }

    private ArrayList<Resena> dummyList() {
        ArrayList<Resena> array = new ArrayList<>();
        Resena res = new Resena("123","lala",2,3,4,5,6,7,1,true,"hola");
        array.add(res);
        Resena res2 = new Resena("5667","AR",1,1,2,3,4,5,9,false,"comentario general");
        array.add(res2);
        return array;
    }

}
