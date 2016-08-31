package com.example.manisharana.twitterclient.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.manisharana.twitterclient.R;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.ArrayList;
import java.util.List;

public class CustomTweetTimelineListAdapter extends RecyclerView.Adapter<CustomTweetTimelineListAdapter.TweetListViewHolder> {


    private final Context context;
    private ArrayList<Tweet> tweets;

    public CustomTweetTimelineListAdapter(Context context, ArrayList<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;

    }

    @Override
    public TweetListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_view, parent, false);
        itemView.setMinimumWidth(parent.getMeasuredWidth());
        return new TweetListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TweetListViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        if (tweet != null) {
            holder.tweetText.setText(tweet.text);
        }
    }

    public void swap(List<Tweet> datas) {
        tweets.clear();
        tweets.addAll(datas);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if (tweets.isEmpty())
            return 0;
        return tweets.size();
    }


    class TweetListViewHolder extends RecyclerView.ViewHolder {

        private final TextView tweetText;

        public TweetListViewHolder(View itemView) {
            super(itemView);
            tweetText = (TextView) itemView.findViewById(R.id.text_view_tweet_text);
        }
    }
}
