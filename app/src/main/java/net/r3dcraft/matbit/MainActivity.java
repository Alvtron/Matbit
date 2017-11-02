package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 09.10.2017
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = "WallActivity";
    private Context context;
    private Toolbar toolbar;
    private ImageView featured_recipe_photo;
    private NewsFeedAdapter newsFeedAdapter;
    private LinearLayoutManager llm;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private View header;
    private ImageView user_photo;
    private TextView user_name;
    private TextView user_email;
    private BottomNavigationView bottomNavigationView;

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        toolbar = (Toolbar) findViewById(R.id.activity_main_content_toolbar);
        setSupportActionBar(toolbar);

        // Feed
        mRecyclerView = (RecyclerView)findViewById(R.id.activity_main_content_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        newsFeedAdapter = new NewsFeedAdapter(context, NewsFeed.DATE_COMPARATOR_DESC);
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
                findViewById(R.id.activity_main_content_bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.navigation_luck:
                                return true;
                            case R.id.navigation_add:
                                startActivity(new Intent(context, AddRecipeActivity.class));
                                return true;
                            case R.id.navigation_search:
                                startActivity(new Intent(context, SearchActivity.class));
                                return true;
                        }
                        return false;
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String photo_url = MatbitDatabase.USER.getPhotoUrl().toString();

        newsFeedAdapter.add(new NewsFeed(photo_url, photo_url, "TITTEL1", getResources().getString(R.string.lorem_ipsum), DateUtility.stringToDate("2017-10-22 14:53:25")));
        newsFeedAdapter.add(new NewsFeed(photo_url, photo_url, "TITTEL2", getResources().getString(R.string.lorem_ipsum), DateUtility.stringToDate("2017-11-02 18:08:08")));
        newsFeedAdapter.add(new NewsFeed(photo_url, photo_url, "TITTEL3", getResources().getString(R.string.lorem_ipsum), DateUtility.stringToDate("2017-10-27 08:22:47")));
        newsFeedAdapter.add(new NewsFeed(photo_url, photo_url, "TITTEL4", getResources().getString(R.string.lorem_ipsum), DateUtility.stringToDate("2017-10-30 19:56:23")));
        newsFeedAdapter.add(new NewsFeed(photo_url, photo_url, "TITTEL5", getResources().getString(R.string.lorem_ipsum), DateUtility.stringToDate("2017-11-02 18:36:20")));
        newsFeedAdapter.add(new NewsFeed(photo_url, photo_url, "TITTEL6", getResources().getString(R.string.lorem_ipsum), DateUtility.stringToDate("2017-11-02 18:36:26")));
        newsFeedAdapter.add(new NewsFeed(photo_url, photo_url, "TITTEL7", getResources().getString(R.string.lorem_ipsum), DateUtility.stringToDate("2017-02-22 14:53:25")));
        newsFeedAdapter.add(new NewsFeed(photo_url, photo_url, "TITTEL8", getResources().getString(R.string.lorem_ipsum), DateUtility.stringToDate("2017-01-22 14:53:25")));

        user_name.setText(MatbitDatabase.USER.getDisplayName());
        user_email.setText(MatbitDatabase.USER.getEmail());
        MatbitDatabase.currentUserPictureToImageView(context, user_photo);

        MatbitDatabase.RECIPES.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Date newest_date = null;
                boolean first = true;
                String newest_recipe_id = "";
                for (DataSnapshot recipesSnapshot : dataSnapshot.getChildren()) {
                    Date date = DateUtility.stringToDate(recipesSnapshot.child("datetime_created").getValue(String.class));
                    String recipe_id = recipesSnapshot.getKey();

                    if (first) {
                        newest_date = date;
                        first = false;
                    } else if (date.after(newest_date)) {
                            newest_date = date;
                            newest_recipe_id = recipe_id;
                        }
                }
                //if (!newest_recipe_id.equals(""))
                    //MatbitDatabase.recipePictureToImageView(newest_recipe_id, MainActivity.this, featured_recipe_photo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

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
