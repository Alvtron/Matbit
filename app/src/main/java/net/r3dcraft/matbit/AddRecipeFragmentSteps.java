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
 *
 * This is one of the fragments initialized in the AddRecipePagerAdapter. This collects a array of
 * Step objects and stores it in the adapter.
 */

public class AddRecipeFragmentSteps extends Fragment {
    private static final String TAG = "AddRecipeFragmentSteps";
    private Context context;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;

    private EditText editStep;
    private Button btnAddStep;
    private StepAdapter stepAdapter;
    private ListView listViewSteps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Default layout initialization -----------------------------------------------------------

        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_add_recipe_steps, container, false);
        View header = view.findViewById(R.id.activity_add_recipe_header_steps);
        View bottomNavigation = view.findViewById(R.id.activity_add_recipe_bottom_navigator_steps);

        viewPager = getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        TextView txt_page_title = header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(AddRecipePagerAdapter.ADD_STEPS_TITLE);
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
        if (pagerAdapter.getRecipe().getId() == null || pagerAdapter.getRecipe().getId().isEmpty()) {
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

        editStep = view.findViewById(R.id.activity_add_recipe_step);

        stepAdapter = new StepAdapter(context, new ArrayList<Step>(), true);

        for (Step step : pagerAdapter.getRecipe().getData().getSteps().values())
            stepAdapter.addStep(step);

        stepAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                pagerAdapter.getRecipe().getData().getSteps().clear();
                for (Step step : stepAdapter.getStepList())
                    pagerAdapter.getRecipe().getData().addStep(step);
            }
        });

        listViewSteps = view.findViewById(R.id.activity_add_recipe_listview_steps);
        listViewSteps.setAdapter(stepAdapter);

        btnAddStep = view.findViewById(R.id.activity_add_recipe_btn_step);
        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editStep.getText().toString();
                if (text.trim().length() < 1)
                    editStep.setError(getString(R.string.error_its_empty_here));
                else if (text.length() > 500 )
                    editStep.setError(getString(R.string.error_step_cant_be_longer_than_500_words));
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
