package net.r3dcraft.matbit;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by unibl on 21.10.2017.
 */

public class RecipeData {
    private String title;
    private String user;
    private String datetime_created;
    private String datetime_updated;
    private int time;
    private int portions;
    private int views;
    private String category;

    @Exclude private Map<String, Rating> ratings = new HashMap<String, Rating>();
    @Exclude private Map<String, Comment> comments = new HashMap<String, Comment>();
    @Exclude private Map<String, Step> steps = new HashMap<String, Step>();
    @Exclude private Map<String, Ingredient> ingredients = new HashMap<String, Ingredient>();

    public RecipeData() {}

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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
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
    public void addRating(Rating rating) {
        this.ratings.put(Integer.toString(this.ratings.size()), rating);
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
    public void addComments(Comment comment) {
        this.comments.put(Integer.toString(this.comments.size()), comment);
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
