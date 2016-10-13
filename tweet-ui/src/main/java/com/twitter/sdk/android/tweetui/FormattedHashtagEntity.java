package com.twitter.sdk.android.tweetui;

import android.net.Uri;

import com.twitter.sdk.android.core.models.HashtagEntity;

public class FormattedHashtagEntity extends FormattedNonUrlEntity {

    final String text;
    final int end;
    final int start;
    final String url;

    public FormattedHashtagEntity(HashtagEntity entity) {
        super(entity);
        text = entity.text;
        end = entity.getEnd();
        start = entity.getStart();
        url = Uri.parse("https://twitter.com/hashtag/").buildUpon().appendPath(text).appendQueryParameter("src", "hash").build().toString();
    }
}
