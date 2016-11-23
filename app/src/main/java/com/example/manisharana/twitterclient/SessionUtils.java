package com.example.manisharana.twitterclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

public class SessionUtils {

    private static String MY_PREF = "MyPreferences";
    private final Context context;

    public SessionUtils(Context context){
        this.context = context;
    }

    public void showErrorDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title)
                .setMessage("Please retry later")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void saveUserSessionDetails(String session) {
        SharedPreferences myPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("UserSession", session);
        editor.apply();
    }

    public String getUserSessionDetails() {
        SharedPreferences myPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        return myPreferences.getString("UserSession", "");
    }

    public void removeUserSessionDetails() {
        SharedPreferences myPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.remove("UserSession");
        editor.apply();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


}
