package hci.voladeacapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;


public class ApplicationVoladeacapp extends Application {
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadLanguage();
    }

    private void loadLanguage() {
        SharedPreferences shp = getSharedPreferences(
                "hci.voladeacapp.PREFERENCES", Context.MODE_PRIVATE);
        String language = shp.getString("USER_LANGUAGE", "es");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics()); //TODO: deprecated
    }
}
