package net.r3dcraft.matbit;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 27.10.2017.
 */

public class MatbitApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
    }
}
