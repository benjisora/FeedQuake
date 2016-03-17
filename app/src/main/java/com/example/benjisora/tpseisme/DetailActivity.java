package com.example.benjisora.tpseisme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by Benjisora
 */
public class DetailActivity extends AppCompatActivity {

    HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        update();

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        setContentView(R.layout.detailactivity);
        addMapFragment();

        TextView feedTitle = (TextView) findViewById(R.id.textView2);
        feedTitle.setText(map.get(MainActivity.TAG_PLACE));

        TextView feedTime = (TextView) findViewById(R.id.textView3);
        feedTime.setText(map.get(MainActivity.TAG_TIME));

        TextView feedType = (TextView) findViewById(R.id.textView4);
        feedType.setText(map.get(MainActivity.TAG_TYPE));
        /*
        myList = (ListView) findViewById(android.R.id.list);
        AsyncTask students = new GetStudents().execute();
        */
    }

    private void addMapFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MapFragment fragment = new MapFragment(map);
        transaction.add(R.id.mapView, fragment);
        transaction.commit();
    }

    protected void update() {

        map = (HashMap<String, String>) getIntent().getSerializableExtra("map");

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean darkTheme = SP.getBoolean("darkTheme", false);

        if (!darkTheme)
            setTheme(R.style.AppTheme);
        else
            setTheme(R.style.AppThemeBlack);

    }
}