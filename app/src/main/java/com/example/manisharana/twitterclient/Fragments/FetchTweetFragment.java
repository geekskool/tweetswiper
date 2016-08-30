package com.example.manisharana.twitterclient.Fragments;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.twitter.sdk.android.core.models.Tweet;

public class FetchTweetFragment extends Fragment implements LoaderManager.LoaderCallbacks<Tweet> {
    @Override
    public Loader<Tweet> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Tweet> loader, Tweet tweet) {

    }

    @Override
    public void onLoaderReset(Loader<Tweet> loader) {

    }
}
