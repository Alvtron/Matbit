package net.r3dcraft.matbit;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 27.10.2017.
 */

public class MatbitApplication extends Application {

    protected static MatbitApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);
    }

    public static Resources resources() {
        return instance.getResources();
    }
}
