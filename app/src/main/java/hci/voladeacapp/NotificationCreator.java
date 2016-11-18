package hci.voladeacapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

//TODO: Completar con los datos del Flight real

/**
 * Se encarga de generar las notificaciones según el estado de un vuelo
 */
public class NotificationCreator extends Activity {

    public static void createNotification(Context c, Flight f, NotificationType nt) {
        switch (nt) {
            case ON_TIME:
                createNotification(c, f, createOnTimeBuilder(c, f));
                break;
            case DELAYED:
                createNotification(c, f,createDelayedBuilder(c,f));
                break;
            case LANDED:
                createNotification(c, f, createLandedBuilder(c,f));
                break;
        }

    }

    private static void createNotification(Context c, Flight f, NotificationCompat.Builder builder) {
        Intent intent = new Intent(c, FlightDetails.class);
        intent.putExtra("Flight", f);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
        stackBuilder.addParentStack(FlightDetails.class);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pIntent);
        NotificationManager notificationManager = (NotificationManager)
                c.getSystemService(NOTIFICATION_SERVICE);
        //El id está construido por el numero de vuelo
        //TODO: ver de incluir de alguna forma la aerolinea para que no se repitan
        notificationManager.notify(Integer.parseInt(f.getNumber()), builder.build());
        //TODO: ver que pasa si se superponen ¿deberia ser asi?
    }


    private static NotificationCompat.Builder createOnTimeBuilder(Context c, Flight f) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.drawable.ic_flight_takeoff_black_24px)
                        .setContentTitle(c.getString(R.string.the_flight_notif) + " " + f.getNumber() + " " + c.getString(R.string.onTimeNotif))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(c.getString(R.string.boardingTimeNotif) + " Alguno" + "\n" +
                                        c.getString(R.string.boardingGateNotif) + " Alguno"));
        return mBuilder;
    }

    private static NotificationCompat.Builder createDelayedBuilder(Context c, Flight f) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.drawable.ic_info_black_24px)
                        .setContentTitle(c.getString(R.string.the_flight_notif) + " " + f.getNumber() + " " + c.getString(R.string.delayedNotif))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(c.getString(R.string.newTakeOffTimeNotif) + " Alguno" + "\n" +
                                        c.getString(R.string.newLandTimeNotif) + " Alguno"));
        return mBuilder;
    }

    private static NotificationCompat.Builder createLandedBuilder(Context c, Flight f) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(R.drawable.ic_flight_land_black_24px)
                        .setContentTitle(c.getString(R.string.the_flight_notif) + " " + f.getNumber() + " " + c.getString(R.string.landedNotif))
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(c.getString(R.string.baggageClaimGateNotif) + " Alguno" + "\n" +
                                        c.getString(R.string.exitGateNotif) + " Alguno"));
        return mBuilder;
    }
}