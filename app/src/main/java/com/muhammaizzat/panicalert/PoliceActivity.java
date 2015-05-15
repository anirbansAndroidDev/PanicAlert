package com.muhammaizzat.panicalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PoliceActivity extends Activity {

    ListView myListView;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police);

        myListView = (ListView) findViewById(R.id.myListView);

        for (int i = 1; i < 11; i++) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("First Line", "First line of text. Line No: " + i);
            datum.put("Second Line", "Second line of text. Line No: " + i);
            data.add(datum);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, data,
                R.layout.simplerow,
                new String[]{"First Line", "Second Line"},
                new int[]{R.id.rowTextView, R.id.rowTextView2});

        myListView.setAdapter(adapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Log.d("TAG", "value: " + data.get(position).get("First Line"));
                Log.d("TAG", "value: " + data.get(position).get("Second Line"));

                Intent myIntent = new Intent(PoliceActivity.this, ShowLocationActivity.class);
                finish();
                startActivity(myIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(PoliceActivity.this, LoginActivity.class);
        finish();
        startActivity(myIntent);
    }
}
