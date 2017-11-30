package net.r3dcraft.matbit;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 09.10.2017.
 *
 * The Recipe Class represents a recipe in the Matbit Database. It is meant to be used when loading
 * recipes from the database. It stores the recipe's unique ID/key as a String and the data on that
 * key as a RecipeObject.
 *
 * This class also includes a bunch of functions:
 * Functions for checking if the recipe has data.
 * Functions for adding thumbs up, thumbs down, views.
 * Functions for uploading specific data if this recipe to the database, or everything.
 *
 * This class also has comparators for comparing recipes in various ways. These are extensively used
 * in the SearchActivity.
 */

public class Recipe {
    private static final String TAG = "Recipe";

    private String id; // recipe ID/key from database
    private RecipeData data; // data at recipe in database

    // Thumb enums for adding thumbs up or thumbs down to this recipe
    public enum THUMB { UP, DOWN, NOTHING }

    /**
     * Default constructor
     */
    public Recipe() { }

    // this recipe is represented with it's ID/key
    @Override
    public String toString() {
        return id;
    }

    /**
     * Create a Recipe object with a snapshot of a specific recipe in Matbit Database
     * @param DATA_SNAPSHOT data snapshot of a recipe in Matbit Database
     */
    public Recipe(final DataSnapshot DATA_SNAPSHOT) {
        downloadData(DATA_SNAPSHOT);
    }

    /**
     * Create a Recipe object with a snapshot of a specific recipe in Matbit Database. Decided
     * whether to make it minimal: specific data won't be stored (ratings, ingredients, steps). This
     * option is used when recipes is stored as objects in lists. This makes loading andn listing
     * recipes with this option more efficient.
     * @param DATA_SNAPSHOT data snapshot of a recipe in Matbit Database
     */
    public Recipe(final DataSnapshot DATA_SNAPSHOT, final boolean minimal) {
        if (minimal) downloadDataMinimal(DATA_SNAPSHOT);
        else downloadData(DATA_SNAPSHOT);
    }

    /**
     * Create a new recipe by providing a ID/key and a RecipeData object.
     * @param id ID/key string
     * @param recipeData recipe data
     */
    public Recipe(String id, RecipeData recipeData) {
        this.id = id;
        this.data = recipeData;
    }

    /**
     * Print this Recipe object to log.
     */
    public void printRecipeToLog(){
        printRecipeToLog(this);
    }

    /**
     * Print a specified Recipe object to log.
     * @param recipe the recipe to be printed.
     */
    public static void printRecipeToLog(final Recipe recipe){
        Log.d(TAG, "RECIPE  INFORMATION PRINT:");
        Log.d(TAG, "ID: " + recipe.getId());
        Log.d(TAG, "title: " + recipe.getData().getTitle());
        Log.d(TAG, "user: " + recipe.getData().getUser());
        Log.d(TAG, "user_nickname: " + recipe.getData().getUser_nickname());
        Log.d(TAG, "datetime_created: " + recipe.getData().getDatetime_created());
        Log.d(TAG, "datetime_updated: " + recipe.getData().getDatetime_updated());
        Log.d(TAG, "info: " + recipe.getData().getInfo());
        Log.d(TAG, "category: " + recipe.getData().getCategory());
        Log.d(TAG, "step_time: " + Integer.toString(recipe.getData().getTime()));
        Log.d(TAG, "portions: " + Integer.toString(recipe.getData().getPortions()));
        Log.d(TAG, "views: " + Integer.toString(recipe.getData().getViews()));
        Log.d(TAG, "thumbs_up: " + Integer.toString(recipe.getData().getThumbs_up()));
        Log.d(TAG, "thumbs_down: " + Integer.toString(recipe.getData().getThumbs_down()));
        Log.d(TAG, "ratings: " + Integer.toString(recipe.getData().getRatings().size()));
        Log.d(TAG, "comments: " + Integer.toString(recipe.getData().getComments().size()));
        Log.d(TAG, "steps: " + Integer.toString(recipe.getData().getSteps().size()));
        Log.d(TAG, "ingredients: " + Integer.toString(recipe.getData().getIngredients().size()));
    }

    /**
     * Load recipe from a snapshot of the specific recipe stored in Matbit Database.
     * @param DATA_SNAPSHOT data snapshot of the specific recipe
     */
    public void downloadData(final DataSnapshot DATA_SNAPSHOT) {;
        this.id = DATA_SNAPSHOT.getKey();
        if (id == null || id.isEmpty()) {
            Log.e(TAG, "downloadData: Unable to download recipe from Firebase");
            return;
        }
        this.data = DATA_SNAPSHOT.getValue(RecipeData.class);
        downloadRatings(DATA_SNAPSHOT);
        downloadComments(DATA_SNAPSHOT);
        downloadSteps(DATA_SNAPSHOT);
        downloadIngredients(DATA_SNAPSHOT);
    }

    /**
     * Load recipe from a snapshot of the specific recipe stored in Matbit Database.
     * @param DATA_SNAPSHOT data snapshot of the specific recipe
     */
    public void downloadDataMinimal(final DataSnapshot DATA_SNAPSHOT) {;
        this.id = DATA_SNAPSHOT.getKey();
        if (id == null || id.isEmpty())
            return;
        this.data = DATA_SNAPSHOT.getValue(RecipeData.class);
    }

    /**
     * Prepares a new recipe to be created and uploaded to Matbit Database
     * @param AUTHOR The author as a User Object
     */
    public void prepareNewRecipe(final User AUTHOR) {
        if (!hasData())
            data = new RecipeData();
        if (!hasTitle())
            return;
        if (!hasTime())
            return;
        if (!hasPortions())
            return;
        if (!hasCategory())
            return;
        if (!hasInfo())
            return;
        if (!hasSteps())
            return;
        if (!hasIngredients())
            return;
        data.setUser(AUTHOR.getId());
        data.setUser_nickname(AUTHOR.getData().getNickname());
        data.setDatetime_created(DateUtility.nowString());
        data.setDatetime_updated(DateUtility.nowString());
        data.setViews(0);
        data.setThumbs_up(0);
        data.setThumbs_down(0);
    }

    /**
     * Set recipe ID/key
     * @param id Recipe ID/key
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return recipe ID/key string
     */
    public String getId() {
        return id;
    }

    /**
     * Set recipe data
     * @param data recipe data
     */
    public void setData(RecipeData data) {
        this.data = data;
    }

    /**
     * Get recipe data.
     * @return Recipe Data object
     */
    public RecipeData getData() {
        return data;
    }

    /**
     * Get number of thumbs up as a shortened string (ex. 1234 --> 1.234k)
     * @return thumbs up as string
     */
    public String getThumbsUp() {
        if (!hasData() || !hasThumbsUp())
            return null;

        return StringUtility.shortNumber(data.getThumbs_up());
    }

    /**
     * Get number of thumbs down as a shortened string (ex. 1234 --> 1.23k)
     * @return thumbs down as string
     */
    public String getThumbsDown() {
        if (!hasData() || !hasThumbsUp())
            return null;

        return StringUtility.shortNumber(data.getThumbs_down());
    }

    // ADD & REMOVE --------------------------------------------------------------------------------

    /**
     * Increment recipe views by one and update the database accordingly
     */
    public void addView() {
        if (!hasViews()) {
            Log.e(TAG, "addView: Can't add views to uninitialized views");
            return;
        }
        data.setViews(data.getViews() + 1);
        uploadViews();
    }

    /**
     * Add new rating by specifying what kind of rating you are adding. Use Recipe.THUMB.
     * @param thumb Recipe.THUMB type
     */
    public void addRating(final THUMB thumb) {
        if (thumb == null || thumb == THUMB.NOTHING)
            return;

        removeUserRating();
        if (thumb == THUMB.UP) {
            data.setThumbs_up(data.getThumbs_up() + 1);
            uploadThumbsUp();
            Rating new_rating = new Rating(MatbitDatabase.getCurrentUserUID(), true, DateUtility.nowString());
            String key = MatbitDatabase.recipeRatings(id).push().getKey();
            MatbitDatabase.recipeRatings(id).child(key).setValue(new_rating);
            data.addRating(key, new_rating);
        } else if (thumb == THUMB.DOWN) {
            data.setThumbs_down(data.getThumbs_down() + 1);
            uploadThumbsDown();
            Rating new_rating = new Rating(MatbitDatabase.getCurrentUserUID(), false, DateUtility.nowString());
            String key = MatbitDatabase.recipeRatings(id).push().getKey();
            MatbitDatabase.recipeRatings(id).child(key).setValue(new_rating);
            data.addRating(key, new_rating);
        }
    }

    /**
     * Remove recipe rating made by the user.
     */
    public void removeUserRating() {
        ArrayList<String> keys_to_remove = new ArrayList<String>();
        for (Map.Entry<String, Rating> ratingSet : data.getRatings().entrySet()) {
            String key = ratingSet.getKey();
            Rating rating = ratingSet.getValue();
            if (rating.getUser().equals(MatbitDatabase.getCurrentUserUID())) {
                if (rating.getThumbsUp()) {
                    data.setThumbs_up(data.getThumbs_up() - 1);
                    uploadThumbsUp();
                } else {
                    data.setThumbs_down(data.getThumbs_down() - 1);
                    uploadThumbsDown();
                }
                keys_to_remove.add(key);
            }
        }
        for (String key : keys_to_remove) {
            MatbitDatabase.recipeRatings(id).child(key).removeValue();
            data.getRatings().remove(key);
        }
    }

    /**
     * Check to see if user has rated this recipe. Return what the user has rated.
     * @return THUMB type: UP, DOWN or NOTHING
     */
    public THUMB getCurrentUserRating(){
        for (Rating rating : data.getRatings().values())
            if (rating.getUser().equals(MatbitDatabase.getCurrentUserUID())) {
                if (rating.getThumbsUp())
                    return THUMB.UP;
                else
                    return THUMB.DOWN;
            }
        return THUMB.NOTHING;
    }

    /**
     * Add comment to specified recipe and update the database accordingly.
     * @param RECIPE_UID Recipe ID/KEY
     * @param COMMENT_TEXT Comment string
     * @return successfully added comment
     */
    public static boolean addComment(final String RECIPE_UID, final String COMMENT_TEXT) {
        if (RECIPE_UID == null || RECIPE_UID.isEmpty()) {
            Log.e(TAG, "addComment: Can't add comment. Recipe key is empty");
            return false;
        }
        if (COMMENT_TEXT == null || COMMENT_TEXT.isEmpty()) {
            Log.e(TAG, "addComment: Can't add comment. Comment text is empty");
            return false;
        }

        MatbitDatabase.getUser().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String USER_NICKNAME = dataSnapshot.child("nickname").getValue(String.class);
                if (USER_NICKNAME == null) return;

                Comment comment = new Comment(MatbitDatabase.getCurrentUserUID(), USER_NICKNAME, COMMENT_TEXT, DateUtility.nowString(), DateUtility.nowString());
                MatbitDatabase.recipeComments(RECIPE_UID).push().setValue(comment);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }

    /**
     * Change comment text in the specified recipe and update the database accordingly.
     * @param RECIPE_UID Recipe ID/KEY
     * @param COMMENT_UID Comment ID/KEY
     * @param COMMENT_TEXT Comment string
     * @return successfully changed comment
     */
    public static boolean changeComment(final String RECIPE_UID, final String COMMENT_UID, final String COMMENT_TEXT) {
        if (RECIPE_UID == null || RECIPE_UID.isEmpty()) {
            Log.e(TAG, "deleteComment: Can't delete. Recipe key is empty");
            return false;
        }
        if (COMMENT_UID == null || COMMENT_UID.isEmpty()) {
            Log.e(TAG, "deleteComment: Can't delete. Comment key is empty");
            return false;
        }
        if (COMMENT_TEXT == null || COMMENT_TEXT.isEmpty()) {
            Log.e(TAG, "deleteComment: Can't delete. Comment text is empty");
            return false;
        }
        MatbitDatabase.recipeComments(RECIPE_UID).child(COMMENT_UID).child("datetimeUpdated").setValue(DateUtility.nowString());
        MatbitDatabase.recipeComments(RECIPE_UID).child(COMMENT_UID).child("comment").setValue(COMMENT_TEXT);
        return true;
    }

    /**
     * Change comment object in the specified recipe and update the database accordingly.
     * @param RECIPE_UID Recipe ID/KEY
     * @param COMMENT_UID Comment ID/KEY
     * @param COMMENT Comment data
     * @return successfully updated comment
     */
    public static boolean updateComment(final String RECIPE_UID, final String COMMENT_UID, final Comment COMMENT) {
        if (RECIPE_UID == null || RECIPE_UID.isEmpty()) {
            Log.e(TAG, "updateComment: Can't update comment. Recipe key is empty");
            return false;
        }
        if (COMMENT_UID == null || COMMENT_UID.isEmpty()) {
            Log.e(TAG, "updateComment: Can't update comment. Comment key is empty");
            return false;
        }
        if (COMMENT == null) {
            Log.e(TAG, "updateComment: Can't update comment. Comment is empty");
            return false;
        }

        MatbitDatabase.recipeComments(RECIPE_UID).child(COMMENT_UID).setValue(COMMENT);
        return true;
    }

    /**
     * Delete comment object in the specified recipe and update the database accordingly.
     * @param RECIPE_UID Recipe ID
     * @param COMMENT Comment data
     * @return successfully deleted comment
     */
    public static boolean deleteComment(final String RECIPE_UID, final Comment COMMENT) {
        if (COMMENT.getKey() == null || COMMENT.getKey().isEmpty()) {
            Log.e(TAG, "deleteComment: Can't delete. Comment key is invalid");
            return false;
        }
        if (!COMMENT.getUser().equals(MatbitDatabase.getCurrentUserUID())) {
            Log.e(TAG, "deleteComment: Can't delete. Comment author is not equal to current logged in user");
            return false;
        }
        MatbitDatabase.recipeComments(RECIPE_UID).child(COMMENT.getKey()).removeValue();
        return true;
    }

    /**
     * Return a shortened recipe time as a string. Ex. 123 minutes --> 2t:3m
     * @return Shortened recipe time string
     */
    public String getTimeToText() {
        if (hasTime()) {
            String hours = Integer.toString(data.getTime() / 60 % 24);
            String minutes = Integer.toString(data.getTime() % 60);
            return hours + "t:" + minutes + "m";
        } else
            return "";
    }

    /**
     * Change the average time in recipe by adding new time to the old and dividing the result by two.
     * If the new time is twice as large or half the original time, return -1, indicating that this
     * time was invalid.
     * @param TIME new time to be added
     * @return new average recipe time, if new time is valid
     */
    public int changeTimeAverage(final int TIME) {
        int recipeTime = getData().getTime();
        // If new TIME is twice as high or half as low as current TIME, discard it
        if (TIME > recipeTime * 2 || TIME < recipeTime * 0.5)
            return -1;
        else {
            int newAverage = (int) Math.round((double) (recipeTime + TIME) / 2.0);
            getData().setTime(newAverage);
            uploadTime();
            return newAverage;
        }
    }

    // VALIDATION ----------------------------------------------------------------------------------

    /**
     * @return Whether recipe has data or not.
     */
    public boolean hasData() {
        if (data == null) {
            Log.i(TAG, "hasData(): Data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has title or not.
     */
    public boolean hasTitle() {
        if (!hasData()) return false;
        if (data.getTitle() == null || data.getTitle().isEmpty()) {
            Log.i(TAG, "hasTitle: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has user or not.
     */
    public boolean hasUser() {
        if (!hasData()) return false;
        if (data.getUser() == null  || data.getUser().isEmpty()) {
            Log.i(TAG, "hasUser: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has user nickname or not.
     */
    public boolean hasUserNickname() {
        if (!hasData()) return false;
        if (data.getUser_nickname() == null || data.getUser_nickname().isEmpty()) {
            Log.i(TAG, "hasUserNickname: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has date/time created or not.
     */
    public boolean hasDatetimeCreated() {
        if (!hasData()) return false;
        if (data.getDatetime_created() == null || data.getDatetime_created().isEmpty()) {
            Log.i(TAG, "hasDatetimeCreated: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has date/time updated or not.
     */
    public boolean hasDatetimeUpdated() {
        if (!hasData()) return false;
        if (data.getDatetime_updated() == null || data.getDatetime_updated().isEmpty()) {
            Log.i(TAG, "hasDatetimeUpdated: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has time or not.
     */
    public boolean hasTime() {
        if (!hasData()) return false;
        if (data.getTime() < 0) {
            Log.i(TAG, "hasTime: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has portions or not.
     */
    public boolean hasPortions() {
        if (!hasData()) return false;
        if (data.getPortions() < 0) {
            Log.i(TAG, "hasPortions: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has views or not.
     */
    public boolean hasViews() {
        if (!hasData()) return false;
        if (data.getViews() < 0) {
            Log.i(TAG, "hasViews: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has info or not.
     */
    public boolean hasInfo() {
        if (!hasData()) return false;
        if (data.getInfo() == null || data.getInfo().isEmpty()) {
            Log.i(TAG, "hasInfo: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has category or not.
     */
    public boolean hasCategory() {
        if (!hasData()) return false;
        if (data.getCategory() == null || data.getCategory().isEmpty()) {
            Log.i(TAG, "hasCategory: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has thumbs up or not.
     */
    public boolean hasThumbsUp() {
        if (!hasData()) return false;
        if (data.getThumbs_up() < 0) {
            Log.i(TAG, "hasThumbsUp: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has thumbs down or not.
     */
    public boolean hasThumbsDown() {
        if (!hasData()) return false;
        if (data.getThumbs_down() < 0) {
            Log.i(TAG, "hasThumbsDown: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has ratings or not.
     */
    public boolean hasRatings() {
        if (!hasData()) return false;
        if (data.getRatings() == null || data.getRatings().size() == 0) {
            Log.i(TAG, "hasRatings: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has comments or not.
     */
    public boolean hasComments() {
        if (!hasData()) return false;
        if (data.getComments() == null || data.getComments().size() == 0) {
            Log.i(TAG, "hasComments: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has steps or not.
     */
    public boolean hasSteps() {
        if (!hasData()) return false;
        if (data.getSteps() == null || data.getSteps().size() == 0) {
            Log.i(TAG, "hasSteps: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * @return Whether recipe has ingredients or not.
     */
    public boolean hasIngredients() {
        if (!hasData()) return false;
        if (data.getIngredients() == null || data.getIngredients().size() == 0) {
            Log.i(TAG, "hasIngredients: data not initialized.");
            return false;
        }
        return true;
    }

    // DOWNLOAD ------------------------------------------------------------------------------------

    /**
     * Download recipe title to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadTitle(DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        data.setTitle(DATA_SNAPSHOT.child("title").getValue(String.class));
    }

    /**
     * Download recipe user to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadUser (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        data.setUser(DATA_SNAPSHOT.child("user").getValue(String.class));
    }

    /**
     * Download recipe date/time created to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadDatetimeCreated (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        data.setDatetime_created(DATA_SNAPSHOT.child("datetime_created").getValue(String.class));
    }

    /**
     * Download recipe date/time updated to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadDatetimeUpdated (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        data.setDatetime_updated(DATA_SNAPSHOT.child("datetime_updated").getValue(String.class));
    }

    /**
     * Download recipe time to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadTime (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        data.setTime(DATA_SNAPSHOT.child("time").getValue(Integer.class));
    }

    /**
     * Download recipe portions to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadPortions (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        data.setPortions(DATA_SNAPSHOT.child("portions").getValue(Integer.class));
    }

    /**
     * Download recipe views to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadViews (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        data.setViews(DATA_SNAPSHOT.child("views").getValue(Integer.class));
    }

    /**
     * Download recipe thumbs up to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadThumbsUp (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        data.setThumbs_up(DATA_SNAPSHOT.child("thumbs_up").getValue(Integer.class));
    }

    /**
     * Download recipe thumbs down to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadThumbsDown (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        data.setThumbs_down(DATA_SNAPSHOT.child("thumbs_down").getValue(Integer.class));
    }

    /**
     * Download recipe info to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadInfo (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        data.setInfo(DATA_SNAPSHOT.child("info").getValue(String.class));
    }

    /**
     * Download recipe ratings to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadRatings (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        Map<String, Rating> ratings = new HashMap<String, Rating>();
        for (DataSnapshot ratingsSnapshot : DATA_SNAPSHOT.child("ratings").getChildren()) {
            Rating rating = ratingsSnapshot.getValue(Rating.class);
            ratings.put(ratingsSnapshot.getKey(), rating);
        }
        data.setRatings(ratings);
    }

    /**
     * Download recipe comments to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadComments (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        Map<String, Comment> comments = new HashMap<String, Comment>();
        for (DataSnapshot commentSnapshot : DATA_SNAPSHOT.child("comments").getChildren()) {
            Comment comment = commentSnapshot.getValue(Comment.class);
            comments.put(commentSnapshot.getKey(), comment);
        }
        data.setComments(comments);
    }

    /**
     * Download recipe steps to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadSteps (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        Map<String, Step> steps = new HashMap<String, Step>();
        for (DataSnapshot stepsSnapshot : DATA_SNAPSHOT.child("steps").getChildren()) {
            Step step = stepsSnapshot.getValue(Step.class);
            steps.put(stepsSnapshot.getKey(), step);
        }
        data.setSteps(steps);
    }

    /**
     * Download recipe ingredients to recipe with data snapshot of recipe in database.
     * @param DATA_SNAPSHOT data snapshot of recipe in database.
     */
    public void downloadIngredients (DataSnapshot DATA_SNAPSHOT) {
        if (!hasData()) return;
        Map<String, Ingredient> ingredients = new HashMap<String, Ingredient>();
        for (DataSnapshot ingredientsSnapshot : DATA_SNAPSHOT.child("ingredients").getChildren()) {
            Ingredient ingredient = ingredientsSnapshot.getValue(Ingredient.class);
            ingredients.put(ingredientsSnapshot.getKey(), ingredient);
        }
        data.setIngredients(ingredients);
    }

    // UPLOAD --------------------------------------------------------------------------------------

    /**
     * Upload recipe title to database and return whether this was successful.
     * @return whether the recipe title was valid.
     */
    public boolean uploadTitle() {
        if (!hasTitle()) {
            Log.e(TAG, "uploadTitle: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeTitle(id).setValue(getData().getTitle());
        return true;
    }

    /**
     * Upload recipe user to database and return whether this was successful.
     * @return whether the recipe user was valid.
     */
    public boolean uploadUser () {
        if (!hasUser()) {
            Log.e(TAG, "uploadUser: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeUser(id).setValue(getData().getUser());
        return true;
    }

    /**
     * Upload recipe date/time created to database and return whether this was successful.
     * @return whether the recipe date/time created was valid.
     */
    public boolean uploadDatetimeCreated () {
        if (!hasDatetimeCreated()) {
            Log.e(TAG, "uploadDatetimeCreated: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeDatetimeUpdated(id).setValue(getData().getDatetime_created());
        return true;
    }

    /**
     * Upload recipe date/time updated to database and return whether this was successful.
     * @return whether the recipe date/time updated was valid.
     */
    public boolean uploadDatetimeUpdated () {
        if (!hasDatetimeUpdated()) {
            Log.e(TAG, "uploadDatetimeUpdated: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeDatetimeUpdated(id).setValue(getData().getDatetime_updated());
        return true;
    }

    /**
     * Upload recipe time to database and return whether this was successful.
     * @return whether the recipe time was valid.
     */
    public boolean uploadTime () {
        if (!hasTime()) {
            Log.e(TAG, "uploadTime: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeTime(id).setValue(getData().getTime());
        return true;
    }

    /**
     * Upload recipe portions to database and return whether this was successful.
     * @return whether the recipe portions was valid.
     */
    public boolean uploadPortions () {
        if (!hasPortions()) {
            Log.e(TAG, "uploadPortions: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipePortions(id).setValue(getData().getPortions());
        return true;
    }

    /**
     * Upload recipe views to database and return whether this was successful.
     * @return whether the recipe views was valid.
     */
    public boolean uploadViews () {
        if (!hasViews()) {
            Log.e(TAG, "uploadViews: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeViews(id).setValue(getData().getViews());;
        return true;
    }

    /**
     * Upload recipe info to database and return whether this was successful.
     * @return whether the recipe info was valid.
     */
    public boolean uploadInfo () {
        if (!hasInfo()) {
            Log.e(TAG, "uploadInfo: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeInfo(id).setValue(getData().getInfo());;
        return true;
    }

    /**
     * Upload recipe thumbs up to database and return whether this was successful.
     * @return whether the recipe thumbs up was valid.
     */
    public boolean uploadThumbsUp () {
        if (!hasThumbsUp()) {
            Log.e(TAG, "uploadThumbsUp: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeThumbsUp(id).setValue(getData().getThumbs_up());;
        return true;
    }

    /**
     * Upload recipe thumbs down to database and return whether this was successful.
     * @return whether the recipe thumbs down was valid.
     */
    public boolean uploadThumbsDown () {
        if (!hasThumbsDown()) {
            Log.e(TAG, "uploadThumbsDown: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeThumbsDown(id).setValue(getData().getThumbs_down());;
        return true;
    }

    /**
     * Upload recipe ratings to database and return whether this was successful.
     * @return whether the recipe ratings was valid.
     */
    public boolean uploadRatings () {
        if (!hasRatings()) {
            Log.e(TAG, "uploadRatings: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeRatings(id).setValue(getData().getRatings());
        return true;
    }

    /**
     * Upload recipe comments to database and return whether this was successful.
     * @return whether the recipe comments was valid.
     */
    public boolean uploadComments () {
        if (!hasComments()) {
            Log.e(TAG, "uploadComments: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeComments(id).setValue(getData().getComments());
        return true;
    }

    /**
     * Upload recipe steps to database and return whether this was successful.
     * @return whether the recipe steps was valid.
     */
    public boolean uploadSteps () {
        if (hasSteps()) {
            Log.e(TAG, "uploadSteps: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeSteps(id).setValue(getData().getSteps());
        return true;
    }

    /**
     * Upload recipe ingredients to database and return whether this was successful.
     * @return whether the recipe ingredients was valid.
     */
    public boolean uploadIngredients () {
        if (hasIngredients()) {
            Log.e(TAG, "uploadIngredients: Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipeIngredients(id).setValue(data.getIngredients());
        return true;
    }

    /**
     * Upload recipe to database and return whether this was successful.
     * @return whether the recipe was valid.
     */
    public boolean uploadAll() {
        if (!hasData()) {
            Log.e(TAG, "uploadAll(): Can't upload empty data");
            return false;
        }
        MatbitDatabase.recipe(id).setValue(data);
        MatbitDatabase.recipeRatings(id).setValue(data.getRatings());
        MatbitDatabase.recipeComments(id).setValue(data.getComments());
        MatbitDatabase.recipeSteps(id).setValue(data.getSteps());
        MatbitDatabase.recipeIngredients(id).setValue(data.getIngredients());
        return true;
    }

    // STATIC METHODS ------------------------------------------------------------------------------

    /**
     * Comparator for comparing recipe to recipe alphabetically by their title, A-Å,
     */
    public static final Comparator<Recipe> ALPHABETICAL_COMPARATOR_ASC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            Locale noLocale = new Locale("no", "NO");
            Collator noCollator = Collator.getInstance(noLocale);
            noCollator.setStrength(Collator.PRIMARY);
            return noCollator.compare(a.getData().getTitle(), b.getData().getTitle());
        }
    };

    /**
     * Comparator for comparing recipe to recipe alphabetically by their title, Å-A,
     */
    public static final Comparator<Recipe> ALPHABETICAL_COMPARATOR_DESC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            Locale noLocale = new Locale("no", "NO");
            Collator noCollator = Collator.getInstance(noLocale);
            noCollator.setStrength(Collator.PRIMARY);
            return noCollator.compare(b.getData().getTitle(), a.getData().getTitle());
        }
    };

    /**
     * Comparator for comparing recipe to recipe by their views, low to high,
     */
    public static final Comparator<Recipe> VIEWS_COMPARATOR_ASC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(a.getData().getViews(), b.getData().getViews());
        }
    };

    /**
     * Comparator for comparing recipe to recipe by their views, high to low,
     */
    public static final Comparator<Recipe> VIEWS_COMPARATOR_DESC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(b.getData().getViews(), a.getData().getViews());
        }
    };

    /**
     * Comparator for comparing recipe to recipe by their ratings, low to high,
     */
    public static final Comparator<Recipe> RATING_COMPARATOR_ASC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(
                    a.getData().getThumbs_up() - a.getData().getThumbs_down(),
                    b.getData().getThumbs_up() - b.getData().getThumbs_down()
            );        }
    };

    /**
     * Comparator for comparing recipe to recipe by their ratings, high to low,
     */
    public static final Comparator<Recipe> RATING_COMPARATOR_DESC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(
                    b.getData().getThumbs_up() - b.getData().getThumbs_down(),
                    a.getData().getThumbs_up() - a.getData().getThumbs_down()
            );
        }
    };

    /**
     * Comparator for comparing recipe to recipe by their dates, old to new,
     */
    public static final Comparator<Recipe> DATE_COMPARATOR_ASC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return DateUtility.stringToDate(a.getData().getDatetime_created())
                    .compareTo(DateUtility.stringToDate(b.getData().getDatetime_created()));
        }
    };

    /**
     * Comparator for comparing recipe to recipe by their dates, new to old,
     */
    public static final Comparator<Recipe> DATE_COMPARATOR_DESC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return DateUtility.stringToDate(b.getData().getDatetime_created())
                    .compareTo(DateUtility.stringToDate(a.getData().getDatetime_created()));
        }
    };

    /**
     * Comparator for comparing recipe to recipe by their time, low to high,
     */
    public static final Comparator<Recipe> TIME_COMPARATOR_ASC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(a.getData().getTime(), b.getData().getTime());
        }
    };

    /**
     * Comparator for comparing recipe to recipe by their time, high to low,
     */
    public static final Comparator<Recipe> TIME_COMPARATOR_DESC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(b.getData().getTime(), a.getData().getTime());
        }
    };

}