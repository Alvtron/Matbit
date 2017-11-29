package net.r3dcraft.matbit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 *
 * This is one of the fragments in RecipeActivity that is created in the view pager. This displays
 * the recipe ingredients in a list view with a custom ingredient adapter. The user can change the
 * portion size and the UI will update accordingly. If user clicks on one of the ingredients, they
 * will be stroked.
 */

public class RecipeFragmentIngredients extends Fragment {
    private static final String TAG = "FragmentRecipeIngred";
    private String recipeID;
    private Recipe recipe;
    private List<Ingredient> ingredients;
    private TextView txt_portions;
    private SeekBar seekBar;
    private ListView listview;
    private int portions;
    private IngredientRecipeAdapter ingredientRecipeAdapter;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
        ingredients = new ArrayList<>();
        context = getActivity();
        // get bundle with recipe ID/KEY
        recipeID = getArguments().getString(getResources().getString(R.string.key_recipe_id));
        // Text view that displays the portion size
        txt_portions = rootViewInfo.findViewById(R.id.fragment_recipe_ingredients_portions);
        // Setup seek bar that adjusts the portion sizes
        seekBar = rootViewInfo.findViewById(R.id.fragment_recipe_ingredients_seekBar);
        seekBar.setMax(11);
        // Setup list view
        listview = rootViewInfo.findViewById(R.id.fragment_recipe_ingredients_listview);

        return rootViewInfo;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get recipe information
        MatbitDatabase.recipe(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Create recipe
                recipe = new Recipe(dataSnapshot);
                // Collect all ingredients from recipe
                ingredients.addAll(recipe.getData().getIngredients().values());
                // Set portion size and adjust seek bar accordingly
                portions = recipe.getData().getPortions();
                txt_portions.setText(Integer.toString(portions) + " porsjoner");
                seekBar.setProgress(portions - 1);
                // Add ingredients to adapter and set adapter with list view
                ingredientRecipeAdapter = new IngredientRecipeAdapter(context, ingredients);
                listview.setAdapter(ingredientRecipeAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });

        // When user uses the seek bar, adjust the ingredient portion size accordingly and update the
        // ingredient amount.
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                if (ingredientRecipeAdapter != null) {
                    for (Ingredient ingredient : ingredientRecipeAdapter.getIngredients())
                        ingredient.setAmount((ingredient.getAmount() / portions) * (progresValue + 1));
                    portions = progresValue + 1;
                    txt_portions.setText(Integer.toString(portions) + " porsjoner");
                    ingredientRecipeAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
