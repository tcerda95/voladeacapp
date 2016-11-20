package hci.voladeacapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.provider.Settings;

public class OnBootReceiver extends BroadcastReceiver {
    public OnBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmManager alarmMgr;
            PendingIntent alarmIntent;

            alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(context, PullRequestReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
            alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 30 * 1000, alarmIntent);
        }

    }
}
