package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 */

public final class MatbitDatabase {
    private static final String TAG = "MatbitDatabase";
    public static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    public static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    public static final FirebaseStorage STORAGE = FirebaseStorage.getInstance();
    public static final StorageReference RECIPE_PHOTOS = STORAGE.getReference("recipe_photos");
    public static final StorageReference USER_PHOTOS = STORAGE.getReference("user_photos");
    public static final DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();
    public static final DatabaseReference RECIPES = FirebaseDatabase.getInstance().getReference().child("recipes");
    public static final DatabaseReference USERS = FirebaseDatabase.getInstance().getReference().child("users");

    public static String getCurrentUserID() {
        return USER.getUid();
    }

    public static DatabaseReference recipeTitle (final String ID) {
        return RECIPES.child(ID).child("title");
    }

    public static DatabaseReference recipeUser (final String ID) {
        return RECIPES.child(ID).child("user");
    }

    public static DatabaseReference recipeDatetimeCreated (final String ID) {
        return RECIPES.child(ID).child("datetime_created");
    }

    public static DatabaseReference recipeDatetimeUpdated (final String ID) {
        return RECIPES.child(ID).child("datetime_updated");
    }

    public static DatabaseReference recipeTime (final String ID) {
        return RECIPES.child(ID).child("time");
    }

    public static DatabaseReference recipePortions (final String ID) {
        return RECIPES.child(ID).child("portions");
    }

    public static DatabaseReference recipeViews (final String ID) {
        return RECIPES.child(ID).child("views");
    }

    public static DatabaseReference recipeRatings (final String ID) {
        return RECIPES.child(ID).child("ratings");
    }

    public static DatabaseReference recipeComments (final String ID) {
        return RECIPES.child(ID).child("comments");
    }

    public static DatabaseReference recipeSteps (final String ID) {
        return RECIPES.child(ID).child("steps");
    }

    public static DatabaseReference recipeIngredients (final String ID) {
        return RECIPES.child(ID).child("ingredients");
    }

    public static DatabaseReference recipeThumbsUp (final String ID) {
        return RECIPES.child(ID).child("thumbs_up");
    }

    public static DatabaseReference recipeThumbsDown (final String ID) {
        return RECIPES.child(ID).child("thumbs_down");
    }

    public static DatabaseReference recipeInfo (final String ID) {
        return RECIPES.child(ID).child("info");
    }

    public static String uploadNewRecipe(final RecipeData recipeData) {
        String uid = MatbitDatabase.RECIPES.push().getKey();
        RECIPES.child(uid).setValue(recipeData);
        recipeRatings(uid).setValue(recipeData.getRatings());
        recipeComments(uid).setValue(recipeData.getComments());
        recipeSteps(uid).setValue(recipeData.getSteps());
        recipeIngredients(uid).setValue(recipeData.getIngredients());
        return uid;
    }

    public static void uploadNewUser(final UserData userData) {
        USERS.child(USER.getUid()).setValue(userData);
    }

    public static void recipePictureToImageView(final String RECIPE_UID, final Context CONTEXT, final ImageView IMAGE_VIEW) {
        MatbitDatabase.RECIPE_PHOTOS.child(RECIPE_UID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(CONTEXT)
                        .load(uri)
                        .into(IMAGE_VIEW);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "recipePictureToImageView: Could not load recipe photo");
            }
        });
    }

    public static void userPictureToImageView(final String USER_UID, final Context CONTEXT, final ImageView IMAGE_VIEW) {
        MatbitDatabase.USER_PHOTOS.child(USER_UID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(CONTEXT)
                        .load(uri)
                        .into(IMAGE_VIEW);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "userPictureToImageView: Could not load user photo");
                IMAGE_VIEW.setImageResource(R.drawable.icon_profile);
            }
        });
    }

    public static void currentUserPictureToImageView(final Context CONTEXT, final ImageView IMAGE_VIEW) {
        Glide.with(CONTEXT).load(USER.getPhotoUrl()).into(IMAGE_VIEW);
    }

    public static void userNicknameToTextView(final String USER_UID, final TextView TEXT_VIEW) {
        USERS.child(USER_UID).addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TEXT_VIEW.setText(dataSnapshot.child("nickname").getValue(String.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "userNicknameToTextView: onCancelled", databaseError.toException());
            }
        });
    }

    public static void uploadNewUserIfNew() {
        final String UID = USER.getUid();
        final Uri PHOTO_URL = USER.getPhotoUrl();
        USERS.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(UID)) {
                    Log.d(TAG, "uploadNewUserIfNew: Uploading new empty user to database at: " + UID);
                    new UploadUserPhoto().execute(PHOTO_URL);
                    uploadNewUser(UserExamples.NEW_USER_TEMPLATE());
                }
                else {
                    Log.d(TAG, "uploadNewUserIfNew: User " + UID + " already exists");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "uploadNewUserIfNew: onCancelled", databaseError.toException());
            }
        });
    }

    public static void gotToRecipe(final Context CONTEXT, Recipe recipe) {
        recipe.addView();
        Intent intent = new Intent(CONTEXT, RecipeActivity.class);
        intent.putExtra("recipeID", recipe.getId());
        CONTEXT.startActivity(intent);
    }

    public static void goToUser(final Context CONTEXT, final String UID) {
        Intent intent = new Intent(CONTEXT, UserActivity.class);
        intent.putExtra("userID", UID);
        CONTEXT.startActivity(intent);
    }

}
