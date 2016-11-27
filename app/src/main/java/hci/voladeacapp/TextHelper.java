package hci.voladeacapp;

import android.app.Activity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Bianchi on 27/11/16.
 */

public class TextHelper {
    public static String getSimpleDate(Date date, Activity activity) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(activity.getResources().getString(R.string.formato_fecha));
        return dateFormat.format(date);
    }

    public static String getAsPrice(double price) {
        return "U$D " + Double.valueOf(price).intValue();
    }
}
