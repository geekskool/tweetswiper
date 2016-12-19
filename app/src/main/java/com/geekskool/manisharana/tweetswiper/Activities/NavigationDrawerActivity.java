package com.geekskool.manisharana.tweetswiper.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.geekskool.manisharana.tweetswiper.Fragments.AboutUsFragment;
import com.geekskool.manisharana.tweetswiper.Fragments.ComposeTweetFragment;
import com.geekskool.manisharana.tweetswiper.Fragments.LoginAgainFragment;
import com.geekskool.manisharana.tweetswiper.Fragments.TweetListFragment;
import com.geekskool.manisharana.R;
import com.geekskool.manisharana.tweetswiper.SessionUtils;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.User;

public class NavigationDrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private SessionUtils sessionUtils;
    private User currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);
        sessionUtils = new SessionUtils(this);
        currentUser = sessionUtils.getUserDetails();
        if (currentUser == null)
            getUser();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        RelativeLayout navHeaderView = null;

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (navigationView != null) {
            selectDrawerItem(navigationView.getMenu().getItem(0));
            navHeaderView = (RelativeLayout) navigationView.getHeaderView(0);
        }
        setUpNavigationDrawer(navigationView);
        populateNavHeader(navHeaderView, currentUser);
    }

    private void getUser() {
        Session session = sessionUtils.getUserSessionDetails();
        Twitter.getApiClient(session).getAccountService().verifyCredentials(true, false, false, new Callback<User>() {

            @Override
            public void success(Result<User> result) {
                currentUser = result.data;
                sessionUtils.saveUserDetails(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Crashlytics.logException(exception);
                currentUser = null;
            }
        });
    }

    private void populateNavHeader(RelativeLayout navHeaderView, User currentUser) {
        ImageView profileBannerImg = (ImageView) navHeaderView.findViewById(R.id.image_view_profile_banner_img);
        ImageView usrImg = (ImageView) navHeaderView.findViewById(R.id.image_view_user_img);
        TextView usrScreenName = (TextView) navHeaderView.findViewById(R.id.tv_user_screen_name);
        TextView usrName = (TextView) navHeaderView.findViewById(R.id.tv_user_name);

        if (currentUser == null) return;

        if (currentUser.screenName != null)
            usrScreenName.setText(UserUtils.formatScreenName(currentUser.screenName));

        if (currentUser.name != null)
            usrName.setText(currentUser.name);

        Picasso picasso = Picasso.with(this);
        if (currentUser.profileImageUrlHttps != null) {
            String url = UserUtils.getProfileImageUrlHttps(currentUser, UserUtils.AvatarSize.BIGGER);
            picasso.load(url).placeholder(R.drawable.ic_tw_default_user_img).into(usrImg);
        }
        if (currentUser.profileImageUrlHttps != null) {
            String bannerUrl = currentUser.profileBannerUrl + "/mobile";
            picasso.load(bannerUrl).into(profileBannerImg);
        }
    }

    private void selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass;

        fragmentClass = getFragmentClass(item);

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        item.setChecked(true);
        drawerLayout.closeDrawers();

    }

    private Class getFragmentClass(MenuItem item) {
        Class fragmentClass = null;
        TwitterSession userSession = sessionUtils.getUserSessionDetails();

        switch (item.getItemId()) {

            case R.id.nav_home:
                if (userSession == null)
                    fragmentClass = LoginAgainFragment.class;
                else
                    fragmentClass = TweetListFragment.class;
                break;

            case R.id.nav_send_tweet:
                if (!item.isChecked()) {
                    if (userSession == null)
                        fragmentClass = LoginAgainFragment.class;
                    else
                        fragmentClass = ComposeTweetFragment.class;
                }
                break;

            case R.id.nav_about_us:
                if (!item.isChecked()) {
                    fragmentClass = AboutUsFragment.class;
                }
                break;

            case R.id.nav_logout:
                if (!item.isChecked()) {
                    sessionUtils.removeUserSessionDetails();
                    fragmentClass = LoginAgainFragment.class;
                }
                break;

            default:
                fragmentClass = TweetListFragment.class;
        }
        return fragmentClass;
    }

    private void setUpNavigationDrawer(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.findFragmentById(R.id.content_frame).onActivityResult(requestCode, resultCode, data);
        }
    }
}
