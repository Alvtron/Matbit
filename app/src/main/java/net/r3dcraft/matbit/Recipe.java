package net.r3dcraft.matbit;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by unibl on 09.10.2017.
 */

public class Recipe {
    private static final String TAG = "Recipe";
    private static final boolean THUMBS_UP = true;
    private static final boolean THUMBS_DOWN = false;
    private String id;
    private RecipeData data;
    private boolean synced = false;

    public Recipe() {
        // Default constructor
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

    public boolean downloadData(final DataSnapshot DATA_SNAPSHOT) {;
        this.id = DATA_SNAPSHOT.getKey();
        this.data = DATA_SNAPSHOT.getValue(RecipeData.class);
        downloadRatings(DATA_SNAPSHOT);
        downloadComments(DATA_SNAPSHOT);
        downloadSteps(DATA_SNAPSHOT);
        downloadIngredients(DATA_SNAPSHOT);
        return synced = true;
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
        double total = 0;
        for (Rating rating : data.getRatings().values())
            if (rating.getThumbsUp()) total++;
        BigDecimal average = new BigDecimal(100 * (total / (double)data.getRatings().size()));
        average = average.setScale(0, RoundingMode.HALF_UP);
        return average.intValue();
    }

    public boolean hasData() {
        if (data != null) {
            return true;
        }
        Log.i(TAG, "hasData(): Data not initialized.");
        return false;
    }

    public boolean addView() {
        if (data.hasViews() && synced) {
            data.setViews(data.getViews() + 1);
            uploadViews();
            return true;
        }
        else {
            Log.e(TAG, "addView(): Can't add views to uninitialized views");
            return false;
        }
    }

    public void addRating(final boolean VALUE) {
        data.addRating(new Rating(MatbitDatabase.getCurrentUser(), VALUE, DateTime.nowString()));
        uploadRatings();
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

    public void downloadRatings (DataSnapshot DATA_SNAPSHOT) {
        Map<String, Rating> ratings = new HashMap<String, Rating>();
        for (DataSnapshot ratingsSnapshot : DATA_SNAPSHOT.child("ratings").getChildren()) {
            ratings.put(ratingsSnapshot.getKey(), new Rating(
                    ratingsSnapshot.child("user").getValue(String.class),
                    ratingsSnapshot.child("rating").getValue(Boolean.class),
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
                    commentSnapshot.child("datetime").getValue(String.class))
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
        if (hasData() && data.hasTitle()) {
            MatbitDatabase.recipeTitle(id).setValue(getData().getTitle());
            return true;
        }
        else {
            Log.e(TAG, "uploadTitle(): Can't upload empty values");
            return false;
        }
    }

    public boolean uploadUser () {
        if (hasData() && data.hasUser()) {
            MatbitDatabase.recipeUser(id).setValue(getData().getUser());
            return true;
        }
        else {
            Log.e(TAG, "uploadUser(): Can't upload empty values");
            return false;
        }
    }

    public boolean uploadDatetimeCreated () {
        if (hasData() && data.hasDatetimeCreated()) {
            MatbitDatabase.recipeDatetimeUpdated(id).setValue(getData().getDatetime_created());
            return true;
        }
        else {
            Log.e(TAG, "uploadDatetimeCreated(): Can't upload empty values");
            return false;
        }
    }

    public boolean uploadDatetimeUpdated () {
        if (hasData() && data.hasDatetimeUpdated()) {
            MatbitDatabase.recipeDatetimeUpdated(id).setValue(getData().getDatetime_updated());
            return true;
        }
        else {
            Log.e(TAG, "uploadDatetimeUpdated(): Can't upload empty values");
            return false;
        }
    }

    public boolean uploadTime () {
        if (hasData() && data.hasTime()) {
            MatbitDatabase.recipeTime(id).setValue(getData().getTime());
            return true;
        }
        else {
            Log.e(TAG, "uploadTime(): Can't upload empty values");
            return false;
        }
    }

    public boolean uploadPortions () {
        if (hasData() && data.hasPortions()) {
            MatbitDatabase.recipePortions(id).setValue(getData().getPortions());
            return true;
        }
        else {
            Log.e(TAG, "uploadPortions(): Can't upload empty values");
            return false;
        }
    }

    public boolean uploadViews () {
        if (hasData() && data.hasViews()) {
            MatbitDatabase.recipeViews(id).setValue(getData().getViews());;
            return true;
        }
        else {
            Log.e(TAG, "uploadViews(): Can't upload empty values");
            return false;
        }
    }

    public boolean uploadRatings () {
        if (hasData() && data.hasRatings()) {
            MatbitDatabase.recipeRatings(id).setValue(getData().getRatings());
            return true;
        }
        else {
            Log.e(TAG, "uploadRatings(): Can't upload empty values");
            return false;
        }
    }

    public boolean uploadComments () {
        if (hasData() && data.hasComments()) {
            MatbitDatabase.recipeComments(id).setValue(getData().getComments());
            return true;
        }
        else {
            Log.e(TAG, "uploadComments(): Can't upload empty values");
            return false;
        }
    }

    public boolean uploadSteps () {
        if (hasData() && data.hasSteps()) {
            MatbitDatabase.recipeSteps(id).setValue(getData().getSteps());
            return true;
        }
        else {
            Log.e(TAG, "uploadSteps(): Can't upload empty values");
            return false;
        }
    }

    public boolean uploadIngredients () {
        if (hasData() && data.hasIngredients()) {
            MatbitDatabase.recipeIngredients(id).setValue(data.getIngredients());
            return true;
        }
        else {
            Log.e(TAG, "uploadIngredients(): Can't upload empty values");
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
}