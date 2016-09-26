package com.example.manisharana.twitterclient.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.manisharana.twitterclient.R;
import com.example.manisharana.twitterclient.TweetUtils;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CustomTweetViewAdapter;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.HomeTimeline;

import java.util.ArrayList;
import java.util.List;

public class TweetListFragment extends Fragment {


    private static final String TAG = TweetListFragment.class.getSimpleName();

    private CustomTweetViewAdapter adapter;
    private TextView mErrorTextView;
    private LinearLayout mProgressBarContainer;
    private ViewPager tweetPage;
    private FixedTweetTimeline userTimeline;
    private View mRootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootview = inflater.inflate(R.layout.tweet_list_view, container);
        tweetPage = (ViewPager) mRootview.findViewById(R.id.view_pager_tweet_list);
        mProgressBarContainer = (LinearLayout)mRootview.findViewById(R.id.progress_bar_container);

        //adapter = new CustomTweetViewAdapter(getActivity(), new ArrayList<Tweet>());
        //tweetPage.setAdapter(adapter);

 //       TwitterSession session = Twitter.getInstance().core.getSessionManager().getActiveSession();
        long uid = TweetUtils.getUserSessionDetails(getActivity());

        HomeTimeline homeTimeLine = new HomeTimeline.Builder().userId(uid).includeReplies(false).includeRetweets(true).maxItemsPerRequest(30).build();
        hideProgressBar();
        adapter = new CustomTweetViewAdapter(getActivity(),homeTimeLine);
        tweetPage.setAdapter(adapter);

        // getHomeTimelineTweets(uid);
        return mRootview;
    }

    private void getHomeTimelineTweets(long uid) {
        if (uid != 0) {

            final StatusesService statusesService = Twitter.getApiClient().getStatusesService();
            statusesService.homeTimeline(30, null, null, false, false, false, true, new Callback<List<Tweet>>() {

                @Override
                public void success(Result<List<Tweet>> listResult) {
                    ArrayList<Tweet> tweets = new ArrayList<>(listResult.data);
                    final FixedTweetTimeline userTimeline = new FixedTweetTimeline.Builder()
                            .setTweets(tweets)
                            .build();
                    hideProgressBar();

                    adapter = new CustomTweetViewAdapter(getActivity(),userTimeline);
                    tweetPage.setAdapter(adapter);

                  //  adapter.swap(tweets);
                }

                @Override
                public void failure(TwitterException e) {
                    Log.d(TAG, "twitter " + e);
                    hideProgressBar();
                    TweetUtils.showErrorDialog(getActivity(),"Error In Fetching Tweets");
                }
            });
        }
    }

    private void hideProgressBar() {
        mProgressBarContainer.setVisibility(View.GONE);
    }

}
