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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class AddRecipeFragmentCategory extends Fragment {
    private static final String TAG = "AddRecipeFragmentCategory";
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

    private Spinner spinnerCategory;
    private ArrayAdapter<CharSequence> categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_add_recipe_category, container, false);
        header = (View) view.findViewById(R.id.activity_add_recipe_header_category);
        bottomNavigation = (View) view.findViewById(R.id.activity_add_recipe_bottom_navigator_category);
        viewPager = (ViewPager) getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        txt_page_title = (TextView) header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(pagerAdapter.ADD_CATEGORY_TITLE);
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

        spinnerCategory = (Spinner) view.findViewById(R.id.activity_add_recipe_category);
        categoryAdapter = ArrayAdapter.createFromResource(context, R.array.categories, android.R.layout.simple_spinner_item);
        if (!pagerAdapter.getCategory().equals(null)) {
            int spinnerPosition = categoryAdapter.getPosition(pagerAdapter.getCategory());
            spinnerCategory.setSelection(spinnerPosition);
        }
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pagerAdapter.setCategory(parent.getItemAtPosition(position).toString());
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }
}
