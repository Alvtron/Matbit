package net.r3dcraft.matbit;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
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
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private String filter = "";
    private String category = "";
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
        toolbar = (Toolbar) findViewById(R.id.activity_search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView)findViewById(R.id.activity_search_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recipeAdapter = new RecipeAdapter(this, Recipe.DATE_COMPARATOR_DESC);
        recyclerView.setAdapter(recipeAdapter);

        spinner_filter = (Spinner) findViewById(R.id.activity_search_filter_spinner);
        adapter_filter = ArrayAdapter.createFromResource(this, R.array.filters, android.R.layout.simple_spinner_item);
        adapter_filter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter.setAdapter(adapter_filter);

        spinner_category = (Spinner) findViewById(R.id.activity_search_category_spinner);
        adpater_category = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adpater_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category.setAdapter(adpater_category);

        spinner_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filter = parent.getItemAtPosition(position).toString();

                switch (filter) {
                    case "Nyeste":
                        updateRecipeAdapter();
                    case "Mest sett":
                        updateRecipeAdapter();
                    case "Mest likt":
                        updateRecipeAdapter();
                    case "Tid":
                        updateRecipeAdapter();
                    case "Alfabetisk":
                        updateRecipeAdapter();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                updateRecipeAdapter();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        MatbitDatabase.RECIPES.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot recipesSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = new Recipe(recipesSnapshot);
                    recipes.add(recipe);
                }
                recipeAdapter.replaceAll(recipes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        recipes.clear();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
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
            if ((category.equals("Alle") || recipe.getData().getCategory().equals(category))
                    && (StringTools.search(searchString, recipe.getData().getTitle())))
                recipeAdapter.add(recipe);
            else
                recipeAdapter.remove(recipe);
        }
    }
}