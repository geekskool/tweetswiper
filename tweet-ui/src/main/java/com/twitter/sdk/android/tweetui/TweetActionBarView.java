/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.tweetui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.IntentUtils;
import com.twitter.sdk.android.core.models.Tweet;

public class TweetActionBarView extends LinearLayout {
    private static final String TAG = TweetActionBarView.class.getSimpleName();
    final DependencyProvider dependencyProvider;
    ToggleImageButton likeButton;
    ImageButton shareButton;
    Callback<Tweet> actionCallback;
    private TextView likeCount;
    private TextView retweetCount;
    private ImageButton replyButton;
    private ToggleImageButton retweetButton;
    private Callback<Tweet> retweetActionCallback;

    public TweetActionBarView(Context context) {
        this(context, null, new DependencyProvider());
    }

    public TweetActionBarView(Context context, AttributeSet attrs) {
        this(context, attrs, new DependencyProvider());
    }

    TweetActionBarView(Context context, AttributeSet attrs, DependencyProvider dependencyProvider) {
        super(context, attrs);
        this.dependencyProvider = dependencyProvider;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findSubviews();
    }

    /*
     * Sets the callback to call when a Tweet Action (favorite, unfavorite) is performed.
     */
    void setOnActionCallback(Callback<Tweet> actionCallback) {
        this.actionCallback = actionCallback;
    }

    void findSubviews() {
        likeButton = (ToggleImageButton) findViewById(R.id.tw__tweet_like_button);
        shareButton = (ImageButton) findViewById(R.id.tw__tweet_share_button);
        replyButton = (ImageButton) findViewById(R.id.tweet_reply_button);
        retweetButton = (ToggleImageButton) findViewById(R.id.tweet_retweet_button);
        likeCount = (TextView) findViewById(R.id.textview_like_count);
        retweetCount = (TextView) findViewById(R.id.textview_retweet_count);
    }

    /*
     * Setup action bar buttons with Tweet and action performer.
     * @param tweet Tweet source for whether an action has been performed (e.g. isFavorited?)
     */
    void setTweet(Tweet tweet) {
        setLike(tweet);
        setShare(tweet);
        setRetweet(tweet);
        setReply(tweet);
    }

    private void setReply(final Tweet tweet) {
        if (tweet != null) {
            replyButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getReplyIntent(tweet);
                    if (!IntentUtils.safeStartActivity(getContext(), intent)) {
                        Log.i(TAG,"Activity not found");
                        }
                    }
            });
        }
    }

    private Intent getReplyIntent(Tweet tweet) {
        Intent replyIntent = new Intent(getContext(), TweetReplyActivity.class);
        replyIntent.putExtra("Tweet", tweet);
        return replyIntent;
    }

    private void setRetweet(Tweet tweet) {
        final TweetUi tweetUi = dependencyProvider.getTweetUi();
        retweetCount.setText(String.valueOf(tweet.retweetCount));
        if (tweet != null) {
             retweetButton.setToggledOn(tweet.retweeted);
            final RetweetAction retweetAction = new RetweetAction(tweet,
                    tweetUi, retweetActionCallback);
            retweetButton.setOnClickListener(retweetAction);
        }
    }

    void setLike(Tweet tweet) {
        final TweetUi tweetUi = dependencyProvider.getTweetUi();
        likeCount.setText(String.valueOf(tweet.favoriteCount));
        if (tweet != null) {
            likeButton.setToggledOn(tweet.favorited);
            final LikeTweetAction likeTweetAction = new LikeTweetAction(tweet,
                    tweetUi, actionCallback);
            likeButton.setOnClickListener(likeTweetAction);
        }
    }

    void setShare(Tweet tweet) {
        final TweetUi tweetUi = dependencyProvider.getTweetUi();
        if (tweet != null) {
            shareButton.setOnClickListener(new ShareTweetAction(tweet, tweetUi));
        }
    }

    public void setRetweetCallback(Callback<Tweet> retweetReplaceCallback) {
        this.retweetActionCallback = retweetReplaceCallback;
    }

    /**
     * This is a mockable class that extracts our tight coupling with the TweetUi singleton.
     */
    static class DependencyProvider {
        /**
         * Return TweetRepository
         */
        TweetUi getTweetUi() {
            return TweetUi.getInstance();
        }
    }
}
