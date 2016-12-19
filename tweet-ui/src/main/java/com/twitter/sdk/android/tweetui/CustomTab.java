package com.twitter.sdk.android.tweetui;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

public class CustomTab {
    private Context mContext;
    private CustomTabsIntent intent;


    public CustomTab(Context context) {
        mContext = context;
    }

    public void openUrl(String url) {
        intent = new CustomTabsIntent.Builder().enableUrlBarHiding().setShowTitle(false).setCloseButtonIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_navigation_close)).build();
        intent.launchUrl((Activity) mContext, Uri.parse(url));
    }

}