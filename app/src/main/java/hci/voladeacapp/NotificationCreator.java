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
        switch(nt) {
            case ON_TIME:
                createNotification(c,f, R.drawable.ic_flight_takeoff_black_24px,c.getString(R.string.onTimeNotif), c.getString(R.string.boarding_time_label), c.getString(R.string.boardingGateNotif));
                break;
            case DELAYED:
                createNotification(c,f,R.drawable.ic_info_black_24px,c.getString(R.string.delayedNotif), c.getString(R.string.newTakeOffTimeNotif), c.getString(R.string.newLandTimeNotif));
                break;
            case LANDED:
                createNotification(c,f, R.drawable.ic_flight_land_black_24px,c.getString(R.string.landedNotif), c.getString(R.string.exitGateNotif), c.getString(R.string.baggageClaimGateNotif));
                break;
        }

    }

    private static void createNotification(Context c, Flight f, int img, String string, String string1, String string2) {
        Intent intent = new Intent(c, FlightDetails.class);
        intent.putExtra("Flight",f);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //TODO: arreglar esto del stack builder
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(c);
        stackBuilder.addParentStack(FlightDetails.class);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent pIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(c)
                        .setSmallIcon(img)
                        .setContentTitle(c.getString(R.string.the_flight_notif) + " " + f.getNumber() + " "+ string)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(string1 + " Alguno" + "\n" +
                                        string2 + " Alguno"));

        //TODO:DIBUJO TIPO BITMAP PARA LA FOTITO

        mBuilder.setContentIntent(pIntent);
        NotificationManager notificationManager = (NotificationManager)
                c.getSystemService(NOTIFICATION_SERVICE);
        //El id está construido por el numero de vuelo
        //TODO: ver de incluir de alguna forma la aerolinea para que no se repitan
        notificationManager.notify(Integer.parseInt(f.getNumber()),mBuilder.build());
        //TODO: ver que pasa si se superponen ¿deberia ser asi?
    }


    }
