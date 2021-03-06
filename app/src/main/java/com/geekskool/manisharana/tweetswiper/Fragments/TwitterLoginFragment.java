package com.geekskool.manisharana.tweetswiper.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.geekskool.manisharana.tweetswiper.Activities.NavigationDrawerActivity;
import com.geekskool.manisharana.R;
import com.geekskool.manisharana.tweetswiper.SessionUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

public class TwitterLoginFragment extends Fragment implements View.OnClickListener {

    private Button mloginButton;
    private TwitterAuthClient mTwitterAuthClient;
    private SessionUtils sessionUtils;
    private FragmentActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTwitterAuthClient = new TwitterAuthClient();
        activity = getActivity();
        sessionUtils = new SessionUtils(activity);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.twitter_login_view, container, false);
        mloginButton = (Button) rootView.findViewById(R.id.login_button);
        TwitterSession userSession = sessionUtils.getUserSessionDetails();

        if (userSession != null) {
            Intent intent = new Intent(activity, NavigationDrawerActivity.class);
            startActivity(intent);
            activity.finish();

        } else {
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
        if(sessionUtils.isNetworkAvailable()){
            authorizeUser();
        }else{
            sessionUtils.showErrorDialog(activity.getResources().getString(R.string.internet_connection));
        }

    }

    private void authorizeUser() {
        mTwitterAuthClient.authorize(activity, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                mloginButton.setVisibility(View.GONE);
                sessionUtils.saveUserSessionDetails(result.data);
                Intent intent = new Intent(activity, NavigationDrawerActivity.class);
                startActivity(intent);
                activity.finish();
            }


            @Override
            public void failure(TwitterException exception) {
                sessionUtils.showErrorDialog(exception.getMessage());
                mloginButton.setVisibility(View.VISIBLE);
                Crashlytics.logException(exception);
            }
        });
    }
}
