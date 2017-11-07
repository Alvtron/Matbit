package net.r3dcraft.matbit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 24.10.2017.
 */

public class AddRecipePagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private User user;
    private Recipe recipe;
    private byte[] recipePhoto;
    private String title, info;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;
    private String category;
    private int hour, minutes, portions;

    private static final int NUM_PAGES = 8;

    private static final String ADD_PHOTO_TITLE = "Foto";
    private static final String ADD_TITLE_TITILE = "Tittel";
    private static final String ADD_INFO_TITLE = "Info";
    private static final String ADD_INGREDIENTS_TITLE = "Ingredienser";
    private static final String ADD_STEPS_TITLE = "Steg";
    private static final String ADD_CATEGORY_TITLE = "Kategori";
    private static final String ADD_TIME_TITLE = "Tid";
    private static final String ADD_PORTIONS_TITLE = "Porsjoner";

    private AddRecipeFragmentPhoto addRecipeFragmentPhoto;
    private AddRecipeFragmentTitle addRecipeFragmentTitle;
    private AddRecipeFragmentInfo addRecipeFragmentInfo;
    private AddRecipeFragmentIngredients addRecipeFragmentIngredients;
    private AddRecipeFragmentSteps addRecipeFragmentSteps;
    private AddRecipeFragmentCategory addRecipeFragmentCategory;
    private AddRecipeFragmentTime addRecipeFragmentTime;
    private AddRecipeFragmentPortions addRecipeFragmentPortions;

    public AddRecipePagerAdapter(FragmentManager fm, User user, final Context CONTEXT) {
        super(fm);
        this.context = CONTEXT;
        this.user = user;
        this.addRecipeFragmentPhoto = new AddRecipeFragmentPhoto();
        this.addRecipeFragmentTitle = new AddRecipeFragmentTitle();
        this.addRecipeFragmentInfo = new AddRecipeFragmentInfo();
        this.addRecipeFragmentIngredients = new AddRecipeFragmentIngredients();
        this.addRecipeFragmentSteps = new AddRecipeFragmentSteps();
        this.addRecipeFragmentCategory = new AddRecipeFragmentCategory();
        this.addRecipeFragmentTime = new AddRecipeFragmentTime();
        this.addRecipeFragmentPortions = new AddRecipeFragmentPortions();
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
                return ADD_TITLE_TITILE;
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

    public void createRecipe(){
        boolean valid = true;
        if (recipePhoto == null) {
            Toast.makeText(context, "Du må laste opp et bilde!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(title.trim().equals("")) {
            Toast.makeText(context, "DÅRLIG INPUT", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(hour * 60 + minutes < 1) {
            Toast.makeText(context, "Velg en tid som virker fornuftig", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(ingredients.isEmpty()) {
            Toast.makeText(context, "DÅRLIG INPUT", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(steps.isEmpty()) {
            Toast.makeText(context, "DÅRLIG INPUT", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(category == null || category.trim().equals("")) {
            Toast.makeText(context, "DÅRLIG INPUT", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(portions < 1) {
            Toast.makeText(context, "DÅRLIG INPUT", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (valid) {
            recipe = new Recipe();
            recipe.createNewRecipe(title,
                    hour * 60 + minutes,
                    portions,
                    category,
                    info,
                    steps,
                    ingredients
            );

            // Upload recipe data to new unique firebase key
            recipe.setId(MatbitDatabase.uploadNewRecipe(recipe.getData()));
            // Add and upload new recipe to user
            user.addRecipe(recipe.getId(), recipe.getData().getDatetime_created());
            user.uploadRecipes();
            // Upload recipe photo
            UploadTask uploadTask = MatbitDatabase.RECIPE_PHOTOS.child(recipe.getId() +  ".jpg").putBytes(recipePhoto);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
            // Show dialog box for next step
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Oppskriften er lastet opp!")
                    .setPositiveButton("Gå til oppskrift", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((AddRecipeActivity)context).finish();
                            MatbitDatabase.gotToRecipe(context, recipe);
                        }
                    })
                    .setNegativeButton("Gå til startsiden", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((AddRecipeActivity)context).finish();
                            context.startActivity(new Intent(context, MainActivity.class));
                        }
                    });
            builder.show();
        }
    }

    public void setRecipePhoto(byte[] recipePhoto) {
        this.recipePhoto = recipePhoto;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }
}
