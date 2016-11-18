package com.example.manisharana.twitterclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

public class TweetUtils {

    private static String MY_PREF = "MyPreferences";

    public static void showErrorDialog(Context context, String title) {
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

    public static void saveUserSessionDetails(Context context, String session) {
        SharedPreferences myPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("UserSession", session);
        editor.apply();
    }

    public static String getUserSessionDetails(Context context) {
        SharedPreferences myPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        return myPreferences.getString("UserSession", "");
    }

    public static void removeUserSessionDetails(Context context) {
        SharedPreferences myPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.remove("UserSession");
        editor.apply();
    }
}
