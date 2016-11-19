package hci.voladeacapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class PostResenaDummy extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Carli es la mejor");

        Resena res = (Resena)this.getIntent().getSerializableExtra("resena");


        new AlertDialog.Builder(this)
                .setTitle("Datos de la reseña")
                .setMessage("Numero de vuelo: " + res.getFlightNumber() + "\n" +
                        "Nombre Aerolinea: " + res.getFlightAirline() + "\n" +
                        "Amabilidad: " + res.getAmabilidad() + "\n" +
                        "Confort: " + res.getConfort() + "\n" +
                        "Comida: " + res.getComida() + "\n" +
                        "Precio-calidad: " + res.getPreciocalidad() + "\n" +
                        "Puntualidad: " + res.getPuntualidad() + "\n" +
                        "Viajero frecuente: " + res.getViajerosFrec() + "\n" +
                        "Total: " + res.getPuntuacion() + "\n" +
                        "Recomendado: " + res.getRecomendado() + "\n"
                        + "Comentario: " + res.getComentario()
                )
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
