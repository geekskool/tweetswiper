package com.example.manisharana.twitterclient.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.manisharana.twitterclient.R;
import com.example.manisharana.twitterclient.TweetUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CustomTweetViewAdapter;
import com.twitter.sdk.android.tweetui.HomeTimeline;
import com.twitter.sdk.android.tweetui.TimelineResult;

public class TweetListFragment extends Fragment {


    private static final String TAG = TweetListFragment.class.getSimpleName();
    private static final String ERROR_MSG = "Couldn't fetch tweets.\nTry again later";

    private CustomTweetViewAdapter adapter;
    private TextView mErrorTextView;
    //  private LinearLayout mProgressBarContainer;
    private ViewPager tweetPage;
    private View mRootview;
    private TwitterSession.Serializer serializer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // TwitterSession session = Twitter.getInstance().core.getSessionManager().getActiveSession();
        serializer = new TwitterSession.Serializer();
        TwitterSession userSession = serializer.deserialize(TweetUtils.getUserSessionDetails(getActivity()));

        mRootview = inflater.inflate(R.layout.tweet_list_view, container, false);
        tweetPage = (ViewPager) mRootview.findViewById(R.id.view_pager_tweet_list);
        mErrorTextView = (TextView) mRootview.findViewById(R.id.text_view_error);
        //      mProgressBarContainer = (LinearLayout)mRootview.findViewById(R.id.progress_bar_container);

        HomeTimeline homeTimeLine = new HomeTimeline.Builder().userId(userSession.getUserId()).includeReplies(false).includeRetweets(true).maxItemsPerRequest(30).build();

        adapter = new CustomTweetViewAdapter(getActivity(), homeTimeLine, new Callback<TimelineResult<Tweet>>() {

            @Override
            public void success(Result<TimelineResult<Tweet>> result) {
                mErrorTextView.setVisibility(View.GONE);
                //         hideProgressBar();
                tweetPage.setAdapter(adapter);
            }

            @Override
            public void failure(TwitterException exception) {
                mErrorTextView.setText(ERROR_MSG);
                //            hideProgressBar();
            }
        });

        return mRootview;
    }

    private void hideProgressBar() {

        //    mProgressBarContainer.setVisibility(View.GONE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


}
