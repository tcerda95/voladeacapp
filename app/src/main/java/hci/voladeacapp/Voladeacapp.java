package hci.voladeacapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class Voladeacapp extends AppCompatActivity {
    private Fragment misVuelosFragment;
    private Fragment promocionesFragment;
    private Fragment resenasFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voladeacapp);

        final FragmentManager fragmentManager = getFragmentManager();

        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        // TODO: preservar el estado de los fragments

        misVuelosFragment = new MisVuelosFragment();
        promocionesFragment = new PromocionesFragment();
        resenasFragment = new ResenasFragment();

    /* Comienza en el fragmento Mis Vuelos */
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.fragment_main_container, misVuelosFragment, MisVuelosFragment.INSTANCE_TAG).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {

                        FragmentTransaction transaction = fragmentManager.beginTransaction();

                        switch (item.getItemId()) {
                            case R.id.action_mis_vuelos:
                                setTitle("Mis Vuelos"); // TODO: agarrar los textos de un string resource
                                transaction.replace(R.id.fragment_main_container, misVuelosFragment, MisVuelosFragment.INSTANCE_TAG).commit();
                                break;
                            case R.id.action_promociones:
                                setTitle("Promociones");
                                transaction.replace(R.id.fragment_main_container, promocionesFragment, PromocionesFragment.INSTANCE_TAG).commit();
                                break;
                            case R.id.action_resenas:
                                setTitle("Reseñas");
                                transaction.replace(R.id.fragment_main_container, resenasFragment, ResenasFragment.INSTANCE_TAG).commit();
                                break;
                        }

                        return true;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mis_vuelos_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        return true;
    }
}
