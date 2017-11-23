package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 03.11.2017.
 *
 * This is a single fragment that displays a step. This also includes a countdown-timer-function
 * that is used when a step has time. When the timer ends, the user can continue to the next step.
 */

public class CreateRecipeFragment extends Fragment {
    private static final String TAG = "CreateRecipeFrag";
    private int step_time = 0;
    private boolean hasTimer = false;
    private int step_nr = 0;
    private int total_steps = 0;
    private String recipeID;
    private ViewPager viewPager;
    private Button btn_action_button;
    private CountDownTimer countDownTimer;
    private long milliLeft = 0;
    private long min = 0;
    private long sec = 0;
    private char timerState = 'S';
    private static int currentStep = 0;
    private static Intent intent;
    private static long total_time_cooking = 0;

    // Update total time counted in a timer service with a BroadcastReceiver
    private static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            total_time_cooking = intent.getIntExtra("time", 0);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        viewPager.setCurrentItem(currentStep);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize layout view
        View rootView = inflater.inflate(R.layout.fragment_create_recipe, container, false);

        // Get bundled step-data
        recipeID = getArguments().getString(getResources().getString(R.string.key_recipe_id));
        String step_string = getArguments().getString("string");
        step_nr = getArguments().getInt("step_nr");
        total_steps = getArguments().getInt("total_steps");
        step_time = getArguments().getInt("seconds");

        // If the step has time, enable the timer-function
        if (step_time > 0) hasTimer = true;

        // if this is the first step, start the background timer service
        if (step_nr == 0) {
            intent = new Intent(getActivity(), TimerService.class);
            getActivity().startService(intent);
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
        }

        // Initialize layouts
        viewPager = getActivity().findViewById(R.id.activity_create_recipe_viewpager);
        TextView txt_step_nr = rootView.findViewById(R.id.fragment_create_recipe_step_nr_txt);
        TextView txt_step = rootView.findViewById(R.id.fragment_create_recipe_step_txt);
        ImageView btn_close = rootView.findViewById(R.id.fragment_create_recipe_step_close_btn);
        ImageView btn_back = rootView.findViewById(R.id.fragment_create_recipe_step_back_btn);
        btn_action_button = rootView.findViewById(R.id.fragment_create_recipe_action_btn);

        txt_step_nr.setText(String.format(getResources().getString(R.string.format_step_nr), (step_nr + 1)));
        txt_step.setText(step_string);

        if (!hasTimer) {
            // This step has no timer. Set the button text accordingly
            btn_action_button.setText(R.string.string_btn_next);
            btn_back.setVisibility(View.INVISIBLE);
        }
        else {
            // This step has timer. Set the button text accordingly and attach a listener
            btn_action_button.setText(String.format(getResources().getString(R.string.format_hours_minutes), (step_time / 60), (step_time % 60)));
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(--currentStep);
                }
            });
        }

        // Destroy the activity if the user click the cancel button
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        // TIMER -----------------------------------------------------------------------------------
        btn_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasTimer || step_time == 0) nextClicked();
                else timerClicked();
            }
        });
        return rootView;
    }

    /**
     * Loads next step-fragment if there is any.
     *
     * If this is the final fragment (and user is logged in), saved the total time counted by the timer service and prompt
     * the user with a dialog. Ask the user if the user wants to store the time. Destroy activity.
     */
    public void nextClicked() {
        if (step_nr == total_steps - 1) {
            // This is the final step.
            currentStep = 0;
            getActivity().unregisterReceiver(broadcastReceiver);
            getActivity().stopService(intent);
            // Show dialog box with total time spent and two choices; save or not save time.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setMessage(String.format(getString(R.string.format_create_recipe_total_time_display), (total_time_cooking / 60), (total_time_cooking % 60)));
            if (MatbitDatabase.hasUser()) {
                // User is logged in
                builder.setPositiveButton(getString(R.string.string_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MatbitDatabase.recipe(recipeID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Recipe recipe = new Recipe(dataSnapshot);
                                recipe.changeTimeAverage((int) total_time_cooking / 60);
                                getActivity().finish();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w(TAG, "createRecipeFromDatabase: Cancelled", databaseError.toException());
                            }
                        });
                    }
                });
                builder.setNegativeButton(R.string.string_do_not_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
            }
            else {
                // User is not logged in. Do not save time.
                builder.setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
            }
            builder.show();
        }
        else{
            // This is not the final step. Load next step-fragment.
            viewPager.setCurrentItem(++currentStep);
        }
    }

    /**
     * Handle what to happen when timer is clicked. If the timer finishes, enable next step button.
     * Onclick - timer active: Pause the timer and store the remaining time.
     * Onclick - timer stopped: Start the timer if there are any remaining time.
     */
    public void timerClicked() {
        if (timerState == 'S') {
            timerState = 'P';
            startTimer(step_time * 1000);
        } else if (timerState == 'P') {
            timerState = 'R';
            pauseTimer();
        } else if (timerState == 'R') {
            timerState = 'S';
            resumeTimer();
        }
    }

    /**
     * Start a timer that counts down time.
     *
     * @param milliseconds - Amount of time (in milliseconds) to count down
     */
    public void startTimer(long milliseconds) {
        countDownTimer = new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                milliLeft = millisUntilFinished;
                min = (millisUntilFinished / (1000 * 60));
                sec = ((millisUntilFinished / 1000) - min * 60);
                btn_action_button.setText(String.format("%s:%s", Long.toString(min), Long.toString(sec)));
            }

            public void onFinish() {
                if (step_nr == total_steps - 1)
                    btn_action_button.setText(R.string.string_btn_finished);
                else
                    btn_action_button.setText(R.string.string_btn_next);
                step_time = 0;
            }
        }.start();
    }

    /**
     * Pause the timer
     */
    public void pauseTimer() {
        countDownTimer.cancel();
    }

    /**
     * Resume the timer
     */
    private void resumeTimer() {
        startTimer(milliLeft);
    }
}