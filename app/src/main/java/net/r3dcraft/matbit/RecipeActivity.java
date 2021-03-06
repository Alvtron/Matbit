package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
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
 *
 * The RecipeActivity displays most of the information about a specific recipe. It includes a view
 * pager that holds four fragments: info, ingredients, steps and comments. These are navigated by
 * swiping to right or using the tab layout in the top.
 *
 * If the current signed in user is the author of a recipe, an edit icon will be set visible in the
 * top-right corner.
 */

public class RecipeActivity extends AppCompatActivity {
    private static String TAG = "RecipeActivity";
    private static Context context;
    private Toolbar toolbar;
    private RecipeTabAdapter recipeTabAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String recipeID;
    private String authorID;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        context = RecipeActivity.this;

        bundle = getIntent().getExtras();
        recipeID = bundle.getString(getResources().getString(R.string.key_recipe_id));
        authorID = bundle.getString(getResources().getString(R.string.key_user_id));
        if (recipeID == null || recipeID.isEmpty()) {
            Toast.makeText(context, R.string.string_this_recipe_is_unreadable, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up toolbar
        setUpToolBar();

        // Set up the ViewPager with the sections adapter.
        setUpTabViewLayout();
    }

    private void setUpTabViewLayout() {
        recipeTabAdapter = new RecipeTabAdapter(getSupportFragmentManager(), bundle);

        viewPager = findViewById(R.id.activity_main_container);
        viewPager.setAdapter(recipeTabAdapter);

        tabLayout = findViewById(R.id.activity_recipe_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.icon_info_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.icon_ingredients_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.icon_list_white_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.icon_comment_white_24dp);
    }

    private void setUpToolBar() {
        toolbar = findViewById(R.id.activity_recipe_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (authorID.equals(MatbitDatabase.getCurrentUserUID()))
            getMenuInflater().inflate(R.menu.activity_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.activity_recipe_action_edit) {
            Intent intent = new Intent(context, AddRecipeActivity.class);
            intent.putExtra(getResources().getString(R.string.key_recipe_id), recipeID);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}