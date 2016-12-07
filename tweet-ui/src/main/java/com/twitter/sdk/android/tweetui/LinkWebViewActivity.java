package com.twitter.sdk.android.tweetui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LinkWebViewActivity extends AppCompatActivity {

    private WebView mWebView;
    public static final String URL = "url";

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw_link_web_view);

        mWebView = (WebView) findViewById(R.id.link_webview);
        mWebView.setWebViewClient(new WebViewClient());
        Intent intent = getIntent();
        String url = intent.getStringExtra(URL);
        if(url!=null){
            mWebView.loadUrl(url);
        }

    }
}