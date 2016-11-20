package hci.voladeacapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.HashMap;

public class AddReviewActivity extends AppCompatActivity {

    final private static String RECOMMENDED_BOOLEAN = "voladeacapp.RECOMMENDED_BOOLEAN";

    private String aerolinea;
    private String comentario;
    private Integer numeroVuelo;
    private DiscreteSeekBar amabilidad;
    private DiscreteSeekBar confort;
    private DiscreteSeekBar comida;
    private DiscreteSeekBar preciocalidad;
    private DiscreteSeekBar puntualidad;
    private DiscreteSeekBar viajerosFrec;
    private Boolean recommended;
    private RatingBar stars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        /* Lo mas feo que vi en mi vida */

        amabilidad = (DiscreteSeekBar) findViewById(R.id.amabilidad_bar);
        confort = (DiscreteSeekBar) findViewById(R.id.confort_bar);
        comida = (DiscreteSeekBar) findViewById(R.id.comida_bar);
        preciocalidad = (DiscreteSeekBar) findViewById(R.id.precio_calidad_bar);
        puntualidad = (DiscreteSeekBar) findViewById(R.id.puntualidad_bar);
        viajerosFrec = (DiscreteSeekBar) findViewById(R.id.viajeros_frecuentes_bar);
        stars = (RatingBar) findViewById(R.id.ratingBar);


        HashMap<DiscreteSeekBar,TextView> map = new HashMap<>();
        map.put(amabilidad,(TextView)findViewById(R.id.amabilidad_data));
        map.put(confort,(TextView)findViewById(R.id.confort_data));
        map.put(comida,(TextView)findViewById(R.id.comida_data));
        map.put(preciocalidad,(TextView)findViewById(R.id.precio_calidad_data));
        map.put(puntualidad,(TextView)findViewById(R.id.puntualidad_data));
        map.put(viajerosFrec,(TextView)findViewById(R.id.viajeros_fecuentes_data));
        /* --- */

        /* Agrega los listeners de los bars */
        addCommonMethods(map,stars);
        final ImageButton happyBtn = (ImageButton) findViewById(R.id.happy_button);
        final ImageButton sadBtn = (ImageButton) findViewById(R.id.sad_button) ;
        Button submitButtom = (Button)findViewById(R.id.send_review_button);

        submitButtom.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //Aca se tienen que chequear cosas
                EditText aerolineaText = (EditText)findViewById(R.id.airline_input);
                EditText numeroVueloText = (EditText)findViewById(R.id.flight_number_input);

                try {
                    numeroVuelo = new Integer(numeroVueloText.getText().toString());
                }catch(NumberFormatException e){
                    numeroVuelo = null; //TODO: Hacer esto es un a s c o
                }

                aerolinea = aerolineaText.getText().toString();

                EditText comentarioText = (EditText) findViewById(R.id.comment_data);
                comentario = comentarioText.getText().toString();

                ReviewGson res = new ReviewGson(aerolinea, numeroVuelo, comentario, amabilidad.getProgress(), confort.getProgress(),comida.getProgress(),
                        preciocalidad.getProgress(),puntualidad.getProgress(),viajerosFrec.getProgress(), recommended);

                if (checkCompletedFields(res)) {
                    ApiService.startActionSendReview(view.getContext(), res);
                    Toast.makeText(getApplication(),"¡Reseña enviada!",Toast.LENGTH_SHORT).show();
                } else {
                    //Para debug... Faltan otras validaciones
                    Toast.makeText(getApplication(),"Falta aerolinea,numero o recomendado",Toast.LENGTH_SHORT).show();
                }

//                Intent intent = new Intent(getApplication(),PostResenaDummy.class);
 //               intent.putExtra("resena", res);

//                startActivity(intent);
                //TODO: Mandar a la api
                //Salir de la actividad

            }
        });

        happyBtn.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                happyBtn.setBackgroundColor(ContextCompat.getColor(AddReviewActivity.this, R.color.green));
                sadBtn.setBackgroundColor(ContextCompat.getColor(AddReviewActivity.this, R.color.grey));
                recommended = true;
            }
        });

        sadBtn.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                sadBtn.setBackgroundColor(ContextCompat.getColor(AddReviewActivity.this, R.color.red));
                happyBtn.setBackgroundColor(ContextCompat.getColor(AddReviewActivity.this, R.color.grey));
                recommended = false;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(RECOMMENDED_BOOLEAN)) {
            recommended = savedInstanceState.getBoolean(RECOMMENDED_BOOLEAN);
            if (recommended)
                happyBtn.setBackgroundColor(ContextCompat.getColor(AddReviewActivity.this, R.color.green));
            else
                sadBtn.setBackgroundColor(ContextCompat.getColor(AddReviewActivity.this, R.color.red));
        }
    }

    private boolean checkCompletedFields(ReviewGson res) {
        if(res.flight.airline.id == null || res.flight.number == null || res.yes_recommend == null)
            return false;
        return true;
    }

    private void addCommonMethods(final HashMap<DiscreteSeekBar,TextView> map, final RatingBar stars) {
        for(final DiscreteSeekBar ds : map.keySet() ){
            ds.setProgress(0);
            ds.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
                @Override
                public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                    float sum = amabilidad.getProgress() + confort.getProgress() +
                            comida.getProgress() + preciocalidad.getProgress() + puntualidad.getProgress() +
                            viajerosFrec.getProgress() ;
                    stars.setRating((sum*5)/60);
                    map.get(ds).setText(String.valueOf(value));
                }

                @Override
                public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recommended != null)
            outState.putBoolean(RECOMMENDED_BOOLEAN, recommended.booleanValue());
    }
}
