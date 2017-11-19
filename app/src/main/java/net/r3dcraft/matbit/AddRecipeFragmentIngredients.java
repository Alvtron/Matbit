package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 *
 * This is one of the fragments initialized in the AddRecipePagerAdapter. This collects an array of
 * Ingredient objects and stores it in the adapter.
 */

public class AddRecipeFragmentIngredients extends Fragment {
    private static final String TAG = "AddRecipeFragmentIngredients";
    private static int currentPage = 0;
    private Context context;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;

    private EditText editAmount;
    private EditText editIngredients;
    private Spinner spinnerMeasurement;
    private ListView listViewIngredients;
    private Button btnAddIngredient;
    private String measurement;
    private ArrayAdapter<CharSequence> measurementAdapter;
    private IngredientAdapter ingredientAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Default layout initialization -----------------------------------------------------------

        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_add_recipe_ingredients, container, false);
        View header = view.findViewById(R.id.activity_add_recipe_header_ingredients);
        View bottomNavigation = view.findViewById(R.id.activity_add_recipe_bottom_navigator_ingredients);

        viewPager = getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        TextView txt_page_title = header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(AddRecipePagerAdapter.ADD_INGREDIENTS_TITLE);
        ImageView btn_cancel = header.findViewById(R.id.fragment_add_recipe_btn_cancel);
        ImageView btn_back = bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_back);
        ImageView btn_next = bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_next);

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

        ImageView btn_delete = header.findViewById(R.id.fragment_add_recipe_btn_delete);
        if (pagerAdapter.getRecipe().getId() == null || pagerAdapter.getRecipe().getId().equals("")) {
            btn_delete.setVisibility(View.GONE);
        }
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show dialog box for next step
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.string_delete_recipe)
                        .setCancelable(false)
                        .setMessage(R.string.string_are_you_sure_you_want_to_delete_this_recipe)
                        .setIcon(R.drawable.icon_delete_black_24dp)
                        .setPositiveButton(R.string.string_delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                MatbitDatabase.deleteRecipe(pagerAdapter.getRecipe().getId());
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
                            }

                        })
                        .setNegativeButton(getResources().getString(R.string.string_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

        // -----------------------------------------------------------------------------------------

        ingredientAdapter = new IngredientAdapter(context, new ArrayList<Ingredient>());

        for (Ingredient ingredient : pagerAdapter.getRecipe().getData().getIngredients().values())
            ingredientAdapter.addIngredient(ingredient);

        ingredientAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                pagerAdapter.getRecipe().getData().getIngredients().clear();
                for (Ingredient ingredient : ingredientAdapter.getIngredients())
                    pagerAdapter.getRecipe().getData().addIngredient(ingredient);
            }
        });

        editAmount = view.findViewById(R.id.activity_add_recipe_ingredient_amount);
        editIngredients = view.findViewById(R.id.activity_add_recipe_ingredient_name);

        listViewIngredients = view.findViewById(R.id.activity_add_recipe_listview_ingredients);
        listViewIngredients.setAdapter(ingredientAdapter);

        spinnerMeasurement = view.findViewById(R.id.activity_add_recipe_ingredient_measurement);
        measurementAdapter = ArrayAdapter.createFromResource(context, R.array.measurements, android.R.layout.simple_spinner_item);
        measurementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMeasurement.setAdapter(measurementAdapter);
        spinnerMeasurement.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                measurement = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAddIngredient = view.findViewById(R.id.activity_add_recipe_btn_ingredient);
        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String course = "Main";
                String ingredient_name = editIngredients.getText().toString();
                Double amount;
                try {
                    amount = Double.parseDouble(editAmount.getText().toString());
                    if (amount <= 0.00)
                        editAmount.setError(getString(R.string.error_number_must_be_bigger_than_zero));
                    else if (amount > 10000.00)
                        editAmount.setError(getString(R.string.error_number_cannot_be_that_big));
                    else if (course.trim().length() < 1)
                        ;
                    else if (ingredient_name.length() < 1 )
                        editIngredients.setError(getString(R.string.error_its_empty_here));
                    else {
                        Ingredient ingredient = new Ingredient(course, ingredient_name, amount, measurement);
                        ingredientAdapter.addIngredient(ingredient);
                        spinnerMeasurement.setSelection(0);
                        editIngredients.setText("");
                        editAmount.setText("");
                        editIngredients.clearFocus();
                        editAmount.clearFocus();
                        // Hide keyboard
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(editAmount.getWindowToken(), 0);
                            imm.hideSoftInputFromWindow(editIngredients.getWindowToken(), 0);
                        }
                    }
                }
                catch (NumberFormatException e) {
                    editAmount.setError(getString(R.string.error_its_empty_here));
                }
            }
        });

        return view;
    }
}
