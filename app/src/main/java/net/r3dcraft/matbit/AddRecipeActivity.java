package net.r3dcraft.matbit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 25.10.2017.
 * Sources: http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample
 */

public class AddRecipeActivity extends AppCompatActivity {
    private static final String TAG = "AddRecipeActivity";
    private Context context;
    private User user;
    private ViewPager viewPager;
    private AddRecipePagerAdapter addRecipePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_recipe);
        context = this;

        viewPager = (ViewPager) findViewById(R.id.activity_add_recipe_viewpager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (MatbitDatabase.hasUser()) {
            MatbitDatabase.getCurrentUser().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = new User(dataSnapshot);
                    // Create the adapter that will return a fragment for each of the three primary sections of the activity.
                    addRecipePagerAdapter = new AddRecipePagerAdapter(getSupportFragmentManager(), user, context);
                    // Set up the ViewPager with the sections adapter.
                    viewPager.setAdapter(addRecipePagerAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "createUserFromDatabase: Cancelled", databaseError.toException());
                }
            });
        }
    }
}