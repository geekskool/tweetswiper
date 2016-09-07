package com.example.manisharana.twitterclient.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.manisharana.twitterclient.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CustomTweetViewAdapter;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;

import java.util.ArrayList;
import java.util.List;

public class TweetListFragment extends Fragment {


    private static final String TAG = TweetListFragment.class.getSimpleName();
    private RecyclerView mTweetListView;
    private CustomTweetViewAdapter adapter;
    private TextView mErrorTextView;
    private ProgressBar mProgressBar;
    private ViewPager tweetPage;
    private FixedTweetTimeline userTimeline;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.tweet_list_view,container);
        tweetPage = (ViewPager) rootview.findViewById(R.id.recycle_view_tweet_list);

        adapter = new CustomTweetViewAdapter(getActivity(),new ArrayList<Tweet>());
        tweetPage.setAdapter(adapter);

        TwitterSession session = Twitter.getInstance().core.getSessionManager().getActiveSession();
        long uid = session.getUserId();

        if (uid != 0) {

            final StatusesService statusesService = Twitter.getApiClient().getStatusesService();
            statusesService.homeTimeline(100,null,null,false,false,false,false,new Callback<List<Tweet>>() {

                @Override
                public void success(Result<List<Tweet>> listResult) {
                    ArrayList<Tweet> tweets = new ArrayList<>(listResult.data);
//                    final FixedTweetTimeline userTimeline = new FixedTweetTimeline.Builder()
//                            .setTweets(tweets)
//                            .build();
                    adapter.swap(tweets);
                }

                @Override
                public void failure(TwitterException e) {
                    Log.d("Twitter","twitter " + e );
                }
            });
        }

        return rootview;
    }
}
