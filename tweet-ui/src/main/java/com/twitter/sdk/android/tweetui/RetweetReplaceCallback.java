package com.twitter.sdk.android.tweetui;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

public class RetweetReplaceCallback extends Callback<Tweet> {
    BaseTweetView baseTweetView;
    TweetRepository tweetRepository;
    Callback<Tweet> cb;

    RetweetReplaceCallback(BaseTweetView baseTweetView, TweetRepository tweetRepository,
                       Callback<Tweet> cb) {
        this.baseTweetView = baseTweetView;
        this.tweetRepository = tweetRepository;
        this.cb = cb;
    }

    @Override
    public void success(Result<Tweet> result) {
        if(result.data.retweeted){
            tweetRepository.updateCache(result.data.retweetedStatus);
            baseTweetView.setTweet(result.data.retweetedStatus);
        }else{
            tweetRepository.updateCache(result.data);
            baseTweetView.setTweet(result.data);
        }
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