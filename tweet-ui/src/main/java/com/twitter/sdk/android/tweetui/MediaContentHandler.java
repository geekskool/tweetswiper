package com.twitter.sdk.android.tweetui;

public class MediaContentHandler {
    private static IMediaContent iMediaContent;
    public static IMediaContent getMediaInstance(String mimeType) {
        if (mimeType.startsWith("image"))
            iMediaContent =  new ImageMediaContent();

        else if (mimeType.startsWith("video"))
            iMediaContent = new VideoMediaContent();

        else if (mimeType.startsWith("gif"))
            iMediaContent =  new GifMediaContent();
        return iMediaContent;
    }
}
