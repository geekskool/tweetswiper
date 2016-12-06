package com.twitter.sdk.android.tweetui;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class GifMediaContent implements IMediaContent{

    private static final long ALLOWED_FILE_SIZE = 4 * 1024 * 1024;
    private long mediaId;
    private long fileSize;


    public boolean isValidMedia(String extension) {
        return fileSize <= ALLOWED_FILE_SIZE && validFileExt(extension);
    }

    @Override
    public Bitmap getThumbnail(Activity activity) {
        return MediaStore.Images.Thumbnails.getThumbnail(activity.getContentResolver(), mediaId, MediaStore.Video.Thumbnails.MINI_KIND, null);
    }

    @Override
    public String getErrorMsg(Activity activity) {
        return activity.getString(R.string.ImageErrorMsg);
    }

    @Override
    public void initializeFileDetails(Activity activity, Uri data) {
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

    private String[] getProjection() {
        return new String[]{MediaStore.MediaColumns._ID, MediaStore.MediaColumns.SIZE};
    }

    public boolean validFileExt(String extension) {
        return extension.equals("gif");
    }
}
