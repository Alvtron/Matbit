package net.r3dcraft.matbit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 26.10.2017.
 */

class IngredientRecipeAdapter extends BaseAdapter {

    private Context context;

    private List<Ingredient> data;
    private static LayoutInflater inflater = null;

    public IngredientRecipeAdapter(Context context, List<Ingredient> data) {
        this.context = context;
        this.data = data;
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
            view = inflater.inflate(R.layout.fragment_recipe_ingredient_item, null);

        final ImageView img_icon = (ImageView) view.findViewById(R.id.fragment_recipe_ingredient_icon);
        final TextView txt_amount = (TextView) view.findViewById(R.id.fragment_recipe_ingredient_amount);
        final TextView txt_name = (TextView) view.findViewById(R.id.fragment_recipe_ingredient_name);
        final View stroke = (View) view.findViewById(R.id.fragment_recipe_ingredient_stroke);

        txt_amount.setText(Math.round(data.get(position).getAmount()* 100.0) / 100.0 + " " + data.get(position).getMeasurement());
        txt_name.setText(data.get(position).getName());

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

    public List<Ingredient> getData() {
        return data;
    }

    public void setData(List<Ingredient> data) {
        this.data = data;
    }
}