package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 *
 * This is one of the fragments initialized in the AddRecipePagerAdapter. This collects a time-string
 * and stores it in the adapter.
 */

public class AddRecipeFragmentTime extends Fragment {
    private static final String TAG = "AddRecipeFragmentTime";
    private Context context;
    private ViewPager viewPager;
    private AddRecipePagerAdapter pagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Default layout initialization -----------------------------------------------------------

        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_add_recipe_time, container, false);
        View header = view.findViewById(R.id.activity_add_recipe_header_time);
        View bottomNavigation = view.findViewById(R.id.activity_add_recipe_bottom_navigator_time);

        viewPager = getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        TextView txt_page_title = header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(AddRecipePagerAdapter.ADD_TIME_TITLE);
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

        // Set timer text
        final TextView txt_time = view.findViewById(R.id.activity_add_recipe_time);
        // If there already is a timer set, display that time. Else display 0:0.
        if (pagerAdapter.getRecipe().hasTime())
            txt_time.setText(String.format(getString(R.string.format_hours_minutes), pagerAdapter.getRecipe().getData().getTime() / 60, pagerAdapter.getRecipe().getData().getTime() % 60));
        else
            txt_time.setText(String.format(getString(R.string.format_hours_minutes), 0, 0));

        // When user click time, a timer picker dialog is shown.
        txt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txt_time.setText(String.format(getString(R.string.format_hours_minutes), selectedHour, selectedMinute));
                        pagerAdapter.getRecipe().getData().setTime(selectedHour * 60 + selectedMinute);
                    }
                }, 0, 0, true);
                timePickerDialog.setTitle(getString(R.string.string_choose_time));
                timePickerDialog.show();
            }
        });
        return view;
    }
}
