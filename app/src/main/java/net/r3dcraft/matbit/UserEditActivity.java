package net.r3dcraft.matbit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class UserEditActivity extends AppCompatActivity {
    private final static String TAG = "UserEditActivity";
    private Context context;
    private Toolbar toolbar;
    private EditText edit_text_nickname;
    private EditText edit_text_bio;
    private EditText edit_text_gender;
    private ImageView img_user_photo;
    private Button btn_save;
    private User user;

    private boolean new_user_photo = false;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1457;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1459;
    private String userChosenTask;

    private ByteArrayOutputStream stream;
    private byte[] user_photo_in_bytes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        context = this;

        toolbar = findViewById(R.id.activity_edit_user_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        img_user_photo = findViewById(R.id.activity_edit_user_profile_photo);
        edit_text_nickname = findViewById(R.id.activity_edit_user_nickname_edit_text);
        edit_text_bio = findViewById(R.id.activity_edit_user_bio_edit_text);
        edit_text_gender = findViewById(R.id.activity_edit_user_gender_edit_text);
        btn_save = findViewById(R.id.activity_edit_user_btn_action);

        img_user_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        if (MatbitDatabase.hasUser()) {
            MatbitDatabase.getCurrentUser().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = new User(dataSnapshot);
                    MatbitDatabase.currentUserPictureToImageView(context, img_user_photo);
                    edit_text_nickname.setText(user.getData().getNickname());
                    edit_text_bio.setText(user.getData().getBio());
                    edit_text_gender.setText(user.getData().getGender());

                    btn_save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (edit_text_nickname.getText().toString().equals("")) {
                                edit_text_nickname.setError(getResources().getString(R.string.error_its_empty_here));
                                return;
                            }
                            else if (!user.getData().getNickname().equals(edit_text_nickname.getText().toString())) {
                                user.getData().setNickname(edit_text_nickname.getText().toString());
                                user.uploadNickname();
                                for (Map.Entry<String, String> recipe : user.getData().getRecipes().entrySet())
                                    MatbitDatabase.recipeUserNickname(recipe.getKey()).setValue(user.getData().getNickname());
                            }
                            if (!user.getData().getBio().equals(edit_text_bio.getText().toString())) {
                                user.getData().setBio(edit_text_bio.getText().toString());
                                user.uploadBio();
                            }
                            if (!user.getData().getGender().equals(edit_text_gender.getText().toString())) {
                                user.getData().setGender(edit_text_gender.getText().toString());
                                user.uploadGender();
                            }

                            if (new_user_photo) {
                                UploadTask uploadTask = MatbitDatabase.getUserPhoto(user.getId()).putBytes(user_photo_in_bytes);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(context, R.string.error_could_not_upload_photo, Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        finish();
                                    }
                                });
                            }
                            else {
                                finish();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(context, R.string.string_this_page_cannot_load_at_this_moment, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        else {
            Toast.makeText(context, getResources().getString(R.string.error_something_went_wrong), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stream != null) {
            user_photo_in_bytes = stream.toByteArray();
            new_user_photo = true;
        }
        if (user_photo_in_bytes != null) {
            img_user_photo.setImageBitmap(BitmapFactory.decodeByteArray(
                    user_photo_in_bytes,
                    0,
                    user_photo_in_bytes.length)
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChosenTask.equals(getString(R.string.string_take_picture)))
                        cameraIntent();
                    else if(userChosenTask.equals(getString(R.string.string_choose_picture)))
                        galleryIntent();
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {
                getString(R.string.string_take_picture),
                getString(R.string.string_choose_picture),
                getString(R.string.string_cancel)
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.string_add_photo);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = PermissionUtility.checkExternalStoragePermission(context);

                if (items[item].equals(getString(R.string.string_take_picture))) {
                    userChosenTask = getString(R.string.string_take_picture);
                    if(result)
                        cameraIntent();
                } else if (items[item].equals(getString(R.string.string_choose_picture))) {
                    userChosenTask = getString(R.string.string_choose_picture);
                    if(result)
                        galleryIntent();
                } else if (items[item].equals(getString(R.string.string_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.string_choose_picture)), SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null)
                try {
                    adjustBitmap((Bitmap) data.getExtras().get("data"));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
        }

        else if (requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    adjustBitmap(MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), data.getData()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void adjustBitmap(Bitmap source_image) {
        int MAX_WIDTH = 1080, MAX_HEIGHT = 1080;
        int width = source_image.getWidth();
        int height = source_image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = MAX_WIDTH;
            height = (int) (width / bitmapRatio);
        } else {
            height = MAX_HEIGHT;
            width = (int) (height * bitmapRatio);
        }

        Bitmap bitmap_recipe_photo = Bitmap.createScaledBitmap(source_image, width, height, true);
        stream = new ByteArrayOutputStream();
        bitmap_recipe_photo.compress(Bitmap.CompressFormat.PNG, 50, stream);
    }
}
