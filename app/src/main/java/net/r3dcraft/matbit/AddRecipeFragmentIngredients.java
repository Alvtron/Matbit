package net.r3dcraft.matbit;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class AddRecipeFragmentIngredients extends Fragment {
    private static final String TAG = "AddRecipeFragmentIngredients";
    private static int currentPage = 0;
    private Context context;
    private View view;
    private View header;
    private View bottomNavigation;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;
    private TextView txt_page_title;
    private ImageView btn_cancel;
    private ImageView btn_back;
    private ImageView btn_next;

    private EditText editAmount;
    private EditText editIngrediens;
    private Spinner spinnerMeasurement;
    private ListView listViewIngredients;
    private Button btnAddIngredient;
    private String measurement;
    private ArrayAdapter<CharSequence> measurementAdapter;
    private IngredientAdapter ingredientAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_add_recipe_ingredients, container, false);
        header = (View) view.findViewById(R.id.activity_add_recipe_header_ingredients);
        bottomNavigation = (View) view.findViewById(R.id.activity_add_recipe_bottom_navigator_ingredients);

        viewPager = (ViewPager) getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        txt_page_title = (TextView) header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(pagerAdapter.ADD_INGREDIENTS_TITLE);
        btn_cancel = (ImageView) header.findViewById(R.id.fragment_add_recipe_btn_cancel);
        btn_back = (ImageView) bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_back);
        btn_next = (ImageView) bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_next);

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

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
            }
        });

        // -----------------------------------------------------------------------------------------

        ingredientAdapter = new IngredientAdapter(context, pagerAdapter.getIngredients());
        ingredientAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                pagerAdapter.setIngredients(ingredientAdapter.getIngredients());
            }
        });

        editAmount = (EditText) view.findViewById(R.id.activity_add_recipe_ingredient_amount);
        editIngrediens = (EditText) view.findViewById(R.id.activity_add_recipe_ingredient_name);

        listViewIngredients = (ListView) view.findViewById(R.id.activity_add_recipe_listview_ingredients);
        listViewIngredients.setAdapter(ingredientAdapter);

        spinnerMeasurement = (Spinner) view.findViewById(R.id.activity_add_recipe_ingredient_measurement);
        measurementAdapter = ArrayAdapter.createFromResource(context, R.array.measurements, android.R.layout.simple_spinner_item);
        measurementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeasurement.setAdapter(measurementAdapter);
        spinnerMeasurement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                measurement = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAddIngredient = (Button) view.findViewById(R.id.activity_add_recipe_btn_ingredient);
        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String course = "Main";
                String ingredient_name = editIngrediens.getText().toString();
                Double amount;
                try {
                    amount = Double.parseDouble(editAmount.getText().toString());
                    if (amount <= 0.00)
                        editAmount.setError("Oisann! Tallet må være større enn null!");
                    else if (amount > 10000.00)
                        editAmount.setError("Oisann! Tallet kan ikke være så stort!");
                    else if (course.trim().length() < 1)
                        ;
                    else if (ingredient_name.length() < 1 )
                        editIngrediens.setError("Oisann! Her var det tomt!");
                    else {
                        Ingredient ingredient = new Ingredient(course, ingredient_name, amount, measurement);
                        ingredientAdapter.addIngredient(ingredient);
                        spinnerMeasurement.setSelection(0);
                        editIngrediens.setText("");
                        editAmount.setText("");
                        editIngrediens.clearFocus();
                        editAmount.clearFocus();
                        // Hide keyboard
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(editAmount.getWindowToken(), 0);
                            imm.hideSoftInputFromWindow(editIngrediens.getWindowToken(), 0);
                        }
                    }
                }
                catch (NumberFormatException e) {
                    editAmount.setError("Oisann! Her var det tomt!");
                }
            }
        });

        return view;
    }
}
