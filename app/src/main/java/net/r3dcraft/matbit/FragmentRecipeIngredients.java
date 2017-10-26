package net.r3dcraft.matbit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by unibl on 24.10.2017.
 */

public class FragmentRecipeIngredients extends Fragment {
    private static final String TAG = "FragmentRecipeIngred";
    private String recipeID;
    private Recipe recipe;
    private List<Ingredient> ingredients;
    private ListView listview;
    private SeekBar seekBar;
    private int portions;
    private IngredientAdapter ingredientAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootViewInfo = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
        ingredients = new ArrayList<Ingredient>();
        recipeID = getArguments().getString("recipeID");
        listview = (ListView) rootViewInfo.findViewById(R.id.fragment_recipe_ingredients_listview);
        seekBar = (SeekBar) rootViewInfo.findViewById(R.id.fragment_recipe_ingredients_seekBar);
        seekBar.setMax(12);

        // Get recipe information
        MatbitDatabase.RECIPES.child(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);

                for (Ingredient ingredient : recipe.getData().getIngredients().values()) {
                    ingredients.add(ingredient);
                }
                portions = recipe.getData().getPortions();
                seekBar.setProgress(portions - 1);
                ingredientAdapter = new IngredientAdapter(RecipeActivity.context, ingredients);
                listview.setAdapter(ingredientAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                if (ingredientAdapter != null) {
                    for (Ingredient ingredient : ingredientAdapter.getData())
                        ingredient.setAmount((ingredient.getAmount() / portions) * (progresValue + 1));
                    portions = progresValue + 1;
                    ingredientAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        return rootViewInfo;
    }
}
