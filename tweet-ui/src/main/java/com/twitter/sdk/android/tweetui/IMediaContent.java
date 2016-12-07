package com.twitter.sdk.android.tweetui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;

public interface IMediaContent {
    boolean isValidMedia(String filePath);

    Bitmap getThumbnail(Activity activity);

    String getErrorMsg(Activity activity);

    void initializeFileDetails(Activity activity, Uri data);

    String getType();
}
