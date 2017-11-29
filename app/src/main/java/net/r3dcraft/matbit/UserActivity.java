package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 *
 * The User Activity displays a specific user from Matbit database. It display the user photo and
 * nubmer of recipes and followers, as well as the users bio. More could be added. If  a logged in
 * user visits his/her own profile, an edit icon will be visible in the top right corner.
 * this edit icon will bring the user to the edit user activity.
 *
 * Clicking the number of recipes layout will show a list of all the recipes that user
 * has posted in a new activity.
 */

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    private Context context;
    private String userID;
    private User user;

    private ImageView img_profile_photo;
    private TextView txt_nickname;
    private TextView txt_recipe_count;
    private TextView txt_follower_count;
    private TextView txt_bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        context = this;

        // Get user ID which  tells this activity what user to load from the database.
        Bundle bundle = getIntent().getExtras();
        userID = bundle.getString(getResources().getString(R.string.key_user_id));
        // If the id is empty, destroy the activity.
        if (userID == null || userID.isEmpty()) {
            Toast.makeText(context, R.string.string_sorry_this_user_is_not_home_today, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.activity_user_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set up views
        img_profile_photo = findViewById(R.id.activity_user_profile_photo);
        txt_nickname = findViewById(R.id.activity_user_nickname);
        txt_recipe_count = findViewById(R.id.activity_user_txt_recipes_count);
        txt_follower_count = findViewById(R.id.activity_user_txt_follower_count);
        txt_bio = findViewById(R.id.activity_user_txt_bio);

        // Add onclick listener to number of recipes layout
        findViewById(R.id.activity_user_recipes_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,  UserRecipeListActivity.class);
                intent.putExtra(getString(R.string.key_user_id), userID);
                intent.putExtra("nickname", user.getData().getNickname());
                startActivity(intent);
            }
        });

        // [DOES NOTHING] Add onclick listener to number of followers layout
        findViewById(R.id.activity_user_followers_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Load profile user from Matbit database and set views accordingly.
            MatbitDatabase.user(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = new User(dataSnapshot);
                    txt_nickname.setText(user.getData().getNickname());
                    txt_recipe_count.setText(String.valueOf(user.getData().getNum_recipes()));
                    txt_follower_count.setText(String.valueOf(user.getData().getNum_followers()));
                    txt_bio.setText(user.getData().getBio());

                    MatbitDatabase.userPictureToImageView(user.getId(), context, img_profile_photo);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; add menu items.
        // Add edit button if visitor is user.
        if (userID.equals(MatbitDatabase.getCurrentUserUID())) {
            getMenuInflater().inflate(R.menu.activity_user, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If user clicks edit, open edit user activity.
            case R.id.activity_user_profile_action_edit:
                startActivity(new Intent(context, UserEditActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
