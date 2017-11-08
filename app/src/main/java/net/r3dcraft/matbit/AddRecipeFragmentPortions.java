package net.r3dcraft.matbit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class AddRecipeFragmentPortions extends Fragment {
    private static final String TAG = "AddRecipeFragmentPortions";
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
    private Button btn_action;

    private EditText editPortions;
    private Button btnCreateRecipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_add_recipe_portions, container, false);
        header = (View) view.findViewById(R.id.activity_add_recipe_header_portions);
        bottomNavigation = (View) view.findViewById(R.id.activity_add_recipe_bottom_navigator_portions);
        viewPager = (ViewPager) getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        txt_page_title = (TextView) header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(pagerAdapter.ADD_PORTIONS_TITLE);
        btn_cancel = (ImageView) header.findViewById(R.id.fragment_add_recipe_btn_cancel);
        btn_back = (ImageView) bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_back);
        bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_next).setVisibility(View.INVISIBLE);
        btn_action = (Button) bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_action);
        btn_action.setVisibility(View.VISIBLE);

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pagerAdapter.createRecipe())
                    getActivity().finish();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
            }
        });

        // -----------------------------------------------------------------------------------------

        editPortions = (EditText) view.findViewById(R.id.activity_add_recipe_portions);
        if (pagerAdapter.getPortions() > 0)
            editPortions.setText(Integer.toString(pagerAdapter.getPortions()));
        editPortions.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                try {
                    int portions = Integer.parseInt(editPortions.getText().toString());
                    if (portions <= 0)
                        editPortions.setError("Oisann! Dette er for lavt!");
                    else if (portions > 12)
                        editPortions.setError("Oisann! Dette er for høyt");
                    else
                        pagerAdapter.setPortions(portions);
                }
                catch (NumberFormatException e) {
                    editPortions.setError("Oisann! Dette er ikke et gyldig nummer!");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return view;
    }
}
