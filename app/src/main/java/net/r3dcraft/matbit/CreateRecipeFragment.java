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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 03.11.2017.
 */

public class CreateRecipeFragment extends Fragment {
    private static final String TAG = "CreaRecipeFragment";
    private String step_string = "";
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
        View rootView = inflater.inflate(R.layout.fragment_create_recipe, container, false);
        recipeID = getArguments().getString("recipeID");
        step_string = getArguments().getString("string");
        step_time = getArguments().getInt("seconds");
        if (step_time > 0) hasTimer = true;
        step_nr = getArguments().getInt("step_nr");
        total_steps = getArguments().getInt("total_steps");

        if (step_nr == 0) {
            intent = new Intent(getActivity(), TimerService.class);
            getActivity().startService(intent);
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
        }

        viewPager = getActivity().findViewById(R.id.activity_create_recipe_viewpager);
        TextView txt_step_nr = rootView.findViewById(R.id.fragment_create_recipe_step_nr_txt);
        TextView txt_step = rootView.findViewById(R.id.fragment_create_recipe_step_txt);
        ImageButton btn_close = rootView.findViewById(R.id.fragment_create_recipe_step_close_btn);
        ImageButton btn_back = rootView.findViewById(R.id.fragment_create_recipe_step_back_btn);
        btn_action_button = rootView.findViewById(R.id.fragment_create_recipe_action_btn);

        txt_step_nr.setText("Steg " + (step_nr + 1));
        txt_step.setText(step_string);

        if (!hasTimer) {
            btn_action_button.setText(R.string.string_btn_next);
            btn_back.setVisibility(View.INVISIBLE);
        }
        else {
            btn_action_button.setText((step_time / 60) + ":" + (step_time % 60));
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager.setCurrentItem(--currentStep);
                }
            });
        }

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        // TIMER -------------------------------------------------------------------------------
        btn_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasTimer || step_time == 0) nextClicked();
                else timerClicked();
            }
        });
        return rootView;
    }

    public void nextClicked() {
        if (step_nr == total_steps - 1) {
            currentStep = 0;
            getActivity().unregisterReceiver(broadcastReceiver);
            getActivity().stopService(intent);
            // Show dialog box for next step
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setMessage("Du brukte " + (total_time_cooking / 60) + " minutter og " + (total_time_cooking % 60) + " sekunder på denne retten.");
            if (MatbitDatabase.hasUser()) {
                builder.setPositiveButton("Lagre", new DialogInterface.OnClickListener() {
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
                builder.setNegativeButton("Ikke lagre", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
            }
            else {
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                    }
                });
            }
            builder.show();
        }
        else{
            viewPager.setCurrentItem(++currentStep);
        }
    }

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

    public void startTimer(long milliseconds) {
        countDownTimer = new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                milliLeft = millisUntilFinished;
                min = (millisUntilFinished / (1000 * 60));
                sec = ((millisUntilFinished / 1000) - min * 60);
                btn_action_button.setText(Long.toString(min) + ":" + Long.toString(sec));
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
    public void pauseTimer() {
        countDownTimer.cancel();
    }

    private void resumeTimer() {
        startTimer(milliLeft);
    }
}