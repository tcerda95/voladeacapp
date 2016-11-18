package hci.voladeacapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class Voladeacapp extends AppCompatActivity {
    private Fragment misVuelosFragment;
    private Fragment promocionesFragment;
    private Fragment resenasFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voladeacapp);

        final FragmentManager fragmentManager = getFragmentManager();

        // TODO: preservar el estado de los fragments

        misVuelosFragment = new MisVuelosFragment();
        promocionesFragment = new PromocionesFragment();
        resenasFragment = new ResenasFragment();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);

    /* Comienza en el fragmento Mis Vuelos */
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.fragment_main_container, misVuelosFragment, MisVuelosFragment.INSTANCE_TAG).commit();
            bottomBar.selectTabWithId(R.id.action_mis_vuelos);
        }

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (tabId) {
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

        menu.findItem(R.id.action_configuration).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent settingsIntent = new Intent(getApplicationContext(), AppSettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            }
        });

        return true;
    }
}
