package com.twitter.sdk.android.tweetui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;

import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.Tweet;

public class CustomCompactTweetView extends BaseTweetView{
    private static final String VIEW_TYPE_NAME = "default";
    private static final double SQUARE_ASPECT_RATIO = 1.0;
    private static final double MAX_LANDSCAPE_ASPECT_RATIO = 3.0;
    private static final double MIN_LANDSCAPE_ASPECT_RATIO = 4.0 / 3.0;

    public CustomCompactTweetView(Context context, Tweet tweet) {
        super(context, tweet);
    }

    public CustomCompactTweetView(Context context, Tweet tweet, int styleResId) {
        super(context, tweet, styleResId);
    }

    CustomCompactTweetView(Context context, Tweet tweet, int styleResId,
                     DependencyProvider dependencyProvider) {
        super(context, tweet, styleResId, dependencyProvider);
    }

    public CustomCompactTweetView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public CustomCompactTweetView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getLayout() {
        return R.layout.tw_custom_view_pager_view;
    }

    @Override
    void render() {
        super.render();
        screenNameView.requestLayout();
    }

    /**
     * Returns the desired aspect ratio of the Tweet media entity according to "sizes" metadata
     * and the aspect ratio display rules.
     * @param photoEntity the first
     * @return the target image and bitmap width to height aspect ratio
     */
    @Override
    protected double getAspectRatio(MediaEntity photoEntity) {
        final double ratio = super.getAspectRatio(photoEntity);
        if (ratio <= SQUARE_ASPECT_RATIO) {
            // portrait (tall) photos should be cropped to be square aspect ratio
          //  return SQUARE_ASPECT_RATIO;
            if(photoEntity.sizes.medium.h > getScreenHeight()){
                return photoEntity.sizes.medium.w/getScreenHeight();
            }
             else if(photoEntity.sizes.medium.w > getScreenWidth()){
                return getScreenWidth()/photoEntity.sizes.medium.h;
            } else{
                return ratio;
            }
        } else if (ratio > MAX_LANDSCAPE_ASPECT_RATIO) {
            // the widest landscape photos allowed are 3:1
            return MAX_LANDSCAPE_ASPECT_RATIO;
        } else if (ratio < MIN_LANDSCAPE_ASPECT_RATIO) {
            // the tallest landscape photos allowed are 4:3
            return MIN_LANDSCAPE_ASPECT_RATIO;
        } else {
            // landscape photos between 3:1 to 4:3 present the original width to height ratio
            return ratio;
        }
    }

    @Override
    String getViewTypeName() {
        return VIEW_TYPE_NAME;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


}
