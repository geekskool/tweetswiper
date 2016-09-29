package com.example.manisharana.twitterclient.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.manisharana.twitterclient.R;

public class TweetListActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        selectDrawerItem(navigationView.getMenu().findItem(R.id.nav_home));
        setUpNavigationDrawer(navigationView);
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

    private void selectDrawerItem(MenuItem item) {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = (LinearLayout) findViewById(R.id.content_frame);

        switch (item.getItemId()){
            case R.id.nav_home:
                if(!item.isChecked()) {
                    inflater.inflate(R.layout.activity_tweet_list, container);
                }
                break;
            case R.id.nav_send_tweet:
                if(!item.isChecked()) {
                    inflater.inflate(R.layout.activity_compose_tweet, container);
                }
                break;
            case R.id.nav_about_us:
                if(!item.isChecked()) {
                    inflater.inflate(R.layout.activity_about_us, container);
                }
                break;
            case R.id.nav_logout:
                if(!item.isChecked()) {
                   // TweetUtils.removeUserSessionDetails(this);
                  //  inflater.inflate(R.layout.activity_main, container);
                }
                break;
            default:
                inflater.inflate(R.layout.activity_tweet_list,container);
        }

        item.setChecked(true);

        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
