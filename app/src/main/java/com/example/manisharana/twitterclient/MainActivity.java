package com.example.manisharana.twitterclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private static final String TWITTER_KEY = "";
    private static final String TWITTER_SECRET = "";
    private TwitterLoginButton mloginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        mloginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        mloginButton.setCallback(new LoginHandler());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mloginButton.onActivityResult(requestCode, resultCode, data);
    }

    private class LoginHandler extends Callback<TwitterSession>{

        private TwitterSession session;

        @Override
        public void success(Result<TwitterSession> result) {
            session = Twitter.getSessionManager().getActiveSession();
            Twitter.getApiClient(session).getAccountService().verifyCredentials(true,false).enqueue(new Callback<User>() {
                @Override
                public void success(Result<User> result) {
                    String output = "Status: " +
                            "Your login was successful " +
                            result.data.name ;
                }

                @Override
                public void failure(TwitterException exception) {

                }
            });
            String output = "Status: " +
                    "Your login was successful " +
                    session.getAuthToken() +
                    "\nAuth Token Received: " +
                    result.data.getAuthToken().token;
        }

        @Override
        public void failure(TwitterException exception) {

        }
    }


}
