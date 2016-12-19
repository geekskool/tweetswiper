package com.twitter.sdk.android.tweetui;

public class MediaContentHandler {
    private static IMediaContent iMediaContent;
    public static IMediaContent getMediaInstance(String mimeType) {
        if (mimeType.startsWith("image") && mimeType.endsWith("gif"))
            iMediaContent =  new GifMediaContent();

       // else if (mimeType.startsWith("video"))
            //iMediaContent = new VideoMediaContent();

        else if (mimeType.startsWith("image"))
            iMediaContent =  new ImageMediaContent();
        return iMediaContent;
    }
}
