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
    private boolean edit = false;
    private User user;
    private Recipe recipe;
    private byte[] recipePhoto;
    private String title = "", info = "", category = "";
    private int hour = 0, minutes = 0, portions = 0;
    private ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
    private ArrayList<Step> steps = new ArrayList<Step>();

    private static final int NUM_PAGES = 8;

    public static final String ADD_PHOTO_TITLE = "Legg til bilde";
    public static final String ADD_TITLE_TITLE = "Legg til tittel";
    public static final String ADD_INFO_TITLE = "Legg til info";
    public static final String ADD_INGREDIENTS_TITLE = "Legg til ingredienser";
    public static final String ADD_STEPS_TITLE = "Legg til steg";
    public static final String ADD_CATEGORY_TITLE = "Velg kategori";
    public static final String ADD_TIME_TITLE = "Legg til tid";
    public static final String ADD_PORTIONS_TITLE = "Legg til porsjoner";

    public static final String CHANGE_PHOTO_TITLE = "Endre bilde";
    public static final String CHANGE_TITLE_TITLE = "Endre tittel";
    public static final String CHANGE_INFO_TITLE = "Endre info";
    public static final String CHANGE_INGREDIENTS_TITLE = "Endre ingredienser";
    public static final String CHANGE_STEPS_TITLE = "Endre steg";
    public static final String CHANGE_CATEGORY_TITLE = "Endre kategori";
    public static final String CHANGE_TIME_TITLE = "Endre tid";
    public static final String CHANGE_PORTIONS_TITLE = "Endre antall porsjoner";

    private AddRecipeFragmentPhoto addRecipeFragmentPhoto = new AddRecipeFragmentPhoto();
    private AddRecipeFragmentTitle addRecipeFragmentTitle = new AddRecipeFragmentTitle();
    private AddRecipeFragmentInfo addRecipeFragmentInfo = new AddRecipeFragmentInfo();
    private AddRecipeFragmentIngredients addRecipeFragmentIngredients = new AddRecipeFragmentIngredients();
    private AddRecipeFragmentSteps addRecipeFragmentSteps = new AddRecipeFragmentSteps();
    private AddRecipeFragmentCategory addRecipeFragmentCategory = new AddRecipeFragmentCategory();
    private AddRecipeFragmentTime addRecipeFragmentTime = new AddRecipeFragmentTime();
    private AddRecipeFragmentPortions addRecipeFragmentPortions = new AddRecipeFragmentPortions();

    public AddRecipePagerAdapter(FragmentManager fm, User user, final Context CONTEXT) {
        super(fm);
        this.context = CONTEXT;
        this.user = user;
    }

    public AddRecipePagerAdapter(FragmentManager fm, User user, final Context CONTEXT, final boolean EDIT) {
        super(fm);
        this.context = CONTEXT;
        this.edit = EDIT;
        this.user = user;
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
        if (!edit) {
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
        }
        else {
            switch (position) {
                case 0:
                    return CHANGE_PHOTO_TITLE;
                case 1:
                    return CHANGE_TITLE_TITLE;
                case 2:
                    return CHANGE_INFO_TITLE;
                case 3:
                    return CHANGE_INGREDIENTS_TITLE;
                case 4:
                    return CHANGE_STEPS_TITLE;
                case 5:
                    return CHANGE_CATEGORY_TITLE;
                case 6:
                    return CHANGE_TIME_TITLE;
                case 7:
                    return CHANGE_PORTIONS_TITLE;
                default:
                    break;
            }
        }
        return null;
    }

    public boolean createRecipe(){
        boolean valid = true;
        if (recipePhoto == null) {
            Toast.makeText(context, "Du må laste opp et bilde!", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(title.trim().equals("")) {
            Toast.makeText(context, "Du mangler tittel!", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(hour * 60 + minutes < 1) {
            Toast.makeText(context, "Velg en tid høyere enn 0 minutter", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(hour * 60 + minutes > 1440) {
            Toast.makeText(context, "Velg en tid lavere enn 24 timer", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(ingredients.isEmpty()) {
            Toast.makeText(context, "Du må legge til minst en ignrediens", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(steps.isEmpty()) {
            Toast.makeText(context, "Du må legge til minst ett steg", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(category == null || category.trim().equals("")) {
            Toast.makeText(context, "Du må velge en kategori", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if(portions < 1) {
            Toast.makeText(context, "Antall porsjoner er for lav (minst 1)", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (portions > 12) {
            Toast.makeText(context, "Antall porsjoner er for høy (maks 12)", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if (valid) {
            recipe = new Recipe();
            recipe.createNewRecipe(
                    user,
                    title,
                    hour * 60 + minutes,
                    portions,
                    category,
                    info,
                    steps,
                    ingredients
            );
            recipe.printRecipeToLog();

            // Upload recipe data to new unique firebase key
            recipe.setId(MatbitDatabase.uploadNewRecipe(recipe.getData()));
            // Add and upload new recipe to user
            user.addRecipe(recipe.getId(), recipe.getData().getDatetime_created());
            user.uploadRecipes();
            // Upload recipe photo
            UploadTask uploadTask = MatbitDatabase.getRecipePhoto(recipe.getId()).putBytes(recipePhoto);
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
                    .setCancelable(false)
                    .setPositiveButton("Gå til oppskrift", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((AddRecipeActivity)context).finish();
                            MatbitDatabase.gotToRecipe(context, recipe);
                            ((AddRecipeActivity) context).finish();
                        }
                    })
                    .setNegativeButton("Gå til startsiden", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ((AddRecipeActivity)context).finish();
                            context.startActivity(new Intent(context, MainActivity.class));
                            ((AddRecipeActivity) context).finish();
                        }
                    });
            builder.show();
            return true;
        }
        return false;
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

    public void addStep(Step step) {
        steps.add(step);
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

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public String getCategory() {
        return category;
    }

    public int getHour() {
        return hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getPortions() {
        return portions;
    }
}
