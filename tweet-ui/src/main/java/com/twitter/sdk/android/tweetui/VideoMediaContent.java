package com.twitter.sdk.android.tweetui;

import android.app.Activity;
import android.graphics.Bitmap;

public class VideoMediaContent implements IMediaContent{

    private static final long ALLOWED_FILE_SIZE = 3 * 1024 * 1024;


    public boolean isValidMedia(String extension, String mimeType, Long fileSize) {
        if (fileSize <= ALLOWED_FILE_SIZE && mimeType.startsWith("image") && validFileExt(extension))
            return true;
        return false;
    }

    @Override
    public Bitmap getThumbnail(Activity activity, long mediaId) {
        return null;
    }

    private boolean validFileExt(String extension) {
        if (extension.equals("png") || extension.equals("jpg") || extension.equals("webp"))
            return true;
        return false;
    }
}
