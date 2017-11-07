package net.r3dcraft.matbit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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

public class AddRecipeFragmentInfo extends Fragment {
    private static final String TAG = "AddRecipeFragmentInfo";
    private static int currentPage = 0;
    private Context context;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;
    private TextView txt_page_title;
    private ImageView btn_cancel;
    private ImageView btn_back;
    private ImageView btn_next;
    private Button btn_action;

    private EditText editInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe_info, container, false);
        initialize(view);

        editInfo = (EditText) view.findViewById(R.id.activity_add_recipe_info);

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
