package hci.voladeacapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

        promocionesFragment = new PromocionesFragment();
        resenasFragment = new ResenasFragment();

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        misVuelosFragment = fragmentManager.findFragmentByTag(MisVuelosFragment.INSTANCE_TAG);

        if (misVuelosFragment == null)
            misVuelosFragment = new MisVuelosFragment();

        /* Comienza en el fragmento Mis Vuelos */
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction().add(R.id.fragment_main_container, misVuelosFragment, MisVuelosFragment.INSTANCE_TAG).commit();
            bottomBar.selectTabWithId(R.id.action_mis_vuelos);
        }

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                Resources res = getResources();
                switch (tabId) {
                    case R.id.action_mis_vuelos:
                        setTitle(res.getString(R.string.title_mis_vuelos));
                        transaction.replace(R.id.fragment_main_container, misVuelosFragment, MisVuelosFragment.INSTANCE_TAG).commit();
                        break;
                    case R.id.action_promociones:
                        setTitle(res.getString(R.string.title_promociones));
                        transaction.replace(R.id.fragment_main_container, promocionesFragment, PromocionesFragment.INSTANCE_TAG).commit();
                        break;
                    case R.id.action_resenas:
                        setTitle(res.getString(R.string.title_resenas));
                        transaction.replace(R.id.fragment_main_container, resenasFragment, ResenasFragment.INSTANCE_TAG).commit();
                        break;
                }

            }
        });
    }

    public void hideActions() {
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setVisibility(View.GONE);
    }

    public void showActions() {
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mis_vuelos_menu, menu);

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
