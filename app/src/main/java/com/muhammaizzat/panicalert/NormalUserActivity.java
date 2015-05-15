package com.muhammaizzat.panicalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class NormalUserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user);
    }

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(myIntent);
    }
}
