package com.twitter.sdk.android.tweetui;

import android.net.Uri;

import com.twitter.sdk.android.core.models.MentionEntity;

public class FormattedMentionEntity extends FormattedNonUrlEntity {

    int start;
    int end;
    final String userId;
    final String screenName;
    final String url;

    public FormattedMentionEntity(MentionEntity entity) {
        super(entity);
        start = entity.getStart();
        end = entity.getEnd();
        userId = entity.idStr;
        screenName = entity.screenName;
        url = Uri.parse("https://twitter.com/").buildUpon().appendPath(screenName).build().toString();
    }
}
