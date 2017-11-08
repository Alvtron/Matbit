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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class AddRecipeFragmentSteps extends Fragment {
    private static final String TAG = "AddRecipeFragmentSteps";
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

    private EditText editStep;
    private Button btnAddStep;

    private StepAdapter stepAdapter;
    private ListView listViewSteps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_add_recipe_steps, container, false);
        header = (View) view.findViewById(R.id.activity_add_recipe_header_steps);
        bottomNavigation = (View) view.findViewById(R.id.activity_add_recipe_bottom_navigator_steps);

        viewPager = (ViewPager) getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        txt_page_title = (TextView) header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(pagerAdapter.ADD_STEPS_TITLE);
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

        editStep = (EditText) view.findViewById(R.id.activity_add_recipe_step);

        stepAdapter = new StepAdapter(context, pagerAdapter.getSteps());
        stepAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                pagerAdapter.setSteps(stepAdapter.getStepList());
            }
        });

        listViewSteps = (ListView) view.findViewById(R.id.activity_add_recipe_listview_steps);
        listViewSteps.setAdapter(stepAdapter);

        btnAddStep = (Button) view.findViewById(R.id.activity_add_recipe_btn_step);
        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editStep.getText().toString();
                if (text.trim().length() < 1)
                    editStep.setError("Oisann! Her var det tomt!");
                else if (text.length() > 500 )
                    editStep.setError("Oisann! Steget kan ikke overstige 500 ord!");
                else {
                    Step step = new Step(text);
                    stepAdapter.addStep(step);
                    editStep.setText("");
                    editStep.clearFocus();
                    // Hide keyboard
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(editStep.getWindowToken(), 0);
                }
            }
        });

        return view;
    }
}
