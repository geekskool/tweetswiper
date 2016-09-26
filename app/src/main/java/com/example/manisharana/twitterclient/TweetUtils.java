package com.example.manisharana.twitterclient;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

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

}
