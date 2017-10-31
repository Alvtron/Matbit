package net.r3dcraft.matbit;

import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    private Context context;
    private Bundle bundle;
    private String userID;
    private User user;
    Toolbar toolbar;

    private ImageView img_profile_photo;
    private TextView txt_nickname;
    private TextView txt_recipe_count;
    private TextView txt_follower_count;
    private TextView txt_bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;

        bundle = getIntent().getExtras();
        userID = bundle.getString("userID");
        if (userID == null || userID.trim() == "") {
            Toast.makeText(context, "Denne brukeren er ikke hjemme i dag.", Toast.LENGTH_SHORT).show();
            finish();
        }

        toolbar = (Toolbar) findViewById(R.id.activity_user_profile_toolbar);
        setSupportActionBar(toolbar);

        img_profile_photo = (ImageView) findViewById(R.id.activity_user_profile_photo);
        txt_nickname = (TextView) findViewById(R.id.activity_user_nickname);
        txt_recipe_count = (TextView) findViewById(R.id.activity_user_txt_recipes_count);
        txt_follower_count = (TextView) findViewById(R.id.activity_user_txt_follower_count);
        txt_bio = (TextView) findViewById(R.id.activity_user_txt_bio);
    }



    @Override
    protected void onStart() {
        super.onStart();

        MatbitDatabase.USERS.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = new User(dataSnapshot);
                txt_nickname.setText(user.getData().getNickname());
                txt_recipe_count.setText(Integer.toString(user.getData().getNum_recipes()));
                txt_follower_count.setText(Integer.toString(user.getData().getNum_followers()));
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
        // Inflate the menu; add menu items. Add edit button if visitor is user.
        if (userID.equals(MatbitDatabase.getCurrentUserID())) {
            getMenuInflater().inflate(R.menu.activity_user, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_user_profile_action_edit:
                Toast.makeText(context, "splashy effects!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
