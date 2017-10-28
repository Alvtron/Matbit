package net.r3dcraft.matbit;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        context = this;
        bundle = getIntent().getExtras();
        userID = bundle.getString("userID");
    }

    @Override
    protected void onStart() {
        super.onStart();
        MatbitDatabase.USERS.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = new User(dataSnapshot);
                Toast.makeText(context, user.getData().getNickname(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
}
