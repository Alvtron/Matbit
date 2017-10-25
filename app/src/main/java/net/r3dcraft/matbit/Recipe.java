package net.r3dcraft.matbit;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by unibl on 09.10.2017.
 */

public class Recipe {
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

    public void addView() {
        if (data.getViews() != -1 && synced) {
            data.setViews(data.getViews() + 1);
            MatbitDatabase.recipeViews(id).setValue(data.getViews());
        }
    }

    public double getRatingAverage() {
        double total = 0;
        for (Rating rating : data.getRatings().values())
            total += rating.getRating();
        BigDecimal average = new BigDecimal(total / (double)data.getRatings().size());
        average = average.setScale(2, RoundingMode.HALF_UP);
        return average.doubleValue();
    }

    public double getRatingAveragePercentage() {
        double total = 0;
        for (Rating rating : data.getRatings().values())
            total += rating.getRating();
        BigDecimal average = new BigDecimal(((total / (double)data.getRatings().size()) / 5.00) * 100.00);
        average = average.setScale(2, RoundingMode.HALF_UP);
        return average.doubleValue();
    }

    public void uploadTitle() {
        MatbitDatabase.recipeTitle(id).setValue(getData().getTitle());
    }

    public void downloadTitle(DataSnapshot DATA_SNAPSHOT) {
        data.setTitle(DATA_SNAPSHOT.child("title").getValue(String.class));
    }

    public void uploadUser () {
        MatbitDatabase.recipeUser(id).setValue(getData().getUser());
    }

    public void downloadUser (DataSnapshot DATA_SNAPSHOT) {
        data.setUser(DATA_SNAPSHOT.child("user").getValue(String.class));
    }

    public void uploadDatetimeCreated () {
        MatbitDatabase.recipeDatetimeCreated(id).setValue(getData().getDatetime_created());
    }

    public void downloadDatetimeCreated (DataSnapshot DATA_SNAPSHOT) {
        data.setDatetime_created(DATA_SNAPSHOT.child("datetime_created").getValue(String.class));
    }

    public void uploadDatetimeUpdated () {
        MatbitDatabase.recipeDatetimeUpdated(id).setValue(getData().getDatetime_updated());
    }

    public void downloadDatetimeUpdated (DataSnapshot DATA_SNAPSHOT) {
        data.setDatetime_updated(DATA_SNAPSHOT.child("datetime_updated").getValue(String.class));
    }

    public void uploadTime () {
        MatbitDatabase.recipeTime(id).setValue(getData().getTime());
    }

    public void downloadTime (DataSnapshot DATA_SNAPSHOT) {
        data.setTime(DATA_SNAPSHOT.child("time").getValue(Integer.class));
    }

    public void uploadPortions () {
        MatbitDatabase.recipePortions(id).setValue(getData().getPortions());
    }

    public void downloadPortions (DataSnapshot DATA_SNAPSHOT) {
        data.setPortions(DATA_SNAPSHOT.child("portions").getValue(Integer.class));
    }

    public void uploadViews () {
        MatbitDatabase.recipeViews(id).setValue(getData().getViews());;
    }

    public void downloadViews (DataSnapshot DATA_SNAPSHOT) {
        data.setViews(DATA_SNAPSHOT.child("views").getValue(Integer.class));
    }

    public void uploadRatings () {
        MatbitDatabase.recipeRatings(id).setValue(getData().getRatings());
    }

    public void downloadRatings (DataSnapshot DATA_SNAPSHOT) {
        Map<String, Rating> ratings = new HashMap<String, Rating>();
        for (DataSnapshot ratingsSnapshot : DATA_SNAPSHOT.child("ratings").getChildren()) {
            ratings.put(ratingsSnapshot.getKey(), new Rating(
                    ratingsSnapshot.child("user").getValue(String.class),
                    ratingsSnapshot.child("rating").getValue(Integer.class),
                    ratingsSnapshot.child("datetime").getValue(String.class))
            );
        }
        data.setRatings(ratings);
    }

    public void uploadComments () {
        MatbitDatabase.recipeComments(id).setValue(getData().getComments());
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

    public void uploadSteps () {
        MatbitDatabase.recipeSteps(id).setValue(getData().getSteps());
    }

    public void downloadSteps (DataSnapshot DATA_SNAPSHOT) {
        Map<String, Step> steps = new HashMap<String, Step>();
        for (DataSnapshot stepsSnapshot : DATA_SNAPSHOT.child("steps").getChildren()) {
            steps.put(stepsSnapshot.getKey(), new Step(stepsSnapshot.child("string").getValue(String.class)));
        }
        data.setSteps(steps);
    }

    public void uploadIngredients () {
        MatbitDatabase.recipeIngredients(id).setValue(data.getIngredients());
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

    public void uploadAll() {
        MatbitDatabase.RECIPES.child(id).setValue(data);
        MatbitDatabase.recipeRatings(id).setValue(data.getRatings());
        MatbitDatabase.recipeComments(id).setValue(data.getComments());
        MatbitDatabase.recipeSteps(id).setValue(data.getSteps());
        MatbitDatabase.recipeIngredients(id).setValue(data.getIngredients());
    }
}