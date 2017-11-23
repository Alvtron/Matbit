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
 *
 *
 */

public class NewsFeed {
    private static final String TAG = "NewsFeed";
    private static final String RECIPE_OF_THE_WEEK = MatbitApplication.resources().getString(R.string.string_this_weeks_recipe);
    private static final String MOST_LIKED_RECIPE = MatbitApplication.resources().getString(R.string.string_most_liked_recipe);
    private static final String MOST_POPULAR_RECIPE = MatbitApplication.resources().getString(R.string.string_most_popular_recipe);
    private static final String LATEST_RECIPE = MatbitApplication.resources().getString(R.string.string_new_recipe);
    private static final String NEW_FOLLOWERS = MatbitApplication.resources().getString(R.string.string_you_have_new_followers);
    private Context context;
    private StorageReference storage_reference_thumbnail = null;
    private StorageReference storage_reference_featured_image = null;
    private String title = "";
    private String subtitle = "";
    private String text = "";
    private Intent action;
    private DateTime date = null;

    NewsFeed(Context context){
        this.context = context;
    }

    boolean recipeOfTheWeek(final DataSnapshot DATA_SNAPSHOT){
        Recipe recipe = new Recipe();

        for (DataSnapshot recipesSnapshot : DATA_SNAPSHOT.getChildren()) {

            Recipe this_recipe = new Recipe(recipesSnapshot);

            boolean isMorePopular = false;
            boolean sevenDaysOld = false;

            DateTime date_created = DateUtility.stringToDate(this_recipe.getData().getDatetime_created());
            if (date_created != null)
                sevenDaysOld = Days.daysBetween(date_created, DateTime.now()).getDays() <= 7;

            if (recipe.hasData()) {
                isMorePopular = this_recipe.getData().getThumbs_up() * 3 + this_recipe.getData().getViews()
                        > recipe.getData().getThumbs_up() * 3 + recipe.getData().getViews();
            }

            if ((!recipe.hasData() && sevenDaysOld) || (isMorePopular && sevenDaysOld)) {
                recipe = this_recipe;

                title = RECIPE_OF_THE_WEEK;
                text = String.format(
                        MatbitApplication.resources().getString(R.string.format_feed_posted_by),
                        recipe.getData().getInfo(),
                        recipe.getData().getUser_nickname()
                );
                subtitle = String.format(
                        MatbitApplication.resources().getString(R.string.format_feed_views_and_likes),
                        recipe.getData().getViews(),
                        recipe.getThumbsUp()
                );
                storage_reference_thumbnail = MatbitDatabase.getRecipePhoto(recipe.getId());
                storage_reference_featured_image = MatbitDatabase.getRecipePhoto(recipe.getId());

                action = new Intent(context, RecipeActivity.class);
                action.putExtra(MatbitApplication.resources().getString(R.string.key_recipe_id), recipe.getId());
                action.putExtra(MatbitApplication.resources().getString(R.string.key_user_id), recipe.getData().getUser());
            }
        }

        date = new DateTime();
        return recipe.hasData();
    }

    boolean mostLikedRecipe(final DataSnapshot DATA_SNAPSHOT){
        Recipe recipe = new Recipe();

        for (DataSnapshot recipesSnapshot : DATA_SNAPSHOT.getChildren()) {

            Recipe this_recipe = new Recipe(recipesSnapshot);

            boolean mostLiked = false;

            if (recipe.hasData())
                mostLiked = this_recipe.getData().getThumbs_up() > recipe.getData().getThumbs_up();

            if (!recipe.hasData() || mostLiked) {
                recipe = this_recipe;
                title = MOST_LIKED_RECIPE;
                text = String.format(
                        MatbitApplication.resources().getString(R.string.format_feed_posted_by),
                        recipe.getData().getInfo(),
                        recipe.getData().getUser_nickname()
                );
                subtitle = String.format(
                        MatbitApplication.resources().getString(R.string.format_feed_views_and_likes),
                        recipe.getData().getViews(),
                        recipe.getThumbsUp()
                );
                storage_reference_thumbnail = MatbitDatabase.getRecipePhoto(recipe.getId());
                storage_reference_featured_image = MatbitDatabase.getRecipePhoto(recipe.getId());

                action = new Intent(context, RecipeActivity.class);
                action.putExtra(MatbitApplication.resources().getString(R.string.key_recipe_id), recipe.getId());
                action.putExtra(MatbitApplication.resources().getString(R.string.key_user_id), recipe.getData().getUser());
            }
        }

        date = new DateTime();

        return recipe.hasData();
    }

    boolean mostPopularRecipe(final DataSnapshot DATA_SNAPSHOT){
        Recipe recipe = new Recipe();

        for (DataSnapshot recipesSnapshot : DATA_SNAPSHOT.getChildren()) {

            Recipe this_recipe = new Recipe(recipesSnapshot);

            boolean morePopular = false;

            if (recipe.hasData())
                morePopular = this_recipe.getData().getThumbs_up() * 5 + this_recipe.getData().getViews()
                        > recipe.getData().getThumbs_up() * 5 + recipe.getData().getViews();

            if (!recipe.hasData() || morePopular) {
                recipe = this_recipe;
                title = MOST_POPULAR_RECIPE;
                text = String.format(
                        MatbitApplication.resources().getString(R.string.format_feed_posted_by),
                        recipe.getData().getInfo(),
                        recipe.getData().getUser_nickname()
                );

                subtitle = String.format(
                        MatbitApplication.resources().getString(R.string.format_feed_views_and_likes),
                        recipe.getData().getViews(),
                        recipe.getThumbsUp()
                );
                storage_reference_thumbnail = MatbitDatabase.getRecipePhoto(recipe.getId());
                storage_reference_featured_image = MatbitDatabase.getRecipePhoto(recipe.getId());

                action = new Intent(context, RecipeActivity.class);
                action.putExtra(MatbitApplication.resources().getString(R.string.key_recipe_id), recipe.getId());
                action.putExtra(MatbitApplication.resources().getString(R.string.key_user_id), recipe.getData().getUser());
            }
        }

        date = new DateTime();

        return recipe.hasData();
    }

    boolean newestRecipe(final DataSnapshot DATA_SNAPSHOT){
        Recipe recipe = new Recipe();

        for (DataSnapshot recipesSnapshot : DATA_SNAPSHOT.getChildren()) {
            Recipe this_recipe = new Recipe(recipesSnapshot);

            boolean isNewer = false;

            if (recipe.hasData()) {
                DateTime recipe_date = DateUtility.stringToDate(recipe.getData().getDatetime_created());
                DateTime this_date = DateUtility.stringToDate(this_recipe.getData().getDatetime_created());
                if (recipe_date != null && this_date != null)
                    isNewer = this_date.isAfter(recipe_date);
            }

            if (!recipe.hasData() || isNewer){
                recipe = this_recipe;

                title = LATEST_RECIPE;
                text = String.format(
                        MatbitApplication.resources().getString(R.string.format_feed_posted_by),
                        recipe.getData().getInfo(),
                        recipe.getData().getUser_nickname()
                );
                subtitle = String.format(
                        MatbitApplication.resources().getString(R.string.format_feed_views_and_likes),
                        recipe.getData().getViews(),
                        recipe.getThumbsUp()
                );
                storage_reference_thumbnail = MatbitDatabase.getRecipePhoto(recipe.getId());
                storage_reference_featured_image = MatbitDatabase.getRecipePhoto(recipe.getId());

                action = new Intent(context, RecipeActivity.class);
                action.putExtra(MatbitApplication.resources().getString(R.string.key_recipe_id), recipe.getId());
                action.putExtra(MatbitApplication.resources().getString(R.string.key_user_id), recipe.getData().getUser());
            }
        }

        date = new DateTime();

        return recipe.hasData();
    }

    boolean newFollowers(final DataSnapshot DATA_SNAPSHOT){
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
            text = String.format(
                    MatbitApplication.resources().getString(R.string.format_feed_new_followers),
                    newFollowers.size()
            );
            subtitle = DateUtility.dateToPeriod(newest_date) + " "
                    + MatbitApplication.resources().getString(R.string.string_ago);

            storage_reference_thumbnail = MatbitDatabase.getUserPhoto(user.getId());

            action = new Intent(context, UserActivity.class);
            action.putExtra(MatbitApplication.resources().getString(R.string.key_user_id), MatbitDatabase.getCurrentUserUID());

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

    static final Comparator<NewsFeed> DATE_COMPARATOR_ASC = new Comparator<NewsFeed>() {
        @Override
        public int compare(NewsFeed a, NewsFeed b) {
            if (a.date == null || b.date == null)
                return 0;
            else
                return a.date.compareTo(b.date);
        }
    };

    static final Comparator<NewsFeed> DATE_COMPARATOR_DESC = new Comparator<NewsFeed>() {
        @Override
        public int compare(NewsFeed a, NewsFeed b) {
            if (a.date == null || b.date == null)
                return 0;
            else
                return b.date.compareTo(a.date);
        }
    };

    StorageReference getStorage_reference_thumbnail() {
        return storage_reference_thumbnail;
    }

    StorageReference getStorage_reference_featured_image() {
        return storage_reference_featured_image;
    }

    Intent getAction() {
        return action;
    }

    DateTime getDate() {
        return date;
    }

    String getSubtitle() {
        return subtitle;
    }
}
