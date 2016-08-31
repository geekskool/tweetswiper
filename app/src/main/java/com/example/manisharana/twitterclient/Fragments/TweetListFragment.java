package com.example.manisharana.twitterclient.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.manisharana.twitterclient.Activities.MainActivity;
import com.example.manisharana.twitterclient.Adapters.CustomTweetTimelineListAdapter;
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

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

public class TweetListFragment extends Fragment {


    private static final String TAG = TweetListFragment.class.getSimpleName();
    private RecyclerView mTweetListView;
    private CustomTweetTimelineListAdapter adapter;
    private TextView mErrorTextView;
    private ProgressBar mProgressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootview = inflater.inflate(R.layout.tweet_list_view,container);

        adapter = new CustomTweetTimelineListAdapter(getActivity(), new ArrayList<Tweet>());
        mTweetListView = (RecyclerView) rootview.findViewById(R.id.recycle_view_tweet_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mTweetListView.setLayoutManager(linearLayoutManager);
        mTweetListView.setItemAnimator(new DefaultItemAnimator());
        mTweetListView.setAdapter(adapter);

//        mProgressBar = (ProgressBar) rootview.findViewById(R.id.progressBar);
//        mErrorTextView = (TextView) rootview.findViewById(R.id.text_view_error);



        TwitterSession session = Twitter.getInstance().core.getSessionManager().getActiveSession();
        long uid = session.getUserId();

        if (uid != 0) {
            final StatusesService statusesService = Twitter.getApiClient().getStatusesService();
            Call<List<Tweet>> lista = statusesService.homeTimeline(100,null,null,false,false,false,false);
            lista.enqueue(new Callback<List<Tweet>>() {

                @Override
                public void success(Result<List<Tweet>> listResult) {
                    ArrayList<Tweet> tweets = new ArrayList<>(listResult.data);
//                    final FixedTweetTimeline userTimeline = new FixedTweetTimeline.Builder()
//                            .setTweets(tweets)
//                            .build();
                  //  final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter(getActivity(),userTimeline);
                    adapter.swap(tweets);
                    //adapter add tweets
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
