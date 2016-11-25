package hci.voladeacapp;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.Locale;

public class Voladeacapp extends AppCompatActivity {
    private Fragment misVuelosFragment;
    private PromocionesFragment promocionesFragment;
    private Fragment resenasFragment;

    public static String PERMSISSION_BROADCAST = "PermissionBroadcast";
    public static String PERMISSION_CODE_EXTRA = "BroadcastPermissionCode";
    public static String PERMISSION_GRANT_EXTRA = "BroadcastPermissionGrant";

    private BroadcastReceiver errorReceiver;

    public final static int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, PullRequestReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 180 * 1000, alarmIntent);


        errorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Resources res = getResources();
                ErrorHelper.alert(context, res.getString(R.string.global_conn_err_title), res.getString(R.string.global_conn_err_msg));
            }
        };

        registerReceiver(errorReceiver, new IntentFilter(ErrorHelper.NO_CONNECTION_ERROR));

        checkConnection();
        loadLanguage();

        StorageHelper.initialize(this);

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

    }

    private void checkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo == null){
            ErrorHelper.sendNoConnectionNotice(this);
        }
    }

    private void loadLanguage() {
        SharedPreferences shp = getSharedPreferences(
                "hci.voladeacapp.PREFERENCES",Context.MODE_PRIVATE);
        String language = shp.getString("USER_LANGUAGE", "es");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics()); //TODO: deprecated
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            promocionesFragment.notifyLocationPermission(grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(errorReceiver);
    }
}
