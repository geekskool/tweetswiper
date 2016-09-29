package com.example.manisharana.twitterclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.twitter.sdk.android.core.TwitterSession;

public class TweetUtils {

    public static void showErrorDialog(Context context, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
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

    public static void saveUserSessionDetails(Context context, TwitterSession session) {
        SharedPreferences myPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putLong("UserId",session.getUserId());
        editor.apply();
    }

    public static Long getUserSessionDetails(Context context) {
        SharedPreferences myPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return myPreferences.getLong("UserId", 0);
    }

    public static void removeUserSessionDetails(Context context) {
        SharedPreferences myPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.remove("UserId");
        editor.apply();
    }
}
