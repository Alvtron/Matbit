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
 */

class IngredientAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<Ingredient> data = new ArrayList<Ingredient>();
    private static LayoutInflater inflater = null;

    public IngredientAdapter(Context context, ArrayList<Ingredient> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public IngredientAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.ingredient_item, null);

        final int index = position;
        final ImageView img_icon = (ImageView) view.findViewById(R.id.ingredient_item_icon);
        final TextView txt_amount = (TextView) view.findViewById(R.id.ingredient_item_amount);
        final TextView txt_name = (TextView) view.findViewById(R.id.ingredient_item_name);

        txt_amount.setText(Math.round(data.get(position).getAmount()* 100.0) / 100.0 + " " + data.get(position).getMeasurement());
        txt_name.setText(data.get(position).getName());

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Vil du slette ingrediensen?")
                        .setPositiveButton("Slett", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                data.remove(index);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
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

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.data = ingredients;
        notifyDataSetChanged();
    }

    public void addIngredient(Ingredient ingredient) {
        data.add(ingredient);
        notifyDataSetChanged();
    }
}