package com.example.benjisora.tpseisme;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Benjisora
 */
public class SplashActivity extends AppCompatActivity {

    /**
     * Crée le splashscreen défini dans res/xml/backgroundsplash.xml
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (connectedToInternet()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "You are not connected to any Network", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    protected boolean connectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ConnexionActive = cm.getActiveNetworkInfo();

        if (ConnexionActive != null) {
            return true;
        } else {
            return false;
        }
    }


}

