package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
 *
 * This is one of the fragments initialized in the AddRecipePagerAdapter. This collects a number
 * and stores it in the adapter.
 */

public class AddRecipeFragmentPortions extends Fragment {
    private static final String TAG = "AddRecipeFragmentPortions";
    private static int currentPage = 0;
    private Context context;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;

    private EditText editPortions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Default layout initialization -----------------------------------------------------------

        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_add_recipe_portions, container, false);
        View header = view.findViewById(R.id.activity_add_recipe_header_portions);
        View bottomNavigation = view.findViewById(R.id.activity_add_recipe_bottom_navigator_portions);
        viewPager = getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        TextView txt_page_title = header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(AddRecipePagerAdapter.ADD_PORTIONS_TITLE);
        ImageView btn_cancel = header.findViewById(R.id.fragment_add_recipe_btn_cancel);
        ImageView btn_back = bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_back);
        bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_next).setVisibility(View.INVISIBLE);
        Button btn_action = bottomNavigation.findViewById(R.id.fragment_add_recipe_btn_action);
        btn_action.setVisibility(View.VISIBLE);

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerAdapter.createRecipe();
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

        editPortions = view.findViewById(R.id.activity_add_recipe_portions);
        if (pagerAdapter.getRecipe().hasPortions())
            editPortions.setText(Integer.toString(pagerAdapter.getRecipe().getData().getPortions()));
        editPortions.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                try {
                    int portions = Integer.parseInt(editPortions.getText().toString());
                    if (portions <= 0)
                        editPortions.setError("Oisann! Dette er for lavt!");
                    else if (portions > 12)
                        editPortions.setError("Oisann! Dette er for høyt");
                    else
                        pagerAdapter.getRecipe().getData().setPortions(portions);
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
