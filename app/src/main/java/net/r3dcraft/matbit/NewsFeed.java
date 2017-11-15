package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 17.10.2017.
 */

public class NewsFeed {
    private static final String TAG = "NewsFeed";
    private static final String RECIPE_OF_THE_WEEK = "Denne ukas oppskrift!";
    private static final String MOST_LIKED_RECIPE = "Mest populær oppskrift!";
    private static final String LASTEST_RECIPE = "Ny oppskrift!";
    private static final String NEW_FOLLOWERS = "Du har nye følgere!";
    private Context context;
    private StorageReference storage_reference_thumbnail = null;
    private StorageReference storage_reference_featured_image = null;
    private String title = "";
    private String subtitle = "";
    private String text = "";
    private Intent action;
    private DateTime date = null;

    public NewsFeed(Context context){
        this.context = context;
    }

    public boolean recipeOfTheWeek(final DataSnapshot DATA_SNAPSHOT){
        Recipe recipe = new Recipe();

        for (DataSnapshot recipesSnapshot : DATA_SNAPSHOT.getChildren()) {

            Recipe this_recipe = new Recipe(recipesSnapshot);

            if (!recipe.hasData() || this_recipe.getData().getThumbs_up() * 3 + this_recipe.getData().getViews()
                    > recipe.getData().getThumbs_up() * 3 + recipe.getData().getViews()) {
                recipe = this_recipe;

                title = RECIPE_OF_THE_WEEK;
                text = recipe.getData().getInfo() + " - <i><font color='#9E9E9E'> postet av "
                        + recipe.getData().getUser_nickname() + "</font></i>";
                subtitle = "Med " + String.valueOf(recipe.getData().getViews()) + " visninger og " + String.valueOf(recipe.getThumbsUp()) + " likes!";

                storage_reference_thumbnail = MatbitDatabase.getRecipePhoto(recipe.getId());
                storage_reference_featured_image = MatbitDatabase.getRecipePhoto(recipe.getId());

                action = new Intent(context, RecipeActivity.class);
                action.putExtra("recipeID", recipe.getId());
                action.putExtra("authorID", recipe.getData().getUser());
            }
        }

        date = new DateTime();
        return recipe.hasData();
    }

    public boolean mostLikedRecipe(final DataSnapshot DATA_SNAPSHOT){
        Recipe recipe = new Recipe();

        for (DataSnapshot recipesSnapshot : DATA_SNAPSHOT.getChildren()) {

            Recipe this_recipe = new Recipe(recipesSnapshot);

            if (!recipe.hasData() || this_recipe.getData().getThumbs_up() > recipe.getData().getThumbs_up()) {
                recipe = this_recipe;

                title = MOST_LIKED_RECIPE;
                text = recipe.getData().getInfo() + " - <i><font color='#9E9E9E'> postet av "
                        + recipe.getData().getUser_nickname() + "</font></i>";
                subtitle = "Med " + String.valueOf(recipe.getData().getViews()) + " visninger og " + String.valueOf(recipe.getThumbsUp()) + " likes!";

                storage_reference_thumbnail = MatbitDatabase.getRecipePhoto(recipe.getId());
                storage_reference_featured_image = MatbitDatabase.getRecipePhoto(recipe.getId());

                action = new Intent(context, RecipeActivity.class);
                action.putExtra("recipeID", recipe.getId());
                action.putExtra("authorID", recipe.getData().getUser());
            }
        }

        date = new DateTime();

        return recipe.hasData();
    }

    public boolean newestRecipe(final DataSnapshot DATA_SNAPSHOT){
        Recipe recipe = new Recipe();

        for (DataSnapshot recipesSnapshot : DATA_SNAPSHOT.getChildren()) {
            Recipe this_recipe = new Recipe(recipesSnapshot);
            DateTime this_date = DateUtility.stringToDate(this_recipe.getData().getDatetime_created());

            if (!recipe.hasData() || this_date.isAfter(DateUtility.stringToDate(recipe.getData().getDatetime_created()))){
                recipe = this_recipe;

                title = LASTEST_RECIPE;
                text = recipe.getData().getInfo() + " - <i><font color='#9E9E9E'> postet av "
                        + recipe.getData().getUser_nickname() + "</font></i>";
                subtitle = "Med " + String.valueOf(recipe.getData().getViews()) + " visninger og " + String.valueOf(recipe.getThumbsUp()) + " likes!";

                storage_reference_thumbnail = MatbitDatabase.getRecipePhoto(recipe.getId());
                storage_reference_featured_image = MatbitDatabase.getRecipePhoto(recipe.getId());

                action = new Intent(context, RecipeActivity.class);
                action.putExtra("recipeID", recipe.getId());
                action.putExtra("authorID", recipe.getData().getUser());
            }
        }

        date = new DateTime();

        return recipe.hasData();
    }

    public boolean newFollowers(final DataSnapshot DATA_SNAPSHOT){
        User user = new User(DATA_SNAPSHOT);
        ArrayList<String> newFollowers = new ArrayList<String>();
        DateTime newest_date = null;

        for (Map.Entry<String, String> ratingSet : user.getData().getFollowers().entrySet()) {
            String user_id = ratingSet.getKey();
            DateTime date = DateUtility.stringToDate(ratingSet.getValue()) ;

            if (Days.daysBetween(date, DateTime.now()).getDays() <= 7) {
                newFollowers.add(user_id);
                if (newest_date == null || date.isAfter(newest_date))
                    newest_date = date;
            }
        }
        if (!newFollowers.isEmpty()) {
            title = NEW_FOLLOWERS;
            text = "Du har " + String.valueOf(newFollowers.size()) + " nye følgere!";

            subtitle = DateUtility.dateToTimeText(newest_date) + " siden";

            storage_reference_thumbnail = MatbitDatabase.getUserPhoto(user.getId());

            action = new Intent(context, UserActivity.class);
            action.putExtra("userID", MatbitDatabase.getCurrentUserUID());

            date = newest_date;
            return true;
        }
        else return false;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public static final Comparator<NewsFeed> DATE_COMPARATOR_ASC = new Comparator<NewsFeed>() {
        @Override
        public int compare(NewsFeed a, NewsFeed b) {
            if (a.date == null || b.date == null)
                return 0;
            else
                return a.date.compareTo(b.date);
        }
    };

    public static final Comparator<NewsFeed> DATE_COMPARATOR_DESC = new Comparator<NewsFeed>() {
        @Override
        public int compare(NewsFeed a, NewsFeed b) {
            if (a.date == null || b.date == null)
                return 0;
            else
                return b.date.compareTo(a.date);
        }
    };

    public StorageReference getStorage_reference_thumbnail() {
        return storage_reference_thumbnail;
    }

    public StorageReference getStorage_reference_featured_image() {
        return storage_reference_featured_image;
    }

    public Intent getAction() {
        return action;
    }

    public DateTime getDate() {
        return date;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
