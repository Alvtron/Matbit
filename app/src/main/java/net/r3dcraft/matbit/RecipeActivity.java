package net.r3dcraft.matbit;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 09.10.2017.
 */

public class RecipeActivity extends AppCompatActivity {

    private static String TAG = "RecipeActivity";
    private static Context context;
    private Toolbar toolbar;
    private RecipeTabAdapter recipeTabAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Recipe recipe;
    private String recipeID;
    private String authorID;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        context = RecipeActivity.this;
        
        bundle = getIntent().getExtras();
        recipeID = bundle.getString("recipeID");
        authorID = bundle.getString("authorID");
        if (recipeID == null || recipeID.trim().equals("")) {
            Toast.makeText(context, "Denne oppskriften er uleselig. Huff!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up toolbar
        toolbar = (Toolbar) findViewById(R.id.activity_recipe_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recipeTabAdapter = new RecipeTabAdapter(getSupportFragmentManager(), bundle);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.activity_main_container);
        viewPager.setAdapter(recipeTabAdapter);

        tabLayout = (TabLayout) findViewById(R.id.activity_recipe_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (authorID.equals(MatbitDatabase.getCurrentUserID()))
            getMenuInflater().inflate(R.menu.activity_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.activity_recipe_action_edit) {
            Toast.makeText(context, "[Ikke ferdig]", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}