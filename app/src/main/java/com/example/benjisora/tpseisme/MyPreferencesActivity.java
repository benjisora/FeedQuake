package com.example.benjisora.tpseisme;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by Benjisora
 */
public class MyPreferencesActivity extends PreferenceActivity {

    /**
     * Crée le fragment des préférences
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        boolean darkTheme = getIntent().getExtras().getBoolean("darkTheme");

        if (darkTheme == false) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppThemeBlack);
        }

        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment())
                .commit();
    }

    @Override
    public void finish() {

        String modified = "true";
        Intent intent = new Intent(MyPreferencesActivity.this, MainActivity.class);
        intent.putExtra("modified", modified);
        setResult(RESULT_OK, intent);
        Log.d("MODIFIER", "OK");
        super.finish();
    }


    /**
     * Génère le menu de préférences défini dans res/xml/preferences.xml
     */
    public static class MyPreferenceFragment extends PreferenceFragment {

        private CheckBoxPreference checkBoxPreference;
        private ListPreference listPreference;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

}