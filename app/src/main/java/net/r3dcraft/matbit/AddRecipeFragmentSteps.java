package net.r3dcraft.matbit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class AddRecipeFragmentSteps extends Fragment {
    private static final String TAG = "AddRecipeFragmentSteps";
    private static int currentPage = 0;
    private Context context;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;
    private TextView txt_page_title;
    private ImageView btn_cancel;
    private ImageView btn_back;
    private ImageView btn_next;
    private Button btn_action;

    private EditText editStep;
    private Button btnAddStep;

    private ArrayList<Step> steps;
    private ArrayAdapter<Step> stepAdapter;
    private ListView listViewSteps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_recipe_steps, container, false);
        initialize(view);

        steps = new ArrayList<Step>();

        editStep = (EditText) view.findViewById(R.id.activity_add_recipe_step);

        listViewSteps = (ListView) view.findViewById(R.id.activity_add_recipe_listview_steps);
        btnAddStep = (Button) view.findViewById(R.id.activity_add_recipe_btn_step);
        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                steps.add(new Step(editStep.getText().toString()));
                stepAdapter = new ArrayAdapter<Step>(context, android.R.layout.simple_list_item_1, steps);
                listViewSteps.setAdapter(stepAdapter);
                stepAdapter.notifyDataSetChanged();
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
