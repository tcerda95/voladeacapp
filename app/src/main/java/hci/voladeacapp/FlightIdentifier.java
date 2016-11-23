package hci.voladeacapp;

import java.io.Serializable;

/**
 * Created by chelo on 11/22/16.
 */

public class FlightIdentifier implements Serializable {
    private String number;
    private String airline;

    public FlightIdentifier(Flight f){
        number = f.getNumber();
        airline = f.getAirline();
    }

    public String getNumber(){
        return number;
    }

    public String getAirline(){
        return airline;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlightIdentifier identifier = (FlightIdentifier) o;

        if (!number.equals(identifier.number)) return false;
        return airline.equals(identifier.airline);
    }


}
