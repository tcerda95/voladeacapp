package hci.voladeacapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;


public class FlightSettingsActivity extends AppCompatActivity {

    private ConfiguredFlight flight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.flight =  (ConfiguredFlight) this.getIntent().getSerializableExtra("Flight");
        getFragmentManager().beginTransaction().replace(android.R.id.content, new FlightPreferenceFragment()).commit();
    }

    public static class FlightPreferenceFragment extends PreferenceFragment
    {
        private FlightSettings fn;
        private ConfiguredFlight flight;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.flight_preferences);

            flight =  (ConfiguredFlight) getActivity().getIntent().getSerializableExtra("Flight");
            fn = flight.getSettings();


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

        @Override
        public void onPause(){
            super.onPause();
            System.out.println("FLIGHT CHANGED! : ");
            System.out.println(flight);
            System.out.println("NOTIFICATIONS: ");
            System.out.println(fn);
            System.out.println("Cancelation: " + fn.isActive(NotificationCategory.CANCELATION));
            System.out.println("Delay: " + fn.isActive(NotificationCategory.DELAY));
            System.out.println("Landing: " + fn.isActive(NotificationCategory.LANDING));
        }
    }

}

