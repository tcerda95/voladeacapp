package hci.voladeacapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class Voladeacapp extends AppCompatActivity {
    private Fragment misVuelosFragment;
    private Fragment promocionesFragment;
    private Fragment resenasFragment;

    private BroadcastReceiver mLangaugeChangedReceiver;


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

        mLangaugeChangedReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, final Intent intent) {
                startActivity(getIntent());
                finish();
            }
        };

        // Register receiver
        registerReceiver(mLangaugeChangedReceiver, new IntentFilter("Language.changed"));

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLangaugeChangedReceiver != null) {
            try {
                unregisterReceiver(mLangaugeChangedReceiver);
                mLangaugeChangedReceiver = null;
            } catch (final Exception e) {
            }
        }
    }
}
