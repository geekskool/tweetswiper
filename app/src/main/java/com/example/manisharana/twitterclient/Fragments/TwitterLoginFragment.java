package com.example.manisharana.twitterclient.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.manisharana.twitterclient.Activities.TweetListActivity;
import com.example.manisharana.twitterclient.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

public class TwitterLoginFragment extends Fragment  implements View.OnClickListener {

    private Button mloginButton;
    private TwitterAuthClient mTwitterAuthClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTwitterAuthClient = new TwitterAuthClient();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.twitter_login_view,container,false);
        mloginButton = (Button) rootView.findViewById(R.id.login_button);

        mloginButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        mTwitterAuthClient.authorize(getActivity(), new Callback<TwitterSession>() {
            private TwitterSession session;

            @Override
            public void success(Result<TwitterSession> result) {
                session = Twitter.getSessionManager().getActiveSession();
                String output = "Status: " +
                        "Your login was successful " +
                        session.getAuthToken() +
                        "\nAuth Token Received: " +
                        result.data.getAuthToken().token;
                Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false, new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        String output = "Status: " +
                                "Your login was successful " +
                                result.data.name;
                        mloginButton.setVisibility(View.GONE);
                        Intent intent = getIntentData();
                        startActivity(intent);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.i("MainActivity","Errorrr in getting user details");
                    }
                });

            }


            @Override
            public void failure(TwitterException exception) {
                Log.i("MainActivity","Errorrr in getting session details");
            }
        });
    }

    private Intent getIntentData() {
        return new Intent(getActivity(), TweetListActivity.class);
    }

}
