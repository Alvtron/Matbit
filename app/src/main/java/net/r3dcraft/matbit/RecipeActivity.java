package net.r3dcraft.matbit;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        context = RecipeActivity.this;
        
        bundle = getIntent().getExtras();
        recipeID = bundle.getString("recipeID");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recipeTabAdapter = new RecipeTabAdapter(getSupportFragmentManager(), bundle);

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(recipeTabAdapter);

        tabLayout = (TabLayout) findViewById(R.id.activity_recipe_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.activity_recipe_menu_edit) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}