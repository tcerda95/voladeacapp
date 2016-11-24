package hci.voladeacapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import static hci.voladeacapp.MisVuelosFragment.FLIGHT_IDENTIFIER;


public class FlightSettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new FlightPreferenceFragment()).commit();
    }

    public static class FlightPreferenceFragment extends PreferenceFragment
    {
        private FlightSettings settings;
        private FlightIdentifier identifier;

        private SharedPreferences sp;
        private SharedPreferences.OnSharedPreferenceChangeListener spChanged;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

            identifier =  (FlightIdentifier) getActivity().getIntent().getSerializableExtra(FLIGHT_IDENTIFIER);

            spChanged = new
                    SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences SP, String key) {
                            switch (key) {
                                case "flight_notifications_switch":
                                    settings.setAllNotifications(SP.getBoolean(key, true));
                                    break;
                                case "takeoff_notifications_switch":
                                    settings.setNotification(NotificationCategory.TAKEOFF, SP.getBoolean(key, true));
                                    break;
                                case "landing_notifications_switch":
                                    settings.setNotification(NotificationCategory.LANDING, SP.getBoolean(key, true));
                                    break;
                                case "delay_notifications_switch":
                                    settings.setNotification(NotificationCategory.DELAY, SP.getBoolean(key, true));
                                    break;
                                case "deviation_notifications_switch":
                                    settings.setNotification(NotificationCategory.DEVIATION, SP.getBoolean(key, true));
                                    break;
                                case "cancelation_notifications_switch":
                                    settings.setNotification(NotificationCategory.CANCELATION, SP.getBoolean(key, true));
                                    break;
                            }

                            return;
                        }
                    };

        }


        @Override
        public void onResume(){
            super.onResume();

            settings = StorageHelper.getSettings(getActivity(), identifier);


            System.out.println("HEY THESE ARE THE SETTINGS");
            System.out.println("Landing: " + settings.isActive(NotificationCategory.LANDING));
            System.out.println("Delay: " + settings.isActive(NotificationCategory.DELAY));
            System.out.println("Cancelation: " + settings.isActive(NotificationCategory.CANCELATION));
            System.out.println("Takeoff: " + settings.isActive(NotificationCategory.TAKEOFF));
            System.out.println("Deviation: " + settings.isActive(NotificationCategory.DEVIATION));
            System.out.println("ALL: " + settings.notificationsActive());

            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("flight_notifications_switch", settings.notificationsActive());
            editor.putBoolean("takeoff_notifications_switch", settings.isActive(NotificationCategory.TAKEOFF));
            editor.putBoolean("landing_notifications_switch", settings.isActive(NotificationCategory.LANDING));
            editor.putBoolean("delay_notifications_switch", settings.isActive(NotificationCategory.DELAY));
            editor.putBoolean("deviation_notifications_switch", settings.isActive(NotificationCategory.DEVIATION));
            editor.putBoolean("cancelation_notifications_switch", settings.isActive(NotificationCategory.CANCELATION));
            editor.commit();

            addPreferencesFromResource(R.xml.flight_preferences);


            sp.registerOnSharedPreferenceChangeListener(spChanged);


        }

        @Override
        public void onPause(){
            super.onPause();

            StorageHelper.saveSettings(getActivity(), identifier, settings);

            sp.unregisterOnSharedPreferenceChangeListener(spChanged);
        }
    }

}

