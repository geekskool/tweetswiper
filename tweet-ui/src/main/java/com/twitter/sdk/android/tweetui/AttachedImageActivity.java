package com.twitter.sdk.android.tweetui;

import android.app.Activity;
import android.os.Bundle;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetui.internal.MultiTouchImageView;

public class AttachedImageActivity  extends Activity{
    public static final String IMAGE_PATH = "IMAGE_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tw__gallery_activity);

        final String filepath = getIntent().getStringExtra(IMAGE_PATH);
        final MultiTouchImageView imageView = (MultiTouchImageView) findViewById(R.id.image_view); // try and replace with tweetMediaView

        Picasso.with(this).load(filepath).fit().centerInside().into(imageView);
        //remove fit and check
    }
}
