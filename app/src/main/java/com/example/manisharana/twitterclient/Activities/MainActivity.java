package com.example.manisharana.twitterclient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.manisharana.twitterclient.Fragments.TwitterLoginFragment;
import com.example.manisharana.twitterclient.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public static final String TWITTER_KEY = "";
    public static final String TWITTER_SECRET = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Class twitterLoginFragmentClass = TwitterLoginFragment.class;
        try {
            Fragment fragment = (Fragment) twitterLoginFragmentClass.newInstance();
            supportFragmentManager.beginTransaction().replace(R.id.framelayout_twitter_login, fragment).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragment = getSupportFragmentManager();
        if (fragment != null) {
            fragment.findFragmentById(R.id.framelayout_twitter_login).onActivityResult(requestCode, resultCode, data);
        } else Log.i("Twitter", "fragment is null");
    }
}
