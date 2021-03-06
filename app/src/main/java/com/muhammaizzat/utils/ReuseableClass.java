package com.muhammaizzat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

public class ReuseableClass {

    public static String baseUrl = "http://panicalert.byethost13.com/";

    public static Typeface getFontStyle(Context c) {
        return Typeface.createFromAsset(c.getAssets(), "fonts/gothic.ttf");
    }

    //===================================================================================================================================
    //check Mobile data and wifi
    //===================================================================================================================================
    public static boolean haveNetworkConnection(Activity myActivity) {
        ConnectivityManager cm = (ConnectivityManager) myActivity.getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    //====================================================================================================================================
    //checking Mobile data and wifi END
    //====================================================================================================================================


    //===================================================================================================================================
    //Preference variable
    //===================================================================================================================================

    //--------------------------------------------
    // method to save variable in preference
    //--------------------------------------------
    public static void saveInPreference(String name, String content, Context myActivity) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(myActivity);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, content);
        editor.commit();
    }

    //--------------------------------------------
    // getting content from preferences
    //--------------------------------------------
    public static String getFromPreference(String variable_name, Context myActivity) {
        String preference_return;
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(myActivity);
        preference_return = preferences.getString(variable_name, "");

        return preference_return;
    }


    //===================================================================================================================================
    //Preference variable
    //===================================================================================================================================
}
