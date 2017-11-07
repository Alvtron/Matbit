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

public class CreateRecipeActivity extends AppCompatActivity {
    private static final String TAG = "CreateRecipeActivity";
    private CreateRecipePagerAdapter createRecipePagerAdapter;
    private ViewPager viewPager;
    private Context context;
    private Bundle bundle;
    private String recipeID;
    private Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_recipe);
        context = this;

        bundle = getIntent().getExtras();
        recipeID = bundle.getString("recipeID");
        if (recipeID == null || recipeID.trim().equals("")) {
            Toast.makeText(context, "Oppskriften er uleselig. Prøv igjen neste gang!", Toast.LENGTH_SHORT).show();
            finish();
        }

        viewPager = (ViewPager) findViewById(R.id.activity_create_recipe_viewpager);
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

        MatbitDatabase.RECIPES.child(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);
                ArrayList<Step> steps = new ArrayList<Step>();
                steps.addAll(recipe.getData().getSteps().values());

                // Create the adapter that will return a fragment for each of the three primary sections of the activity.
                createRecipePagerAdapter = new CreateRecipePagerAdapter(getSupportFragmentManager(), steps);

                // Set up the ViewPager with the sections adapter.
                viewPager.setAdapter(createRecipePagerAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });
    }

    public class CreateRecipePagerAdapter extends FragmentPagerAdapter {

        private ArrayList<CreateRecipeFragment> step_pages = new ArrayList<CreateRecipeFragment>();

        public CreateRecipePagerAdapter(FragmentManager fm, ArrayList<Step> steps) {
            super(fm);

            int step_nr = 0;
            for (Step step : steps) {
                CreateRecipeFragment createRecipeFragment = new CreateRecipeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("string", step.getString());
                bundle.putInt("seconds", step.getSeconds());
                bundle.putInt("step_nr", step_nr++);
                bundle.putInt("total_steps", steps.size());
                bundle.putString("recipeID", recipeID);
                createRecipeFragment.setArguments(bundle);
                step_pages.add(createRecipeFragment);
            }
        }

        @Override
        public Fragment getItem(int position) {
            return step_pages.get(position);
        }

        @Override
        public int getCount() {
            return step_pages.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Steg " + position + 1;
        }
    }
}
