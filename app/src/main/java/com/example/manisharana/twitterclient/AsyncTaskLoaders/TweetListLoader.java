package com.example.manisharana.twitterclient.AsyncTaskLoaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class TweetListLoader extends AsyncTaskLoader<List<Tweet>> {

    public TweetListLoader(Context context) {
        super(context);
    }

    @Override
    public List<Tweet> loadInBackground() {
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl url = HttpUrl.parse("api.twitter.com").newBuilder()
                .addPathSegments("1.1/statuses/home_timeline.json").build();

//        final Request request = new Request.Builder()
//                .url(url)
//                .get()
//                .build();

        return null;
    }
}
