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
import com.example.manisharana.twitterclient.TweetUtils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

public class TwitterLoginFragment extends Fragment implements View.OnClickListener {

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
        View rootView = inflater.inflate(R.layout.twitter_login_view, container, false);
        mloginButton = (Button) rootView.findViewById(R.id.login_button);
        Long userId = TweetUtils.getUserSessionDetails(getActivity());
        if(userId != 0){
            Intent intent = new Intent(getActivity(),TweetListActivity.class);
            startActivity(intent);
            getActivity().finish();

        }else{
            mloginButton.setVisibility(View.VISIBLE);
            mloginButton.setOnClickListener(this);
        }
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
                TweetUtils.saveUserSessionDetails(getActivity(), session);
                Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false, new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        mloginButton.setVisibility(View.GONE);
                        Intent intent = getIntentData();
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        mloginButton.setVisibility(View.VISIBLE);
                        Log.i("MainActivity", "Error in getting user details");
                        TweetUtils.showErrorDialog(getActivity(),"Error During Login");
                    }
                });

            }


            @Override
            public void failure(TwitterException exception) {
                mloginButton.setVisibility(View.VISIBLE);
                Log.i("MainActivity", "Error in getting session details");
                TweetUtils.showErrorDialog(getActivity(),"Error During Login");
            }
        });
        mloginButton.setVisibility(View.GONE);

    }

    private Intent getIntentData() {
        return new Intent(getActivity(), TweetListActivity.class);
    }



}
