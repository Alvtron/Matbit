package net.r3dcraft.matbit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserEditActivity extends AppCompatActivity {
    private final static String TAG = "UserEditActivity";

    private EditText edit_text_nickname;
    private EditText edit_text_bio;
    private Button btn_save;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        edit_text_nickname = (EditText) findViewById(R.id.activity_edit_user_nickname_edit_text);
        edit_text_bio = (EditText) findViewById(R.id.activity_edit_user_bio_edit_text);
        btn_save = (Button) findViewById(R.id.activity_edit_user_btn_action);

        if (MatbitDatabase.hasUser()) {
            MatbitDatabase.getCurrentUser().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = new User(dataSnapshot);
                    edit_text_nickname.setText(user.getData().getNickname());
                    edit_text_bio.setText(user.getData().getBio());

                    btn_save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!user.getData().getNickname().equals(edit_text_nickname.getText().toString())) {
                                user.getData().setNickname(edit_text_nickname.getText().toString());
                                user.uploadNickname();
                            }
                            if (!user.getData().getBio().equals(edit_text_bio.getText().toString())) {
                                user.getData().setBio(edit_text_bio.getText().toString());
                                user.uploadBio();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
