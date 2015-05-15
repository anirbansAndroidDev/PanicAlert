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


public class RegistrationActivity extends Activity {

    EditText editTextName;
    EditText editTextEmailAddress;
    EditText editTextMobileNo;
    EditText editTextAddress;
    EditText editTextICNo;
    EditText editTextUsername;
    EditText editTextPassword;
    EditText editTextConfirmPassword;

    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ((TextView) findViewById(R.id.textViewTitle)).setTypeface(ReuseableClass.getFontStyle(this));

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmailAddress = (EditText) findViewById(R.id.editTextEmailAddress);
        editTextMobileNo = (EditText) findViewById(R.id.editTextMobileNo);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextICNo = (EditText) findViewById(R.id.editTextICNo);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(myIntent);
    }

    public void registeringUserData(View v) {
        String value_name = editTextName.getText().toString();
        String value_email_id = editTextEmailAddress.getText().toString();
        String value_mobile_no = editTextMobileNo.getText().toString();
        String value_address = editTextAddress.getText().toString();
        String value_id_card_no = editTextICNo.getText().toString();
        String value_username = editTextUsername.getText().toString();
        String value_password = editTextPassword.getText().toString();
        String value_confirm_password = editTextConfirmPassword.getText().toString();

        if (ReuseableClass.haveNetworkConnection(RegistrationActivity.this)) {
            if (value_name.trim().equalsIgnoreCase("") || value_email_id.trim().equalsIgnoreCase("") ||
                    value_mobile_no.trim().equalsIgnoreCase("") || value_username.trim().equalsIgnoreCase("") ||
                    value_password.trim().equalsIgnoreCase("") || value_confirm_password.trim().equalsIgnoreCase("") ||
                    value_address.trim().equalsIgnoreCase("") || value_id_card_no.trim().equalsIgnoreCase("")) {
                Toast.makeText(this, R.string.all_field_mandatory, Toast.LENGTH_SHORT).show();
            } else {
                if (value_password.equalsIgnoreCase(value_confirm_password)) {
                    dialog = new ProgressDialog(RegistrationActivity.this);
                    dialog.setMessage(RegistrationActivity.this.getString(R.string.wait_a_min_dialog_message));
                    dialog.show();

                    new UserRegistrationTask().execute(value_name, value_email_id, value_mobile_no, value_username, value_password, value_address, value_id_card_no);
                } else {
                    Toast.makeText(this, R.string.password_missmatch_error, Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, R.string.check_network, Toast.LENGTH_SHORT).show();
        }
    }

    private class UserRegistrationTask extends AsyncTask<String, String, String> {
        protected String doInBackground(String... values) {
            String responseBody = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(ReuseableClass.baseUrl + "panic_alert_api/register_user.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
                nameValuePairs.add(new BasicNameValuePair("name", values[0]));
                nameValuePairs.add(new BasicNameValuePair("email_id", values[1]));
                nameValuePairs.add(new BasicNameValuePair("mobile_no", values[2]));
                nameValuePairs.add(new BasicNameValuePair("username", values[3]));
                nameValuePairs.add(new BasicNameValuePair("password", values[4]));
                nameValuePairs.add(new BasicNameValuePair("address", values[5]));
                nameValuePairs.add(new BasicNameValuePair("id_card_no", values[6]));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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
            if (result.equalsIgnoreCase("YES")) {
                Toast.makeText(RegistrationActivity.this, R.string.registration_successful_message, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            } else if (result.equalsIgnoreCase("NO")) {
                Toast.makeText(RegistrationActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
            } else if (result.equalsIgnoreCase("EXISTS")) {
                Toast.makeText(RegistrationActivity.this, R.string.user_exists_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RegistrationActivity.this, R.string.other_error, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    }
}
