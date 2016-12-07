package com.twitter.sdk.android.tweetui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;

public class CustomTab {

    private static final String PACKAGE_NAME = "com.android.chrome";
    private CustomTabsClient mTabsClient;
    private CustomTabsSession customTabsSession;
    private Context mContext;
    private CustomTabsIntent intent;


    public CustomTab(Context context) {
        mContext = context;
    }

    public void getTabsIntent(String url) {
        CustomTabsServiceConnection serviceConnection = new CustomTabsServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mTabsClient = null;
            }

            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mTabsClient = customTabsClient;
                mTabsClient.warmup(0L);
                customTabsSession = mTabsClient.newSession(null);
            }
        };

        CustomTabsClient.bindCustomTabsService(mContext, PACKAGE_NAME, serviceConnection);
        intent = new CustomTabsIntent.Builder(customTabsSession).setShowTitle(false).setCloseButtonIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ic_navigation_close)).build();
        intent.launchUrl((Activity) mContext, Uri.parse(url));
    }

}