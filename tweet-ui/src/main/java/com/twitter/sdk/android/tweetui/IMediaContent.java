package com.twitter.sdk.android.tweetui;

import android.app.Activity;
import android.graphics.Bitmap;

public interface IMediaContent {
    boolean isValidMedia(String extension, String mimeType, Long fileSize);

    Bitmap getThumbnail(Activity activity, long imgId);
}
