package net.r3dcraft.matbit;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 04.11.2017.
 * Sourced from: https://stackoverflow.com/questions/19741477/timer-in-background
 *
 * The TimerService class extends service and takes the time in as a background operation. I use this
 * in the create recipe fragment to time how long the user 'creates' the recipe. This data is used
 * to calculate a new average cook time for a recipe (if user agree to it).
 */

public class TimerService extends Service {

    private Intent intent;
    public static final String BROADCAST_ACTION = "net.r3dcraft.matbit.TimerService";

    private Handler handler = new Handler();
    private long initial_time;
    long timeInMilliseconds = 0L;

    @Override
    public void onCreate() {
        super.onCreate();
        initial_time = SystemClock.uptimeMillis();
        intent = new Intent(BROADCAST_ACTION);
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

    }

    /**
     * Send updates to UI.
     */
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 1000); // 1 seconds
        }
    };

    /**
     * Display logging info.
     */
    private void DisplayLoggingInfo() {
        timeInMilliseconds = SystemClock.uptimeMillis() - initial_time;
        int timer = (int) timeInMilliseconds / 1000;
        intent.putExtra("time", timer);
        sendBroadcast(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(sendUpdatesToUI);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}