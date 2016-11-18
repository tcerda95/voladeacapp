package hci.voladeacapp;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class AppSettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);


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
