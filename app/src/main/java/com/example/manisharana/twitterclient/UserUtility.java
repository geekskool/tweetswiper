package com.example.manisharana.twitterclient;

import android.support.v4.util.LruCache;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

public class UserUtility {
    LruCache<String, User> userCache;
    User user;

    public UserUtility(){
        userCache = new LruCache<>(1);
    }

    public User getDetails() {
        User cachedUser = userCache.get("Current User");
        if (cachedUser != null) return cachedUser;
        return fetchUserDetails(Twitter.getSessionManager().getActiveSession());
    }

    private User fetchUserDetails(TwitterSession session) {

        Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false, new Callback<User>() {

            @Override
            public void success(Result<User> result) {
                userCache.put("Current User",result.data);
                user = result.data;
            }

            @Override
            public void failure(TwitterException exception) {
                Log.i("UserUtility", "Error in getting user details");
                user = null;
            }

        });
        return user;
    }

}
