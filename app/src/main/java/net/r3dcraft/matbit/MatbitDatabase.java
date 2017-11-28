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
 *
 * The MatbitDatabase class is a static class that generalizes the way this apps loads and writes
 * data to Matbit's Database and storage. This class ensures that data is read from the
 * correct location and written to the correct location. This also makes updating the database less
 * of a hassle. If at some point I choose to add new types of data to the database, or change names,
 * I just need to change this class and Matbit will continue to work as intended.
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

    /**
     * Check if FirebaseAuth is connected.
     * @return true if there is a connection
     */
    public static boolean hasAuth() {
        if (AUTH == null) {
            Log.d(TAG, "FirebaseAuth is not connected.");
            return false;
        }
        return true;
    }

    /**
     * Check if Database is connected.
     * @return true if there is a connection
     */
    public static boolean hasDatabase() {
        if (DATABASE == null) {
            Log.d(TAG, "FirebaseDatabase is not connected.");
            return false;
        }
        return true;
    }

    /**
     * Check if User is connected.
     * @return true if there is a connection
     */
    public static boolean hasUser() {
        if (USER == null) {
            Log.d(TAG, "FirebaseUser is not logged in");
            return false;
        }
        return true;
    }

    // STORAGE -------------------------------------------------------------------------------------

    /**
     * Get storage reference of requested recipe photo by providing it's ID/key from the database
     * @param RECIPE_ID ID/key of recipe from database
     * @return storage reference of requested recipe photo
     */
    public static StorageReference getRecipePhoto(final String RECIPE_ID) {
        return RECIPE_PHOTOS.child(RECIPE_ID +  ".jpg");
    }

    /**
     * Get storage reference of requested user photo by providing it's ID/key from the database
     * @param USER_ID ID/key of user from database
     * @return storage reference of requested user photo
     */
    public static StorageReference getUserPhoto(final String USER_ID) {
        return USER_PHOTOS.child(USER_ID +  ".jpg");
    }

    // CURRENT USER --------------------------------------------------------------------------------
    /**
     * @return UID of current user, if user is logged in
     */
    public static String getCurrentUserUID() {
        if (!hasAuth()) return "";
        if (!hasUser()) return "";
        if (USER == null) return "";
        return USER.getUid();
    }

    /**
     * @return DisplayName of current user, if user is logged in
     */
    public static String getCurrentUserDisplayName() {
        if (!hasAuth()) return "";
        if (!hasUser()) return "";
        if (USER == null) return "";
        return USER.getDisplayName();
    }

    /**
     * @return E-mail of current user, if user is logged in
     */
    public static String getCurrentUserEmail() {
        if (!hasAuth()) return "";
        if (!hasUser()) return "";
        if (USER == null) return "";
        return USER.getEmail();
    }

    /**
     * @return Photo URL (Uri) of current user if user is logged in
     */
    public static Uri getCurrentUserPhotoURL() {
        if (!hasAuth()) return null;
        if (!hasUser()) return null;
        if (USER == null) return null;
        return USER.getPhotoUrl();
    }

    // USER DATABASE -----------------------------------------------------------------------------

    /**
     * Upload new user to database.
     * @param USER_DATA user data to be uploaded
     */
    public static void uploadNewUser(final UserData USER_DATA) {
        if (!hasAuth()) return;
        if (!hasDatabase()) return;

        if (USER_DATA == null) {
            Log.d(TAG, "uploadNewUser: USER_DATA is not initialized (is null)");
            return;
        }
        MatbitDatabase.USER_DATA.child(USER.getUid()).setValue(USER_DATA);
    }

    /**
     * @return database reference to users in Matbit Database
     */
    public static DatabaseReference USERS () {
        if (!hasDatabase()) return null;
        return USER_DATA;
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested user
     */
    public static DatabaseReference user (final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID);
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested user
     */
    public static DatabaseReference getUser(final String ID) {
        return user(ID);
    }

    /**
     * @return database reference to current user, if logged in
     */
    public static DatabaseReference getCurrentUser() {
        if (!hasAuth()) return null;
        if (!hasUser()) return null;
        if (!hasDatabase()) return null;
        return USER_DATA.child(MatbitDatabase.USER.getUid());
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users nickname
     */
    public static DatabaseReference getUserNickname(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("nickname");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users gender
     */
    public static DatabaseReference getUserGender(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("gender");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users birthday
     */
    public static DatabaseReference getUserBirthday(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("birthday");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users sign up date
     */
    public static DatabaseReference getUserSignUpDate(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("signUpDate");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users last login date
     */
    public static DatabaseReference getUserLastLoginDate(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("lastLoginDate");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users bio
     */
    public static DatabaseReference getUserBio(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("bio");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users exp
     */
    public static DatabaseReference getUserExp(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("exp");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users number of followers
     */
    public static DatabaseReference getUserNumFollowers(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("num_followers");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users nubmer of recipes
     */
    public static DatabaseReference getUserNumRecipes(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("num_recipes");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users following
     */
    public static DatabaseReference getUserFollowing(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("following");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users followers
     */
    public static DatabaseReference getUserFollowers(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("followers");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users recipes
     */
    public static DatabaseReference getUserRecipes(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("recipes");
    }

    /**
     * @param ID ID/key of user
     * @return database reference to requested users favorites
     */
    public static DatabaseReference getUserFavorites(final String ID) {
        if (!hasDatabase()) return null;
        return USER_DATA.child(ID).child("favorites");
    }

    // RECIPE DATABASE -----------------------------------------------------------------------------

    /**
     * @return get new unique key for recipe
     */
    public static String newRecipeKey(){
        return RECIPE_DATA.push().getKey();
    }

    /**
     * Upload new recipe to database.
     * @param RECIPE_DATA recipe data to be uploaded
     */
    public static void uploadNewRecipe(final RecipeData RECIPE_DATA) {
        uploadNewRecipe(RECIPE_DATA, newRecipeKey());
    }

    /**
     * Upload new recipe to database.
     * @param RECIPE_DATA recipe data to be uploaded
     * @param KEY which recipe id/key to store data to
     */
    public static void uploadNewRecipe(final RecipeData RECIPE_DATA, final String KEY) {
        if (!hasAuth()) return;
        if (!hasDatabase()) return;

        if (RECIPE_DATA == null) {
            Log.d(TAG, "uploadNewRecipe: RECIPE_DATA is not initialized (is null)");
            return;
        }

        if (KEY == null) {
            Log.d(TAG, "uploadNewRecipe: KEY is not initialized (is null)");
            return;
        }

        if (KEY.trim().equals("")) {
            Log.d(TAG, "uploadNewRecipe: KEY is empty");
            return;
        }

        MatbitDatabase.RECIPE_DATA.child(KEY).setValue(RECIPE_DATA);
        recipeRatings(KEY).setValue(RECIPE_DATA.getRatings());
        recipeComments(KEY).setValue(RECIPE_DATA.getComments());
        recipeSteps(KEY).setValue(RECIPE_DATA.getSteps());
        recipeIngredients(KEY).setValue(RECIPE_DATA.getIngredients());
    }

    /**
     * Delete recipe in database. This also updates the rest of the database with this deletion.
     * @param RECIPE_ID ID/key of recipe to be deleted
     */
    public static void deleteRecipe (final String RECIPE_ID) {
        if (!hasDatabase()) return;
        if (!hasUser()) return;

        // Remove recipe from database
        recipe(RECIPE_ID).removeValue();
        // Remove recipe from current users recipe list in database
        getUserRecipes(getCurrentUserUID()).child(RECIPE_ID).removeValue();
        // Update users number of recipes
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
    }

    /**
     * @return database reference to recipes in database
     */
    public static DatabaseReference RECIPES () {
        return RECIPE_DATA;
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipe
     */
    public static DatabaseReference recipe (final String ID) {
        return RECIPE_DATA.child(ID);
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes title
     */
    public static DatabaseReference recipeTitle (final String ID) {
        return RECIPE_DATA.child(ID).child("title");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes user
     */
    public static DatabaseReference recipeUser (final String ID) {
        return RECIPE_DATA.child(ID).child("user");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes user nickname
     */
    public static DatabaseReference recipeUserNickname (final String ID) {
        return RECIPE_DATA.child(ID).child("user_nickname");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes date/time created
     */
    public static DatabaseReference recipeDatetimeCreated (final String ID) {
        return RECIPE_DATA.child(ID).child("datetime_created");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes date/time updated
     */
    public static DatabaseReference recipeDatetimeUpdated (final String ID) {
        return RECIPE_DATA.child(ID).child("datetime_updated");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes time
     */
    public static DatabaseReference recipeTime (final String ID) {
        return RECIPE_DATA.child(ID).child("time");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes portions
     */
    public static DatabaseReference recipePortions (final String ID) {
        return RECIPE_DATA.child(ID).child("portions");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes views
     */
    public static DatabaseReference recipeViews (final String ID) {
        return RECIPE_DATA.child(ID).child("views");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes ratings
     */
    public static DatabaseReference recipeRatings (final String ID) {
        return RECIPE_DATA.child(ID).child("ratings");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes comments
     */
    public static DatabaseReference recipeComments (final String ID) {
        return RECIPE_DATA.child(ID).child("comments");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes steps
     */
    public static DatabaseReference recipeSteps (final String ID) {
        return RECIPE_DATA.child(ID).child("steps");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes ingredients
     */
    public static DatabaseReference recipeIngredients(final String ID) {
        return RECIPE_DATA.child(ID).child("ingredients");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes thumbs up
     */
    public static DatabaseReference recipeThumbsUp(final String ID) {
        return RECIPE_DATA.child(ID).child("thumbs_up");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes thumbs down
     */
    public static DatabaseReference recipeThumbsDown(final String ID) {
        return RECIPE_DATA.child(ID).child("thumbs_down");
    }

    /**
     * @param ID ID/key of recipe
     * @return database reference to requested recipes info
     */
    public static DatabaseReference recipeInfo(final String ID) {
        return RECIPE_DATA.child(ID).child("info");
    }

    // FUNCTIONS -----------------------------------------------------------------------------------

    /**
     * Load recipe photo to specified image view. This uses Glide which loads the photo
     * directly from Matbit's database.
     * @param RECIPE_UID ID/key of recipe
     * @param CONTEXT context that holds image view
     * @param IMAGE_VIEW image view to load recipe photo
     */
    public static void recipePictureToImageView(final String RECIPE_UID, final Context CONTEXT, final ImageView IMAGE_VIEW) {
        if (!hasAuth()) return;

        if (RECIPE_UID == null || RECIPE_UID.trim().equals("")) {
            Log.d(TAG, "recipePictureToImageView: RECIPE_UID is not initialized (is null)");
            return;
        } else if (CONTEXT == null) {
            Log.d(TAG, "recipePictureToImageView: CONTEXT is not initialized (is null)");
            return;
        } else if (IMAGE_VIEW == null) {
            Log.d(TAG, "recipePictureToImageView: IMAGE_VIEW is not initialized (is null)");
            return;
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
    }

    /**
     * Load user photo to specified image view. This uses Glide which loads the photo
     * directly from Matbit's database.
     * @param USER_UID ID/key of user
     * @param CONTEXT context that holds image view
     * @param IMAGE_VIEW image view to load user photo
     */
    public static void userPictureToImageView(final String USER_UID, final Context CONTEXT, final ImageView IMAGE_VIEW) {
        if (!hasAuth()) return;

        if (USER_UID == null || USER_UID.trim().equals("")) {
            Log.d(TAG, "userPictureToImageView: USER_UID is not initialized (is null)");
            return;
        } else if (CONTEXT == null) {
            Log.d(TAG, "userPictureToImageView: CONTEXT is not initialized (is null)");
            return;
        } else if (IMAGE_VIEW == null) {
            Log.d(TAG, "userPictureToImageView: IMAGE_VIEW is not initialized (is null)");
            return;
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
    }

    /**
     * Download photo from storage and place it in a specified image view.
     * @param STORAGE_REFERENCE storage reference of photo
     * @param CONTEXT context that holds image view
     * @param IMAGE_VIEW image view to load photo
     */
    public static void downloadToImageView(final StorageReference STORAGE_REFERENCE, final Context CONTEXT, final ImageView IMAGE_VIEW) {
        if (!hasAuth()) return;

        if (STORAGE_REFERENCE == null) {
            Log.d(TAG, "downloadToImageView: STORAGE_REFERENCE is not initialized (is null)");
            return;
        } else if (CONTEXT == null) {
            Log.d(TAG, "userPictureToImageView: CONTEXT is not initialized (is null)");
            return;
        } else if (IMAGE_VIEW == null) {
            Log.d(TAG, "userPictureToImageView: IMAGE_VIEW is not initialized (is null)");
            return;
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
    }

    /**
     * Load current user profile picture to image view
     * @param CONTEXT context that holds image view
     * @param IMAGE_VIEW image view to load photo
     */
    public static void currentUserPictureToImageView(final Context CONTEXT, final ImageView IMAGE_VIEW) {
        userPictureToImageView(getCurrentUserUID(), CONTEXT, IMAGE_VIEW);
    }

    public static void userNicknameToTextView(final String USER_UID, final TextView TEXT_VIEW) {
        if (!hasAuth()) return;
        if (!hasDatabase()) return;

        if (USER_UID == null || USER_UID.trim().equals("")) {
            Log.d(TAG, "userNicknameToTextView: USER_UID is not initialized (is null)");
            return;
        } else if (TEXT_VIEW == null) {
            Log.d(TAG, "userNicknameToTextView: TEXT_VIEW is not initialized (is null)");
            return;
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
    }

    /**
     * Checks if current user is new (no data in database). If user is new, upload user data to
     * database and user photo to storage.
     * @param CONTEXT
     */
    public static void handleNewUserIfNew(final Context CONTEXT) {
        if (!hasAuth() || !hasUser() || !hasDatabase()) return;

        // Get current user UID and Profile photo URL
        final String USER_UID = getCurrentUserUID();
        final Uri PHOTO_URL = getCurrentUserPhotoURL();

        // Return if UID or photo url is invalid
        if (USER_UID.equals("") || PHOTO_URL != null) return;

        // Load users from database
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
    }

    /**
     * Go to RecipeActivity. Increment views of recipe and update the database.
     * @param CONTEXT Context of from-activity
     * @param recipe recipe to navigate to
     */
    public static void goToRecipe(final Context CONTEXT, Recipe recipe) {
        if (recipe == null || recipe.getId() == null || recipe.getId().equals("")) {
            Toast.makeText(CONTEXT, R.string.string_this_recipe_has_the_wrong_address, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "goToRecipe: Recipe is not initialized or recipe id is not initialized");
            return;
        } else if (CONTEXT == null) {
            Log.e(TAG, "goToRecipe: CONTEXT is not initialized");
            return;
        }
        // Increment views of recipe
        recipe.addView();
        // Create intent and add extra data: recipe ID and author ID
        Intent intent = new Intent(CONTEXT, RecipeActivity.class);
        intent.putExtra(CONTEXT.getString(R.string.key_recipe_id), recipe.getId());
        intent.putExtra(CONTEXT.getString(R.string.key_user_id), recipe.getData().getUser());
        // Start recipe activity
        CONTEXT.startActivity(intent);
    }

    /**
     * Go to a random RecipeActivity. Increment views of recipe and update the database.
     * @param CONTEXT Context of from-activity
     */
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

    /**
     * Go to user by specifying its ID.
     * @param CONTEXT Context of from-activity
     * @param USER_UID ID/key of user to go to
     */
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
    }
}
