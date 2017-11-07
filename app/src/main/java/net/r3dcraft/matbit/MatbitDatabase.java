package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        return RECIPES.child(ID).child("step_time");
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

    public static String uploadNewRecipe(final RecipeData RECIPE_DATA) {
        if (RECIPE_DATA == null) {
            Log.d(TAG, "uploadNewRecipe: RECIPE_DATA is not initialized (is null)");
            return null;
        }
        String uid = MatbitDatabase.RECIPES.push().getKey();
        RECIPES.child(uid).setValue(RECIPE_DATA);
        recipeRatings(uid).setValue(RECIPE_DATA.getRatings());
        recipeComments(uid).setValue(RECIPE_DATA.getComments());
        recipeSteps(uid).setValue(RECIPE_DATA.getSteps());
        recipeIngredients(uid).setValue(RECIPE_DATA.getIngredients());
        return uid;
    }

    public static boolean uploadNewUser(final UserData USER_DATA) {
        if (USER_DATA == null) {
            Log.d(TAG, "uploadNewUser: USER_DATA is not initialized (is null)");
            return false;
        }
        USERS.child(USER.getUid()).setValue(USER_DATA);
        return true;
    }

    public static boolean recipePictureToImageView(final String RECIPE_UID, final Context CONTEXT, final ImageView IMAGE_VIEW) {
        if (RECIPE_UID == null || RECIPE_UID.trim().equals("")) {
            Log.d(TAG, "recipePictureToImageView: RECIPE_UID is not initialized (is null)");
            return true;
        } else if (CONTEXT == null) {
            Log.d(TAG, "recipePictureToImageView: CONTEXT is not initialized (is null)");
            return true;
        } else if (IMAGE_VIEW == null) {
            Log.d(TAG, "recipePictureToImageView: IMAGE_VIEW is not initialized (is null)");
            return true;
        }
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
        return true;
    }

    public static boolean userPictureToImageView(final String USER_UID, final Context CONTEXT, final ImageView IMAGE_VIEW) {
        if (USER_UID == null || USER_UID.trim().equals("")) {
            Log.d(TAG, "userPictureToImageView: USER_UID is not initialized (is null)");
            return true;
        } else if (CONTEXT == null) {
            Log.d(TAG, "userPictureToImageView: CONTEXT is not initialized (is null)");
            return true;
        } else if (IMAGE_VIEW == null) {
            Log.d(TAG, "userPictureToImageView: IMAGE_VIEW is not initialized (is null)");
            return true;
        }

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
        return true;
    }

    public static void currentUserPictureToImageView(final Context CONTEXT, final ImageView IMAGE_VIEW) {
        Glide.with(CONTEXT).load(USER.getPhotoUrl()).into(IMAGE_VIEW);
    }

    public static boolean userNicknameToTextView(final String USER_UID, final TextView TEXT_VIEW) {
        if (USER_UID == null || USER_UID.trim().equals("")) {
            Log.d(TAG, "userNicknameToTextView: USER_UID is not initialized (is null)");
            return false;
        } else if (TEXT_VIEW == null) {
            Log.d(TAG, "userNicknameToTextView: TEXT_VIEW is not initialized (is null)");
            return false;
        }
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
        return true;
    }

    public static boolean uploadNewUserIfNew() {
        final String USER_UID = USER.getUid();
        final Uri PHOTO_URL = USER.getPhotoUrl();
        if (USER_UID.equals("") || PHOTO_URL.equals(""))
            return false;
        USERS.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(USER_UID)) {
                    Log.d(TAG, "uploadNewUserIfNew: User " + USER_UID + " already exists");
                }
                else {
                    Log.d(TAG, "uploadNewUserIfNew: Uploading new empty user to database at: " + USER_UID);
                    new UploadUserPhoto().execute(PHOTO_URL);
                    uploadNewUser(UserExamples.NEW_USER_TEMPLATE());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "uploadNewUserIfNew: onCancelled", databaseError.toException());
            }
        });
        return true;
    }

    public static boolean gotToRecipe(final Context CONTEXT, Recipe recipe) {
        if (recipe == null || recipe.getId() == null || recipe.getId().equals("")) {
            Toast.makeText(CONTEXT, "Denne oppskriften har feil addresse.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "gotToRecipe: Recipe is not initialized or recipe id is not initialized");
            return false;
        } else if (CONTEXT == null) {
            Toast.makeText(CONTEXT, "Oi! Her skjedde det noe galt.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "gotToRecipe: CONTEXT is not initialized");
            return false;
        }
        recipe.addView();
        Intent intent = new Intent(CONTEXT, RecipeActivity.class);
        intent.putExtra("recipeID", recipe.getId());
        intent.putExtra("authorID", recipe.getData().getUser());
        CONTEXT.startActivity(intent);
        return true;
    }

    public static boolean goToUser(final Context CONTEXT, final String USER_UID) {
        if (USER_UID == null || USER_UID.equals("")) {
            Toast.makeText(CONTEXT, "Beklager, men denne brukeren er ikke hjemme i dag.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "goToUser: USER_UID is not initialized");
            return false;
        } else if (CONTEXT == null) {
            Toast.makeText(CONTEXT, "Oi! Her skjedde det noe galt.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "goToUser: CONTEXT is not initialized");
            return false;
        }
        Intent intent = new Intent(CONTEXT, UserActivity.class);
        intent.putExtra("userID", USER_UID);
        CONTEXT.startActivity(intent);
        return true;
    }
}
