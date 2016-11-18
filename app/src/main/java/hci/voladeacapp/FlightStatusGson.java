package hci.voladeacapp;

import java.io.Serializable;

/**
 * Created by CarliMacbook on 11/17/16.
 */

public class FlightStatusGson implements Serializable {
   private static final long serialVersiouUID = 1L;

    public class AirportDetails implements Serializable{
        private static final long serialVersiouUID = 1L;

        // public String city;
        public String description;
        public String gate;
        public String id;
        public String terminal;
    }

    public class Schedule implements Serializable{
        private static final long serialVersiouUID = 1L;

        public String actual_gate_time;
        public String actual_runway_time;
        public String gate_delay;
        public String runway_delay;
        public String scheduled_gate_time;
        public String scheduled_time;
        public String estimate_runway_time;

        AirportDetails airport;
    }

    public class AirlineDetails implements Serializable{
        private static final long serialVersiouUID = 1L;

        String id;
        String name;
        String logo;
    }

    public AirlineDetails airline;
    public Schedule arrival;
    public Schedule departure;

    public int id;
    public int number;

    public String status;


    public String toString(){
        return "GSON for flight " + airline.name + " " + number;
    }

}
