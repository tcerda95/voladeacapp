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
        private ConfiguredFlight flight;
        private FlightIdentifier identifier;

        private SharedPreferences sp;
        private SharedPreferences.OnSharedPreferenceChangeListener spChanged;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());


            addPreferencesFromResource(R.xml.flight_preferences);


            identifier =  (FlightIdentifier) getActivity().getIntent().getSerializableExtra("FLIGHT_IDENTIFIER");

            spChanged = new
                    SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences SP, String key) {
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

        }


        @Override
        public void onResume(){
            super.onResume();

            flight =  StorageHelper.getFlight(getActivity(), identifier);
            fn = flight.getSettings();

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("flight_notifications_switch", fn.notificationsActive());
            editor.putBoolean("takeoff_notifications_switch", fn.isActive(NotificationCategory.TAKEOFF));
            editor.putBoolean("landing_notifications_switch", fn.isActive(NotificationCategory.LANDING));
            editor.putBoolean("delay_notifications_switch", fn.isActive(NotificationCategory.DELAY));
            editor.putBoolean("deviation_notifications_switch", fn.isActive(NotificationCategory.DEVIATION));
            editor.putBoolean("cancelation_notifications_switch", fn.isActive(NotificationCategory.CANCELATION));
            editor.commit();


            sp.registerOnSharedPreferenceChangeListener(spChanged);


        }

        @Override
        public void onPause(){
            super.onPause();

            StorageHelper.saveFlight(getActivity(), flight);

            sp.unregisterOnSharedPreferenceChangeListener(spChanged);

            System.out.println("FLIGHT CHANGED! : ");
            System.out.println("NOTIFICATIONS: ");
            System.out.println("Cancelation: " + fn.isActive(NotificationCategory.CANCELATION));
            System.out.println("Takeoff: " + fn.isActive(NotificationCategory.TAKEOFF));
            System.out.println("Deviation: " + fn.isActive(NotificationCategory.DEVIATION));
            System.out.println("Delay: " + fn.isActive(NotificationCategory.DELAY));
            System.out.println("Landing: " + fn.isActive(NotificationCategory.LANDING));
        }
    }

}

