package com.twitter.sdk.android.tweetui;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.Tweet;

public class TweetReplyActivity extends AppCompatActivity implements TextWatcher {

    private static final int SELECT_PICTURE = 0;
    private static final int CHAR_ALLOWED_COUNT = 140;
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
    private ImageButton cameraButton;
    private Button tweetButton;
    private TextView charCountView;

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
        cameraButton = (ImageButton) findViewById(R.id.tweet_camera_button);
        tweetButton = (Button) findViewById(R.id.tw__tweet_reply_button);
        charCountView = (TextView) findViewById(R.id.text_view_char_count);
        Intent intent = getIntent();
        tweet = (Tweet) intent.getSerializableExtra("Tweet");
        if (tweet != null) {
            dependencyProvider = new BaseTweetView.DependencyProvider();
            polpulateTweetView(tweet);
        }
    }

    public void pickPhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = getPath(data.getData());
        }
    }

    private Bitmap getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        cursor.close();
        // Convert file path into bitmap image using below line.
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        return bitmap;
    }

    private void polpulateTweetView(Tweet tweet) {
        final Tweet displayTweet = TweetUtils.getDisplayTweet(tweet);
        userScreenName = UserUtils.formatScreenName(displayTweet.user.screenName) + " ";
        header.setText(getString(R.string.reply_to) +" "+userScreenName + " ");
        TweetViewUtils.setProfilePhotoView(displayTweet, dependencyProvider, avatarView);
        TweetViewUtils.setName(displayTweet, fullNameView);
        TweetViewUtils.setScreenName(displayTweet, screenNameView);
        TweetViewUtils.setTimestamp(displayTweet, getResources(), timestampView);
        TweetViewUtils.setText(displayTweet, contentView);
        TweetViewUtils.setContentDescription(displayTweet, rootView, dependencyProvider);
        userInput.setText(userScreenName);
        userInput.setSelection(userScreenName.length());
        userInput.addTextChangedListener(this);
        int length = CHAR_ALLOWED_COUNT-userInput.getText().length();
        charCountView.setText(String.valueOf(length));

    }

    public void replyToTweet(View view) {
        String replyText = userInput.getText().toString().trim();
        if (replyText.length() != userScreenName.length())
            new ReplyTask(dependencyProvider).execute(replyText);
    }

    public void closeActivity(View v) {
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int length = userInput.getText().length();

        if(length == userScreenName.length() && userInput.getText().toString() == userScreenName){
            tweetButton.setEnabled(false);
        }else{
            tweetButton.setEnabled(true);
        }
        int charCount = CHAR_ALLOWED_COUNT-userInput.getText().length();
        charCountView.setText(String.valueOf(charCount));
     //   utf encoding
        //   try {
     //   String userText = URLEncoder.encode(inputs[0], "UTF-8");
   // } catch (UnsupportedEncodingException e) {
    //    e.printStackTrace();
    }
    

    @Override
    public void afterTextChanged(Editable s) {

    }


    class ReplyTask extends AsyncTask<String, Void, Void> {

        private final BaseTweetView.DependencyProvider dependencyProvider;

        public ReplyTask(BaseTweetView.DependencyProvider dependencyProvider) {
            this.dependencyProvider = dependencyProvider;
        }

        @Override
        protected Void doInBackground(String... inputs) {
            dependencyProvider.getTweetUi().getTweetRepository().update(tweet.id, inputs[0], null, null, null, null, new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    finish();
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.i("TweetReplyActivity", "Exception: " + exception);
                }
            });
            return null;
        }

    }

}
