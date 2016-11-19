package hci.voladeacapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

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

    //TODO: el stack, ya probe de todo(incluyendo poner todos estos metodos directo en el fragment y cambiar flags), pero se cierra la aplicacion (!!)
    private static void createNotification(Context c, Flight f, NotificationCompat.Builder builder) {
        Intent intent = new Intent(c, FlightDetails.class);
        intent.putExtra("Flight", f);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
        stackBuilder.addParentStack(FlightDetails.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

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

    private static NotificationCompat.Builder createOnTimeBuilder(Context c, Flight f) {
        String contentTitle = c.getString(R.string.the_flight_notif) + " " + f.getNumber() + " " + c.getString(R.string.onTimeNotif);
        String bigText = c.getString(R.string.boardingTimeNotif) + " Alguno" + "\n" +
                c.getString(R.string.boardingGateNotif) + " Alguno";

        return createNotificationBuilder(c, R.drawable.ic_flight_takeoff_black_24px, contentTitle, bigText);
    }

    private static NotificationCompat.Builder createDelayedBuilder(Context c, Flight f) {
        String contentTitle = c.getString(R.string.the_flight_notif) + " " + f.getNumber() + " " + c.getString(R.string.delayedNotif);
        String bigText = c.getString(R.string.newTakeOffTimeNotif) + new Random().nextInt() + "\n" +
                c.getString(R.string.newLandTimeNotif) + " Alguno";

        return createNotificationBuilder(c, R.drawable.ic_info_black_24px, contentTitle, bigText);
    }

    private static NotificationCompat.Builder createLandedBuilder(Context c, Flight f) {
        String contentTitle = c.getString(R.string.the_flight_notif) + " " + f.getNumber() + " " + c.getString(R.string.landedNotif);
        String bigText = c.getString(R.string.baggageClaimGateNotif) + " Alguno" + "\n" +
                c.getString(R.string.exitGateNotif) + " Alguno";

        return createNotificationBuilder(c, R.drawable.ic_flight_land_black_24px, contentTitle, bigText);
    }
}