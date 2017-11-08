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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class AddRecipeFragmentTime extends Fragment {
    private static final String TAG = "AddRecipeFragmentTime";
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

    private Spinner spinnerTimeHour;
    private Spinner spinnerTimeMinutes;
    private List<Integer> timeValues;
    private ArrayAdapter<Integer> hourAdapter;
    private ArrayAdapter<Integer> minutesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_add_recipe_time, container, false);
        header = (View) view.findViewById(R.id.activity_add_recipe_header_time);
        bottomNavigation = (View) view.findViewById(R.id.activity_add_recipe_bottom_navigator_time);

        viewPager = (ViewPager) getActivity().findViewById(R.id.activity_add_recipe_viewpager);
        pagerAdapter = (AddRecipePagerAdapter) viewPager.getAdapter();
        txt_page_title = (TextView) header.findViewById(R.id.fragment_add_recipe_txt_page_title);
        txt_page_title.setText(pagerAdapter.ADD_TIME_TITLE);
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

        timeValues = new ArrayList<Integer>();
        for (int i = 0; i <= 60; i++) {
            timeValues.add(i);
        }

        spinnerTimeHour = (Spinner) view.findViewById(R.id.activity_add_recipe_time_hours);
        hourAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, timeValues);
        hourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeHour.setAdapter(hourAdapter);
        spinnerTimeHour.setSelection(hourAdapter.getPosition(pagerAdapter.getHour()));

        spinnerTimeHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pagerAdapter.setHour((int)parent.getItemAtPosition(position));
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTimeMinutes = (Spinner) view.findViewById(R.id.activity_add_recipe_time_minutes);
        minutesAdapter = new ArrayAdapter<Integer>(context, android.R.layout.simple_spinner_item, timeValues);
        minutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeMinutes.setAdapter(minutesAdapter);
        spinnerTimeMinutes.setSelection(minutesAdapter.getPosition(pagerAdapter.getHour()));

        spinnerTimeMinutes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pagerAdapter.setMinutes((int)parent.getItemAtPosition(position));
            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }
}
