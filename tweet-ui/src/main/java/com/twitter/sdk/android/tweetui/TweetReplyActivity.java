package com.twitter.sdk.android.tweetui;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.File;
import java.io.UnsupportedEncodingException;

import retrofit.mime.TypedFile;

public class TweetReplyActivity extends AppCompatActivity implements TextWatcher {

    private static final int SELECT_MEDIA = 0;
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
    private Button tweetButton;
    private TextView charCountView;
    private TextView errorView;
    private ImageView imgViewSelectedMedia;
    private TextView imgUrlTextView;
    private ImageButton removeMediaButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_reply);
        rootView = findViewById(R.id.tweet_reply_container);
        populateReplyView();
        Intent intent = getIntent();
        tweet = (Tweet) intent.getSerializableExtra("Tweet");
        if (tweet != null) {
            dependencyProvider = new BaseTweetView.DependencyProvider();
            populateTweetView(tweet);
        }
        //   actualTweetContainer.animate().translationY(0-actualTweetContainer.getHeight());
    }

    private void populateReplyView() {
        avatarView = (ImageView) findViewById(R.id.tw__tweet_author_avatar);
        header = (TextView) findViewById(R.id.text_view_reply_header);
        fullNameView = (TextView) findViewById(R.id.tw__tweet_author_full_name);
        contentView = (TextView) findViewById(R.id.tw__tweet_text);
        timestampView = (TextView) findViewById(R.id.tw__tweet_timestamp);
        userInput = (EditText) findViewById(R.id.edit_text_user_input_text);
        tweetButton = (Button) findViewById(R.id.tw__tweet_reply_button);
        charCountView = (TextView) findViewById(R.id.text_view_char_count);
        errorView = (TextView) findViewById(R.id.textView_error_message);
        imgViewSelectedMedia = (ImageView) findViewById(R.id.image_view_selected_media);
        imgUrlTextView = (TextView) findViewById(R.id.text_view_selected_media_url);
        removeMediaButton = (ImageButton) findViewById(R.id.image_remove_button);
    }

    public void pickPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/* gif/*");
        startActivityForResult(Intent.createChooser(intent, "Select Media"), SELECT_MEDIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            verifyDataAndFillView(data.getData());
        } else {
            // error content cant be fetched
        }
    }

    private void verifyDataAndFillView(Uri data) {

        String[] projection = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.SIZE};
        Cursor cursor = getContentResolver().query(data, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idColumnIndex = cursor.getColumnIndex(projection[0]);
            int pathColumnIndex = cursor.getColumnIndexOrThrow(projection[1]);
            int mimeTypeColumnIndex = cursor.getColumnIndex(projection[2]);
            int sizeColumnIndex = cursor.getColumnIndex(projection[3]);

            long imgId = cursor.getLong(idColumnIndex);
            String filePath = cursor.getString(pathColumnIndex);
            String mimeType = cursor.getString(mimeTypeColumnIndex);
            long fileSize = cursor.getLong(sizeColumnIndex);
            cursor.close();
            fillMediaContainer(imgId, filePath, mimeType, fileSize);

        } else {
            Toast.makeText(this, "You have selected invalid image", Toast.LENGTH_LONG).show();
        }
    }

    private void fillMediaContainer(long imgId, String filePath, String mimeType, long fileSize) {
        String fp = filePath.substring(filePath.lastIndexOf(".") + 1);
        IMediaContent mediaInstance = MediaContentHandler.getMediaInstance(mimeType);

        if (mediaInstance.isValidMedia(fp, mimeType, fileSize)) {
            Bitmap thumbnail = mediaInstance.getThumbnail(this,imgId);
            if (thumbnail == null) {
                Toast.makeText(getApplicationContext(),
                        "Failed to get thumbnail for our image.",
                        Toast.LENGTH_SHORT).show();
            }
            FrameLayout parent = (FrameLayout) imgViewSelectedMedia.getParent();
            parent.setVisibility(View.VISIBLE);
            imgViewSelectedMedia.setImageBitmap(thumbnail);
            imgUrlTextView.setText(filePath);

            removeMediaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup parent = (ViewGroup) v.getParent();
                    ImageView imgView = (ImageView) parent.getChildAt(1);
                    imgView.setImageBitmap(null);
                    imgUrlTextView.setText("");
                    parent.setVisibility(View.GONE);

                }
            });
        }
    }

    private void populateTweetView(Tweet tweet) {
        final Tweet displayTweet = TweetUtils.getDisplayTweet(tweet);
        userScreenName = UserUtils.formatScreenName(displayTweet.user.screenName).toString();
        String headerText = getString(R.string.reply_to) + " " + userScreenName + " ";
        header.setText(headerText);
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

    public void replyToTweet(View view) {
        String replyText = null;
        try {
            replyText = new String(userInput.getText().toString().trim().getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            Log.i("TweetReplyActivity", "Exception: " + exception);
            setErrorMessage("Error in encoding text");
        }
        String imageUrl = imgUrlTextView.getText().toString();

        if (replyText.length() != userScreenName.trim().length()) {
            new ReplyTask(dependencyProvider, this).execute(replyText, imageUrl);
        }
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

    private void setErrorMessage(String errorMessage) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setTextColor(ContextCompat.getColor(this, R.color.holo_red_dark));
        errorView.setText(errorMessage);
    }


    class ReplyTask extends AsyncTask<String, Void, Void> {

        private final BaseTweetView.DependencyProvider dependencyProvider;
        private final Context context;
        private TwitterSession.Serializer serializer;
        private ProgressDialog pD;

        public ReplyTask(BaseTweetView.DependencyProvider dependencyProvider, Context context) {
            this.dependencyProvider = dependencyProvider;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pD = new ProgressDialog(context);
            pD.setMessage(context.getResources().getString(R.string.uploading_msg));
            pD.setCancelable(false);
            pD.setIndeterminate(true);
            pD.show();
        }

        @Override
        protected Void doInBackground(final String... inputs) {
            serializer = new TwitterSession.Serializer();
            TwitterSession userSession = serializer.deserialize(TweetUtils.getUserSessionDetails(context));

            if (inputs[1] != null || !inputs[1].isEmpty()) {
                replyWithMedia(userSession, inputs);
            } else {
                replyWithoutMedia(inputs[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pD.dismiss();
        }

        private void replyWithMedia(TwitterSession userSession, final String[] inputs) {
            File photo = new File(inputs[1]);
            TypedFile typedFile = new TypedFile("application/octet-stream", photo);


            TwitterCore.getInstance().getApiClient(userSession).getMediaService().upload(typedFile, null, null, new Callback<Media>() {
                @Override
                public void success(Result<Media> result) {
                    String mediaIdString = result.data.mediaIdString;
                    dependencyProvider.getTweetUi().getTweetRepository().update(tweet.id, inputs[0], null, null, null, mediaIdString, new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            finish();
                            Toast.makeText(context, "You Tweeted :P", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void failure(TwitterException exception) {
                            Log.i("TweetReplyActivity", "Exception: " + exception);
                            setErrorMessage("Error in posting tweet");
                        }
                    });
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.i("TweetReplyActivity", "Exception: " + exception);
                    setErrorMessage("Error in uploading image");
                }
            });
        }

        private void replyWithoutMedia(String input) {
            dependencyProvider.getTweetUi().getTweetRepository().update(tweet.id, input, null, null, "", "", new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    finish();
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.i("TweetReplyActivity", "Exception: " + exception);
                    setErrorMessage("Error in posting tweet");
                }
            });
        }

    }
}