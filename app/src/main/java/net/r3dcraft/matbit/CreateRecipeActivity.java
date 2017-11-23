package net.r3dcraft.matbit;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 03.11.2017.
 *
 * The CreateRecipeActivity displays any recipes steps in fragments in fullscreen mode. The user
 * can view one step each page. The swipe-gesture has been disabled.
 *
 * The time the user spends in this activity is stored with a timer service. This time is used update
 * the recipe time so that it can more correctly display the expected time one would spend on said
 * recipe.
 */
public class CreateRecipeActivity extends AppCompatActivity {
    private static final String TAG = "CreateRecipeActivity";
    private CreateRecipePagerAdapter createRecipePagerAdapter;
    private ViewPager viewPager;
    private String recipeID;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make activity fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_recipe);
        Context context = this;

        // Get included recipe values from RecipeActivity
        Bundle bundle = getIntent().getExtras();
        recipeID = bundle.getString(getString(R.string.key_recipe_id));
        if (recipeID == null) {
            Toast.makeText(context, R.string.string_this_recipe_is_unreadable, Toast.LENGTH_SHORT).show();
            finish();
        }
        if (recipeID.trim().equals("")) {
            Toast.makeText(context, R.string.string_this_recipe_is_unreadable, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initialize the ViewPager
        viewPager = findViewById(R.id.activity_create_recipe_viewpager);

        // Disable swipe; left and right.
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Load recipe from database
        MatbitDatabase.recipe(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);
                ArrayList<Step> steps = new ArrayList<Step>();
                steps.addAll(recipe.getData().getSteps().values());

                // Create the adapter that will return a fragment for each step.
                createRecipePagerAdapter = new CreateRecipePagerAdapter(getSupportFragmentManager(), steps);
                // Set up the ViewPager with the sections adapter.
                viewPager.setAdapter(createRecipePagerAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });
    }

    public class CreateRecipePagerAdapter extends FragmentPagerAdapter {

        // A list of fragments for each step
        private ArrayList<CreateRecipeFragment> step_pages = new ArrayList<CreateRecipeFragment>();

        public CreateRecipePagerAdapter(FragmentManager fm, ArrayList<Step> steps) {
            super(fm);

            int step_nr = 0;
            // Save a bundle for each step fragment that holds it's step data
            for (Step step : steps) {
                CreateRecipeFragment createRecipeFragment = new CreateRecipeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("string", step.getString());
                bundle.putInt("seconds", step.getSeconds());
                bundle.putInt("step_nr", step_nr++);
                bundle.putInt("total_steps", steps.size());
                bundle.putString(getResources().getString(R.string.key_recipe_id), recipeID);
                createRecipeFragment.setArguments(bundle);
                step_pages.add(createRecipeFragment);
            }
        }

        /**
         * Load current requested step fragment.
         * @param position - The step number / fragment number
         * @return Step-fragment to be displayed
         */
        @Override
        public Fragment getItem(int position) {
            return step_pages.get(position);
        }

        /**
         *
         * @return number of step fragments
         */
        @Override
        public int getCount() {
            return step_pages.size();
        }

        /**
         *
         * @param position - Step number / fragment number
         * @return Step page-title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return "Steg " + position + 1;
        }
    }
}
