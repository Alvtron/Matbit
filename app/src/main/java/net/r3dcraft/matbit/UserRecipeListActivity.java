package net.r3dcraft.matbit;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 29.11.2017.
 *
 * The UserRecipeListActivity displays all recipes created by the specified user. It includes a
 * recycler view that lists all the recipes. The recycler is set with a UserRecipeAdapter which sorts
 * the recipes in a SortList with a specified comparator.
 *
 * The user can search recipes by using the search icon in the top right corner, as well as filtering
 * all the recipes on specific recipe data or categories.
 *
 * The recycle view is nested in a swipe refresh layout and when swiped down, the list is updated
 * with the database.
 */
public class UserRecipeListActivity extends AppCompatActivity {
    private final String TAG = "UserRecipeListActivity";
    Context context;
    Toolbar toolbar;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private UserRecipeAdapter recipeAdapter;
    private String userID;
    private boolean userIsAuthor = false;
    private String current_filter = "";
    private String current_category = "";
    private String searchString = "";
    Spinner spinner_filter;
    Spinner spinner_category;
    ArrayAdapter<CharSequence> adapter_filter;
    ArrayAdapter<CharSequence> adapter_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_recipe_list);
        context = this;

        // Get bundle extras
        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString(getString(R.string.key_user_id));

        // Check if current user is author
        userIsAuthor = userID.equals(MatbitDatabase.getCurrentUserUID());

        // Set activity title
        if (userIsAuthor)
            setTitle(R.string.title_my_recipes);
        else {
            String userNickname = bundle.getString("nickname");
            String title = String.format(getString(R.string.format_user_recipes), userNickname);
            setTitle(title);
        }

        // Set up toolbar
        toolbar = findViewById(R.id.activity_search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Setup recycler view with swipe refresh layout
        swipeRefreshLayout = findViewById(R.id.activity_search_swipeRefreshLayout);
        recyclerView = findViewById(R.id.activity_search_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recipeAdapter = new UserRecipeAdapter(this, Recipe.DATE_COMPARATOR_DESC, userIsAuthor);
        recyclerView.setAdapter(recipeAdapter);

        // setup filter spinner
        spinner_filter = findViewById(R.id.activity_search_filter_spinner);
        adapter_filter = ArrayAdapter.createFromResource(this, R.array.filters, R.layout.search_spinner_item);
        adapter_filter.setDropDownViewResource(R.layout.search_dropdown_item);
        spinner_filter.setAdapter(adapter_filter);

        // setup category spinner
        spinner_category = findViewById(R.id.activity_search_category_spinner);
        adapter_category = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.search_spinner_item);
        adapter_category.setDropDownViewResource(R.layout.search_dropdown_item);
        spinner_category.setAdapter(adapter_category);

        // disable both spinners until recipes are loaded from database
        spinner_filter.setEnabled(false);
        spinner_category.setEnabled(false);

        // filter spinner on click listener. Change comparator in adapter and refresh recycler
        spinner_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_filter = parent.getItemAtPosition(position).toString();

                switch (current_filter) {
                    case "Nyeste":
                        recipeAdapter = new UserRecipeAdapter(context, Recipe.DATE_COMPARATOR_DESC, userIsAuthor);
                        recyclerView.setAdapter(recipeAdapter);
                        updateRecipeAdapter();
                        break;
                    case "Mest sett":
                        recipeAdapter = new UserRecipeAdapter(context, Recipe.VIEWS_COMPARATOR_DESC, userIsAuthor);
                        recyclerView.setAdapter(recipeAdapter);
                        updateRecipeAdapter();
                        break;
                    case "Mest likt":
                        recipeAdapter = new UserRecipeAdapter(context, Recipe.RATING_COMPARATOR_DESC, userIsAuthor);
                        recyclerView.setAdapter(recipeAdapter);
                        updateRecipeAdapter();
                        break;
                    case "Tid":
                        recipeAdapter = new UserRecipeAdapter(context, Recipe.TIME_COMPARATOR_ASC, userIsAuthor);
                        recyclerView.setAdapter(recipeAdapter);
                        updateRecipeAdapter();
                        break;
                    case "Alfabetisk":
                        recipeAdapter = new UserRecipeAdapter(context, Recipe.ALPHABETICAL_COMPARATOR_ASC, userIsAuthor);
                        recyclerView.setAdapter(recipeAdapter);
                        updateRecipeAdapter();
                        break;
                    default:
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // category spinner on click listener. remove any recipes that doesn't have the selected category
        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_category = parent.getItemAtPosition(position).toString();
                updateRecipeAdapter();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // On swipe to refresh listener: Load recipes from database.
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                spinner_filter.setEnabled(false);
                spinner_category.setEnabled(false);
                loadRecipes();
            }
        });
    }

    /**
     * Load recipes on start
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        loadRecipes();
    }

    /**
     * Load recipes from database and store them to a list. Enable filter and category spinner.
     */
    private void loadRecipes() {
        recipes.clear();
        recipeAdapter.clear();
        MatbitDatabase.getUserRecipes(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot recipesSnapshot : dataSnapshot.getChildren()) {
                    MatbitDatabase.recipe(recipesSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, "kek");
                            Recipe recipe = new Recipe(dataSnapshot, true);
                            recipes.add(recipe);
                            updateRecipeAdapter();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "loadRecipe:onCancelled", databaseError.toException());
                        }
                    });
                }
                spinner_filter.setEnabled(true);
                spinner_category.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadUserRecipe:onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * Update recipe list with current filter and category.
     */
    private void updateRecipeAdapter() {
        for (Recipe recipe : recipes) {
            if ((current_category.equals("Alle") || recipe.getData().getCategory().equals(current_category))
                    && (StringUtility.search(searchString, recipe.getData().getTitle())))
                recipeAdapter.add(recipe);
            else
                recipeAdapter.remove(recipe);
        }
    }

    /**
     * Clear recipe list on stop.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        recipes.clear();
    }

    /**
     * Go back to last activity if back arrow is pressed.
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Handle search queue in toolbar.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);

        // Retrieve the searchView and plug it into searchManager
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        if (searchManager != null)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateRecipeAdapter();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchString = query;
                updateRecipeAdapter();
                return true;
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                return false;
            }
        });

        return true;
    }
}