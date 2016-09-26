package com.twitter.sdk.android.tweetui;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.Tweet;

public class TweetReplyActivity extends AppCompatActivity {

    private ImageView avatarView;
    private TextView fullNameView;
    private TextView screenNameView;
    private TextView contentView;
    private TextView timestampView;
    private BaseTweetView.DependencyProvider dependencyProvider;
    private View rootView;
    private TextView header;
    private EditText userInput;
    private Tweet tweet;
    private String userScreenName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_reply);
        rootView = findViewById(R.id.tweet_reply_container);
        avatarView = (ImageView) findViewById(R.id.tw__tweet_author_avatar);
        header = (TextView) findViewById(R.id.text_view_reply_header);
        fullNameView = (TextView) findViewById(R.id.tw__tweet_author_full_name);
        screenNameView = (TextView) findViewById(R.id.tw__tweet_author_screen_name);
        contentView = (TextView) findViewById(R.id.tw__tweet_text);
        timestampView = (TextView) findViewById(R.id.tw__tweet_timestamp);
        userInput = (EditText) findViewById(R.id.edit_text_user_input_text);
        Intent intent = getIntent();
        tweet = (Tweet) intent.getSerializableExtra("Tweet");
        if (tweet != null) {
            dependencyProvider = new BaseTweetView.DependencyProvider();
            polpulateTweetView(tweet);
        }
    }

    private void polpulateTweetView(Tweet tweet) {
        final Tweet displayTweet = TweetUtils.getDisplayTweet(tweet);
        userScreenName = " " + UserUtils.formatScreenName(displayTweet.user.screenName);
        header.setText(getString(R.string.reply_to) + userScreenName+" ");
        TweetViewUtils.setProfilePhotoView(displayTweet, dependencyProvider, avatarView);
        TweetViewUtils.setName(displayTweet, fullNameView);
        TweetViewUtils.setScreenName(displayTweet, screenNameView);
        TweetViewUtils.setTimestamp(displayTweet, getResources(), timestampView);
        TweetViewUtils.setText(displayTweet, contentView);
        TweetViewUtils.setContentDescription(displayTweet, rootView, dependencyProvider);
        userInput.setText(userScreenName);
        int cursorSelectionPoint = userScreenName.length();
        userInput.setSelection(cursorSelectionPoint);
    }

    public void replyToTweet(View view) {
        String replyText = userInput.getText().toString().trim();
        if(replyText.length() != userScreenName.length())
            new ReplyTask(dependencyProvider).execute(replyText);
    }

    public void closeActivity(View v) {
        finish();
    }


    class ReplyTask extends AsyncTask<String, Void, Void> {

        private final BaseTweetView.DependencyProvider dependencyProvider;

        public ReplyTask(BaseTweetView.DependencyProvider dependencyProvider) {
            this.dependencyProvider = dependencyProvider;
        }

        @Override
        protected Void doInBackground(String... inputs) {
            dependencyProvider.getTweetUi().getTweetRepository().update(tweet.id, inputs[0], null, null, null,null, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    finish();
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.i("TweetReplyActivity", "Exception: "+exception);
                }
            });
            return null;
        }

    }

}
