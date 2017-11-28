package net.r3dcraft.matbit;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 27.10.2017.
 *
 * This is a application class that sets defaults each time the app opens. I use this class to set
 * persistence mode for Google Firebase, a mode that stores database data locally. If the user looses
 * internet-connection, the app will display the local database data. This also reduces the users
 * internet-data usage.
 *
 * MatbitApplication is also used to access app resources.
 */

public class MatbitApplication extends Application {

    protected static MatbitApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Set Google Firebase Persistence Mode
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    /**
     * Get app resources.
     * @return app resources.
     */
    public static Resources resources() {
        return instance.getResources();
    }
}
