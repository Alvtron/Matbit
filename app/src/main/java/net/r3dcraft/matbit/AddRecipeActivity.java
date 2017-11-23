package net.r3dcraft.matbit;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 25.10.2017.
 *
 * This activity class function is to add new recipes or change already-existing ones. This
 * includes a ViewPager with a PagerAdapter that keeps track of multiple fragments.
 */
public class AddRecipeActivity extends AppCompatActivity {
    private static final String TAG = "AddRecipeActivity";
    private Context context;
    private User user;
    private ViewPager viewPager;
    private AddRecipePagerAdapter addRecipePagerAdapter;
    private String recipe_id;
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_recipe);
        context = this;

        bundle = getIntent().getExtras();
        if (bundle != null)
            recipe_id = bundle.getString(getResources().getString(R.string.key_recipe_id));

        viewPager = findViewById(R.id.activity_add_recipe_viewpager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Get current user data
        if (MatbitDatabase.hasUser())
            MatbitDatabase.getCurrentUser().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Create user and store it in a user-object
                    user = new User(dataSnapshot);

                    // There is no bundled recipe ID. Create the PagerAdapter without extra parameters.
                    if (bundle == null) {
                        addRecipePagerAdapter = new AddRecipePagerAdapter(getSupportFragmentManager(), user, context);
                        viewPager.setAdapter(addRecipePagerAdapter);
                    }
                    // If there is a bundled recipe ID, retrieve the recipe from the database
                    else if (recipe_id != null) {
                        MatbitDatabase.recipe(recipe_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final Recipe RECIPE = new Recipe(dataSnapshot);

                                // Download recipe photo and store it as a byte-array
                                final long TEN_MEGABYTE = 1024 * 1024 * 10;
                                MatbitDatabase.getRecipePhoto(RECIPE.getId()).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {

                                        // Create the PagerAdapter and include the recipe and the recipe photo
                                        addRecipePagerAdapter = new AddRecipePagerAdapter(getSupportFragmentManager(), user, context, RECIPE, bytes);
                                        viewPager.setAdapter(addRecipePagerAdapter);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Failed to load recipe photo from database. Send message to user and destroy activity
                                        Toast.makeText(context, R.string.string_could_not_load_recipe, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Failed to load recipe from database. Send message to user and destroy activity
                                Log.w(TAG, "loadRecipeFromDatabase: Cancelled", databaseError.toException());
                                Toast.makeText(context, R.string.string_could_not_load_recipe, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to load user from database. Send message to user and destroy activity
                    Log.w(TAG, "loadUserFromDatabase: Cancelled", databaseError.toException());
                    Toast.makeText(context, R.string.string_could_not_load_recipe, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
    }
}