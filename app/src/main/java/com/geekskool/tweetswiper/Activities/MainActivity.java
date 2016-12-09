package com.geekskool.tweetswiper.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.geekskool.tweetswiper.Fragments.TwitterLoginFragment;
import com.geekskool.tweetswiper.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        }
    }
}
