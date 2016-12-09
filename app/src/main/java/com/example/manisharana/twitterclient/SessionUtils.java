package com.example.manisharana.twitterclient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

public class SessionUtils {

    private static String MY_PREF = "MyPreferences";
    private final Context context;
    private final TwitterSession.Serializer serializer;
    private SharedPreferences myPreferences;
    private User user;


    public SessionUtils(Context context){
        this.context = context;
        myPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        serializer = new TwitterSession.Serializer();
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

    public void saveUserSessionDetails(TwitterSession session) {
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString("UserSession",serializer.serialize(session));
        editor.commit();
    }

    public TwitterSession getUserSessionDetails() {
        return serializer.deserialize(myPreferences.getString("UserSession", ""));

    }

    public void removeUserSessionDetails() {
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.remove("UserSession");
        editor.commit();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void saveUserDetails(User user){
        String userString = new Gson().toJson(user);
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString(getUserSessionString(),userString);
        editor.commit();
    }

    public User getUserDetails(){
        String currentUserDetails = myPreferences.getString(getUserSessionString(), "");
        return new Gson().fromJson(currentUserDetails,User.class);
    }

    public String getUserSessionString() {
        return myPreferences.getString("UserSession", "");

    }


}
