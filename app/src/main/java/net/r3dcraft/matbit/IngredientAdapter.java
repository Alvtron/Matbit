package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 08.11.2017.
 *
 * This Ingredient adapter extends the BaseAdapter and keeps track of ingredients.
 */

class IngredientAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<Ingredient> data = new ArrayList<Ingredient>();
    private static LayoutInflater inflater = null;

    /**
     * Construct an IngredientAdapter that handles a list of Ingredient objects.
     * @param context - The context of the Activity in which the Adapter is used
     * @param data - A list of Ingredient objects
     */
    public IngredientAdapter(Context context, ArrayList<Ingredient> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     *
     * @return Number of Ingredient objects in data-set
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * @param position - Ingredient position in the data-set
     * @return Ingredient at position in data-set
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * @param position - Ingredient position in the data-set
     * @return Ingredient position in the data-set
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get initialized layout view of a specific Ingredient in the list.
     * @param position - Position of the ingredient in the data-set.
     * @param convertView - The view of the Ingredient layout
     * @param parent - The ViewGroup parent
     * @return a initialized layout view of the specific Ingredient item
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.ingredient_item, null);

        final int index = position;
        final ImageView img_icon = view.findViewById(R.id.ingredient_item_icon);
        final TextView txt_amount = view.findViewById(R.id.ingredient_item_amount);
        final TextView txt_name = view.findViewById(R.id.ingredient_item_name);

        txt_amount.setText(Math.round(data.get(position).getAmount()* 100.0) / 100.0 + " " + data.get(position).getMeasurement());
        txt_name.setText(data.get(position).getName());

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.string_do_you_want_to_delete_this_ingredient)
                        .setPositiveButton(R.string.string_delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                data.remove(index);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.string_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.show();
            }
        });
        return view;
    }

    public ArrayList<Ingredient> getIngredients() {
        return data;
    }

    /**
     * Replace current data-set with a new one.
     * @param ingredients - A list of Ingredient objects
     */
    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.data = ingredients;
        notifyDataSetChanged();
    }

    /**
     * Add a new ingredient to the data-set.
     * @param ingredient - An Ingredient object
     */
    public void addIngredient(Ingredient ingredient) {
        data.add(ingredient);
        notifyDataSetChanged();
    }
}