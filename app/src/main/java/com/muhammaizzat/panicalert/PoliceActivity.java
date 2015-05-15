package com.muhammaizzat.panicalert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.muhammaizzat.utils.ReuseableClass;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PoliceActivity extends Activity {

    ListView myListView;
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    SimpleAdapter adapter;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police);

        myListView = (ListView) findViewById(R.id.myListView);
        ((TextView) findViewById(R.id.textViewTitle)).setTypeface(ReuseableClass.getFontStyle(PoliceActivity.this));
        if (ReuseableClass.haveNetworkConnection(this)) {
            dialog = new ProgressDialog(PoliceActivity.this);
            dialog.setMessage(PoliceActivity.this.getString(R.string.wait_a_min_dialog_message));
            dialog.show();

            new GetAlertLocationTask().execute();
        } else {
            Toast.makeText(this, R.string.check_network, Toast.LENGTH_SHORT).show();
        }

        adapter = new SimpleAdapter(this, data,
                R.layout.simplerow,
                new String[]{"name", "no_time", "lat", "lng"},
                new int[]{R.id.rowTextView, R.id.rowTextView2});

        myListView.setAdapter(adapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Log.d("TAG", "value: " + data.get(position).get("lat"));
                Log.d("TAG", "value: " + data.get(position).get("lng"));

                Intent myIntent = new Intent(PoliceActivity.this, ShowLocationActivity.class);
                myIntent.putExtra("name", data.get(position).get("name"));
                myIntent.putExtra("no_time", data.get(position).get("no_time"));
                myIntent.putExtra("lat", data.get(position).get("lat"));
                myIntent.putExtra("lng", data.get(position).get("lng"));
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

    private class GetAlertLocationTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... values) {
            String responseBody = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ReuseableClass.baseUrl + "panic_alert_api/get_user_info.php");
            try {
                HttpResponse response = httpclient.execute(httppost);

                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode == 200) {
                    responseBody = EntityUtils.toString(response.getEntity());
                }
            } catch (Exception t) {
                Log.e("TAG", "Error: " + t);
            }
            return responseBody;
        }

        protected void onPostExecute(String result) {
            Log.d("TAG", "value: " + result);
            if (result.equalsIgnoreCase("NO")) {
                Toast.makeText(PoliceActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(PoliceActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else if (result.equalsIgnoreCase("NO DATA")) {
                ((TextView) findViewById(R.id.TextViewNoData)).setTypeface(ReuseableClass.getFontStyle(PoliceActivity.this));
                ((TextView) findViewById(R.id.TextViewNoData)).setVisibility(View.VISIBLE);
            } else {
                try {
                    JSONArray jsonarray = new JSONArray(result);

                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        String name = jsonobject.getString("name");
                        String mobile_no = jsonobject.getString("mobile_no");
                        String date_time = jsonobject.getString("date_time");
                        String lat = jsonobject.getString("lat");
                        String lng = jsonobject.getString("lng");


                        Map<String, String> datum = new HashMap<String, String>(3);
                        datum.put("name", name);
                        datum.put("no_time", "Mobile: " + mobile_no + " Time: " + date_time);
                        datum.put("lat", lat);
                        datum.put("lng", lng);
                        data.add(datum);
                    }
                    myListView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PoliceActivity.this, R.string.other_error, Toast.LENGTH_SHORT).show();
                }
            }
            dialog.dismiss();
        }
    }
}
