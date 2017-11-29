package net.r3dcraft.matbit;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 *
 * This is one of the fragments in RecipeActivity that is created in the view pager. This displays
 * the recipe steps in a list view with a custom step adapter. If user clicks on one of the steps,
 * they will be disabled/faded out.
 */

public class RecipeFragmentSteps extends Fragment {
    private static final String TAG = "RecipeFragmentSteps";
    private Context context;
    private String recipeID;
    private Recipe recipe;
    private ListView myListView;

    private StepAdapter stepAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
        context = getActivity();
        // Get recipe ID from RecipeActivity
        recipeID = getArguments().getString(getResources().getString(R.string.key_recipe_id));
        // Setup listview
        myListView = rootViewInfo.findViewById(R.id.fragment_recipe_steps);

        return rootViewInfo;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Get recipe from database
        MatbitDatabase.recipe(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);

                // Add steps to adapter
                stepAdapter = new StepAdapter(context, false);
                for (Step step : recipe.getData().getSteps().values())
                    stepAdapter.addStep(step);

                // Set adapter to list view
                myListView.setAdapter(stepAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });
    }
}
