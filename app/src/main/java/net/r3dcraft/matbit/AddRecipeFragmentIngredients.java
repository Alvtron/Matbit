package net.r3dcraft.matbit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class AddRecipeFragmentIngredients extends Fragment {
    private static final String TAG = "AddRecipeFragmentIngredients";
    private static int currentPage = 0;
    private Context context;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;
    private TextView txt_page_title;
    private ImageView btn_cancel;
    private ImageView btn_back;
    private ImageView btn_next;
    private Button btn_action;

    private EditText editAmount;
    private EditText editIngrediens;
    private Spinner spinnerMeasurement;
    private ListView listViewIngredients;
    private Button btnAddIngredient;
    private String measurement;
    private ArrayAdapter<CharSequence> measurementAdapter;
    private ArrayList<Ingredient> ingredients;
    private IngredientAdapter ingredientAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe_ingredients, container, false);
        initialize(view);

        ingredients = new ArrayList<Ingredient>();
        ingredientAdapter = new IngredientAdapter(context, ingredients);

        editAmount = (EditText) view.findViewById(R.id.activity_add_recipe_ingredient_amount);
        editIngrediens = (EditText) view.findViewById(R.id.activity_add_recipe_ingredient_name);

        spinnerMeasurement = (Spinner) view.findViewById(R.id.activity_add_recipe_ingredient_measurement);
        measurementAdapter = ArrayAdapter.createFromResource(context, R.array.measurements, android.R.layout.simple_spinner_item);
        measurementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeasurement.setAdapter(measurementAdapter);
        spinnerMeasurement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                measurement = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listViewIngredients = (ListView) view.findViewById(R.id.activity_add_recipe_listview_ingredients);
        btnAddIngredient = (Button) view.findViewById(R.id.activity_add_recipe_btn_ingredient);
        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ingredient ingredient = new Ingredient("Main", editIngrediens.getText().toString(), Double.parseDouble(editAmount.getText().toString()), measurement);
                ingredientAdapter.getData().add(ingredient);
                listViewIngredients.setAdapter(ingredientAdapter);
                ingredientAdapter.notifyDataSetChanged();

            }
        });

        return view;
    }

    private void initialize(View view) {
        viewPager = (ViewPager) getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        context = getActivity();

        txt_page_title = (TextView) view.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(pagerAdapter.getPageTitle(viewPager.getCurrentItem()));
        btn_cancel = (ImageView) view.findViewById(R.id.fragment_add_recipe_btn_cancel);
        btn_back = (ImageView) view.findViewById(R.id.fragment_add_recipe_btn_back);
        btn_next = (ImageView) view.findViewById(R.id.fragment_add_recipe_btn_next);
        btn_action = (Button) view.findViewById(R.id.fragment_add_recipe_btn_action);
        if (viewPager.getCurrentItem() == pagerAdapter.getCount())
            btn_action.setVisibility(View.INVISIBLE);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });

        if (viewPager.getCurrentItem() == 0)
            btn_back.setVisibility(View.INVISIBLE);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
            }
        });
    }
}
