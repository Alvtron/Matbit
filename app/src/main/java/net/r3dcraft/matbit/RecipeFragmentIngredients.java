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
        ingredients = new ArrayList<Ingredient>();
        context = getActivity();
        recipeID = getArguments().getString("recipeID");
        txt_portions = (TextView) rootViewInfo.findViewById(R.id.fragment_recipe_ingredients_portions);
        seekBar = (SeekBar) rootViewInfo.findViewById(R.id.fragment_recipe_ingredients_seekBar);
        seekBar.setMax(11);
        listview = (ListView) rootViewInfo.findViewById(R.id.fragment_recipe_ingredients_listview);

        return rootViewInfo;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Get recipe information
        MatbitDatabase.RECIPES.child(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipe = new Recipe(dataSnapshot);
                for (Ingredient ingredient : recipe.getData().getIngredients().values()) {
                    ingredients.add(ingredient);
                }
                portions = recipe.getData().getPortions();
                txt_portions.setText(Integer.toString(portions) + " porsjoner");
                seekBar.setProgress(portions - 1);
                ingredientRecipeAdapter = new IngredientRecipeAdapter(context, ingredients);
                listview.setAdapter(ingredientRecipeAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                if (ingredientRecipeAdapter != null) {
                    for (Ingredient ingredient : ingredientRecipeAdapter.getData())
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
