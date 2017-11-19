package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 09.10.2017
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static String TAG = "MainActivity";
    private Context context;
    private NewsFeedAdapter newsFeedAdapter;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Toolbar toolbar = findViewById(R.id.activity_main_content_toolbar);
        setSupportActionBar(toolbar);

        // Feed
        RecyclerView mRecyclerView = findViewById(R.id.activity_main_content_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        newsFeedAdapter = new NewsFeedAdapter(context, NewsFeed.DATE_COMPARATOR_DESC);
        mRecyclerView.setAdapter(newsFeedAdapter);

        // Navigation bar
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.activity_main_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View drawer_header = navigationView.getHeaderView(0);
        ImageView user_photo = drawer_header.findViewById(R.id.activity_main_drawer_user_photo);
        TextView user_name = drawer_header.findViewById(R.id.activity_main_drawer_user_name);
        TextView user_email = drawer_header.findViewById(R.id.activity_main_drawer_user_email);
        TextView log_in_message = drawer_header.findViewById(R.id.activity_main_drawer_log_in_message);
        ImageView down_arrow = drawer_header.findViewById(R.id.activity_main_drawer_icon_arrow);
        ConstraintLayout navigation_drawer_header_layout = drawer_header.findViewById(R.id.activity_main_drawer_header_layout);
        navigation_drawer_header_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SignInActivity.class));
            }
        });

        if (MatbitDatabase.hasUser()) {
            down_arrow.setVisibility(View.INVISIBLE);
            log_in_message.setVisibility(View.GONE);
            user_name.setVisibility(View.VISIBLE);
            user_email.setVisibility(View.VISIBLE);
            user_photo.setVisibility(View.VISIBLE);
            user_name.setText(MatbitDatabase.getCurrentUserDisplayName());
            user_email.setText(MatbitDatabase.getCurrentUserEmail());
            MatbitDatabase.currentUserPictureToImageView(context, user_photo);
        }

        // Bottom navigation buttons
        View view_random_recipe = findViewById(R.id.activity_main_bottom_navigation_view_random_recipe);
        view_random_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatbitDatabase.goToRandomRecipe(context);
            }
        });

        View view_add_recipe = (View) findViewById(R.id.activity_main_bottom_navigation_view_add_recipe);
        view_add_recipe.setOnClickListener(new View.OnClickListener() {
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

        View view_search_recipe = (View) findViewById(R.id.activity_main_bottom_navigation_view_search_recipe);
        view_search_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });

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
