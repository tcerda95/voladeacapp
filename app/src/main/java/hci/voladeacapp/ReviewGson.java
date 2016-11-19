package hci.voladeacapp;

import java.io.Serializable;

/**
 * Created by chelo on 11/19/16.
 */

public class ReviewGson implements Serializable{
    private static final long serialVersiouUID = 1L;

        public class AirlineDetails implements  Serializable{
            private static final long serialVersiouUID = 1L;

            public String id;
        }

        public class Rating implements Serializable{
            private static final long serialVersiouUID = 1L;

            int friendliness;
            int food;
            int punctuality;
            int mileage_program;
            int comfort;
            int quality_price;
        }

        public class FlightDetails implements Serializable{
            private static final long serialVersiouUID = 1L;

            public AirlineDetails airline;
            public int number;
        }



    public ReviewGson(String airline_id, int flightNum,
                      int friendliness, int comfort, int food, int quality_price, int punctuality, int mileage_program, boolean yes_recommend){
        rating = new Rating();
        flight = new FlightDetails();
        flight.airline = new AirlineDetails();

        flight.number = flightNum;
        flight.airline.id = airline_id;
        rating.friendliness = friendliness;
        rating.comfort = comfort;
        rating.food = food;
        rating.quality_price = quality_price;
        rating.punctuality = punctuality;
        rating.mileage_program = mileage_program;

        this.yes_recommend = yes_recommend;
        this.comments = "Reseñando desde VoladeAcapp";
    }


    public FlightDetails flight;
        public Rating rating;
        public boolean yes_recommend;
        public String comments;

}
