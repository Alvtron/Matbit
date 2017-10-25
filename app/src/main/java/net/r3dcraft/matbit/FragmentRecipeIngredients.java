package net.r3dcraft.matbit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
 * Created by unibl on 24.10.2017.
 */

public class FragmentRecipeIngredients extends Fragment {
    private static final String TAG = "FragmentRecipeIngred";
    private String recipeID;
    private Recipe recipe;
    private ArrayList<String> listItems = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
        recipeID = getArguments().getString("recipeID");
        final ListView myListView = (ListView) rootViewInfo.findViewById(R.id.fragment_recipe_ingredients);

        // Get recipe information
        MatbitDatabase.RECIPES.child(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);

                for (Ingredient ingredient : recipe.getData().getIngredients().values()) {
                    listItems.add(Double.toString(ingredient.getAmount()) + " " + ingredient.getMeasurement() + " " + ingredient.getName());
                }

                adapter = new ArrayAdapter<String>(RecipeActivity.context,
                        android.R.layout.simple_list_item_1,
                        listItems);
                myListView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });
        return rootViewInfo;
    }
}
