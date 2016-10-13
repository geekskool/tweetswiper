package com.twitter.sdk.android.tweetui;


import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TweetReplyActivity extends AppCompatActivity implements TextWatcher {

    private static final int SELECT_PICTURE = 0;
    private static final int CHAR_ALLOWED_COUNT = 140;
    private ImageView avatarView;
    private TextView fullNameView;
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
    private LinearLayout actualTweetContainer;
    private TextView errorMessage;
    private ImageView imgViewFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_reply);
        rootView = findViewById(R.id.tweet_reply_container);
        actualTweetContainer = (LinearLayout) findViewById(R.id.tweet_text_container);
        avatarView = (ImageView) findViewById(R.id.tw__tweet_author_avatar);
        header = (TextView) findViewById(R.id.text_view_reply_header);
        fullNameView = (TextView) findViewById(R.id.tw__tweet_author_full_name);
        contentView = (TextView) findViewById(R.id.tw__tweet_text);
        timestampView = (TextView) findViewById(R.id.tw__tweet_timestamp);
        userInput = (EditText) findViewById(R.id.edit_text_user_input_text);
        cameraButton = (ImageButton) findViewById(R.id.tweet_camera_button);
        tweetButton = (Button) findViewById(R.id.tw__tweet_reply_button);
        charCountView = (TextView) findViewById(R.id.text_view_char_count);
        errorMessage = (TextView) findViewById(R.id.textView_error_message);
        imgViewFirst = (ImageView) findViewById(R.id.image_view_1);
        Intent intent = getIntent();
        tweet = (Tweet) intent.getSerializableExtra("Tweet");
        if (tweet != null) {
            dependencyProvider = new BaseTweetView.DependencyProvider();
            polpulateTweetView(tweet);
        }
        //   actualTweetContainer.animate().translationY(0-actualTweetContainer.getHeight());
    }

    public void pickPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Media"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            data.getData();
            FrameLayout parent = (FrameLayout) imgViewFirst.getParent();
            parent.setVisibility(View.VISIBLE);
            imgViewFirst.setImageBitmap(getPath(data.getData()));
        } else {
            // error content cant be fetched
        }
    }

    private Bitmap getPath(Uri uri) {
        Bitmap thumbnail = null;

        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.MIME_TYPE};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idColumnIndex = cursor.getColumnIndex(projection[0]);
            int pathColumnIndex = cursor.getColumnIndexOrThrow(projection[1]);
            int mimeTypeColumnIndex = cursor.getColumnIndex(projection[2]);

            long imgId = cursor.getLong(idColumnIndex);
            String filePath = cursor.getString(pathColumnIndex);
            String mimeType = cursor.getString(mimeTypeColumnIndex);
            cursor.close();

            if (mimeType.startsWith("image")) {
                 thumbnail = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), imgId, MediaStore.Images.Thumbnails.MINI_KIND, null);
                if(thumbnail == null){
                    Toast.makeText(getApplicationContext(),
                            "Failed to get thumbnail for our image.",
                            Toast.LENGTH_SHORT).show();

                }
            } else if (mimeType.startsWith("video")) {

            }



        }

        // Convert file path into bitmap image using below line.
        //Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        return thumbnail;
    }

    private void polpulateTweetView(Tweet tweet) {
        final Tweet displayTweet = TweetUtils.getDisplayTweet(tweet);
        userScreenName = UserUtils.formatScreenName(displayTweet.user.screenName).toString();
        header.setText(getString(R.string.reply_to) + " " + userScreenName + " ");
        TweetViewUtils.setProfilePhotoView(displayTweet, dependencyProvider, avatarView);
        TweetViewUtils.setName(displayTweet, fullNameView);
        TweetViewUtils.setTimestamp(displayTweet, getResources(), timestampView);
        TweetViewUtils.setText(displayTweet, contentView);
        TweetViewUtils.setContentDescription(displayTweet, rootView, dependencyProvider);
        userInput.setText(userScreenName);
        userInput.setSelection(userScreenName.length());
        userInput.addTextChangedListener(this);
        int length = CHAR_ALLOWED_COUNT - userInput.getText().length();
        charCountView.setText(String.valueOf(length));

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void replyToTweet(View view) {
        String replyText = null;
        try {
            replyText = URLEncoder.encode(userInput.getText().toString().trim(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            errorMessage.setVisibility(View.VISIBLE);
            //   errorMessage.setTextColor(getResources().getColor(android.R.color.holo_red_dark,null));


            errorMessage.setText("Error in encoding reply");
        }
        if (replyText.length() != userScreenName.trim().length())
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
        String inputWithWhiteSpace = userInput.getText().toString();
        String userString = inputWithWhiteSpace.trim();
        int length = userString.length();

        if ((length == userScreenName.length() && userString.equals(userScreenName)) || (length == 0 || userString.isEmpty())) {
            tweetButton.setEnabled(false);
        } else {
            tweetButton.setEnabled(true);
        }
        int charCount = CHAR_ALLOWED_COUNT - inputWithWhiteSpace.length();
        charCountView.setText(String.valueOf(charCount));
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
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Error in posting tweet");
                }
            });
            return null;
        }

    }

}
