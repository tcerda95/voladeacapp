package hci.voladeacapp;

import java.util.List;

/**
 * Created by chelo on 11/21/16.
 */

public class GlobalReview {
    List<ReviewGson> list;

    private final static int CATEGORIES = 7;

    private Double rating;
    private int percentage_recommend;

    private double friendliness;
    private double food;
    private double punctuality;
    private double mileage_program;
    private double comfort;
    private double quality_price;

    public GlobalReview(List<ReviewGson> reviews){
        if(reviews == null){
            throw new IllegalArgumentException("Param reviews mustn't be null");
        }

        int count = 0;
        int recCount = 0;

        for (ReviewGson r : reviews) {
            friendliness += r.rating.friendliness;
            food += r.rating.food;
            punctuality += r.rating.punctuality;
            mileage_program += r.rating.punctuality;
            comfort += r.rating.comfort;
            quality_price += r.rating.comfort;
            if (r.yes_recommend) {
                recCount++;
            }
            count++;
        }

        if (count > 0) {
            //Promedio
            friendliness /= count;
            food /= count;
            punctuality /= count;
            mileage_program /= count;
            comfort /= count;
            quality_price /= count;
        }

        percentage_recommend = (100 * recCount) / count;

        rating = (friendliness + food + punctuality + mileage_program + comfort + quality_price) / CATEGORIES;

        list = reviews;
    }


    public double getRating(){
        return rating;
    }

    public int getRecommendedPercentage(){
        return percentage_recommend;
    }

    //Getters
    public double friendliness(){ return friendliness; }
    public double food(){ return food; }
    public double mileage_program(){ return mileage_program; }
    public double comfort(){ return comfort; }
    public double quality_price(){return quality_price; }
    public double punctuality() {return punctuality; }



}
