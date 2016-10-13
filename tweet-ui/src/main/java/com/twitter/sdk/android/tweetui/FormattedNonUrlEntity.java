package com.twitter.sdk.android.tweetui;

import com.twitter.sdk.android.core.models.Entity;

public class FormattedNonUrlEntity {

    int start;
    int end;

    public FormattedNonUrlEntity(Entity entity) {
        start = entity.getStart();
        end = entity.getEnd();
    }
}
