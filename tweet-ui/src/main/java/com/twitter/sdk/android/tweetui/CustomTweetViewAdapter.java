package com.twitter.sdk.android.tweetui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.internal.TimelineDelegate;

import java.util.Stack;

public class CustomTweetViewAdapter extends PagerAdapter {


    private static final String TAG = CustomTweetViewAdapter.class.getSimpleName();
    private final Context context;
    private final Stack mRecycledViewStack;
    private final TimelineDelegate<Tweet> delegate;
    //  private ArrayList<Tweet> tweets;
    protected Callback<Tweet> actionCallback;
    final protected int styleResId;

//    public CustomTweetViewAdapter(Context context,ArrayList<Tweet> tweets){
//        this.context = context;
//     //   this.tweets = tweets;
//        this.mRecycledViewStack = new Stack();
//        this.styleResId = R.style.tw__TweetLightWithActionsStyle;
//    }

    public CustomTweetViewAdapter(Context context, Timeline<Tweet> timeline) {
        this(context, timeline, R.style.tw__TweetLightWithActionsStyle, null);
    }

//    CustomTweetViewAdapter(Context context, Timeline<Tweet> timeline, int styleResId, Callback<Tweet> cb) {
//        TimelineDelegate<Tweet> tweetTimelineDelegate =
//
//        this(context,tweetTimelineDelegate, styleResId, cb);
//    }

    CustomTweetViewAdapter(Context context, Timeline<Tweet> timeline, int styleResId, Callback<Tweet> cb) {
        this.context = context;
        //this.tweets = new ArrayList<Tweet>();
        //this.tweets = delegate.getItem()
        this.styleResId = styleResId;
        this.delegate = new TimelineDelegate<>(timeline, this);;
        this.actionCallback = new ReplaceTweetCallback(delegate, cb);
        this.mRecycledViewStack = new Stack();
        delegate.refresh(null);
    }


//    public void swap(ArrayList<Tweet> datas) {
//        tweets.clear();
//        tweets.addAll(datas);
//        notifyDataSetChanged();
//    }



    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Tweet tweet =  delegate.getItem(position);
        View itemView ;

        if (mRecycledViewStack.isEmpty()) {
            final BaseTweetView tv = new CustomCompactTweetView(context, tweet, styleResId);
            tv.setOnActionCallback(actionCallback);
            itemView = tv;
        } else {
            itemView = (View) mRecycledViewStack.pop();
            ((BaseTweetView) itemView).setTweet(tweet);
            Log.i(TAG,"Restored recycled view from cache "+ itemView.hashCode());
        }

        container.addView(itemView);
        return itemView;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager pager = (ViewPager) container;
        View recycledView = (View) object;
        pager.removeView(recycledView);
        mRecycledViewStack.push(recycledView);
        Log.i(TAG,"Stored view in cache "+ recycledView.hashCode());
    }

    @Override
    public int getCount() {
        return delegate.getCount();
//        if (tweets.isEmpty())
//            return 0;
//        return tweets.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    /*
     * On success, sets the updated Tweet in the TimelineDelegate to replace any old copies
     * of the same Tweet by id.
     */
    static class ReplaceTweetCallback extends Callback<Tweet> {
        TimelineDelegate<Tweet> delegate;
        Callback<Tweet> cb;

        ReplaceTweetCallback(TimelineDelegate<Tweet> delegate, Callback<Tweet> cb) {
            this.delegate = delegate;
            this.cb = cb;
        }

        @Override
        public void success(Result<Tweet> result) {
            delegate.setItemById(result.data);
            if (cb != null) {
                cb.success(result);
            }
        }

        @Override
        public void failure(TwitterException exception) {
            if (cb != null) {
                cb.failure(exception);
            }
        }
    }

    /**
     * TweetTimelineListAdapter Builder
     */
    public static class Builder {
        private Context context;
        private Timeline<Tweet> timeline;
        private Callback<Tweet> actionCallback;
        private int styleResId = R.style.tw__TweetLightWithActionsStyle;

        /**
         * Constructs a Builder.
         * @param context Context for Tweet views.
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Sets the Tweet timeline data source.
         * @param timeline Timeline of Tweets
         */
        public Builder setTimeline(Timeline<Tweet> timeline) {
            this.timeline = timeline;
            return this;
        }

        /**
         * Sets the Tweet view style by resource id.
         * @param styleResId resource id of the Tweet view style
         */
        public Builder setViewStyle(int styleResId) {
            this.styleResId = styleResId;
            return this;
        }

        /**
         * Sets the callback to call when a Tweet action is performed on a Tweet view.
         * @param actionCallback called when a Tweet action is performed.
         */
        public Builder setOnActionCallback(Callback<Tweet> actionCallback) {
            this.actionCallback = actionCallback;
            return this;
        }

        /**
         * Builds a TweetTimelineListAdapter from Builder parameters.
         * @return a TweetTimelineListAdpater
         */
        public CustomTweetViewAdapter build() {
            return new CustomTweetViewAdapter(context, timeline, styleResId, actionCallback);
        }
    }


}

