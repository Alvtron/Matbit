package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 09.10.2017
 *
 * MainActivity is the front page of this App. It connects every navigational path that the user can
 * take. For navigation it includes a navigation drawer and 3 buttons at the bottom.
 *
 * MainActivity is inspired by Facebook's and Twitter's wall, and features a RecyclerList with
 * dynamic content in the form of NewsFeed items. These Newsfeed items contains a thumbnail, a
 * title, a subtitle, main-body and an optional photo.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static String TAG = "MainActivity";
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsFeedAdapter newsFeedAdapter;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // Refresh auth, user, database and storage references.
        MatbitDatabase.refresh();

        // Initialize toolbar
        toolbar = findViewById(R.id.activity_main_content_toolbar);
        setSupportActionBar(toolbar);

        // Setup RecyclerView
        swipeRefreshLayout = findViewById(R.id.activity_main_swipeRefreshLayout);
        RecyclerView mRecyclerView = findViewById(R.id.activity_main_content_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        // Setup NewsFeedAdapter
        newsFeedAdapter = new NewsFeedAdapter(context, NewsFeed.DATE_COMPARATOR_DESC);
        mRecyclerView.setAdapter(newsFeedAdapter);

        // Setup NavigationView
        setupNavigationView();

        // Setup bottom navigation
        setupBottomNavigation();

        // Create NewsFeed objects and add them to RecyclerList
        loadNewsFeedItems();

        // On swipe to refresh listener: Load recipes from database.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNewsFeedItems();
            }
        });
    }

    /**
     * Initialize navigation drawer and add information about current logged in user, if any.
     */
    private void setupNavigationView() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.activity_main_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View drawer_header = navigationView.getHeaderView(0);
        // User profile shown in the navigation drawer
        ImageView user_photo = drawer_header.findViewById(R.id.activity_main_drawer_user_photo);
        TextView user_name = drawer_header.findViewById(R.id.activity_main_drawer_user_name);
        TextView user_email = drawer_header.findViewById(R.id.activity_main_drawer_user_email);
        TextView log_in_message = drawer_header.findViewById(R.id.activity_main_drawer_log_in_message);
        // User profile layout in navigation drawer. If clicked, go to SignInActivity.
        ConstraintLayout navigation_drawer_header_layout = drawer_header.findViewById(R.id.activity_main_drawer_header_layout);
        navigation_drawer_header_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SignInActivity.class));
            }
        });
        // Adjust layout design if there is a user logged in
        if (MatbitDatabase.hasUser()) {
            log_in_message.setVisibility(View.GONE);
            user_name.setVisibility(View.VISIBLE);
            user_email.setVisibility(View.VISIBLE);
            user_photo.setVisibility(View.VISIBLE);
            user_name.setText(MatbitDatabase.getCurrentUserDisplayName());
            user_email.setText(MatbitDatabase.getCurrentUserEmail());
            MatbitDatabase.currentUserPictureToImageView(context, user_photo);
        }
    }

    /**
     * Initialize bottom navigation and set onclick listeners
     */
    private void setupBottomNavigation() {
        // Random recipe button
        View view_random_recipe = findViewById(R.id.activity_main_bottom_navigation_view_random_recipe);
        view_random_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MatbitDatabase.goToRandomRecipe(context);
            }
        });

        // Add new recipe button
        View view_add_recipe = findViewById(R.id.activity_main_bottom_navigation_view_add_recipe);
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

        // Search recipe button
        View view_search_recipe = findViewById(R.id.activity_main_bottom_navigation_view_search_recipe);
        view_search_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });
    }

    /**
     * Initialize NewsFeed item with a NewsFeedBuilder and add these to the RecyclerList
     */
    private void loadNewsFeedItems() {
        newsFeedAdapter.empty();

        // If user is logged in, load user from database and create/add NewsFeed object.
        if (MatbitDatabase.hasUser()) {
            MatbitDatabase.user(MatbitDatabase.getCurrentUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    NewsFeedBuilder newsFeedBuilder = new NewsFeedBuilder(context);
                    newsFeedBuilder.setUser(dataSnapshot);
                    NewsFeed new_followers = newsFeedBuilder.newFollowers();
                    if (new_followers != null)
                        newsFeedAdapter.add(new_followers);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadUsers: onCancelled", databaseError.toException());
                }
            });
        }

        // Load all recipes from database
        MatbitDatabase.RECIPES().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                NewsFeedBuilder newsFeedBuilder = new NewsFeedBuilder(context);
                newsFeedBuilder.setRecipes(dataSnapshot);
                NewsFeed recipe_of_the_week = newsFeedBuilder.recipeOfTheWeek();
                NewsFeed most_liked_recipe = newsFeedBuilder.mostLikedRecipe();
                NewsFeed most_popular_recipe = newsFeedBuilder.mostPopularRecipe();
                NewsFeed newest_recipe = newsFeedBuilder.newestRecipe();

                if (recipe_of_the_week != null)
                    newsFeedAdapter.add(recipe_of_the_week);
                if (most_liked_recipe != null)
                    newsFeedAdapter.add(most_liked_recipe);
                if (most_popular_recipe != null)
                    newsFeedAdapter.add(most_popular_recipe);
                if (newest_recipe != null)
                    newsFeedAdapter.add(newest_recipe);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadRecipes: onCancelled", databaseError.toException());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Prepare navigation drawer. Show user profile if user is logged in.
     * @param menu
     * @return prepared menu for navigation drawer
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem profile = navigationView.getMenu().findItem(R.id.nav_profile);
        MenuItem my_recipes = navigationView.getMenu().findItem(R.id.nav_my_recipes);

        if(MatbitDatabase.hasUser()) {
            profile.setVisible(true);
            my_recipes.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Decide what to happen when user clicks top left toolbar icon.
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Decide what to happen when user clicks on items in navigation drawer.
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        // User clicked user profile. Go to current users profile.
        if (id == R.id.nav_profile) {
            MatbitDatabase.goToUser(context, MatbitDatabase.getCurrentUserUID());
        } else if (id == R.id.nav_my_recipes) {
            Intent intent = new Intent(context,  UserRecipeListActivity.class);
            intent.putExtra(getString(R.string.key_user_id), MatbitDatabase.getCurrentUserUID());
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
