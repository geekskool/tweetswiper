package com.twitter.sdk.android.tweetui;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public interface IMediaContent {
    boolean isValidMedia(String filePath);

    Bitmap getThumbnail(Context activity);

    String getErrorMsg(Context activity);

    void initializeFileDetails(Context activity, Uri data);

    String getType();
}
