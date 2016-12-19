package com.geekskool.manisharana.tweetswiper.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.geekskool.manisharana.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.IntentUtils;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.AttachedImageActivity;
import com.twitter.sdk.android.tweetui.IMediaContent;
import com.twitter.sdk.android.tweetui.LinkWebViewActivity;
import com.twitter.sdk.android.tweetui.MediaContentHandler;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.internal.MediaBadgeView;

import java.io.File;
import java.io.UnsupportedEncodingException;

import retrofit.mime.TypedFile;

public class ComposeTweetFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private static final int CHAR_COUNT = 140;
    private static final int SELECT_MEDIA = 1;
    private EditText userInput;
    private Button sendTweetButton;
    private TextView charCountView;
    private TextView errorMsgView;
    private Activity mContext;
    private FrameLayout mediaContainer;
    private ImageView gifOverlay;
    private ImageView mediaImageView;
    private MediaBadgeView mediaBadge;
    private TextView imgUrlTextView;
    private ImageButton removeMediaButton;
    private ImageButton cameraButton;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_compose_new_tweet, container, false);
        userInput = (EditText) rootView.findViewById(R.id.edit_text_compose_tweet);
        userInput.addTextChangedListener(this);
        errorMsgView = (TextView) rootView.findViewById(R.id.text_view_error_message);
        charCountView = (TextView) rootView.findViewById(R.id.text_view_char_count);
        sendTweetButton = (Button) rootView.findViewById(R.id.tw__compose_tweet_button);
        mediaContainer = (FrameLayout) rootView.findViewById(com.twitter.sdk.android.tweetui.R.id.reply_tweet_media_container);
        gifOverlay = (ImageView) rootView.findViewById(com.twitter.sdk.android.tweetui.R.id.tw__gif_overlay);
        mediaImageView = (ImageView) rootView.findViewById(com.twitter.sdk.android.tweetui.R.id.tw__tweet_media);
        mediaBadge = (MediaBadgeView) rootView.findViewById(com.twitter.sdk.android.tweetui.R.id.tw__tweet_media_badge);
        imgUrlTextView = (TextView) rootView.findViewById(com.twitter.sdk.android.tweetui.R.id.text_view_selected_media_url);
        removeMediaButton = (ImageButton) rootView.findViewById(com.twitter.sdk.android.tweetui.R.id.image_remove_button);
        cameraButton = (ImageButton) rootView.findViewById(R.id.tweet_camera_button);
        setClickListeners();
        mContext = getActivity();

        return rootView;
    }

    private void setClickListeners() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Media"), SELECT_MEDIA);
            }
        });
        sendTweetButton.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int length = userInput.getText().toString().length();
        charCountView.setText(String.valueOf(CHAR_COUNT - length));
        enableTweetButton();
    }

    private void enableTweetButton() {
        sendTweetButton.setEnabled(hasContent());
    }

    private boolean hasContent() {
        String inputText = userInput.getText().toString();
        int length = inputText.length();
        return (length > 0 && !inputText.isEmpty()) || hasMedia();
    }

    private boolean hasMedia() {
        return !imgUrlTextView.getText().toString().isEmpty();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void setMessageView(String errorMessage, int msgColor) {
        errorMsgView.setVisibility(View.VISIBLE);
        errorMsgView.setTextColor(ContextCompat.getColor(mContext, msgColor));
        errorMsgView.setText(errorMessage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            getMedia(data.getData());
        }
    }

    private void getMedia(Uri data) {
        String[] projection = {MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.DATA};
        Cursor cursor = mContext.getContentResolver().query(data, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int mimeTypeColumnIndex = cursor.getColumnIndex(projection[0]);
            int pathColumnIndex = cursor.getColumnIndex(projection[1]);

            String mimeType = cursor.getString(mimeTypeColumnIndex);
            String path = cursor.getString(pathColumnIndex);
            cursor.close();
            IMediaContent mediaInstance = MediaContentHandler.getMediaInstance(mimeType);
            mediaInstance.initializeFileDetails(mContext, data);
            fillMediaContainer(mediaInstance, path);
        }
    }

    private void fillMediaContainer(IMediaContent mediaInstance, String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);

        if (mediaInstance.isValidMedia(extension)) {
            Bitmap thumbnail = mediaInstance.getThumbnail(mContext);
            if (thumbnail == null) {
                Toast.makeText(mContext,
                        "Failed to get thumbnail for the media.",
                        Toast.LENGTH_SHORT).show();
            } else {
                setUpMediaView(filePath, thumbnail, mediaInstance.getType());
            }
        } else {
            Toast.makeText(mContext, mediaInstance.getErrorMsg(mContext), Toast.LENGTH_LONG).show();
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
        Intent intent = new Intent(mContext, LinkWebViewActivity.class);
        intent.putExtra(LinkWebViewActivity.URL, filePath);
        IntentUtils.safeStartActivity(mContext, intent);
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
        final Intent intent = new Intent(mContext, AttachedImageActivity.class);
        intent.putExtra(AttachedImageActivity.IMAGE_PATH, "file://" + filePath);
        IntentUtils.safeStartActivity(mContext, intent);
    }

    private void resetMessageView() {
        errorMsgView.setText("");
        errorMsgView.setVisibility(View.GONE);
    }

    private void clearView() {
        userInput.setText("");
        setThumbnailView("", null, View.GONE, "");  /* TODO */
        sendTweetButton.setEnabled(false);
        resetMessageView();
    }

    @Override
    public void onClick(View v) {
        if (v == sendTweetButton) {
            resetMessageView();
            sendTweetButton.setEnabled(false);
            progressDialog = getProgessDialog();
            progressDialog.show();
            if (hasContent()) {
                try {
                    String encodedString = new String(userInput.getText().toString().getBytes("UTF-8"), "UTF-8");
                    if (hasMedia()) {
                        String mediaPath = imgUrlTextView.getText().toString();
                        postTweetWithMedia(encodedString, mediaPath);
                    } else {
                        postTweet(encodedString);
                    }

                } catch (UnsupportedEncodingException exception) {
                    setMessageView(mContext.getString(R.string.error_in_posting_tweet), com.twitter.sdk.android.tweetui.R.color.holo_red_dark);
                    Crashlytics.logException(exception);
                }
            }
        }
    }

    private ProgressDialog getProgessDialog() {
        ProgressDialog pD = new ProgressDialog(mContext);
        pD.setMessage(mContext.getString(com.twitter.sdk.android.tweetui.R.string.uploading_msg));
        pD.setCancelable(false);
        pD.setIndeterminate(true);
        return pD;
    }

    private void postTweetWithMedia(final String encodedString, String mediaPath) {
        TwitterSession.Serializer serializer = new TwitterSession.Serializer();
        TwitterSession userSession = serializer.deserialize(TweetUtils.getUserSessionDetails(mContext));

        File media = new File(mediaPath);
        TypedFile typedFile = new TypedFile("application/octet-stream", media);

        TwitterCore.getInstance().getApiClient(userSession).getMediaService().upload(typedFile, null, null, new Callback<Media>() {
            @Override
            public void success(Result<Media> result) {
                String mediaIdString = result.data.mediaIdString;
                Twitter.getApiClient().getStatusesService().update(encodedString, null, false, null, null, null, false, true, mediaIdString, new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        onSuccess();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        onFailure(exception, R.string.error_in_posting_tweet);
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                onFailure(exception,R.string.error_in_uploading_media);

            }
        });
    }

    private void onSuccess() {
        clearView();
        setMessageView(mContext.getString(R.string.tweet_posted_successfully), com.twitter.sdk.android.tweetui.R.color.holo_green_light);
        progressDialog.dismiss();
    }

    private void postTweet(String encodedString) {
        Twitter.getApiClient().getStatusesService().update(encodedString, null, false, null, null, null, false, true, null, new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                onSuccess();
            }

            @Override
            public void failure(TwitterException exception) {
                onFailure(exception,R.string.error_in_posting_tweet);
            }
        });
    }

    private void onFailure(TwitterException exception,int msgId) {
        setMessageView(mContext.getString(msgId), com.twitter.sdk.android.tweetui.R.color.holo_red_dark);
        Crashlytics.logException(exception);
        progressDialog.dismiss();
        sendTweetButton.setEnabled(true);
    }

}
