package com.example.manisharana.twitterclient.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.manisharana.twitterclient.Fragments.AboutUsFragment;
import com.example.manisharana.twitterclient.Fragments.ComposeTweetFragment;
import com.example.manisharana.twitterclient.Fragments.LoginAgainFragment;
import com.example.manisharana.twitterclient.Fragments.TweetListFragment;
import com.example.manisharana.twitterclient.R;
import com.example.manisharana.twitterclient.TweetUtils;

public class NavigationDrawerActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (navigationView != null) {
            selectDrawerItem(navigationView.getMenu().getItem(0));
        }
        setUpNavigationDrawer(navigationView);
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
