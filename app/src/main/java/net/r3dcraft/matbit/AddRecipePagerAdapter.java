package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 *
 * This extends FragmentPagerAdapter and creates and manages multiple fragments. This also
 * receives, store, validates and uploads recipe-data from the different fragments.
 *
 * This class can be constructed two different ways; With and without already defined recipe-data.
 * If this class is constructed without recipe-data, this adapter will store and create a new recipe.
 * If this class is constructed with recipe-data,  this adapter will modify and update a already
 * -existing recipe.
 */
public class AddRecipePagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private User user;
    private boolean edit_mode = false;
    private Recipe recipe = new Recipe();
    private byte[] recipe_photo;
    private static final int NUM_PAGES = 8;

    // Page titles
    static final String ADD_PHOTO_TITLE = MatbitApplication.resources().getString(R.string.string_add_picture);
    static final String ADD_TITLE_TITLE = MatbitApplication.resources().getString(R.string.string_add_title);
    static final String ADD_INFO_TITLE = MatbitApplication.resources().getString(R.string.string_add_info);
    static final String ADD_INGREDIENTS_TITLE = MatbitApplication.resources().getString(R.string.string_add_ingredients);
    static final String ADD_STEPS_TITLE = MatbitApplication.resources().getString(R.string.string_add_step);
    static final String ADD_CATEGORY_TITLE = MatbitApplication.resources().getString(R.string.string_choose_category);
    static final String ADD_TIME_TITLE = MatbitApplication.resources().getString(R.string.string_add_time);
    static final String ADD_PORTIONS_TITLE = MatbitApplication.resources().getString(R.string.string_add_portions);

    private AddRecipeFragmentPhoto addRecipeFragmentPhoto = new AddRecipeFragmentPhoto();
    private AddRecipeFragmentTitle addRecipeFragmentTitle = new AddRecipeFragmentTitle();
    private AddRecipeFragmentInfo addRecipeFragmentInfo = new AddRecipeFragmentInfo();
    private AddRecipeFragmentIngredients addRecipeFragmentIngredients = new AddRecipeFragmentIngredients();
    private AddRecipeFragmentSteps addRecipeFragmentSteps = new AddRecipeFragmentSteps();
    private AddRecipeFragmentCategory addRecipeFragmentCategory = new AddRecipeFragmentCategory();
    private AddRecipeFragmentTime addRecipeFragmentTime = new AddRecipeFragmentTime();
    private AddRecipeFragmentPortions addRecipeFragmentPortions = new AddRecipeFragmentPortions();

    public AddRecipePagerAdapter(FragmentManager supportFragmentManager, final User USER, final Context CONTEXT) {
        super(supportFragmentManager);
        recipe.setData(new RecipeData());
        this.context = CONTEXT;
        this.user = USER;
    }

    public AddRecipePagerAdapter(FragmentManager supportFragmentManager, final User USER, final Context CONTEXT, final Recipe RECIPE, final byte[] recipe_photo) {
        super(supportFragmentManager);
        this.context = CONTEXT;
        this.user = USER;
        this.recipe = RECIPE;
        this.recipe_photo = recipe_photo;
        edit_mode = true;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return addRecipeFragmentPhoto;
            case 1:
                return addRecipeFragmentTitle;
            case 2:
                return addRecipeFragmentInfo;
            case 3:
                return addRecipeFragmentIngredients;
            case 4:
                return addRecipeFragmentSteps;
            case 5:
                return addRecipeFragmentCategory;
            case 6:
                return addRecipeFragmentTime;
            case 7:
                return addRecipeFragmentPortions;
            default:
                break;
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return ADD_PHOTO_TITLE;
            case 1:
                return ADD_TITLE_TITLE;
            case 2:
                return ADD_INFO_TITLE;
            case 3:
                return ADD_INGREDIENTS_TITLE;
            case 4:
                return ADD_STEPS_TITLE;
            case 5:
                return ADD_CATEGORY_TITLE;
            case 6:
                return ADD_TIME_TITLE;
            case 7:
                return ADD_PORTIONS_TITLE;
            default:
                break;
        }
        return null;
    }

    /**
     * This validates the collected recipe data received from the multiple fragments in this
     * adapter. If the data is valid, a recipe object is created and stored in the database.
     * Lastly, the user is displayed a AlertDialog that gives the user two options; Go back to
     * the front page (MainActivity) or go the the newly created recipe (RecipeActivity).
     */
    public void createRecipe(){

        boolean valid = true;
        if (recipe_photo == null) {
            Toast.makeText(context, "Du må laste opp et bilde!", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(!recipe.hasTitle()) {
            Toast.makeText(context, "Du mangler tittel!", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(recipe.getData().getTime() < 1) {
            Toast.makeText(context, "Velg en tid høyere enn 0 minutter", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(recipe.getData().getTime() > 1440) {
            Toast.makeText(context, "Velg en tid lavere enn 24 timer", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(!recipe.hasIngredients()) {
            Toast.makeText(context, "Du må legge til minst en ingrediens", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(!recipe.hasSteps()) {
            Toast.makeText(context, "Du må legge til minst ett steg", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(!recipe.hasCategory()) {
            Toast.makeText(context, "Du må velge en kategori", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(recipe.getData().getPortions() < 1) {
            Toast.makeText(context, "Antall porsjoner er for lav (minst 1)", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (recipe.getData().getPortions() > 12) {
            Toast.makeText(context, "Antall porsjoner er for høy (maks 12)", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (valid) {
            if (!edit_mode) {
                // Prepare recipe data for upload
                recipe.prepareNewRecipe(user);
                // Request new unique database recipe key
                recipe.setId(MatbitDatabase.newRecipeKey());

                // Upload recipe photo
                UploadTask uploadTask = MatbitDatabase.getRecipePhoto(recipe.getId()).putBytes(recipe_photo);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(context, Resources.getSystem().getString(R.string.error_could_not_upload_recipe), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload new recipe
                        MatbitDatabase.uploadNewRecipe(recipe.getData(), recipe.getId());
                        // Upload new recipe to user
                        user.addRecipe(recipe.getId(), recipe.getData().getDatetime_created());
                        user.uploadRecipes();
                        // Show dialog to user
                        showGoToRecipeDialog();
                    }
                });
            }
            else {
                // Upload recipe photo
                UploadTask uploadTask = MatbitDatabase.getRecipePhoto(recipe.getId()).putBytes(recipe_photo);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(context, Resources.getSystem().getString(R.string.error_could_not_upload_recipe), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Update recipe
                        recipe.getData().setDatetime_updated(DateUtility.nowString());
                        recipe.uploadAll();
                        showGoToRecipeDialog();
                    }
                });
            }
        }
    }

    private void showGoToRecipeDialog() {
        // Show dialog box for next step
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getResources().getString(R.string.string_recipe_is_stored))
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.string_go_to_recipe), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((AddRecipeActivity)context).finish();
                        MatbitDatabase.goToRecipe(context, recipe);
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.string_go_to_startpage), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((AddRecipeActivity)context).finish();
                        context.startActivity(new Intent(context, MainActivity.class));
                    }
                });
        builder.show();
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public byte[] getRecipePhoto() {
        return recipe_photo;
    }

    public void setRecipePhoto(byte[] recipe_photo) {
        this.recipe_photo = recipe_photo;
    }
}
