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

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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
        setContentView(R.layout.activity_search);

        recyclerView = (RecyclerView)findViewById(R.id.activity_search_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        // Read from Firebase database only once (one snapshot)
        MatbitDatabase.RECIPES.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot recipesSnapshot: dataSnapshot.getChildren()) {
                    Recipe recipe = new Recipe(recipesSnapshot);
                    recipes.add(recipe);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        Collections.sort(recipes, new RecipeDateComparator());
        recipeAdapter = new RecipeAdapter(recipes, SearchActivity.this);
        recyclerView.setAdapter(recipeAdapter);

        spinner_filter = (Spinner) findViewById(R.id.activity_search_filter_spinner);
        spinner_filter.setOnItemSelectedListener(this);
        adapter_filter = ArrayAdapter.createFromResource(this, R.array.filters, android.R.layout.simple_spinner_item);
        adapter_filter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_filter.setAdapter(adapter_filter);

        spinner_category = (Spinner) findViewById(R.id.activity_search_category_spinner);
        spinner_category.setOnItemSelectedListener(this);
        adpater_category = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adpater_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category.setAdapter(adpater_category);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()) {
            case R.id.activity_search_filter_spinner:
                filter = parent.getItemAtPosition(pos).toString();
                switch (filter) {
                    case "Nyeste":
                        Collections.sort(recipes, new RecipeDateComparator());
                    case "Mest sett":
                        Collections.sort(recipes, new RecipeViewsComparator());
                    case "Mest likt":
                        Collections.sort(recipes, new RecipeRatingComparator());
                    case "Tid":
                        Collections.sort(recipes, new RecipeTimeComparator());
                    case "Alfabetisk":
                        Collections.sort(recipes, new RecipeAlphabeticalComparator());
                }

            case R.id.activity_search_category_spinner:
                category = parent.getItemAtPosition(pos).toString();
                if (category != "Alle") {
                    ArrayList<Recipe> tmp = recipes;
                    for (Recipe recipe : recipes)
                        if (recipe.getData().getCategory() == category)
                            tmp.add(recipe);
                    recipeAdapter = new RecipeAdapter(tmp, SearchActivity.this);
                    recyclerView.setAdapter(recipeAdapter);
                }
        }
        recipeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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

    public class RecipeDateComparator implements Comparator<Recipe> {
        @Override
        public int compare(Recipe o1, Recipe o2) {
            return DateTime.stringToDate(o2.getData().getDatetime_created()).compareTo(
                    DateTime.stringToDate(o1.getData().getDatetime_created()));
        }
    }

    public class RecipeViewsComparator implements Comparator<Recipe> {
        @Override
        public int compare(Recipe o1, Recipe o2) {
            return o2.getData().getViews() - (o1.getData().getViews());
        }
    }

    public class RecipeRatingComparator implements Comparator<Recipe> {
        @Override
        public int compare(Recipe o1, Recipe o2) {
            return o2.getData().getRatings().size() - (o1.getData().getRatings().size());
        }
    }

    public class RecipeTimeComparator implements Comparator<Recipe> {
        @Override
        public int compare(Recipe o1, Recipe o2) {
            return o2.getData().getTime() - (o1.getData().getTime());
        }
    }

    public class RecipeAlphabeticalComparator implements Comparator<Recipe> {
        @Override
        public int compare(Recipe o1, Recipe o2) {
            Locale noLocale = new Locale("no", "NO");
            Collator noCollator = Collator.getInstance(noLocale);
            noCollator.setStrength(Collator.PRIMARY);
            return noCollator.compare(o2.getData().getTitle(), o1.getData().getTitle());
        }
    }


}