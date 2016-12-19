package com.twitter.sdk.android.tweetui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.IntentUtils;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.VideoInfo;
import com.twitter.sdk.android.tweetui.internal.MediaBadgeView;
import com.twitter.sdk.android.tweetui.internal.TweetMediaUtils;
import com.twitter.sdk.android.tweetui.internal.TweetMediaView;

import java.util.List;

public class MediaEntityAdapter extends RecyclerView.Adapter<MediaEntityAdapter.MediaEntityViewHolder> {
    private static final double DEFAULT_ASPECT_RATIO = 16.0 / 9.0;
    private final List<MediaEntity> mediaEntityList;
    private final Tweet displayTweet;
    private final TweetMediaClickListener tweetMediaClickListener;
    private final int mediaBgColor;
    private Context context;

    public MediaEntityAdapter(Context context, List<MediaEntity> mediaEntityList, Tweet displayTweet, TweetMediaClickListener tweetMediaClickListener, int mediaBgColor) {
        this.mediaEntityList = mediaEntityList;
        this.displayTweet = displayTweet;
        this.tweetMediaClickListener = tweetMediaClickListener;
        this.context = context;
        this.mediaBgColor = mediaBgColor;
    }

    @Override
    public MediaEntityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet_media_layout, parent, false);
        return new MediaEntityViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MediaEntityViewHolder holder, int position) {
        clearMediaView(holder);
        MediaEntity mediaEntity = mediaEntityList.get(position);
        if (TweetMediaUtils.isPhotoType(mediaEntity)) {
            setPhotoLauncher(holder, displayTweet, mediaEntity);
        } else if (TweetMediaUtils.isVideoType(mediaEntity)) {
            holder.mediaView.setOverlayDrawable(ContextCompat.getDrawable(context,R.drawable.tw__player_overlay));
            setMediaLauncher(holder, displayTweet, mediaEntity);

        }
        holder.mediaBadgeView.setMediaEntity(mediaEntity);
        setMediaImage(holder, mediaEntity.mediaUrlHttps, getAspectRatio(mediaEntity));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void clearMediaView(MediaEntityViewHolder holder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.mediaView.setBackground(null);
        } else {
            holder.mediaView.setBackgroundDrawable(null);
        }
        holder.mediaView.setOverlayDrawable(null);
        holder.mediaView.setOnClickListener(null);
        holder.mediaView.setClickable(false);
    }


    private void setPhotoLauncher(MediaEntityViewHolder holder, final Tweet displayTweet, final MediaEntity entity) {
        holder.mediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tweetMediaClickListener != null) {
                    tweetMediaClickListener.onMediaEntityClick(displayTweet, entity);
                } else {
                    final Intent intent = new Intent(context, GalleryActivity.class);
                    intent.putExtra(GalleryActivity.MEDIA_ENTITY, entity);
                    intent.putExtra(GalleryActivity.TWEET_ID, displayTweet.id);
                    IntentUtils.safeStartActivity(context, intent);
                }
            }
        });
    }

    private void setMediaLauncher(MediaEntityViewHolder holder, final Tweet displayTweet, final MediaEntity entity) {
        holder.mediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tweetMediaClickListener != null) {
                    tweetMediaClickListener.onMediaEntityClick(displayTweet, entity);
                } else {
                    final VideoInfo.Variant variant = TweetMediaUtils.getSupportedVariant(entity);
                    if (variant != null) {

                        final Intent intent = new Intent(context, PlayerActivity.class);
                        final boolean looping = TweetMediaUtils.isLooping(entity);
                        final String url = TweetMediaUtils.getSupportedVariant(entity).url;
                        final PlayerActivity.PlayerItem item =
                                new PlayerActivity.PlayerItem(url, looping);
                        intent.putExtra(PlayerActivity.PLAYER_ITEM, item);

                        IntentUtils.safeStartActivity(context, intent);
                    }
                }
            }
        });
    }

    void setMediaImage(final MediaEntityViewHolder holder, String imagePath, double aspectRatio) {
        final Picasso imageLoader = TweetUi.getInstance().getImageLoader();

        if (imageLoader == null) return;

        // Picasso fit is a deferred call to resize(w,h) which waits until the target has a
        // non-zero width or height and resizes the bitmap to the target's width and height.
        // For recycled targets, which already have a width and (stale) height, reset the size
        // target to zero so Picasso fit works correctly.
        holder.mediaView.resetSize();
        holder.mediaView.setAspectRatio(aspectRatio);
        ColorDrawable mediaBg = new ColorDrawable(mediaBgColor);
        imageLoader.load(imagePath)
                .placeholder(mediaBg)
                .fit()
                .centerCrop()
                .into(holder.mediaView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        int photoErrorResId =  R.drawable.tw__ic_tweet_photo_error_light;
                        imageLoader.load(photoErrorResId)
                                .into(holder.mediaView, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.mediaView.setBackgroundColor(mediaBgColor);
                                    }

                                    @Override
                                    public void onError() { /* intentionally blank */ }
                                });
                    }
                });
    }


    protected double getAspectRatio(MediaEntity photoEntity) {
        if (photoEntity == null || photoEntity.sizes == null || photoEntity.sizes.medium == null ||
                photoEntity.sizes.medium.w == 0 || photoEntity.sizes.medium.h == 0) {
            return DEFAULT_ASPECT_RATIO;
        }

        return (double) photoEntity.sizes.medium.w / photoEntity.sizes.medium.h;
    }


    @Override
    public int getItemCount() {
        return mediaEntityList.size();
    }

    public class MediaEntityViewHolder extends RecyclerView.ViewHolder {

        private final MediaBadgeView mediaBadgeView;
        private final TweetMediaView mediaView;

        public MediaEntityViewHolder(View itemView) {
            super(itemView);
            mediaBadgeView = (MediaBadgeView) itemView.findViewById(R.id.tw__tweet_media_badge);
            mediaView = (TweetMediaView) itemView.findViewById(R.id.tw__tweet_media);

        }
    }
}
