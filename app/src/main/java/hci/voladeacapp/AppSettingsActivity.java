package hci.voladeacapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class AppSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new AppPreferenceFragment()).commit();
    }

    public static class AppPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.app_preferences);


            SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
                    SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences SP,
                                                              String key) {
                            if ("notifications_switch".equals(key)) {
                                Boolean val = SP.getBoolean(key, true);
                                System.out.println(key + "Changed to: " + val);
                            } else {
                                String val = SP.getString(key, "NULL");
                                System.out.println(key + "Changed to: " + val);
                            }
                        }
                    };

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SP.registerOnSharedPreferenceChangeListener(spChanged);
        }
    }

}
