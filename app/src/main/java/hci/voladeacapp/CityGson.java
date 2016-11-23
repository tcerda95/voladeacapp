package hci.voladeacapp;

import java.io.Serializable;

/**
 * Created by chelo on 11/23/16.
 */

public class CityGson implements Serializable {
    private static final long serialVersiouUID = 1L;

    public class CountryDescriptor implements Serializable{
        private static final long serialVersiouUID = 1L;

        public String id;
    }

    public String id;
    public String name;
    public CountryDescriptor country;

    public Double latitude;
    public Double longitude;

}
