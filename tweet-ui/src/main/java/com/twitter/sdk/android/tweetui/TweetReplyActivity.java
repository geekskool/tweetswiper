package com.twitter.sdk.android.tweetui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.twitter.sdk.android.core.IntentUtils;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.internal.MediaBadgeView;

import java.io.File;
import java.io.UnsupportedEncodingException;

import io.fabric.sdk.android.Fabric;
import retrofit.mime.TypedFile;

public class TweetReplyActivity extends AppCompatActivity implements TextWatcher {

    private static final int SELECT_MEDIA = 0;
    private static final int CHAR_ALLOWED_COUNT = 140;
    private static final String TAG = TweetReplyActivity.class.getSimpleName();
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
    private TextView imgUrlTextView;
    private ImageButton removeMediaButton;
    private ImageView mediaImageView;
    private FrameLayout mediaContainer;
    private MediaBadgeView mediaBadge;
    private ImageView gifOverlay;
    private Button replyButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_reply);
        rootView = findViewById(R.id.tweet_reply_container);
        getViewReferences();
        Intent intent = getIntent();
        tweet = (Tweet) intent.getSerializableExtra("Tweet");
        if (tweet != null) {
            dependencyProvider = new BaseTweetView.DependencyProvider();
            populateTweetView(tweet);
        }
    }

    private void getViewReferences() {
        avatarView = (ImageView) findViewById(R.id.tw__tweet_author_avatar);
        header = (TextView) findViewById(R.id.text_view_reply_header);
        fullNameView = (TextView) findViewById(R.id.tw__tweet_author_full_name);
        contentView = (TextView) findViewById(R.id.tw__tweet_text);
        timestampView = (TextView) findViewById(R.id.tw__tweet_timestamp);
        userInput = (EditText) findViewById(R.id.edit_text_user_input_text);
        tweetButton = (Button) findViewById(R.id.tw__tweet_reply_button);
        charCountView = (TextView) findViewById(R.id.text_view_char_count);
        errorView = (TextView) findViewById(R.id.textView_error_message);
        mediaContainer = (FrameLayout) findViewById(R.id.reply_tweet_media_container);
        gifOverlay = (ImageView) findViewById(R.id.tw__gif_overlay);
        mediaImageView = (ImageView) findViewById(R.id.tw__tweet_media);
        mediaBadge = (MediaBadgeView) findViewById(R.id.tw__tweet_media_badge);
        imgUrlTextView = (TextView) findViewById(R.id.text_view_selected_media_url);
        removeMediaButton = (ImageButton) findViewById(R.id.image_remove_button);
        replyButton = (Button) findViewById(R.id.tw__tweet_reply_button);
    }

    public void pickPhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Media"), SELECT_MEDIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            getMedia(data.getData());
        }
    }

    private void getMedia(Uri data) {
        String[] projection = {MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(data, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int mimeTypeColumnIndex = cursor.getColumnIndex(projection[0]);
            int pathColumnIndex = cursor.getColumnIndex(projection[1]);

            String mimeType = cursor.getString(mimeTypeColumnIndex);
            String path = cursor.getString(pathColumnIndex);
            cursor.close();
            IMediaContent mediaInstance = MediaContentHandler.getMediaInstance(mimeType);
            mediaInstance.initializeFileDetails(this, data);
            fillMediaContainer(mediaInstance, path);
        }
    }

    private void fillMediaContainer(IMediaContent mediaInstance, String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);

        if (mediaInstance.isValidMedia(extension)) {
            Bitmap thumbnail = mediaInstance.getThumbnail(this);
            if (thumbnail == null) {
                Toast.makeText(getApplicationContext(),
                        "Failed to get thumbnail for the media.",
                        Toast.LENGTH_SHORT).show();
            } else {
                setUpMediaView(filePath, thumbnail, mediaInstance.getType());
            }
        } else {
            Toast.makeText(this, mediaInstance.getErrorMsg(this), Toast.LENGTH_LONG).show();
        }
    }

    private void setUpMediaView(String filePath, Bitmap thumbnail, String type) {
        setThumbnailView(filePath, thumbnail, View.VISIBLE, type);
        enableTweetButton();

        removeMediaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMediaOnClickRemove(v);
                enableTweetButton();
            }
        });
    }

    private void clearMediaOnClickRemove(View v) {
        ViewGroup parent = (ViewGroup) v.getParent();
        ImageView imgView = (ImageView) parent.getChildAt(1);
        imgView.setImageBitmap(null);
        imgUrlTextView.setText("");
        parent.setVisibility(View.GONE);
    }

    private void setThumbnailView(String filePath, Bitmap thumbnail, int visible, String type) {
        mediaContainer.setVisibility(visible);
        mediaImageView.setImageBitmap(thumbnail);
        imgUrlTextView.setText(filePath);
        mediaBadge.setMediaEntity(type);

        if (type.equals("image")) {
            gifOverlay.setVisibility(View.GONE);
            setPhotoLauncher(filePath);
        } else {
            gifOverlay.setVisibility(View.VISIBLE);
            setGifLauncher(mediaImageView, filePath);
            setGifLauncher(gifOverlay, filePath);
        }

    }

    private void setGifLauncher(ImageView imageView, final String filePath) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForVideo("file://" + filePath);
            }
        });
    }

    private void startActivityForVideo(String filePath) {
        Intent intent = new Intent(this, LinkWebViewActivity.class);
        intent.putExtra(LinkWebViewActivity.URL, filePath);
        IntentUtils.safeStartActivity(this, intent);
    }

    private void setPhotoLauncher(final String filePath) {
        mediaImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForViewingImage(filePath);
            }


        });
    }

    private void startActivityForViewingImage(String filePath) {
        final Intent intent = new Intent(this, AttachedImageActivity.class);
        intent.putExtra(AttachedImageActivity.IMAGE_PATH, "file://" + filePath);
        IntentUtils.safeStartActivity(this, intent);
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
        resetMessageView();
        view.setEnabled(false);
        progressDialog = getProgressDialog();
        progressDialog.show();

        String imageUrl = imgUrlTextView.getText().toString();
        try {
            replyText = new String(userInput.getText().toString().trim().getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException exception) {
            Fabric.getLogger().e(TAG, "Exception: " + exception);
            setErrorMessage(getString(R.string.error_encoding_text));
        }

        if (hasContent(replyText)) {
            if (hasMedia()) {
                replyWithMedia(replyText, imageUrl);
            } else {
                replyWithoutMedia(replyText);
            }
        }
    }

    private void resetMessageView() {
        errorView.setText("");
        errorView.setVisibility(View.GONE);
    }

    public void closeActivity(View v) {
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int inputLength = userInput.getText().toString().length();
        int charCount = CHAR_ALLOWED_COUNT - inputLength;

        charCountView.setText(String.valueOf(charCount));
        enableTweetButton();
    }

    private void enableTweetButton() {
        String userString = userInput.getText().toString().trim();
        tweetButton.setEnabled(hasContent(userString));
    }

    private boolean hasContent(String userString) {
        int length = userString.length();
        return (hasText(userString, length) || hasMedia());
    }

    private boolean hasText(String userString, int length) {
        return !((length == userScreenName.length() && userString.equals(userScreenName)) || length == 0 || userString.isEmpty());
    }

    private boolean hasMedia() {
        return !imgUrlTextView.getText().toString().isEmpty();
    }


    @Override
    public void afterTextChanged(Editable s) {

    }

    private void setErrorMessage(String errorMessage) {
        errorView.setVisibility(View.VISIBLE);
        errorView.setTextColor(ContextCompat.getColor(this, R.color.holo_red_dark));
        errorView.setText(errorMessage);
    }

    private void clearView() {
        userInput.setText("");
        setThumbnailView("", null, View.GONE, "");  /* TODO */
        tweetButton.setEnabled(false);
        resetMessageView();
        replyButton.setEnabled(false);
    }


    private ProgressDialog getProgressDialog() {
        ProgressDialog pD = new ProgressDialog(this);
        pD.setMessage(getString(R.string.uploading_msg));
        pD.setCancelable(false);
        pD.setIndeterminate(true);
        return pD;
    }

    private void replyWithMedia(final String text, String filePath) {
        TwitterSession.Serializer serializer = new TwitterSession.Serializer();
        TwitterSession userSession = serializer.deserialize(TweetUtils.getUserSessionDetails(this));
        File photo = new File(filePath);
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);

        TwitterCore.getInstance().getApiClient(userSession).getMediaService().upload(typedFile, null, null, new Callback<Media>() {
            @Override
            public void success(Result<Media> result) {
                String mediaIdString = result.data.mediaIdString;
                dependencyProvider.getTweetUi().getTweetRepository().update(tweet.id, text, null, null, null, mediaIdString, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        onSuccess();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        onFailure(exception, R.string.error_posting_tweet);
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                onFailure(exception, R.string.error_in_uploading_media);
            }
        });
    }

    private void replyWithoutMedia(String input) {
        dependencyProvider.getTweetUi().getTweetRepository().update(tweet.id, input, null, null, "", "", new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                onSuccess();
            }

            @Override
            public void failure(TwitterException exception) {
                onFailure(exception, R.string.error_posting_tweet);
            }
        });
    }


    private void onFailure(TwitterException exception, int stringId) {
        replyButton.setEnabled(true);
        progressDialog.dismiss();
        Fabric.getLogger().e(TAG, "Exception: " + exception);
        setErrorMessage(getString(stringId));
    }

    private void onSuccess() {
        clearView();
        progressDialog.dismiss();
        finish();
        Toast.makeText(this, "You Tweeted :P", Toast.LENGTH_SHORT).show();
    }

}
