package com.example.manisharana.twitterclient.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.manisharana.twitterclient.Activities.MainActivity;
import com.example.manisharana.twitterclient.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

public class TweetListFragment extends ListFragment {


    private static final String TAG = TweetListFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.tweet_list_view,container);

        TwitterSession session = Twitter.getInstance().core.getSessionManager().getActiveSession();
        long uid = session.getUserId();


        Log.d(TAG,"UserID: "+uid);
        if (uid != 0) {
            final StatusesService statusesService = Twitter.getApiClient().getStatusesService();
            Call<List<Tweet>> lista = statusesService.homeTimeline(100,null,null,false,false,false,false);
            lista.enqueue(new Callback<List<Tweet>>() {

                @Override
                public void success(Result<List<Tweet>> listResult) {
                    List<Tweet> tweets = listResult.data;
                    final FixedTweetTimeline userTimeline = new FixedTweetTimeline.Builder()
                            .setTweets(tweets)
                            .build();
                    final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(getActivity(),userTimeline);
                    setListAdapter(adapter);
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
