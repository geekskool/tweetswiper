# TweetSwiper

TweetSwiper allows you to swipe through your tweets horizontally,where each tweet covers entire screen. It also lets the swiper tweet, retweet, like and share their tweets. Following are the screenshots: 
<br> <br>
![Tweet](https://github.com/geekskool/tweetswiper/blob/bdb950e10ea7f1a1bc1f983d2453f56f76246fdc/Screenshots/rsz_screenshot_2016-11-18-07-05-35.png?raw=true "Optional Title") &nbsp;&nbsp;&nbsp;&nbsp; ![Tweet](https://github.com/geekskool/tweetswiper/blob/bdb950e10ea7f1a1bc1f983d2453f56f76246fdc/Screenshots/rsz_screenshot_2016-11-18-07-06-05.png?raw=true "Optional Title")  &nbsp;&nbsp;&nbsp;&nbsp; ![Tweet](https://github.com/geekskool/tweetswiper/blob/bdb950e10ea7f1a1bc1f983d2453f56f76246fdc/Screenshots/rsz_screenshot_2016-11-18-13-12-05.png?raw=true "Optional Title") &nbsp;&nbsp;&nbsp;&nbsp; ![Tweet](https://github.com/geekskool/tweetswiper/blob/bdb950e10ea7f1a1bc1f983d2453f56f76246fdc/Screenshots/rsz_screenshot_2016-11-18-21-07-01.png?raw=true "Optional Title")
<br> <br>
![Tweet](https://github.com/geekskool/tweetswiper/blob/bdb950e10ea7f1a1bc1f983d2453f56f76246fdc/Screenshots/rsz_screenshot_2016-11-18-07-05-25.png?raw=true "Optional Title") 
#Install
1. git clone the TweetSwiper repo.
2. get Twitter API key and Secret Key and replace TWITTER_KEY and TWITTER_SECRET.
3. build and run.

#Code Structure
[MainActivity](geekskool/tweetswiper/blob/master/app/src/main/java/com/example/manisharana/twitterclient/Activities/MainActivity.java)  
[NavigationDrawerActivity](geekskool/tweetswiper/blob/master/app/src/main/java/com/example/manisharana/twitterclient/Activities/NavigationDrawerActivity.java)  
[ComposeTweetFragment](https://github.com/geekskool/tweetswiper/blob/master/tweet-ui/src/main/java/com/twitter/sdk/android/tweetui/CustomTweetViewAdapter.java)  
[TweetListFragment](https://github.com/geekskool/tweetswiper/blob/master/app/src/main/java/com/example/manisharana/twitterclient/Fragments/TweetListFragment.java)  
[TwitterLoginFragment](https://github.com/geekskool/tweetswiper/blob/master/app/src/main/java/com/example/manisharana/twitterclient/Fragments/TwitterLoginFragment.java)  
[CustomTweetViewAdapter](geekskool/tweetswiper/blob/master/tweet-ui/src/main/java/com/twitter/sdk/android/tweetui/CustomTweetViewAdapter.java)  
[MediaEntityAdapter](tweetswiper/tweet-ui/src/main/java/com/twitter/sdk/android/tweetui/MediaEntityAdapter.java )  
[RetweetAction](geekskool/tweetswiper/blob/master/tweet-ui/src/main/java/com/twitter/sdk/android/tweetui/RetweetAction.java)  
[TweetReplyActivity](geekskool/tweetswiper/blob/master/tweet-ui/src/main/java/com/twitter/sdk/android/tweetui/TweetReplyActivity.java)  
[ImageMediaContent](geekskool/tweetswiper/blob/master/tweet-ui/src/main/java/com/twitter/sdk/android/tweetui/ImageMediaContent.java)  
[VideoMediaContent](geekskool/tweetswiper/blob/master/tweet-ui/src/main/java/com/twitter/sdk/android/tweetui/VideoMediaContent.java)  
[MediaContentHandler](geekskool/tweetswiper/blob/master/tweet-ui/src/main/java/com/twitter/sdk/android/tweetui/MediaContentHandler.java)













