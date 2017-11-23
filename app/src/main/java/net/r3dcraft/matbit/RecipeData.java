package net.r3dcraft.matbit;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 *
 * The RecipeData class is a data structure class that represents a recipe block in the Firebase
 * Database. Any changes made here will have an impact on the data structure in the database.
 *
 * Since Google Firebase uses clever ClassWrapping, this class can be use directly with both writing
 * and storing recipe-data from the database, except of ratings, comments, steps
 * and ingredients. I have not yet implemented a ClassWrapper for these, so they need to be uploaded
 * and downloaded separately.
 */

public class RecipeData {
    @Exclude private final static String TAG = "RecipeData";

    private String title;
    private String user;
    private String user_nickname;
    private String datetime_created;
    private String datetime_updated;
    private String info;
    private String category;
    private int time;
    private int portions;
    private int views;
    private int thumbs_up;
    private int thumbs_down;
    @Exclude private Map<String, Rating> ratings;
    @Exclude private Map<String, Comment> comments;
    @Exclude private Map<String, Step> steps;
    @Exclude private Map<String, Ingredient> ingredients;

    public RecipeData() {
        title = new String();
        user = new String();
        user_nickname = new String();
        datetime_created = new String();
        datetime_updated = new String();
        info = new String();
        category = new String();
        time = -1;
        portions = -1;
        views = -1;
        thumbs_up = -1;
        thumbs_down = -1;
        ratings = new HashMap<>();
        comments = new HashMap<>();
        steps = new HashMap<>();
        ingredients = new HashMap<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getDatetime_created() {
        return datetime_created;
    }

    public void setDatetime_created(String datetime_created) {
        this.datetime_created = datetime_created;
    }

    public String getDatetime_updated() {
        return datetime_updated;
    }

    public void setDatetime_updated(String datetime_updated) {
        this.datetime_updated = datetime_updated;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getThumbs_up() {
        return thumbs_up;
    }

    public void setThumbs_up(int thumbs_up) {
        this.thumbs_up = thumbs_up;
    }

    public int getThumbs_down() {
        return thumbs_down;
    }

    public void setThumbs_down(int thumbs_down) {
        this.thumbs_down = thumbs_down;
    }

    public int getPortions() {
        return portions;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    @Exclude
    public Map<String, Rating> getRatings() {
        return ratings;
    }

    @Exclude
    public void setRatings(Map<String, Rating> ratings) {
        this.ratings = ratings;
    }

    @Exclude
    public void addRating(String key, Rating rating) {
        this.ratings.put(key, rating);
    }

    @Exclude
    public Map<String, Comment> getComments() {
        return comments;
    }

    @Exclude
    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }

    @Exclude
    public void addComments(String key, Comment comment) {
        this.comments.put(key, comment);
    }

    @Exclude
    public Map<String, Step> getSteps() {
        return steps;
    }

    @Exclude
    public void setSteps(Map<String, Step> steps) {
        this.steps = steps;
    }

    @Exclude
    public void addStep(Step step) {
        this.steps.put(Integer.toString(this.steps.size()), step);
    }

    @Exclude
    public Map<String, Ingredient> getIngredients() {
        return ingredients;
    }

    @Exclude
    public void setIngredients(Map<String, Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Exclude
    public void addIngredient(Ingredient ingredient) {
        this.ingredients.put(Integer.toString(this.ingredients.size()), ingredient);
    }
}
