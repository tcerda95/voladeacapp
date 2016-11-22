package hci.voladeacapp;

import java.io.Serializable;

/**
 * Created by chelo on 11/20/16.
 */

public class ConfiguredFlight extends Flight implements Serializable {
    private static final long serialVersiouUID = 2L;

    private FlightSettings settings;

    public ConfiguredFlight() {
        super();
        settings = new FlightSettings();
    }

    public ConfiguredFlight(FlightStatusGson seed) {
        super(seed);
        settings = new FlightSettings();
    }


    public FlightSettings getSettings(){
        return settings;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}

