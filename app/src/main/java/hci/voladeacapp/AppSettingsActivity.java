package hci.voladeacapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;

//TODO: TEMAS DE EL STACK PARA REINCIAR LA ACTIVITY VOLADEACAPP Y QUE SE CARGUEN LOS STRINGS DE LAS TABS
public class AppSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null)
            getFragmentManager().beginTransaction().add(android.R.id.content, new AppPreferenceFragment()).commit();
        setTitle(getResources().getString(R.string.title_activity_app_settings));
    }

    public static class AppPreferenceFragment extends PreferenceFragment {

        SharedPreferences.OnSharedPreferenceChangeListener spChanged;
        String previousLocale;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.app_preferences);
            previousLocale = Locale.getDefault().getLanguage();
            ListPreference lp = (ListPreference)findPreference("appLanguage");
            if (previousLocale.toString().toLowerCase().equals("en")) {
                lp.setSummary(getActivity().getString(R.string.english));
            }
            else { lp.setSummary(getActivity().getString(R.string.spanish)); }

            System.out.println(Locale.getDefault().toString().toLowerCase());

            spChanged = new
                    SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences SP,
                                                              String key) {
                            switch(key){
                                case "notifications_switch":
                                    Boolean val = SP.getBoolean(key, true);
                                    System.out.println(key + "Changed to: " + val);
                                    break;

                                case "updateFrequency":
                                    String value = SP.getString(key, "NULL");
                                    System.out.println(key + "Changed to: " + value);
                                    break;

                                case "appLanguage":
                                    String language = SP.getString(key,null);
                                    if( language.equals("en")){
                                        setLocation(Locale.ENGLISH);
                                        ListPreference lp = (ListPreference)findPreference("appLanguage");
                                        lp.setSummary(getActivity().getString(R.string.english));
                                    } else {
                                        setLocation(new Locale("es"));
                                        ListPreference lp = (ListPreference)findPreference("appLanguage");
                                        lp.setSummary(getActivity().getString(R.string.spanish));
                                    }

                                    break;
                            }

                        }

                        private void setLocation(Locale location) {
                            Configuration config = new Configuration(getResources().getConfiguration());
                            config.setLocale(location);
                            Locale.setDefault(location);
                            getResources().updateConfiguration(config,getResources().getDisplayMetrics());
                            // TODO: sacar la activity del stack y reload
                            }
                    };

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(spChanged);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(spChanged);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            String current = Locale.getDefault().getLanguage();
            /*lo guargo en Shared Preferences */
            SharedPreferences shp = getActivity().getSharedPreferences(
                    "hci.voladeacapp.PREFERENCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shp.edit();
            String lang;
            if (Locale.getDefault().toString().toLowerCase().equals("en")) {
                lang = "en";
            }
            else {
                lang = "es";
            };
            editor.putString("USER_LANGUAGE",lang );
            editor.commit();
        }
    }

}
