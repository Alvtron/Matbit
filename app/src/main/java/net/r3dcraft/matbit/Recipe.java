package net.r3dcraft.matbit;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

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
 */

public class Recipe {
    public enum THUMB { UP, DOWN, NOTHING }
    private static final String TAG = "Recipe";
    private String id;
    private RecipeData data;
    private boolean synced = false;

    public Recipe() {
        data = new RecipeData();
    }

    @Override
    public String toString() {
        return getData().getTitle();
    }

    public Recipe(final DataSnapshot DATA_SNAPSHOT) {
        downloadData(DATA_SNAPSHOT);
    }

    public Recipe(String id, RecipeData data, ArrayList<Rating> ratings, ArrayList<Comment> comments, ArrayList<Step> steps, ArrayList<Ingredient> ingredients) {
        this.id = id;
        this.data = data;
    }

    public Recipe(String id, RecipeData recipeData) {
        this.id = id;
        this.data = recipeData;
    }

    public void printRecipeToLog(){
        printRecipeToLog(this);
    }

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
        Log.d(TAG, "time: " + Integer.toString(recipe.getData().getTime()));
        Log.d(TAG, "portions: " + Integer.toString(recipe.getData().getPortions()));
        Log.d(TAG, "views: " + Integer.toString(recipe.getData().getViews()));
        Log.d(TAG, "thumbs_up: " + Integer.toString(recipe.getData().getThumbs_up()));
        Log.d(TAG, "thumbs_down: " + Integer.toString(recipe.getData().getThumbs_down()));
        Log.d(TAG, "ratings: " + Integer.toString(recipe.getData().getRatings().size()));
        Log.d(TAG, "comments: " + Integer.toString(recipe.getData().getComments().size()));
        Log.d(TAG, "steps: " + Integer.toString(recipe.getData().getSteps().size()));
        Log.d(TAG, "ingredients: " + Integer.toString(recipe.getData().getIngredients().size()));
    }

    public boolean downloadData(final DataSnapshot DATA_SNAPSHOT) {;
        this.id = DATA_SNAPSHOT.getKey();
        this.data = DATA_SNAPSHOT.getValue(RecipeData.class);
        downloadRatings(DATA_SNAPSHOT);
        downloadComments(DATA_SNAPSHOT);
        downloadSteps(DATA_SNAPSHOT);
        downloadIngredients(DATA_SNAPSHOT);
        return synced = true;
    }

    public void createNewRecipe(String title, int time, int portions, String category, String info, ArrayList<Step> steps, ArrayList<Ingredient> ingredients) {
        data.setTitle(title);
        data.setUser(MatbitDatabase.getCurrentUserID());
        data.setDatetime_created(DateUtility.nowString());
        data.setDatetime_updated(DateUtility.nowString());
        data.setTime(time);
        data.setPortions(portions);
        data.setViews(0);
        data.setCategory(category);
        data.setThumbs_up(0);
        data.setThumbs_down(0);
        data.setInfo(info);
        data.setRatings(new HashMap<String, Rating>());
        data.setComments(new HashMap<String, Comment>());
        for (Step step : steps)
            data.addStep(step);
        for (Ingredient ingredient : ingredients)
            data.addIngredient(ingredient);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setData(RecipeData data) {
        this.data = data;
    }

    public RecipeData getData() {
        return data;
    }

    public int getRatingAverage() {
        if (hasRatings()) {
            double total = 0;
            for (Rating rating : data.getRatings().values())
                if (rating.getThumbsUp()) total++;
            BigDecimal average = new BigDecimal(100 * (total / (double) data.getRatings().size()));
            average = average.setScale(0, RoundingMode.HALF_UP);
            return average.intValue();
        }
        else
            return 0;
    }

    // ADD & REMOVE --------------------------------------------------------------------------------------

    public boolean addView() {
        if (hasViews() && synced) {
            data.setViews(data.getViews() + 1);
            uploadViews();
            return true;
        }
        else {
            Log.e(TAG, "addView(): Can't add views to uninitialized views");
            return false;
        }
    }

    public void addRating(final THUMB thumb) {
        if (thumb == THUMB.NOTHING)
            return;

        removeUserRating();
        if (thumb == THUMB.UP) {
            data.setThumbs_up(data.getThumbs_up() + 1);
            uploadThumbsUp();
            Rating new_rating = new Rating(MatbitDatabase.getCurrentUserID(), true, DateUtility.nowString());
            String key = MatbitDatabase.recipeRatings(id).push().getKey();
            MatbitDatabase.recipeRatings(id).child(key).setValue(new_rating);
            data.addRating(key, new_rating);
        } else if (thumb == THUMB.DOWN) {
            data.setThumbs_down(data.getThumbs_down() + 1);
            uploadThumbsDown();
            Rating new_rating = new Rating(MatbitDatabase.getCurrentUserID(), false, DateUtility.nowString());
            String key = MatbitDatabase.recipeRatings(id).push().getKey();
            MatbitDatabase.recipeRatings(id).child(key).setValue(new_rating);
            data.addRating(key, new_rating);
        }
    }

    public void removeUserRating() {
        ArrayList<String> keys_to_remove = new ArrayList<String>();
        for (Map.Entry<String, Rating> ratingSet : data.getRatings().entrySet()) {
            String key = ratingSet.getKey();
            Rating rating = ratingSet.getValue();
            if (rating.getUser().equals(MatbitDatabase.getCurrentUserID())) {
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
        printRecipeToLog(this);
    }

    public THUMB hasUserRated(){
        for (Rating rating : data.getRatings().values())
            if (rating.getUser().equals(MatbitDatabase.getCurrentUserID())) {
                if (rating.getThumbsUp())
                    return THUMB.UP;
                else
                    return THUMB.DOWN;
            }
        return THUMB.NOTHING;
    }

    public void addComment(final String COMMENT) {
        Comment comment = new Comment(MatbitDatabase.USER.getUid(), COMMENT, DateUtility.nowString(), DateUtility.nowString());
        MatbitDatabase.recipeComments(id).push().setValue(comment);
    }

    public static void changeComment(final String RECIPE_UID, final String COMMENT_UID, final String COMMENT) {
        MatbitDatabase.recipeComments(RECIPE_UID).child(COMMENT_UID).child("datetimeUpdated").setValue(DateUtility.nowString());
        MatbitDatabase.recipeComments(RECIPE_UID).child(COMMENT_UID).child("comment").setValue(COMMENT);
    }

    public String getTimeToText() {
        if (hasTime()) {
            String hours = Integer.toString(data.getTime() / 60 % 24);
            String minutes = Integer.toString(data.getTime() % 60);
            if (data.getTime() > 60 && data.getTime() % 60 != 0)
                return hours + "t:" + minutes + "m";
            else if (data.getTime() > 60 && data.getTime() % 60 == 0)
                return hours + "t";
            else if (data.getTime() < 60 && data.getTime() % 60 != 0)
                return minutes + "m";
            else
                return "0";
        } else
            return "";
    }

    // VALIDATION ----------------------------------------------------------------------------------

    public boolean hasData() {
        if (data == null) {
            Log.i(TAG, "hasData(): Data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasCompleteData() {
        if (!hasData()) {
            Log.i(TAG, "hasCompleteData(): data not initialized.");
            return false;
        } else if (!hasTitle()) {
            Log.i(TAG, "hasCompleteData(): title not initialized.");
            return false;
        } else if (!hasUser()) {
            Log.i(TAG, "hasCompleteData(): user not initialized.");
            return false;
        } else if (!hasUserNickname()) {
            Log.i(TAG, "hasCompleteData(): user_nickname not initialized.");
            return false;
        } else if (!hasDatetimeCreated()) {
            Log.i(TAG, "hasCompleteData(): datetime_created not initialized.");
            return false;
        } else if (!hasIngredients()) {
            Log.i(TAG, "hasCompleteData(): ingredients not initialized.");
            return false;
        } else if (!hasSteps()) {
            Log.i(TAG, "hasCompleteData(): steps not initialized.");
            return false;
        } else if (!hasTime()) {
            Log.i(TAG, "hasCompleteData(): time not initialized.");
            return false;
        } else return true;
    }

    public boolean hasTitle() {
        if (!hasData())
            return false;
        if (data.getTitle() == null) {
            Log.i(TAG, "hasTitle: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasUser() {
        if (!hasData())
            return false;
        if (data.getUser() == null) {
            Log.i(TAG, "hasUser: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasUserNickname() {
        if (!hasData())
            return false;
        if (data.getUser_nickname() == null) {
            Log.i(TAG, "hasUserNickname: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasDatetimeCreated() {
        if (!hasData())
            return false;
        if (data.getDatetime_created() == null) {
            Log.i(TAG, "hasDatetimeCreated: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasDatetimeUpdated() {
        if (!hasData())
            return false;
        if (data.getDatetime_updated() == null) {
            Log.i(TAG, "hasDatetimeUpdated: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasTime() {
        if (!hasData())
            return false;
        if (data.getTime() < 0) {
            Log.i(TAG, "hasTime: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasPortions() {
        if (!hasData())
            return false;
        if (data.getPortions() < 0) {
            Log.i(TAG, "hasPortions: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasViews() {
        if (!hasData())
            return false;
        if (data.getViews() < 0) {
            Log.i(TAG, "hasViews: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasInfo() {
        if (!hasData())
            return false;
        if (data.getInfo() == null) {
            Log.i(TAG, "hasInfo: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasCategory() {
        if (!hasData())
            return false;
        if (data.getCategory() == null) {
            Log.i(TAG, "hasCategory: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasThumbsUp() {
        if (!hasData())
            return false;
        if (data.getThumbs_up() < 0) {
            Log.i(TAG, "hasThumbsUp: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasThumbsDown() {
        if (!hasData())
            return false;
        if (data.getThumbs_down() < 0) {
            Log.i(TAG, "hasThumbsDown: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasRatings() {
        if (!hasData())
            return false;
        if (data.getRatings() == null || data.getRatings().size() == 0) {
            Log.i(TAG, "hasRatings: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasComments() {
        if (!hasData())
            return false;
        if (data.getComments() == null || data.getComments().size() == 0) {
            Log.i(TAG, "hasComments: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasSteps() {
        if (!hasData())
            return false;
        if (data.getSteps() == null || data.getSteps().size() == 0) {
            Log.i(TAG, "hasSteps: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasIngredients() {
        if (!hasData())
            return false;
        if (data.getIngredients() == null || data.getIngredients().size() == 0) {
            Log.i(TAG, "hasIngredients: data not initialized.");
            return false;
        }
        return true;
    }

    // DOWNLOAD ------------------------------------------------------------------------------------

    public void downloadTitle(DataSnapshot DATA_SNAPSHOT) {
        data.setTitle(DATA_SNAPSHOT.child("title").getValue(String.class));
    }

    public void downloadUser (DataSnapshot DATA_SNAPSHOT) {
        data.setUser(DATA_SNAPSHOT.child("user").getValue(String.class));
    }

    public void downloadDatetimeCreated (DataSnapshot DATA_SNAPSHOT) {
        data.setDatetime_created(DATA_SNAPSHOT.child("datetime_created").getValue(String.class));
    }

    public void downloadDatetimeUpdated (DataSnapshot DATA_SNAPSHOT) {
        data.setDatetime_updated(DATA_SNAPSHOT.child("datetime_updated").getValue(String.class));
    }

    public void downloadTime (DataSnapshot DATA_SNAPSHOT) {
        data.setTime(DATA_SNAPSHOT.child("time").getValue(Integer.class));
    }

    public void downloadPortions (DataSnapshot DATA_SNAPSHOT) {
        data.setPortions(DATA_SNAPSHOT.child("portions").getValue(Integer.class));
    }

    public void downloadViews (DataSnapshot DATA_SNAPSHOT) {
        data.setViews(DATA_SNAPSHOT.child("views").getValue(Integer.class));
    }

    public void downloadThumbsUp (DataSnapshot DATA_SNAPSHOT) {
        data.setThumbs_up(DATA_SNAPSHOT.child("thumbs_up").getValue(Integer.class));
    }

    public void downloadThumbsDown (DataSnapshot DATA_SNAPSHOT) {
        data.setThumbs_down(DATA_SNAPSHOT.child("thumbs_down").getValue(Integer.class));
    }

    public void downloadInfo (DataSnapshot DATA_SNAPSHOT) {
        data.setInfo(DATA_SNAPSHOT.child("info").getValue(String.class));
    }

    public void downloadRatings (DataSnapshot DATA_SNAPSHOT) {
        Map<String, Rating> ratings = new HashMap<String, Rating>();
        for (DataSnapshot ratingsSnapshot : DATA_SNAPSHOT.child("ratings").getChildren()) {
            ratings.put(ratingsSnapshot.getKey(), new Rating(
                    ratingsSnapshot.child("user").getValue(String.class),
                    ratingsSnapshot.child("thumbsUp").getValue(Boolean.class),
                    ratingsSnapshot.child("datetime").getValue(String.class))
            );
        }
        data.setRatings(ratings);
    }

    public void downloadComments (DataSnapshot DATA_SNAPSHOT) {
        Map<String, Comment> comments = new HashMap<String, Comment>();
        for (DataSnapshot commentSnapshot : DATA_SNAPSHOT.child("comments").getChildren()) {
            comments.put(commentSnapshot.getKey(), new Comment(
                    commentSnapshot.child("user").getValue(String.class),
                    commentSnapshot.child("comment").getValue(String.class),
                    commentSnapshot.child("datetimeCreated").getValue(String.class),
                    commentSnapshot.child("datetimeUpdated").getValue(String.class))
            );
        }
        data.setComments(comments);
    }
    public void downloadSteps (DataSnapshot DATA_SNAPSHOT) {
        Map<String, Step> steps = new HashMap<String, Step>();
        for (DataSnapshot stepsSnapshot : DATA_SNAPSHOT.child("steps").getChildren()) {
            steps.put(stepsSnapshot.getKey(), new Step(stepsSnapshot.child("string").getValue(String.class)));
        }
        data.setSteps(steps);
    }

    public void downloadIngredients (DataSnapshot DATA_SNAPSHOT) {
        Map<String, Ingredient> ingredients = new HashMap<String, Ingredient>();
        for (DataSnapshot ingredientsSnapshot : DATA_SNAPSHOT.child("ingredients").getChildren()) {
            ingredients.put(ingredientsSnapshot.getKey(), new Ingredient(
                    ingredientsSnapshot.child("course").getValue(String.class),
                    ingredientsSnapshot.child("name").getValue(String.class),
                    ingredientsSnapshot.child("amount").getValue(Double.class),
                    ingredientsSnapshot.child("measurement").getValue(String.class))
            );
        }
        data.setIngredients(ingredients);
    }

    // UPLOAD --------------------------------------------------------------------------------------

    public boolean uploadTitle() {
        if (hasTitle()) {
            MatbitDatabase.recipeTitle(id).setValue(getData().getTitle());
            return true;
        }
        else {
            Log.e(TAG, "uploadTitle: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadUser () {
        if (hasUser()) {
            MatbitDatabase.recipeUser(id).setValue(getData().getUser());
            return true;
        }
        else {
            Log.e(TAG, "uploadUser: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadDatetimeCreated () {
        if (hasDatetimeCreated()) {
            MatbitDatabase.recipeDatetimeUpdated(id).setValue(getData().getDatetime_created());
            return true;
        }
        else {
            Log.e(TAG, "uploadDatetimeCreated: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadDatetimeUpdated () {
        if (hasDatetimeUpdated()) {
            MatbitDatabase.recipeDatetimeUpdated(id).setValue(getData().getDatetime_updated());
            return true;
        }
        else {
            Log.e(TAG, "uploadDatetimeUpdated: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadTime () {
        if (hasTime()) {
            MatbitDatabase.recipeTime(id).setValue(getData().getTime());
            return true;
        }
        else {
            Log.e(TAG, "uploadTime: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadPortions () {
        if (hasPortions()) {
            MatbitDatabase.recipePortions(id).setValue(getData().getPortions());
            return true;
        }
        else {
            Log.e(TAG, "uploadPortions: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadViews () {
        if (hasViews()) {
            MatbitDatabase.recipeViews(id).setValue(getData().getViews());;
            return true;
        }
        else {
            Log.e(TAG, "uploadViews: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadInfo () {
        if (hasInfo()) {
            MatbitDatabase.recipeInfo(id).setValue(getData().getInfo());;
            return true;
        }
        else {
            Log.e(TAG, "uploadInfo: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadThumbsUp () {
        if (hasThumbsUp()) {
            MatbitDatabase.recipeThumbsUp(id).setValue(getData().getThumbs_up());;
            return true;
        }
        else {
            Log.e(TAG, "uploadThumbsUp: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadThumbsDown () {
        if (hasThumbsDown()) {
            MatbitDatabase.recipeThumbsDown(id).setValue(getData().getThumbs_down());;
            return true;
        }
        else {
            Log.e(TAG, "uploadThumbsDown: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadRatings () {
        if (hasRatings()) {
            MatbitDatabase.recipeRatings(id).setValue(getData().getRatings());
            return true;
        }
        else {
            Log.e(TAG, "uploadRatings: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadComments () {
        if (hasComments()) {
            MatbitDatabase.recipeComments(id).setValue(getData().getComments());
            return true;
        }
        else {
            Log.e(TAG, "uploadComments: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadSteps () {
        if (hasSteps()) {
            MatbitDatabase.recipeSteps(id).setValue(getData().getSteps());
            return true;
        }
        else {
            Log.e(TAG, "uploadSteps: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadIngredients () {
        if (hasIngredients()) {
            MatbitDatabase.recipeIngredients(id).setValue(data.getIngredients());
            return true;
        }
        else {
            Log.e(TAG, "uploadIngredients: Can't upload empty data");
            return false;
        }
    }

    public boolean uploadAll() {
        if (hasData()) {
            MatbitDatabase.RECIPES.child(id).setValue(data);
            MatbitDatabase.recipeRatings(id).setValue(data.getRatings());
            MatbitDatabase.recipeComments(id).setValue(data.getComments());
            MatbitDatabase.recipeSteps(id).setValue(data.getSteps());
            MatbitDatabase.recipeIngredients(id).setValue(data.getIngredients());
            return true;
        }
        else {
            Log.e(TAG, "uploadAll(): Can't upload empty data");
            return false;
        }
    }

    // STATIC METHODS ------------------------------------------------------------------------------

    public static final Comparator<Recipe> ALPHABETICAL_COMPARATOR_ASC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            Locale noLocale = new Locale("no", "NO");
            Collator noCollator = Collator.getInstance(noLocale);
            noCollator.setStrength(Collator.PRIMARY);
            return noCollator.compare(a.getData().getTitle(), b.getData().getTitle());
        }
    };

    public static final Comparator<Recipe> ALPHABETICAL_COMPARATOR_DESC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            Locale noLocale = new Locale("no", "NO");
            Collator noCollator = Collator.getInstance(noLocale);
            noCollator.setStrength(Collator.PRIMARY);
            return noCollator.compare(b.getData().getTitle(), a.getData().getTitle());
        }
    };

    public static final Comparator<Recipe> VIEWS_COMPARATOR_ASC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(a.getData().getViews(), b.getData().getViews());
        }
    };

    public static final Comparator<Recipe> VIEWS_COMPARATOR_DESC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(b.getData().getViews(), a.getData().getViews());
        }
    };

    public static final Comparator<Recipe> RATING_COMPARATOR_ASC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(a.getData().getRatings().size(), b.getData().getRatings().size());
        }
    };

    public static final Comparator<Recipe> RATING_COMPARATOR_DESC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(b.getData().getRatings().size(), a.getData().getRatings().size());
        }
    };

    public static final Comparator<Recipe> DATE_COMPARATOR_ASC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return DateUtility.stringToDate(a.getData().getDatetime_created())
                    .compareTo(DateUtility.stringToDate(b.getData().getDatetime_created()));
        }
    };

    public static final Comparator<Recipe> DATE_COMPARATOR_DESC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(b.getData().getTime(), a.getData().getTime());
        }
    };

    public static final Comparator<Recipe> TIME_COMPARATOR_ASC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(a.getData().getTime(), b.getData().getTime());
        }
    };

    public static final Comparator<Recipe> TIME_COMPARATOR_DESC = new Comparator<Recipe>() {
        @Override
        public int compare(Recipe a, Recipe b) {
            return Integer.compare(b.getData().getTime(), a.getData().getTime());
        }
    };

}