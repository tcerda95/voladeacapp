package hci.voladeacapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import static hci.voladeacapp.NotificationType.ON_TIME;

//TODO: Completar con los datos del Flight real

/**
 * Se encarga de generar las notificaciones según el estado de un vuelo
 */
public class NotificationCreator extends Activity {

    public static void createNotification(Context c, Flight f, NotificationCategory category) {
        switch(category){
            case TAKEOFF:
                createNotification(c,f, createTakeoffBuilder(c,f));
                break;
            case LANDING:
                createNotification(c,f,createLandedBuilder(c,f));
                break;
            case DEVIATION:
                createNotification(c,f,createDeviationBuilder(c,f));
                break;
            case DELAY_TAKEOFF:
                createNotification(c, f, createTakeoffDelayedBuilder(c,f));
                break;
            case DELAY_LANDING:
                createNotification(c, f, createLandingDelayedBuilder(c,f));
                break;
            case CANCELATION:
                createNotification(c,f,createCancelationBuilder(c,f));
                break;
        }

    }


    private static void createNotification(Context c, Flight f, NotificationCompat.Builder builder) {
        Intent intent = new Intent(c, FlightDetails.class);
        intent.putExtra("Flight", f);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
        stackBuilder.addParentStack(FlightDetails.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pIntent = stackBuilder.getPendingIntent(f.hashCode(), PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentIntent(pIntent);
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        //El id está construido por el numero de vuelo
        //TODO: ver de incluir de alguna forma la aerolinea para que no se repitan
        notificationManager.notify(Integer.parseInt(f.getNumber()), builder.build());
        //TODO: ver que pasa si se superponen ¿deberia ser asi?¿ o las agrupo?
    }

    private static NotificationCompat.Builder createNotificationBuilder(Context c, int iconDrawable, String contentTitle, String bigText) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(iconDrawable)
                        .setContentTitle(contentTitle)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(bigText));
        return mBuilder;
    }

    private static NotificationCompat.Builder createScheduledBuilder(Context c, Flight f) {
        String contentTitle = String.format(c.getString(R.string.scheduled_notif_title), f.getAirlineID(), f.getNumber());
        String bigText = String.format(c.getString(R.string.scheduled_notif_body), f.getDepartureBoardingTime());
        return createNotificationBuilder(c, R.drawable.ic_flight_takeoff_black_24px, contentTitle, bigText);
    }

    private static NotificationCompat.Builder createTakeoffDelayedBuilder(Context c, Flight f) {
        String contentTitle = String.format(c.getString(R.string.delay_notif_title), f.getAirlineID(), f.getNumber());
        String bigText = String.format(c.getString(R.string.takeoff_delay_notif_body), f.getDepartureBoardingTime());

        return createNotificationBuilder(c, R.drawable.ic_info_black_24px, contentTitle, bigText);
    }


    private static NotificationCompat.Builder createLandingDelayedBuilder(Context c, Flight f) {
        String contentTitle = String.format(c.getString(R.string.delay_notif_title), f.getAirlineID(), f.getNumber());
        String bigText = String.format(c.getString(R.string.landing_delay_notif_body), f.getDepartureBoardingTime(),f.getArrivalGate());

        return createNotificationBuilder(c, R.drawable.ic_info_black_24px, contentTitle, bigText);
    }


    private static NotificationCompat.Builder createDeviationBuilder(Context c, Flight f) {
        String contentTitle = String.format(c.getString(R.string.deviation_notif_title), f.getAirlineID(), f.getNumber());
        String bigText = String.format(c.getString(R.string.deviation_notif_body), f.getArrivalBoardingTime(), f.getArrivalAirport());

        return createNotificationBuilder(c, R.drawable.ic_info_black_24px, contentTitle, bigText);
    }


    private static NotificationCompat.Builder createCancelationBuilder(Context c, Flight f) {
        String contentTitle = String.format(c.getString(R.string.cancelation_notif_title), f.getAirlineID(), f.getNumber());
        String bigText = String.format(c.getString(R.string.cancelation_notif_body), f.getFullAirlineName());

        return createNotificationBuilder(c, R.drawable.ic_info_black_24px, contentTitle, bigText);
    }




    private static NotificationCompat.Builder createLandedBuilder(Context c, Flight f) {
        String contentTitle = String.format(c.getString(R.string.landed_notif_title), f.getAirlineID(), f.getNumber());
        String bigText = String.format(c.getString(R.string.landed_notif_body), f.getArrivalGate(), f.getBaggageClaim());
        return createNotificationBuilder(c, R.drawable.ic_flight_land_black_24px, contentTitle, bigText);
    }


    private static NotificationCompat.Builder createTakeoffBuilder(Context c, Flight f) {
        String contentTitle = String.format(c.getString(R.string.takeoff_notif_title), f.getAirlineID(), f.getNumber());
        String bigText = String.format(c.getString(R.string.takeoff_notif_body), f.getDepartureBoardingTime(), f.getArrivalBoardingTime());
        return createNotificationBuilder(c, R.drawable.ic_flight_land_black_24px, contentTitle, bigText);
    }



}