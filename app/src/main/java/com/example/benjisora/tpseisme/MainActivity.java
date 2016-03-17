package com.example.benjisora.tpseisme;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * Créé par Benjisora
 */
public class MainActivity extends AppCompatActivity {

    private ListView myList;
    // URL d'obtension du flux
    private static String urlToParse;

    // Noms des objets JSON
    protected static final String TAG_FEATURES = "features";
        protected static final String TAG_TYPE = "type";
        protected static final String TAG_PROPERTIES = "properties";
            protected static final String TAG_MAG = "mag";
            protected static final String TAG_PLACE = "place";
            protected static final String TAG_TIME = "time";
            protected static final String TAG_UPDATED = "updated";
            protected static final String TAG_TZ = "tz";
            protected static final String TAG_URL = "url";
            protected static final String TAG_DETAIL = "detail";
            protected static final String TAG_FELT = "felt";
            protected static final String TAG_CDI = "cdi";
            protected static final String TAG_MMI = "mmi";
            protected static final String TAG_ALERT = "alert";
            protected static final String TAG_STATUS = "status";
            protected static final String TAG_TSUNAMI = "tsunami";
            protected static final String TAG_SIG = "sig";
            protected static final String TAG_NET = "net";
            protected static final String TAG_CODE = "code";
            protected static final String TAG_IDS = "ids";
            protected static final String TAG_SOURCES = "sources";
            protected static final String TAG_TYPES = "types";
            protected static final String TAG_NST = "nst";
            protected static final String TAG_DMIN = "dmin";
            protected static final String TAG_RMS = "rms";
            protected static final String TAG_GAP = "gap";
            protected static final String TAG_MAGTYPE = "magType";
            protected static final String TAG_TYPE2 = "type";
            protected static final String TAG_TITLE = "title";
        protected static final String TAG_GEOMETRY = "geometry";
            protected static final String TAG_COORDINATES = "coordinates";
                protected static final String TAG_LATITUDE = "latitude";
                protected static final String TAG_LONGITUDE = "longitude";
                protected static final String TAG_DEPTH = "depth";
        protected static final String TAG_ID = "id";
    String lol = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final String valueTitle = update();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView feedTitle = (TextView) findViewById(R.id.textView);
        feedTitle.setText(valueTitle);

        myList = (ListView) findViewById(android.R.id.list);
        AsyncTask students = new GetEarthquakes().execute();

        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id) {
                HashMap<String, String> map = (HashMap<String, String>) myList.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("map", map);

                startActivity(intent);

                /*
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle(map.get(TAG_PLACE));
                adb.setMessage("Magnitude: " + map.get(TAG_MAG) + "\nURL: " + map.get(TAG_URL) + "\nTsunami: " + map.get(TAG_TSUNAMI));
                adb.setPositiveButton("OK", null);
                adb.show();
                */
                return true;
            }
        });

    }

    /**
     * Crée le menu défini dans res/menu/menu_main.xml
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Répond à l'appui d'un bouton sur le menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean darkTheme = SP.getBoolean("darkTheme", false);

                Intent intent = new Intent(this, MyPreferencesActivity.class);
                intent.putExtra("darkTheme", darkTheme);
                startActivityForResult(intent, 1);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1) {
            if (Objects.equals(data.getStringExtra("modified"), "true")) {
                this.recreate();
            } else {
                Log.d("RETOUR", "cassé...");
            }
        }
    }

    protected String update() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean darkTheme = SP.getBoolean("darkTheme", false);
        String defaultFeedUpdate = SP.getString("defaultFeedUpdate", "2");
        boolean strongEarthquake = SP.getBoolean("strongEarthquake", false);

        String responseTitle = "",
                strongness = "",
                feedUpdate = "";

        if (!darkTheme)
            setTheme(R.style.AppTheme);
        else
            setTheme(R.style.AppThemeBlack);

        if (strongEarthquake)
            strongness = "4.5";
        else
            strongness = "all";

        switch (defaultFeedUpdate) {
            case "1":
                feedUpdate = "hour";
                break;
            case "2":
                feedUpdate = "day";
                break;
            case "3":
                feedUpdate = "week";
                break;
            case "4":
                feedUpdate = "month";
                break;
        }

        responseTitle = "This " + feedUpdate + "'s earthquakes";
        urlToParse = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/" + strongness + "_" + feedUpdate + ".geojson";
        return responseTitle;
    }


    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
                ArrayList<HashMap<String, String>> earthquakeList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);

                // Récupère l'array Features
                JSONArray earthquakes = jsonObj.getJSONArray(TAG_FEATURES);

                for (int i = 0; i < earthquakes.length(); i++) {

                    JSONObject c = earthquakes.getJSONObject(i);
                    String id = c.getString(TAG_ID);

                    JSONObject properties = c.getJSONObject(TAG_PROPERTIES);

                    String mag = properties.getString(TAG_MAG);
                    String place = properties.getString(TAG_PLACE);
                    String image = properties.getString(TAG_URL);
                    String time = properties.getString(TAG_TIME);
                    String type = properties.getString(TAG_TYPE);
                    String tsunami = properties.getString(TAG_TSUNAMI);

                    long millisecond = Long.parseLong(time);
                    String dateString = DateFormat.format("dd/MM/yyyy hh:mm", new Date(millisecond)).toString();

                    JSONObject geometry = c.getJSONObject(TAG_GEOMETRY);
                    JSONArray coordinates = geometry.getJSONArray(TAG_COORDINATES);
                    double lng = coordinates.getDouble(0);
                    double lat = coordinates.getDouble(1);
                    double dpt = coordinates.getDouble(2);

                    String latitude = String.valueOf(lat);
                    String longitude = String.valueOf(lng);
                    String depth = String.valueOf(dpt);


                    // Hashmap temporaire représentant un séisme
                    HashMap<String, String> earthquake = new HashMap<String, String>();

                    // Ajouter tous les éléments relatifs au séisme
                    earthquake.put(TAG_ID, id);
                    earthquake.put(TAG_PLACE, place);
                    earthquake.put(TAG_MAG, mag);
                    earthquake.put(TAG_TIME, dateString);
                    earthquake.put(TAG_URL, image);
                    earthquake.put(TAG_TYPE, type);
                    earthquake.put(TAG_TSUNAMI, tsunami);
                    earthquake.put(TAG_LATITUDE, latitude);
                    earthquake.put(TAG_LONGITUDE, longitude);
                    earthquake.put(TAG_DEPTH, depth);

                    // Ajouter le séisme à la liste
                    earthquakeList.add(earthquake);
                }
                return earthquakeList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Toast.makeText(getApplicationContext(), "No data received from HTTP request", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    private class GetEarthquakes extends AsyncTask<Void, Void, Void> {

        // Hashmap pour la ListView
        ArrayList<HashMap<String, String>> earthquakeList;
        ProgressDialog proDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress loading dialog
            proDialog = new ProgressDialog(MainActivity.this);
            proDialog.setMessage("Please wait...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Crée une instance de connexion et récupère une String
            WebJSONRequest webreq = new WebJSONRequest();
            String jsonStr = webreq.makeWebServiceCall(urlToParse, WebJSONRequest.GETRequest);

            earthquakeList = ParseJSON(jsonStr);
            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            // Faire disparaitre la ProDialog
            if (proDialog.isShowing())
                proDialog.dismiss();

            /**
             * Actualiser les données du JSON dans la listView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this,
                    earthquakeList,
                    R.layout.listview_row,
                    new String[]{TAG_PLACE, TAG_TIME, TAG_MAG, TAG_TYPE},
                    new int[]{R.id.name, R.id.date, R.id.intensity, R.id.type}
            );

            myList.setAdapter(adapter);
        }
    }
}
