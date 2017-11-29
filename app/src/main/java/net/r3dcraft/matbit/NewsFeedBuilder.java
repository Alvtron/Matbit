package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 27.11.2017.
 *
 * The NewsFeedBuilder creates NewsFeed objects based on data from Matbit Database.
 */

public class NewsFeedBuilder {
    private static final String TAG = "NewsFeedBuilder";
    private static final String RECIPE_OF_THE_WEEK = MatbitApplication.resources().getString(R.string.string_this_weeks_recipe);
    private static final String MOST_LIKED_RECIPE = MatbitApplication.resources().getString(R.string.string_most_liked_recipe);
    private static final String MOST_POPULAR_RECIPE = MatbitApplication.resources().getString(R.string.string_most_popular_recipe);
    private static final String LATEST_RECIPE = MatbitApplication.resources().getString(R.string.string_new_recipe);
    private static final String NEW_FOLLOWERS = MatbitApplication.resources().getString(R.string.string_you_have_new_followers);
    private Context context;
    private ArrayList<Recipe> recipes =  new ArrayList<Recipe>();
    private User user;

    /**
     * NewsFeedBuilder constructor
     * @param CONTEXT context of current Activity
     */
    NewsFeedBuilder(final Context CONTEXT){
        this.context = CONTEXT;
    }

    /**
     * Load recipe data from Matbit Database and add them as Recipe objects to a list.
     * @param RECIPES_SNAPSHOT Snapshot of recipes in Matbit Database
     */
    public void setRecipes(final DataSnapshot RECIPES_SNAPSHOT) {
        for (DataSnapshot recipeSnapshot : RECIPES_SNAPSHOT.getChildren())
            recipes.add(new Recipe(recipeSnapshot));
    }

    /**
     * Load user data from Matbit Database and store it as a User object.
     * @param USER_SNAPSHOT Snapshot of user in Matbit Database
     */
    public void setUser(final DataSnapshot USER_SNAPSHOT) {
        user = new User(USER_SNAPSHOT);
    }

    /**
     * Create "Recipe of the Week" NewsFeed object. Exclude recipes older than seven days. Score
     * each recipe in recipe list after how many thumbs up and views they have. The one with the
     * most is returned.
     * @return A NewsFeed Object considered The "Recipe of the Week"
     */
    public NewsFeed recipeOfTheWeek(){
        Recipe recipe = new Recipe();

        // Check all recipes
        for (Recipe this_recipe : recipes) {

            boolean isMorePopular = false;
            boolean isMaxSevenDaysOld = false;

            // Get recipe's date created
            DateTime date_created = DateUtility.stringToDate(this_recipe.getData().getDatetime_created());

            // Check if this recipe is older than seven days
            if (date_created != null)
                isMaxSevenDaysOld = Days.daysBetween(date_created, DateTime.now()).getDays() <= 7;

            // Check if this recipe is more popular than the current "Recipe of the Week"
            if (recipe.hasData())
                isMorePopular = this_recipe.getData().getThumbs_up() * 3 + this_recipe.getData().getViews()
                        > recipe.getData().getThumbs_up() * 3 + recipe.getData().getViews();

            // If this recipe is max seven days old and more popular than the current, store it
            if ((!recipe.hasData() && isMaxSevenDaysOld) || (isMorePopular && isMaxSevenDaysOld))
                recipe = this_recipe;
        }

        // Create NewsFeed object
        if (recipe.hasData()) {
            NewsFeed newsFeed = new NewsFeed();
            // Set title
            newsFeed.setTitle(RECIPE_OF_THE_WEEK);
            // Set text
            newsFeed.setText(String.format(
                    MatbitApplication.resources().getString(R.string.format_feed_posted_by),
                    recipe.getData().getInfo(),
                    recipe.getData().getUser_nickname()
            ));
            // Set subtitle
            newsFeed.setSubtitle(String.format(
                    MatbitApplication.resources().getString(R.string.format_feed_views_and_likes),
                    recipe.getData().getViews(),
                    recipe.getThumbsUp()
            ));
            // Set thumbnail
            newsFeed.setStorage_reference_thumbnail(MatbitDatabase.getRecipePhoto(recipe.getId()));
            // Set featured image
            newsFeed.setStorage_reference_featured_image(MatbitDatabase.getRecipePhoto(recipe.getId()));
            // Set action (what to happen if user clicks this NewsFeed)
            Intent action = new Intent(context, RecipeActivity.class);
            action.putExtra(MatbitApplication.resources().getString(R.string.key_recipe_id), recipe.getId());
            action.putExtra(MatbitApplication.resources().getString(R.string.key_user_id), recipe.getData().getUser());
            newsFeed.setAction(action);
            // Set date (for sorting purposes)
            newsFeed.setDate(new DateTime(
                    2000,
                    DateTime.now().getMonthOfYear(),
                    DateTime.now().getDayOfMonth(),
                    DateTime.now().getHourOfDay(),
                    DateTime.now().getMinuteOfHour())
            );
            return newsFeed;
        }
        return null;
    }

    /**
     * Create "Most Liked Recipe" NewsFeed object. Score each recipe in recipe list after how many
     * likes they have. The one with the most is returned.
     * @return A NewsFeed Object considered The "Most Liked Recipe"
     */
    public NewsFeed mostLikedRecipe(){
        Recipe recipe = new Recipe();

        // Check all recipes
        for (Recipe this_recipe : recipes) {

            boolean mostLiked = false;

            // Check if this recipe is more liked than the current
            if (recipe.hasData())
                mostLiked = this_recipe.getData().getThumbs_up() > recipe.getData().getThumbs_up();

            // If it is the most liked, store it
            if (!recipe.hasData() || mostLiked)
                recipe = this_recipe;
        }

        if (recipe.hasData()) {
            NewsFeed newsFeed = new NewsFeed();
            // Set title
            newsFeed.setTitle(MOST_LIKED_RECIPE);
            // Set text
            newsFeed.setText(String.format(
                    MatbitApplication.resources().getString(R.string.format_feed_posted_by),
                    recipe.getData().getInfo(),
                    recipe.getData().getUser_nickname()
            ));
            // Set subtitle
            newsFeed.setSubtitle(String.format(
                    MatbitApplication.resources().getString(R.string.format_feed_views_and_likes),
                    recipe.getData().getViews(),
                    recipe.getThumbsUp()
            ));
            // Set thumbnail
            newsFeed.setStorage_reference_thumbnail(MatbitDatabase.getRecipePhoto(recipe.getId()));
            // Set featured image
            newsFeed.setStorage_reference_featured_image(MatbitDatabase.getRecipePhoto(recipe.getId()));
            // Set action (what to happen if user clicks this NewsFeed)
            Intent action = new Intent(context, RecipeActivity.class);
            action.putExtra(MatbitApplication.resources().getString(R.string.key_recipe_id), recipe.getId());
            action.putExtra(MatbitApplication.resources().getString(R.string.key_user_id), recipe.getData().getUser());
            newsFeed.setAction(action);
            // Set date (for sorting purposes)
            newsFeed.setDate(new DateTime(
                    2000,
                    DateTime.now().getMonthOfYear(),
                    DateTime.now().getDayOfMonth(),
                    DateTime.now().getHourOfDay(),
                    DateTime.now().getMinuteOfHour())
            );
            return newsFeed;
        }

        return null;
    }

    /**
     * Create "Most Popular Recipe" NewsFeed object. Score each recipe in recipe list after how many
     * thumbs up and views they have. The one with the most is returned.
     * @return A NewsFeed Object considered The "Most Popular Recipe"
     */
    public NewsFeed mostPopularRecipe(){
        Recipe recipe = new Recipe();

        // Check all recipes
        for (Recipe this_recipe : recipes) {

            boolean morePopular = false;

            // Check if this recipe is more popular than the current
            if (recipe.hasData())
                morePopular = this_recipe.getData().getThumbs_up() * 5 + this_recipe.getData().getViews()
                        > recipe.getData().getThumbs_up() * 5 + recipe.getData().getViews();

            // If this recipe is more popular, store it
            if (!recipe.hasData() || morePopular)
                recipe = this_recipe;
        }

        // Create NewsFeed object
        if (recipe.hasData()) {
            NewsFeed newsFeed = new NewsFeed();
            // Set title
            newsFeed.setTitle(MOST_POPULAR_RECIPE);
            // Set text
            newsFeed.setText(String.format(
                    MatbitApplication.resources().getString(R.string.format_feed_posted_by),
                    recipe.getData().getInfo(),
                    recipe.getData().getUser_nickname()
            ));
            // Set subtitle
            newsFeed.setSubtitle(String.format(
                    MatbitApplication.resources().getString(R.string.format_feed_views_and_likes),
                    recipe.getData().getViews(),
                    recipe.getThumbsUp()
            ));
            // Set thumbnail
            newsFeed.setStorage_reference_thumbnail(MatbitDatabase.getRecipePhoto(recipe.getId()));
            // Set featured image
            newsFeed.setStorage_reference_featured_image(MatbitDatabase.getRecipePhoto(recipe.getId()));
            // Set action (what to happen if user clicks this NewsFeed)
            Intent action = new Intent(context, RecipeActivity.class);
            action.putExtra(MatbitApplication.resources().getString(R.string.key_recipe_id), recipe.getId());
            action.putExtra(MatbitApplication.resources().getString(R.string.key_user_id), recipe.getData().getUser());
            newsFeed.setAction(action);
            // Set date (for sorting purposes)
            newsFeed.setDate(new DateTime(
                    2000,
                    DateTime.now().getMonthOfYear(),
                    DateTime.now().getDayOfMonth(),
                    DateTime.now().getHourOfDay(),
                    DateTime.now().getMinuteOfHour())
            );
            return newsFeed;
        }

        return null;
    }

    /**
     * Create "Newest Recipe" NewsFeed object. Find the newest recipe in the Matbit Database and
     * return it.
     * @return A NewsFeed Object considered The "Newest Recipe"
     */
    public NewsFeed newestRecipe(){
        Recipe recipe = new Recipe();

        // Check all recipes
        for (Recipe this_recipe : recipes) {

            boolean isNewer = false;

            // Check if this recipe is newer than the current recipe
            if (recipe.hasData()) {
                DateTime recipe_date = DateUtility.stringToDate(recipe.getData().getDatetime_created());
                DateTime this_date = DateUtility.stringToDate(this_recipe.getData().getDatetime_created());
                if (recipe_date != null && this_date != null)
                    isNewer = this_date.isAfter(recipe_date);
            }

            // If this recipe is newer, store it
            if (!recipe.hasData() || isNewer)
                recipe = this_recipe;
        }

        // Create NewsFeed object
        if (recipe.hasData()) {
            NewsFeed newsFeed = new NewsFeed();
            // Set title
            newsFeed.setTitle(LATEST_RECIPE);
            // Set text
            newsFeed.setText(String.format(
                    MatbitApplication.resources().getString(R.string.format_feed_posted_by),
                    recipe.getData().getInfo(),
                    recipe.getData().getUser_nickname()
            ));
            // Set subtitle
            newsFeed.setSubtitle(String.format(
                    MatbitApplication.resources().getString(R.string.format_feed_views_and_likes),
                    recipe.getData().getViews(),
                    recipe.getThumbsUp()
            ));
            // Set thumbnail
            newsFeed.setStorage_reference_thumbnail(MatbitDatabase.getRecipePhoto(recipe.getId()));
            // Set featured image
            newsFeed.setStorage_reference_featured_image(MatbitDatabase.getRecipePhoto(recipe.getId()));
            // Set action (what to happen if user clicks this NewsFeed)
            Intent action = new Intent(context, RecipeActivity.class);
            action.putExtra(MatbitApplication.resources().getString(R.string.key_recipe_id), recipe.getId());
            action.putExtra(MatbitApplication.resources().getString(R.string.key_user_id), recipe.getData().getUser());
            newsFeed.setAction(action);
            // Set date (for sorting purposes)
            newsFeed.setDate(new DateTime(
                    2000,
                    DateTime.now().getMonthOfYear(),
                    DateTime.now().getDayOfMonth(),
                    DateTime.now().getHourOfDay(),
                    DateTime.now().getMinuteOfHour())
            );
            return newsFeed;
        }

        return null;
    }

    /**
     * Create "New Followers" Newsfeed object. Read user data and if there are any new followers
     * the last seven days, create this NewsFeed object.
     * @return A NewsFeed Object with "New Followers"
     */
    public NewsFeed newFollowers(){
        if (!user.hasData()) return null;
        if (!user.hasFollowers()) return null;

        ArrayList<String> newFollowers = new ArrayList<String>();
        DateTime newest_date = null;

        // Check all followers of user
        for (Map.Entry<String, String> ratingSet : user.getData().getFollowers().entrySet()) {
            String user_id = ratingSet.getKey();
            DateTime date = DateUtility.stringToDate(ratingSet.getValue()) ;

            // If follower followed this user max seven days ago, add it
            if (Days.daysBetween(date, DateTime.now()).getDays() <= 7) {
                newFollowers.add(user_id);
                if (newest_date == null || date.isAfter(newest_date))
                    newest_date = date;
            }
        }

        // As long as there are at least one new follower, create Newsfeed object
        if (!newFollowers.isEmpty()) {
            NewsFeed newsFeed = new NewsFeed();
            // Set title
            newsFeed.setTitle(NEW_FOLLOWERS);
            // Set text
            newsFeed.setText(String.format(
                    MatbitApplication.resources().getString(R.string.format_feed_new_followers),
                    newFollowers.size()
            ));
            // Set subtitle
            newsFeed.setSubtitle(DateUtility.dateToPeriod(newest_date) + " "
                    + MatbitApplication.resources().getString(R.string.string_ago));
            // Set thumbnail storage reference
            newsFeed.setStorage_reference_thumbnail(MatbitDatabase.getUserPhoto(user.getId()));
            // Set action (intent)
            Intent action = new Intent(context, UserActivity.class);
            action.putExtra(MatbitApplication.resources().getString(R.string.key_user_id), MatbitDatabase.getCurrentUserUID());
            newsFeed.setAction(action);
            // Set date as the newest date
            newsFeed.setDate(newest_date);

            return newsFeed;
        }

        return null;
    }
}
