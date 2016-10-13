package com.twitter.sdk.android.tweetui;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.Tweet;

import java.text.DateFormat;
import java.util.Date;

public class TweetViewUtils {

    static void setProfilePhotoView(Tweet displayTweet, BaseTweetView.DependencyProvider dependencyProvider, ImageView avatarView) {
        final Picasso imageLoader = dependencyProvider.getImageLoader();

        if (imageLoader == null) return;

        final String url;
        if (displayTweet == null || displayTweet.user == null) {
            url = null;
        } else {
            url = UserUtils.getProfileImageUrlHttps(displayTweet.user,
                    UserUtils.AvatarSize.REASONABLY_SMALL);
        }

        imageLoader.load(url).into(avatarView);
    }

    /**
     * Set the timestamp if data from the Tweet is available. If timestamp cannot be determined,
     * set the timestamp to an empty string to handle view recycling.
     * @param displayTweet
     * @param timestampView
     */
    public static void setTimestamp(Tweet displayTweet, Resources resources, TextView timestampView) {
        final String formattedTimestamp;
        if (displayTweet != null && displayTweet.createdAt != null &&
                TweetDateUtils.isValidTimestamp(displayTweet.createdAt)) {
            final Long createdAtTimestamp
                    = TweetDateUtils.apiTimeToLong(displayTweet.createdAt);
            final String timestamp = TweetDateUtils.getRelativeTimeString(resources,
                    System.currentTimeMillis(),
                    createdAtTimestamp);
            formattedTimestamp = TweetDateUtils.dotPrefix(timestamp);
        } else {
            formattedTimestamp = BaseTweetView.EMPTY_STRING;
        }

        timestampView.setText(formattedTimestamp);
    }

    /**
     * Sets the Tweet author name. If author name is unavailable, resets to empty string.
     * @param displayTweet
     * @param fullNameView
     * */
    public static void setName(Tweet displayTweet, TextView fullNameView) {
        if (displayTweet != null && displayTweet.user != null) {
            fullNameView.setText(Utils.stringOrEmpty(displayTweet.user.name));
        } else {
            fullNameView.setText(BaseTweetView.EMPTY_STRING);
        }

    }

    /**
     * Sets the Tweet author screen name. If screen name is unavailable, resets to empty string.
     * @param displayTweet
     * @param screenNameView
     */
    public static void setScreenName(Tweet displayTweet, TextView screenNameView) {
        if (displayTweet != null && displayTweet.user != null) {
            screenNameView.setText(UserUtils.formatScreenName(
                    Utils.stringOrEmpty(displayTweet.user.screenName)));
        } else {
            screenNameView.setText(BaseTweetView.EMPTY_STRING);
        }
    }

    /**
     * Sets the Tweet text. If the Tweet text is unavailable, resets to empty string.
     * @param displayTweet
     * @param contentView
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setText(Tweet displayTweet, TextView contentView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            contentView.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
        final CharSequence tweetText = Utils.charSeqOrEmpty(displayTweet.text);

        if (!TextUtils.isEmpty(tweetText)) {
            contentView.setText(tweetText);
            contentView.setVisibility(View.VISIBLE);
        } else {
            contentView.setText(BaseTweetView.EMPTY_STRING);
            contentView.setVisibility(View.GONE);
        }
    }

    public static void setContentDescription(Tweet displayTweet, View view, BaseTweetView.DependencyProvider dependencyProvider) {
        if (!TweetUtils.isTweetResolvable(displayTweet)) {
            view.setContentDescription(view.getResources().getString(R.string.tw__loading_tweet));
            return;
        }

        final FormattedTweetText formattedTweetText = dependencyProvider.getTweetUi()
                .getTweetRepository().formatTweetText(displayTweet);
        String tweetText = null;
        if (formattedTweetText != null) tweetText = formattedTweetText.text;

        final long createdAt = TweetDateUtils.apiTimeToLong(displayTweet.createdAt);
        String timestamp = null;
        if (createdAt != TweetDateUtils.INVALID_DATE) {
            timestamp = DateFormat.getDateInstance().format(new Date(createdAt));
        }

        view.setContentDescription(view.getResources().getString(R.string.tw__tweet_content_description,
                Utils.stringOrEmpty(displayTweet.user.name), Utils.stringOrEmpty(tweetText),
                Utils.stringOrEmpty(timestamp)));
    }


}
