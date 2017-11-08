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
import android.widget.Toast;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class AddRecipeFragmentTitle extends Fragment {
    private static final String TAG = "AddRecipeFragmentTitle";
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

    private EditText editTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_add_recipe_title, container, false);
        header = (View) view.findViewById(R.id.activity_add_recipe_header_title);
        bottomNavigation = (View) view.findViewById(R.id.activity_add_recipe_bottom_navigator_title);

        viewPager = (ViewPager) getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        txt_page_title = (TextView) header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(pagerAdapter.ADD_TITLE_TITLE);
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

        editTitle = (EditText) view.findViewById(R.id.activity_add_recipe_title);
        editTitle.setText(pagerAdapter.getTitle());
        editTitle.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                pagerAdapter.setTitle(editTitle.getText().toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return view;
    }
}
