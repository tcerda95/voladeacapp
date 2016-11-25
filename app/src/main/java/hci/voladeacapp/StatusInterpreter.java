package hci.voladeacapp;

import android.content.Context;

public class StatusInterpreter {

    /*Retorna la imagen correspondiente al estado recibido como String */
    public static int getStateImage(String state) {
        switch(state){
            case "L":
                return R.drawable.ic_landedbadge;

            case "S":
                return R.drawable.ic_okbadge;

            case "A":
                return R.drawable.ic_takeoffbadge;

            case "D":
                return R.drawable.ic_deviationbadge;

            case "C":
                return R.drawable.ic_cancelbadge;

        }
        return -1;
    }

    /*Retorna el nombre de el estado según la letra recibida */
    public static String getStatusName(Context c,String state){
        switch(state){
            case "L":
                return c.getString(R.string.landed);

            case "S":
                return c.getString(R.string.programmed);

            case "A":
                return c.getString(R.string.active);

            case "D":
                return c.getString(R.string.diverted);

            case "C":
                return c.getString(R.string.cancelled);

        }
        return "";
    }

}
