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

public class SearchActivity extends AppCompatActivity {
    private final String TAG = "SearchActivity";
    Context context;
    Toolbar toolbar;
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private String current_filter = "";
    private String current_category = "";
    private String searchString = "";
    Spinner spinner_filter;
    Spinner spinner_category;
    ArrayAdapter<CharSequence> adapter_filter;
    ArrayAdapter<CharSequence> adpater_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_search);
        context = this;

        // Set up toolbar
        toolbar = findViewById(R.id.activity_search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        swipeRefreshLayout = findViewById(R.id.activity_search_swipeRefreshLayout);
        recyclerView = findViewById(R.id.activity_search_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recipeAdapter = new RecipeAdapter(this, Recipe.DATE_COMPARATOR_DESC);
        recyclerView.setAdapter(recipeAdapter);

        spinner_filter = findViewById(R.id.activity_search_filter_spinner);
        adapter_filter = ArrayAdapter.createFromResource(this, R.array.filters, R.layout.search_spinner_item);
        adapter_filter.setDropDownViewResource(R.layout.search_dropdown_item);
        spinner_filter.setAdapter(adapter_filter);

        spinner_category = findViewById(R.id.activity_search_category_spinner);
        adpater_category = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.search_spinner_item);
        adpater_category.setDropDownViewResource(R.layout.search_dropdown_item);
        spinner_category.setAdapter(adpater_category);

        spinner_filter.setEnabled(false);
        spinner_category.setEnabled(false);

        spinner_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_filter = parent.getItemAtPosition(position).toString();

                switch (current_filter) {
                    case "Nyeste":
                        recipeAdapter = new RecipeAdapter(context, Recipe.DATE_COMPARATOR_DESC);
                        recyclerView.setAdapter(recipeAdapter);
                        updateRecipeAdapter();
                        break;
                    case "Mest sett":
                        recipeAdapter = new RecipeAdapter(context, Recipe.VIEWS_COMPARATOR_DESC);
                        recyclerView.setAdapter(recipeAdapter);
                        updateRecipeAdapter();
                        break;
                    case "Mest likt":
                        recipeAdapter = new RecipeAdapter(context, Recipe.RATING_COMPARATOR_DESC);
                        recyclerView.setAdapter(recipeAdapter);
                        updateRecipeAdapter();
                        break;
                    case "Tid":
                        recipeAdapter = new RecipeAdapter(context, Recipe.TIME_COMPARATOR_ASC);
                        recyclerView.setAdapter(recipeAdapter);
                        updateRecipeAdapter();
                        break;
                    case "Alfabetisk":
                        recipeAdapter = new RecipeAdapter(context, Recipe.ALPHABETICAL_COMPARATOR_ASC);
                        recyclerView.setAdapter(recipeAdapter);
                        updateRecipeAdapter();
                        break;
                    default:
                        break;
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        loadRecipes();
    }

    private void loadRecipes() {
        recipes.clear();
        recipeAdapter.clear();
        MatbitDatabase.RECIPES().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot recipesSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = new Recipe(recipesSnapshot, true);
                    recipes.add(recipe);
                }
                updateRecipeAdapter();
                spinner_filter.setEnabled(true);
                spinner_category.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        recipes.clear();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);

        // Retrieve the SearchView and plug it into SearchManager
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

    private void updateRecipeAdapter() {
        for (Recipe recipe : recipes) {
            if ((current_category.equals("Alle") || recipe.getData().getCategory().equals(current_category))
                    && (StringUtility.search(searchString, recipe.getData().getTitle())))
                recipeAdapter.add(recipe);
            else
                recipeAdapter.remove(recipe);
        }
    }
}