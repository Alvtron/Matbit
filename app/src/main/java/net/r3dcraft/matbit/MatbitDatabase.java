package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.URI;
import java.util.Random;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 */

public final class MatbitDatabase {
    private static final String TAG = "MatbitDatabase";
    private static final FirebaseAuth AUTH = FirebaseAuth.getInstance();
    private static final FirebaseUser USER = AUTH.getCurrentUser();
    private static final FirebaseStorage STORAGE = FirebaseStorage.getInstance();
    private static final StorageReference RECIPE_PHOTOS = STORAGE.getReference("recipe_photos");
    private static final StorageReference USER_PHOTOS = STORAGE.getReference("user_photos");
    private static final FirebaseDatabase DATABASE = FirebaseDatabase.getInstance();
    private static final DatabaseReference DATABASE_ROOT = DATABASE.getReference();
    private static final DatabaseReference RECIPE_DATA = DATABASE_ROOT.child("recipes");
    private static final DatabaseReference USER_DATA = DATABASE_ROOT.child("users");

    public static boolean hasAuth() {
        if (AUTH == null) {
            Log.d(TAG, "FirebaseAuth is not connected");
            return false;
        }
        return true;
    }

    public static boolean hasStorage() {
        if (STORAGE == null) {
            Log.d(TAG, "FirebaseStorage is not connected");
            return false;
        }
        return true;
    }

    public static boolean hasDatabase() {
        if (DATABASE == null) {
            Log.d(TAG, "FirebaseDatabase is not connected");
            return false;
        }
        return true;
    }

    public static boolean hasUser() {
        if (USER == null) {
            Log.d(TAG, "FirebaseUser is not logged in");
            return false;
        }
        return true;
    }

    public static String getCurrentUserUID() {
        if (!hasAuth()) return null;
        if (!hasUser()) return null;
        return USER.getUid();
    }

    public static String getCurrentUserDisplayName() {
        if (!hasAuth()) return null;
        if (!hasUser()) return null;
        return USER.getDisplayName();
    }

    public static String getCurrentUserEmail() {
        if (!hasAuth()) return null;
        if (!hasUser()) return null;
        return USER.getEmail();
    }

    public static StorageReference getRecipePhoto(final String RECIPE_ID) {
        if (!hasStorage()) return null;
        return RECIPE_PHOTOS.child(RECIPE_ID +  ".jpg");
    }

    public static StorageReference getUserPhoto(final String USER_ID) {
        if (!hasStorage()) return null;
        return USER_PHOTOS.child(USER_ID +  ".jpg");
    }

    // USER DATABASE -----------------------------------------------------------------------------

    public static DatabaseReference USERS () {
        if (!hasDatabase()) return null;
        return USER_DATA;
    }

    public static DatabaseReference user (final String USER_ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(USER_ID);
    }

    public static DatabaseReference getCurrentUser() {
        if (!hasAuth()) return null;
        if (!hasUser()) return null;
        if (!hasDatabase()) return null;
        return USER_DATA.child(MatbitDatabase.USER.getUid());
    }

    public static DatabaseReference getUser(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID);
    }

    public static DatabaseReference getUserNickname(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("nickname");
    }

    public static DatabaseReference getUserGender(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("gender");
    }

    public static DatabaseReference getUserBirthday(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("birthday");
    }

    public static DatabaseReference getUserSignUpDate(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("signUpDate");
    }

    public static DatabaseReference getUserLastLoginDate(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("lastLoginDate");
    }

    public static DatabaseReference getUserBio(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("bio");
    }

    public static DatabaseReference getUserExp(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("exp");
    }

    public static DatabaseReference getUserNumFollowers(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("num_followers");
    }

    public static DatabaseReference getUserNumRecipes(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("num_recipes");
    }

    public static DatabaseReference getUserFollowing(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("following");
    }

    public static DatabaseReference getUserFollowers(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("followers");
    }

    public static DatabaseReference getUserRecipes(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("recipes");
    }

    public static DatabaseReference getUserFavorites(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("favorites");
    }

    // RECIPE DATABASE -----------------------------------------------------------------------------

    public static String newRecipeKey(){
        return RECIPE_DATA.push().getKey();
    }

    public static boolean deleteRecipe (final String RECIPE_ID) {
        if (!hasDatabase()) return false;

        recipe(RECIPE_ID).removeValue();
        getUserRecipes(getCurrentUserUID()).child(RECIPE_ID).removeValue();
        getUserNumRecipes(getCurrentUserUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("num_recipes").exists()) {
                    final int num_recipes = dataSnapshot.getValue(Integer.class);
                    getUserNumRecipes(getCurrentUserUID()).setValue(num_recipes - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }

    public static DatabaseReference RECIPES () {
        if (!hasDatabase()) return null;
        return RECIPE_DATA;
    }

    public static DatabaseReference recipe (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID);
    }

    public static DatabaseReference recipeTitle (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("title");
    }

    public static DatabaseReference recipeUser (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("user");
    }

    public static DatabaseReference recipeUserNickname (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("user_nickname");
    }

    public static DatabaseReference recipeDatetimeCreated (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("datetime_created");
    }

    public static DatabaseReference recipeDatetimeUpdated (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("datetime_updated");
    }

    public static DatabaseReference recipeTime (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("step_time");
    }

    public static DatabaseReference recipePortions (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("portions");
    }

    public static DatabaseReference recipeViews (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("views");
    }

    public static DatabaseReference recipeRatings (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("ratings");
    }

    public static DatabaseReference recipeComments (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("comments");
    }

    public static DatabaseReference recipeSteps (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("steps");
    }

    public static DatabaseReference recipeIngredients (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("ingredients");
    }

    public static DatabaseReference recipeThumbsUp (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("thumbs_up");
    }

    public static DatabaseReference recipeThumbsDown (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("thumbs_down");
    }

    public static DatabaseReference recipeInfo (final String ID) {
        if (!hasDatabase()) return null;
        return RECIPE_DATA.child(ID).child("info");
    }

    // FUNCTIONS -----------------------------------------------------------------------------------

    public static String uploadNewRecipe(final RecipeData RECIPE_DATA) {
        if (!hasAuth()) return null;
        if (!hasDatabase()) return null;

        if (RECIPE_DATA == null) {
            Log.d(TAG, "uploadNewRecipe: RECIPE_DATA is not initialized (is null)");
            return null;
        }

        String uid = MatbitDatabase.RECIPE_DATA.push().getKey();
        MatbitDatabase.RECIPE_DATA.child(uid).setValue(RECIPE_DATA);
        recipeRatings(uid).setValue(RECIPE_DATA.getRatings());
        recipeComments(uid).setValue(RECIPE_DATA.getComments());
        recipeSteps(uid).setValue(RECIPE_DATA.getSteps());
        recipeIngredients(uid).setValue(RECIPE_DATA.getIngredients());
        return uid;
    }

    public static boolean uploadNewRecipe(final RecipeData RECIPE_DATA, final String KEY) {
        if (!hasAuth()) return false;
        if (!hasDatabase()) return false;

        if (RECIPE_DATA == null) {
            Log.d(TAG, "uploadNewRecipe: RECIPE_DATA is not initialized (is null)");
            return false;
        }

        MatbitDatabase.RECIPE_DATA.child(KEY).setValue(RECIPE_DATA);
        recipeRatings(KEY).setValue(RECIPE_DATA.getRatings());
        recipeComments(KEY).setValue(RECIPE_DATA.getComments());
        recipeSteps(KEY).setValue(RECIPE_DATA.getSteps());
        recipeIngredients(KEY).setValue(RECIPE_DATA.getIngredients());
        return true;
    }

    public static boolean uploadNewUser(final UserData USER_DATA) {
        if (!hasAuth()) return false;
        if (!hasDatabase()) return false;

        if (USER_DATA == null) {
            Log.d(TAG, "uploadNewUser: USER_DATA is not initialized (is null)");
            return false;
        }
        MatbitDatabase.USER_DATA.child(USER.getUid()).setValue(USER_DATA);
        return true;
    }

    public static boolean recipePictureToImageView(final String RECIPE_UID, final Context CONTEXT, final ImageView IMAGE_VIEW) {
        if (!hasAuth()) return false;
        if (!hasStorage()) return false;

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

        IMAGE_VIEW.setImageResource(R.color.grey_100);

        MatbitDatabase.RECIPE_PHOTOS.child(RECIPE_UID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Glide.with(CONTEXT)
                            .load(uri)
                            .into(IMAGE_VIEW);
                } catch(IllegalArgumentException e) {
                    Log.e(TAG, "recipePictureToImageView: Cannot start a load for a destroyed activity");
                    IMAGE_VIEW.setImageResource(R.drawable.icon_broken_image_black_24dp);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "recipePictureToImageView: Could not load recipe photo");
                IMAGE_VIEW.setImageResource(R.drawable.icon_broken_image_black_24dp);
            }
        });
        return true;
    }

    public static boolean userPictureToImageView(final String USER_UID, final Context CONTEXT, final ImageView IMAGE_VIEW) {
        if (!hasAuth()) return false;
        if (!hasStorage()) return false;

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

        IMAGE_VIEW.setImageResource(R.color.grey_100);

        MatbitDatabase.USER_PHOTOS.child(USER_UID + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Glide.with(CONTEXT)
                            .load(uri)
                            .into(IMAGE_VIEW);
                } catch(IllegalArgumentException e) {
                    Log.e(TAG, "userPictureToImageView: Cannot start a load for a destroyed activity");
                    IMAGE_VIEW.setImageResource(R.drawable.icon_broken_image_black_24dp);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "userPictureToImageView: Could not load user photo");
                IMAGE_VIEW.setImageResource(R.drawable.icon_profile_black_24dp);
            }
        });
        return true;
    }

    public static boolean downloadToImageView(final StorageReference STORAGE_REFERENCE, final Context CONTEXT, final ImageView IMAGE_VIEW) {
        if (!hasAuth()) return false;
        if (!hasStorage()) return false;

        if (STORAGE_REFERENCE == null) {
            Log.d(TAG, "downloadToImageView: STORAGE_REFERENCE is not initialized (is null)");
            return true;
        } else if (CONTEXT == null) {
            Log.d(TAG, "userPictureToImageView: CONTEXT is not initialized (is null)");
            return true;
        } else if (IMAGE_VIEW == null) {
            Log.d(TAG, "userPictureToImageView: IMAGE_VIEW is not initialized (is null)");
            return true;
        }

        STORAGE_REFERENCE.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try{
                    Glide.with(CONTEXT)
                            .load(uri)
                            .into(IMAGE_VIEW);
                } catch(IllegalArgumentException e) {
                    Log.e(TAG, "downloadToImageView: Cannot start a load for a destroyed activity");
                    IMAGE_VIEW.setImageResource(R.drawable.icon_broken_image_black_24dp);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "downloadToImageView: Could not load image. Maybe storage reference is invalid");
                IMAGE_VIEW.setImageResource(R.drawable.icon_broken_image_black_24dp);
            }
        });

        return true;
    }

    public static void currentUserPictureToImageView(final Context CONTEXT, final ImageView IMAGE_VIEW) {
        userPictureToImageView(getCurrentUserUID(), CONTEXT, IMAGE_VIEW);
    }

    public static boolean userNicknameToTextView(final String USER_UID, final TextView TEXT_VIEW) {
        if (!hasAuth()) return false;
        if (!hasDatabase()) return false;

        if (USER_UID == null || USER_UID.trim().equals("")) {
            Log.d(TAG, "userNicknameToTextView: USER_UID is not initialized (is null)");
            return false;
        } else if (TEXT_VIEW == null) {
            Log.d(TAG, "userNicknameToTextView: TEXT_VIEW is not initialized (is null)");
            return false;
        }
        USER_DATA.child(USER_UID).addListenerForSingleValueEvent(new ValueEventListener()  {
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

    public static boolean handleNewUserIfNew(final Context CONTEXT) {
        if (!hasAuth()) return false;
        if (!hasUser()) return false;
        if (!hasDatabase()) return false;

        final String USER_UID = USER.getUid();
        final Uri PHOTO_URL = USER.getPhotoUrl();

        if (USER_UID.equals("") || PHOTO_URL.equals(""))
            return false;

        USER_DATA.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(USER_UID)) {
                    Log.d(TAG, "handleNewUserIfNew: User " + USER_UID + " already exists");
                    DataSnapshot nickname_snapshot = dataSnapshot.child(USER_UID).child("nickname");
                        if (!nickname_snapshot.exists()) {
                            CONTEXT.startActivity(new Intent(CONTEXT, UserEditActivity.class));
                        }
                        else if (nickname_snapshot.getValue(String.class).trim().equals(""))
                            CONTEXT.startActivity(new Intent(CONTEXT, UserEditActivity.class));
                    }
                else {
                    Log.d(TAG, "handleNewUserIfNew: Uploading new empty user to database at: " + USER_UID);
                    new UploadUserPhoto().execute(PHOTO_URL);
                    uploadNewUser(UserExamples.NEW_USER_TEMPLATE());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "handleNewUserIfNew: onCancelled", databaseError.toException());
            }
        });
        return true;
    }

    public static void goToRecipe(final Context CONTEXT, Recipe recipe) {
        if (recipe == null || recipe.getId() == null || recipe.getId().equals("")) {
            Toast.makeText(CONTEXT, "Denne oppskriften har feil addresse.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "goToRecipe: Recipe is not initialized or recipe id is not initialized");
            return;
        } else if (CONTEXT == null) {
            Toast.makeText(CONTEXT, "Oi! Her skjedde det noe galt.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "goToRecipe: CONTEXT is not initialized");
            return;
        }
        recipe.addView();
        Intent intent = new Intent(CONTEXT, RecipeActivity.class);
        intent.putExtra(CONTEXT.getString(R.string.key_recipe_id), recipe.getId());
        intent.putExtra(CONTEXT.getString(R.string.key_user_id), recipe.getData().getUser());
        CONTEXT.startActivity(intent);
    }

    public static void goToRandomRecipe(final Context CONTEXT) {
        MatbitDatabase.RECIPES().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Random random = new Random();
                int index = random.nextInt((int) dataSnapshot.getChildrenCount());
                int count = 0;
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    if (count++ == index) {
                        Recipe recipe = new Recipe(recipeSnapshot, true);
                        goToRecipe(CONTEXT, recipe);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void goToUser(final Context CONTEXT, final String USER_UID) {
        if (USER_UID == null || USER_UID.equals("")) {
            Toast.makeText(CONTEXT, R.string.string_sorry_this_user_is_not_home_today, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "goToUser: USER_UID is not initialized");
            return;
        } else if (CONTEXT == null) {
            Toast.makeText(CONTEXT, "Oi! Her skjedde det noe galt.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "goToUser: CONTEXT is not initialized");
            return;
        }
        Intent intent = new Intent(CONTEXT, UserActivity.class);
        intent.putExtra(CONTEXT.getString(R.string.key_user_id), USER_UID);
        CONTEXT.startActivity(intent);
        return;
    }
}
