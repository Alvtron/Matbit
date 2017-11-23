package net.r3dcraft.matbit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 08.11.2017.
 *
 * This Ingredient adapter extends the BaseAdapter and keeps track of ingredients.
 */

class IngredientRecipeAdapter extends BaseAdapter {

    private Context context;

    private List<Ingredient> ingredients;
    private static LayoutInflater inflater = null;

    /**
     * Construct an IngredientAdapter that handles a list of Ingredient objects.
     * @param context - The context of the Activity in which the Adapter is used
     * @param data - A list of Ingredient objects
     */
    public IngredientRecipeAdapter(Context context, List<Ingredient> data) {
        this.context = context;
        this.ingredients = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     *
     * @return Number of Ingredient objects in ingredients-set
     */
    @Override
    public int getCount() {
        return ingredients.size();
    }

    /**
     * @param position - Ingredient position in the ingredients-set
     * @return Ingredient at position in ingredients-set
     */
    @Override
    public Object getItem(int position) {
        return ingredients.get(position);
    }

    /**
     * @param position - Ingredient position in the ingredients-set
     * @return Ingredient position in the ingredients-set
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get initialized layout view of a specific Ingredient in the list.
     * @param position - Position of the ingredient in the ingredients-set.
     * @param convertView - The view of the Ingredient layout
     * @param parent - The ViewGroup parent
     * @return a initialized layout view of the specific Ingredient item
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.fragment_recipe_ingredient_item, null);

        final ImageView img_icon = view.findViewById(R.id.fragment_recipe_ingredient_icon);
        final TextView txt_amount = view.findViewById(R.id.fragment_recipe_ingredient_amount);
        final TextView txt_name = view.findViewById(R.id.fragment_recipe_ingredient_name);
        final View stroke = view.findViewById(R.id.fragment_recipe_ingredient_stroke);

        txt_amount.setText(Math.round(ingredients.get(position).getAmount()* 100.0) / 100.0 + " " + ingredients.get(position).getMeasurement());
        txt_name.setText(ingredients.get(position).getName());

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (stroke.getVisibility() == View.INVISIBLE)
                    stroke.setVisibility(View.VISIBLE);
                else
                    stroke.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }

    /**
     * Get the ingredient objects in this adapter.
     * @return ingredients - A list of Ingredient objects
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * Replace data-set with new ingredients
     * @param ingredients - A list of Ingredient objects
     */
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}