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
        private FlightSettings fn;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.flight_preferences);
//            fn = (FlightSettings) getActivity().getIntent().getSerializableExtra("FlightSettings");
            fn = new FlightSettings(); // TODO: sacar, se lo tienen que mandar por afuera

            SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
                    SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences SP, String key) {

                            // TODO: Esto es un asco pero por ahora no se hacerlo de otra forma
                            switch (key) {
                                case "flight_notifications_switch":
                                    fn.setAllNotifications(SP.getBoolean(key, true));
                                    break;
                                case "takeoff_notifications_switch":
                                    fn.setNotification(NotificationCategory.TAKEOFF, SP.getBoolean(key, true));
                                    break;
                                case "landing_notifications_switch":
                                    fn.setNotification(NotificationCategory.LANDING, SP.getBoolean(key, true));
                                    break;
                                case "delay_notifications_switch":
                                    fn.setNotification(NotificationCategory.DELAY, SP.getBoolean(key, true));
                                    break;
                                case "deviation_notifications_switch":
                                    fn.setNotification(NotificationCategory.DEVIATION, SP.getBoolean(key, true));
                                    break;
                                case "cancelation_notifications_switch":
                                    fn.setNotification(NotificationCategory.CANCELATION, SP.getBoolean(key, true));
                                    break;
                            }

                            return;
                        }
                    };

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SP.registerOnSharedPreferenceChangeListener(spChanged);
        }
    }

}

