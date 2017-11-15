package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
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

import java.util.Random;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 09.10.2017
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static String TAG = "MainActivity";
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
    private ConstraintLayout navigation_drawer_header_layout;

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
        navigationView = (NavigationView) findViewById(R.id.activity_main_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        user_photo = (ImageView) header.findViewById(R.id.activity_main_drawer_user_photo);
        user_name = (TextView) header.findViewById(R.id.activity_main_drawer_user_name);
        user_email = (TextView) header.findViewById(R.id.activity_main_drawer_user_email);
        navigation_drawer_header_layout = (ConstraintLayout) header.findViewById(R.id.activity_main_drawer_header_layout);
        navigation_drawer_header_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SignInActivity.class));
            }
        });

        View try_luck = (View) findViewById(R.id.navigation_luck);
        try_luck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatbitDatabase.RECIPES().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Random random = new Random();
                        int index = random.nextInt((int) dataSnapshot.getChildrenCount());
                        int count = 0;
                        for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                            if (count++ == index) {
                                Recipe recipe = new Recipe(recipeSnapshot, true);
                                MatbitDatabase.gotToRecipe(context, recipe);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        View btn_add_recipe = (View) findViewById(R.id.navigation_add);
        btn_add_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MatbitDatabase.hasUser()) {
                    startActivity(new Intent(context, AddRecipeActivity.class));
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.string_you_must_log_in_to_publish_dish);
                    builder.setPositiveButton(R.string.string_log_in, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(context, SignInActivity.class));
                        }
                    });
                    builder.setNegativeButton(R.string.string_dont_log_in, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.show();
                }
            }
        });

        View btn_search_recipe = (View) findViewById(R.id.navigation_search);
        btn_search_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });

        user_name.setText(MatbitDatabase.getCurrentUserDisplayName());
        user_email.setText(MatbitDatabase.getCurrentUserEmail());
        MatbitDatabase.currentUserPictureToImageView(context, user_photo);

        MatbitDatabase.RECIPES().addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                newsFeedAdapter.empty();
                NewsFeed recipe_of_the_week = new NewsFeed(context);
                NewsFeed most_liked_recipe = new NewsFeed(context);
                NewsFeed newest_recipe = new NewsFeed(context);

                if (recipe_of_the_week.recipeOfTheWeek(dataSnapshot))
                    newsFeedAdapter.add(recipe_of_the_week);
                if (most_liked_recipe.mostLikedRecipe(dataSnapshot))
                    newsFeedAdapter.add(most_liked_recipe);
                if (newest_recipe.newestRecipe(dataSnapshot))
                    newsFeedAdapter.add(newest_recipe);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadRecipes: onCancelled", databaseError.toException());
            }
        });

        if (MatbitDatabase.hasUser()) {
            MatbitDatabase.user(MatbitDatabase.getCurrentUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    NewsFeed new_followers = new NewsFeed(context);
                    if (new_followers.newFollowers(dataSnapshot))
                        newsFeedAdapter.add(new_followers);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadUsers: onCancelled", databaseError.toException());
                }
            });
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem profile = navigationView.getMenu().findItem(R.id.nav_profile);
        MenuItem feed = navigationView.getMenu().findItem(R.id.nav_feed);

        if(MatbitDatabase.hasUser()) {
            profile.setVisible(true);
            feed.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
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
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            MatbitDatabase.goToUser(context, MatbitDatabase.getCurrentUserUID());
        } else if (id == R.id.nav_feed) {
            startActivity(new Intent(context, FeedActivity.class));
        } else if (id == R.id.nav_find_user) {
            startActivity(new Intent(context, FindUserActivity.class));
        } else if (id == R.id.nav_settings) {
            ;
        } else if (id == R.id.nav_about) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
