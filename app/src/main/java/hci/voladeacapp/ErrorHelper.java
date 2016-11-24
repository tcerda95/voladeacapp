package hci.voladeacapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chelo on 11/24/16.
 */

public class ErrorHelper {

    public static final String NO_CONNECTION_ERROR = "hci.voladeacapp.error.NO_CONNECTION_ERROR";

    public static void connectionErrorShow(Context context){
        new AlertDialog.Builder(context)
                .setTitle("Error de conexion")
                .setMessage("Qué triste")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


    public static void alert(Context context, String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public static void sendNoConnection(Context context) {
        context.sendBroadcast(new Intent(NO_CONNECTION_ERROR));
    }
}
