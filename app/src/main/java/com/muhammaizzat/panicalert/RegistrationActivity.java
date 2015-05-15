package com.muhammaizzat.panicalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class RegistrationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(myIntent);
    }
}
