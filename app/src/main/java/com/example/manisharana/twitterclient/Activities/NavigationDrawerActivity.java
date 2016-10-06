package com.example.manisharana.twitterclient.Activities;

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

import com.example.manisharana.twitterclient.Fragments.AboutUsFragment;
import com.example.manisharana.twitterclient.Fragments.ComposeTweetFragment;
import com.example.manisharana.twitterclient.Fragments.LoginAgainFragment;
import com.example.manisharana.twitterclient.Fragments.TweetListFragment;
import com.example.manisharana.twitterclient.R;
import com.example.manisharana.twitterclient.TweetUtils;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetui.TweetUi;

public class NavigationDrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RelativeLayout navHeaderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Intent intent = getIntent();
        if (navigationView != null) {
            selectDrawerItem(navigationView.getMenu().getItem(0));
            navHeaderView = (RelativeLayout) navigationView.getHeaderView(0);
        }
        User currentUser = (User) intent.getSerializableExtra("Current User");
        if(currentUser == null){
            getUserAndPopulateNavHeader(navHeaderView);
        }else{
            populateNavHeader(navHeaderView,currentUser);
        }
        setUpNavigationDrawer(navigationView);

    }

    private void getUserAndPopulateNavHeader(RelativeLayout navHeaderView) {

    }

    private void populateNavHeader(RelativeLayout navHeaderView, User currentUser) {
        ImageView profileBannerImg = (ImageView)navHeaderView.findViewById(R.id.image_view_profile_banner_img);

        ImageView usrImg = (ImageView)navHeaderView.findViewById(R.id.image_view_user_img);
        TextView usrDesc = (TextView) navHeaderView.findViewById(R.id.tv_user_desc);
        TextView usrScreenName = (TextView) navHeaderView.findViewById(R.id.tv_user_screen_name);
        TextView usrName = (TextView) navHeaderView.findViewById(R.id.tv_user_name);


        Picasso imageLoader = TweetUi.getInstance().getImageLoader();
        if (imageLoader == null) return;

        final String url;
        if (currentUser.profileImageUrlHttps == null) {
            url = null;
        } else {
            url = UserUtils.getProfileImageUrlHttps(currentUser, UserUtils.AvatarSize.REASONABLY_SMALL);
        }

        imageLoader.load(url).placeholder(R.drawable.ic_tw_default_user_img).into(usrImg);

        if(currentUser.description != null){
            usrDesc.setText(currentUser.description);
        }

        if(currentUser.screenName !=null){

            usrScreenName.setText(UserUtils.formatScreenName(currentUser.screenName));
        }

        if(currentUser.name !=null){
            usrName.setText(currentUser.name);
        }


    }

    private void selectDrawerItem(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        Long userSession = TweetUtils.getUserSessionDetails(this);

        switch (item.getItemId()) {
            case R.id.nav_home:
                if (userSession == 0 || userSession == null) {
                    fragmentClass = LoginAgainFragment.class;
                } else {
                    fragmentClass = TweetListFragment.class;
                }
                break;
            case R.id.nav_send_tweet:
                if (!item.isChecked()) {
                    if (userSession == 0 || userSession == null) {
                        fragmentClass = LoginAgainFragment.class;
                    } else {
                        fragmentClass = ComposeTweetFragment.class;
                    }
                }
                break;
            case R.id.nav_about_us:
                if (!item.isChecked()) {
                    fragmentClass = AboutUsFragment.class;
                }
                break;
            case R.id.nav_logout:
                if (!item.isChecked()) {
                    TweetUtils.removeUserSessionDetails(this);
                    fragmentClass = LoginAgainFragment.class;
                }
                break;
            default:
                fragmentClass = TweetListFragment.class;
        }

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

    private void setUpNavigationDrawer(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
