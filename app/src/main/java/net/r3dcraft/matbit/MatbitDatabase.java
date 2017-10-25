package net.r3dcraft.matbit;

import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Created by unibl on 21.10.2017.
 */

public final class MatbitDatabase {
    public static final FirebaseUser USER = FirebaseAuth.getInstance().getCurrentUser();
    public static final FirebaseStorage STORAGE = FirebaseStorage.getInstance();
    public static final StorageReference RECIPE_PHOTOS = STORAGE.getReference("recipe_photos");
    public static final StorageReference USER_PHOTOS = STORAGE.getReference("user_photos");
    public static final DatabaseReference RECIPES = FirebaseDatabase.getInstance().getReference().child("recipes");
    public static final DatabaseReference USERS = FirebaseDatabase.getInstance().getReference().child("users");

    public static String getCurrentUser() {
        return USER.toString();
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

    public static void uploadNewRecipe(final RecipeData recipeData) {
        String uid = MatbitDatabase.RECIPES.push().getKey();
        RECIPES.child(uid).setValue(recipeData);
        recipeRatings(uid).setValue(recipeData.getRatings());
        recipeComments(uid).setValue(recipeData.getComments());
        recipeSteps(uid).setValue(recipeData.getSteps());
        recipeIngredients(uid).setValue(recipeData.getIngredients());
    }

    public static void uploadNewUser(final UserData userData) {
        USERS.child(USER.getUid()).setValue(userData);
    }
}
