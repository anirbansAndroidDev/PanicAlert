package com.muhammaizzat.panicalert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity {

    EditText userName;
    EditText password;
    TextView TextViewRegister;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        TextViewRegister = (TextView) findViewById(R.id.TextViewRegister);

        TextViewRegister.setTypeface(ReuseableClass.getFontStyle(this));
    }

    public void openingRegistration(View v) {
        //Don't have acc Clicked
        Intent myIntent = new Intent(this, RegistrationActivity.class);
        finish();
        startActivity(myIntent);
    }

    public void login(View v) {
        //Login  Button Click
        String usernameValue = userName.getText().toString();
        String passwordValue = password.getText().toString();
        if ((usernameValue.equalsIgnoreCase("police1") && passwordValue.equalsIgnoreCase("police123")) ||
                (usernameValue.equalsIgnoreCase("police2") && passwordValue.equalsIgnoreCase("police123")) ||
                (usernameValue.equalsIgnoreCase("police3") && passwordValue.equalsIgnoreCase("police123"))) {
            Log.d("TAG", "Police");

            Intent myIntent = new Intent(this, PoliceActivity.class);
            finish();
            startActivity(myIntent);
        } else {
            Log.d("TAG", "Try to check for normal user!!");

            if (ReuseableClass.haveNetworkConnection(LoginActivity.this)) {
                //Checking internet connection
                if (usernameValue.trim().equalsIgnoreCase("") || passwordValue.trim().equalsIgnoreCase("")) {
                    //Login page validation
                    Toast.makeText(LoginActivity.this, R.string.all_field_mandatory, Toast.LENGTH_SHORT).show();
                } else {
                    //Calling Webservice to check the username and password
                    dialog = new ProgressDialog(LoginActivity.this);
                    dialog.setMessage(LoginActivity.this.getString(R.string.wait_a_min_dialog_message));
                    dialog.show();

                    new LoginTask().execute(usernameValue, passwordValue);
                }
            } else {
                Toast.makeText(this, R.string.check_network, Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Async Task for Login
    private class LoginTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... values) {
            String responseBody = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ReuseableClass.baseUrl + "panic_alert_api/login_user.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", values[0]));
                nameValuePairs.add(new BasicNameValuePair("password", values[1]));

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
            if (result.contains("user_info")) {

                String[] name_mobile = result.replace("user_info", "").split("@#@");

                ReuseableClass.saveInPreference("name", name_mobile[0], LoginActivity.this);
                ReuseableClass.saveInPreference("mobile_no", name_mobile[1], LoginActivity.this);

                Log.d("TAG", "name: " + ReuseableClass.getFromPreference("name", LoginActivity.this) + " mobile no: " + ReuseableClass.getFromPreference("mobile_no", LoginActivity.this));

                //Opening Normal User Activity
                Intent i = new Intent(LoginActivity.this, NormalUserActivity.class);
                startActivity(i);
                finish();
            } else if (result.equalsIgnoreCase("NO")) {
                Toast.makeText(LoginActivity.this, R.string.login_check_credentials_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, R.string.other_error, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }
}
