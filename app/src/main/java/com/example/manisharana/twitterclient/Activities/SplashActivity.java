package com.example.manisharana.twitterclient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.BuildConfig;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CrashlyticsCore core = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        TwitterAuthConfig authConfig = new TwitterAuthConfig(com.example.manisharana.twitterclient.BuildConfig.TWITTER_KEY, com.example.manisharana.twitterclient.BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
