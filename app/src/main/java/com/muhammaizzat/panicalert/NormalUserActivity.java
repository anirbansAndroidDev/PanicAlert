package com.muhammaizzat.panicalert;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.muhammaizzat.utils.ReuseableClass;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NormalUserActivity extends Activity {

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    ImageView imageViewOnOffButton;
    TextView TextViewMessage;
    int PowerButtonPressedCount = 0;
    private long mBackPressed;
    private BroadcastReceiver mPowerKeyReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user);

        imageViewOnOffButton = (ImageView) findViewById(R.id.imageViewOnOffButton);
        TextViewMessage = (TextView) findViewById(R.id.TextViewMessage);
        TextViewMessage.setTypeface(ReuseableClass.getFontStyle(NormalUserActivity.this));

        if (ReuseableClass.getFromPreference("onOffFlag", NormalUserActivity.this).equalsIgnoreCase("off") ||
                ReuseableClass.getFromPreference("onOffFlag", NormalUserActivity.this).equalsIgnoreCase("")) {
            imageViewOnOffButton.setImageResource(R.drawable.off_btn);
            TextViewMessage.setText(getString(R.string.panic_alert_off));
        } else if (ReuseableClass.getFromPreference("onOffFlag", NormalUserActivity.this).equalsIgnoreCase("on")) {
            imageViewOnOffButton.setImageResource(R.drawable.on_btn);
            TextViewMessage.setText(getString(R.string.panic_alert_on));
        }
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(myIntent);
    }

    public void sendingInfoToPolice() {
        Double lat = 0.0;
        Double lng = 0.0;
        if (!ReuseableClass.getFromPreference("nlat", NormalUserActivity.this).equalsIgnoreCase("")) {

            if (ReuseableClass.getFromPreference("glat", NormalUserActivity.this).equalsIgnoreCase("")) {
                lat = Double.parseDouble(ReuseableClass.getFromPreference("nlat", NormalUserActivity.this));
                lng = Double.parseDouble(ReuseableClass.getFromPreference("nlng", NormalUserActivity.this));
            } else {
                lat = Double.parseDouble(ReuseableClass.getFromPreference("glat", NormalUserActivity.this));
                lng = Double.parseDouble(ReuseableClass.getFromPreference("glng", NormalUserActivity.this));
            }

            if (lat != 0.0 && lng != 0.0) {
                if (ReuseableClass.haveNetworkConnection(NormalUserActivity.this)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - dd.MM.yyyy");
                    String currentDatedTime = sdf.format(new Date());

                    new SendLocationAsyncTask().execute(ReuseableClass.getFromPreference("name", NormalUserActivity.this),
                            ReuseableClass.getFromPreference("mobile_no", NormalUserActivity.this), lat.toString(), lng.toString(), currentDatedTime);
                }
            }
        } else {
            Log.d("TAG", "gLat, GLng is blank !!");
        }
    }

    public void OnOffClicked(View v) {
        if (ReuseableClass.getFromPreference("onOffFlag", NormalUserActivity.this).equalsIgnoreCase("off") ||
                ReuseableClass.getFromPreference("onOffFlag", NormalUserActivity.this).equalsIgnoreCase("")) {
            imageViewOnOffButton.setImageResource(R.drawable.on_btn);
            TextViewMessage.setText(R.string.panic_alert_on);
            ReuseableClass.saveInPreference("onOffFlag", "on", NormalUserActivity.this);
            registerBroadcastReceiver();
        } else if (ReuseableClass.getFromPreference("onOffFlag", NormalUserActivity.this).equalsIgnoreCase("on")) {
            imageViewOnOffButton.setImageResource(R.drawable.off_btn);
            TextViewMessage.setText(R.string.panic_alert_off);
            ReuseableClass.saveInPreference("onOffFlag", "off", NormalUserActivity.this);
            unregisterReceiver();
        }
    }

    private void registerBroadcastReceiver() {
        final IntentFilter theFilter = new IntentFilter();
        /** Registering System Defined Broadcast For listening power button click*/
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mPowerKeyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String strAction = intent.getAction();

                if (strAction.equals(Intent.ACTION_SCREEN_OFF) || strAction.equals(Intent.ACTION_SCREEN_ON)) {

                    if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                        if (ReuseableClass.getFromPreference("onOffFlag", NormalUserActivity.this).equalsIgnoreCase("on")) {
                            Log.d("TAG", "Power Button pressed 2 Times within 2 sec!!");
                            //Starting Service for getting current location
                            startService(new Intent(NormalUserActivity.this, CurrentLocationService.class));
                            PowerButtonPressedCount = 0;

                            //Making 3 Sec delay so that Location service can get the location
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendingInfoToPolice();
                                }
                            }, 3 * 1000);
                        } else {
                            Log.d("TAG", "Panic Alert is OFF");
                        }

                        return;
                    } else {

                        Log.d("TAG", "Panic Alert is pressed");
                    }
                    mBackPressed = System.currentTimeMillis();
                }
            }
        };

        getApplicationContext().registerReceiver(mPowerKeyReceiver, theFilter);
    }

    private void unregisterReceiver() {

        /** Unregister System Defined Broadcast For listening power button click*/
        int apiLevel = Build.VERSION.SDK_INT;

        if (apiLevel >= 7) {
            try {
                getApplicationContext().unregisterReceiver(mPowerKeyReceiver);
            } catch (IllegalArgumentException e) {
                mPowerKeyReceiver = null;
            }
        } else {
            getApplicationContext().unregisterReceiver(mPowerKeyReceiver);
            mPowerKeyReceiver = null;
        }
    }

    //Async Task for sendingLocation
    private class SendLocationAsyncTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... values) {
            String responseBody = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ReuseableClass.baseUrl + "panic_alert_api/send_user_info.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("name", values[0]));
                nameValuePairs.add(new BasicNameValuePair("mobile_no", values[1]));
                nameValuePairs.add(new BasicNameValuePair("lat", values[2]));
                nameValuePairs.add(new BasicNameValuePair("lng", values[3]));
                nameValuePairs.add(new BasicNameValuePair("dateTime", values[4]));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);

                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode == 200) {
                    responseBody = EntityUtils.toString(response.getEntity());
                    Log.d("TAG", "value: " + responseBody);
                }
            } catch (Exception t) {
                Log.e("TAG", "Error: " + t);
            }
            return responseBody;
        }

        protected void onPostExecute(String result) {
            Log.d("TAG", "value: " + result);
            if (result.contains("YES")) {
                Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {0, 300, 600, 900, 300, 600, 900, 200, 100};
                vb.vibrate(pattern, -1);
            } else if (result.equalsIgnoreCase("NO")) {
                Toast.makeText(NormalUserActivity.this, R.string.login_check_credentials_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(NormalUserActivity.this, R.string.other_error, Toast.LENGTH_SHORT).show();
            }
            stopService(new Intent(NormalUserActivity.this, CurrentLocationService.class));
        }
    }
}
