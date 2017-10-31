package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 09.10.2017
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "WallActivity";

    private Toolbar toolbar;
    private ImageView featured_recipe_photo;
    private NewsFeedAdapter newsFeedAdapter;
    private LinearLayoutManager llm;
    private ArrayList<NewsFeed> newsFeedList;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private View header;
    private ImageView user_photo;
    private TextView user_name;
    private TextView user_email;
    private BottomNavigationView bottomNavigationView;
    private Context context;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Featured recipe
        featured_recipe_photo = (ImageView) findViewById(R.id.content_main_featured_recipe_photo);

        // Feed
        mRecyclerView = (RecyclerView)findViewById(R.id.content_main_recyclerview_feed);
        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        newsFeedList = new ArrayList<NewsFeed>();
        newsFeedList.add(new NewsFeed());
        newsFeedList.add(new NewsFeed());
        newsFeedList.add(new NewsFeed());

        newsFeedAdapter = new NewsFeedAdapter(newsFeedList, MainActivity.this);
        mRecyclerView.setAdapter(newsFeedAdapter);

        // Navigation bar
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        user_photo = (ImageView) header.findViewById(R.id.activity_main_user_photo);
        user_name = (TextView) header.findViewById(R.id.activity_main_user_name);
        user_email = (TextView) header.findViewById(R.id.activity_main_user_email);

        // Bottom navigation
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_luck:
                                return true;
                            case R.id.navigation_add:
                                startActivity(new Intent(MainActivity.this, AddRecipeActivity.class));
                                return true;
                            case R.id.navigation_search:
                                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                                return true;
                        }
                        return false;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        user_name.setText(MatbitDatabase.USER.getDisplayName());
        user_email.setText(MatbitDatabase.USER.getEmail());

        MatbitDatabase.recipePictureToImageView("KT4NTZTzFduj3DNLQgg", MainActivity.this, featured_recipe_photo);
        MatbitDatabase.currentUserPictureToImageView(MainActivity.this, user_photo);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            MatbitDatabase.goToUser(context, MatbitDatabase.USER.getUid());
        } else if (id == R.id.nav_feed) {
            startActivity(new Intent(MainActivity.this, FeedActivity.class));
        } else if (id == R.id.nav_find_user) {
            startActivity(new Intent(MainActivity.this, FindUserActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_about) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
