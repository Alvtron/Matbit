package net.r3dcraft.matbit;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private final String TAG = "SearchActivity";
    private ArrayList<Recipe> recipes = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private String filter = "none";
    private String category = "none";
    Spinner spinner_filter;
    Spinner spinner_category;
    ArrayAdapter<CharSequence> adapter_filter;
    ArrayAdapter<CharSequence> adpater_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_search);

        recyclerView = (RecyclerView)findViewById(R.id.activity_search_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recipeAdapter = new RecipeAdapter(recipes, SearchActivity.this);
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
                        Collections.sort(recipeAdapter.getRecipeList(), new Recipe.RecipeDateComparator());
                    case "Mest sett":
                        Collections.sort(recipeAdapter.getRecipeList(), new Recipe.RecipeViewsComparator());
                    case "Mest likt":
                        Collections.sort(recipeAdapter.getRecipeList(), new Recipe.RecipeRatingComparator());
                    case "Tid":
                        Collections.sort(recipeAdapter.getRecipeList(), new Recipe.RecipeTimeComparator());
                    case "Alfabetisk":
                        Collections.sort(recipeAdapter.getRecipeList(), new Recipe.RecipeAlphabeticalComparator());
                }
                recipeAdapter.notifyDataSetChanged();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                recipeAdapter.setRecipeList(recipes);

                if (category.equals("Alle")) {
                    ArrayList<Recipe> removeObjects = new ArrayList<Recipe>();
                    for (Recipe recipe : recipeAdapter.getRecipeList())
                        if (recipe.getData().getCategory().equals(category))
                            removeObjects.add(recipe);
                    recipeAdapter.getRecipeList().removeAll(removeObjects);
                }
                recipeAdapter.notifyDataSetChanged();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

        MatbitDatabase.RECIPES.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot recipesSnapshot: dataSnapshot.getChildren()) {
                    Recipe recipe = new Recipe(recipesSnapshot);
                    recipes.add(recipe);
                }
                Collections.sort(recipes, new Recipe.RecipeDateComparator());
                recipeAdapter.notifyDataSetChanged();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }
}