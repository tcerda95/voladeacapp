package hci.voladeacapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RatingBar;
import android.widget.TextView;

public class ReviewDetail extends AppCompatActivity {
    private static final double SAD_RATING_BOUND = 7.0;
    GlobalReview review;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);
        Intent intent = getIntent();
        review = (GlobalReview)intent.getSerializableExtra("review");
        setTitle(review.airline() + " " + review.flightNumber());
        fillDetails();

    }

    private void fillDetails() {
        RatingBar kindnessBar = (RatingBar) findViewById(R.id.kindness_rating);
        RatingBar comfortBar = (RatingBar) findViewById(R.id.comfort_rating);
        RatingBar foodBar = (RatingBar) findViewById(R.id.food_rating);
        RatingBar priceQualityBar = (RatingBar) findViewById(R.id.pricequality_rating);
        RatingBar frequentFlyerBar = (RatingBar) findViewById(R.id.frequentflyer_rating);
        RatingBar punctualityFlyerBar = (RatingBar) findViewById(R.id.punctuality_rating);
        TextView percentaje = (TextView) findViewById(R.id.percentage_recommend);

        kindnessBar.setRating((float)review.friendliness()/2);
        comfortBar.setRating((float)review.comfort()/2);
        foodBar.setRating((float)review.food());
        priceQualityBar.setRating((float)review.quality_price()/2);
        frequentFlyerBar.setRating((float)review.mileage_program()/2);
        punctualityFlyerBar.setRating((float)review.punctuality()/2);
        percentaje.setText(review.getRecommendedPercentage() + "%");

    }
}
