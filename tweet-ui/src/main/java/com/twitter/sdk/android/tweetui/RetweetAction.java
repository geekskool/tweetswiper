package com.twitter.sdk.android.tweetui;

import android.view.View;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.TwitterApiConstants;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetBuilder;

public class RetweetAction extends BaseTweetAction implements View.OnClickListener{
    private final Tweet tweet;
    private final TweetRepository tweetRepository;

    public RetweetAction(Tweet tweet, TweetUi tweetUi, Callback<Tweet> actionCallback) {
        super(actionCallback);
        this.tweet = tweet;
        this.tweetRepository = tweetUi.getTweetRepository();

    }

    @Override
    public void onClick(View view) {
        if (view instanceof ToggleImageButton) {
            final ToggleImageButton toggleImageButton = (ToggleImageButton) view;
            if (tweet.retweeted) {
                tweetRepository.unretweet(tweet.id,new RetweetCallback(toggleImageButton,tweet,getActionCallback()));
            } else {
                tweetRepository.retweet(tweet.id,new RetweetCallback(toggleImageButton,tweet,getActionCallback()));
            }
        }
    }

    static class RetweetCallback extends Callback<Tweet> {
        ToggleImageButton button;
        Tweet tweet;
        Callback<Tweet> cb;

        RetweetCallback(ToggleImageButton button, Tweet tweet, Callback<Tweet> cb) {
            this.button = button;
            this.tweet = tweet;
            this.cb = cb;
        }

        @Override
        public void success(Result<Tweet> result) {
            cb.success(result);
        }

        @Override
        public void failure(TwitterException exception) {
            if (exception instanceof TwitterApiException) {
                final TwitterApiException apiException = (TwitterApiException) exception;
                final int errorCode = apiException.getErrorCode();

                switch (errorCode) {
                    case TwitterApiConstants.Errors.ALREADY_RETWEETED:
                        final Tweet retweeted = new TweetBuilder().copy(tweet).setRetweeted(true)
                                .build();
                        cb.success(new Result<>(retweeted, null));
                        return;
                    default:
                        // reset the toggle state back to match the Tweet
                        button.setToggledOn(tweet.retweeted);
                        cb.failure(exception);
                        return;
                }
            }
            // reset the toggle state back to match the Tweet
            button.setToggledOn(tweet.retweeted);
            cb.failure(exception);
        }
    }
}
