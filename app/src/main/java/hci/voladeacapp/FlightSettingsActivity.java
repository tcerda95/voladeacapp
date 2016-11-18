package hci.voladeacapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;


public class FlightSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new FlightPreferenceFragment()).commit();
    }

    public static class FlightPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.flight_preferences);


            SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
                    SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences SP,
                                                              String key) {
                            System.out.println("Preferences changed");
                            return;
                        }
                    };

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SP.registerOnSharedPreferenceChangeListener(spChanged);
        }
    }

}

