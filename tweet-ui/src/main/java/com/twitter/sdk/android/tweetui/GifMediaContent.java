package com.twitter.sdk.android.tweetui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class GifMediaContent implements IMediaContent {

    private static final long ALLOWED_FILE_SIZE = 3 * 1024 * 1024;
    private long mediaId;
    private long fileSize;


    public boolean isValidMedia(String extension) {
        return fileSize <= ALLOWED_FILE_SIZE && validFileExt(extension);
    }

    @Override
    public Bitmap getThumbnail(Context activity) {
        return MediaStore.Images.Thumbnails.getThumbnail(activity.getContentResolver(), mediaId, MediaStore.Video.Thumbnails.MINI_KIND, null);
    }

    @Override
    public String getErrorMsg(Context activity) {
        return activity.getString(R.string.gifErrorMsg);
    }

    @Override
    public void initializeFileDetails(Context activity, Uri data) {
        String[] projection = getProjection();
        Cursor cursor = activity.getContentResolver().query(data, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idColumnIndex = cursor.getColumnIndex(projection[0]);
            int sizeColumnIndex = cursor.getColumnIndex(projection[1]);

            mediaId = cursor.getLong(idColumnIndex);
            fileSize = cursor.getLong(sizeColumnIndex);
            cursor.close();
        }
    }

    @Override
    public String getType() {
        return "gif";
    }


    private String[] getProjection() {
        return new String[]{MediaStore.MediaColumns._ID, MediaStore.MediaColumns.SIZE};
    }

    public boolean validFileExt(String extension) {
        return extension.equals("gif");
    }

}
