package net.r3dcraft.matbit;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 22.10.2017.
 *
 * The UserData class is a data structure class that represents a user block in the Firebase
 * Database. Any changes made here will have an impact on the data structure in the database.
 *
 * Since Google Firebase uses clever ClassWrapping, this class can be use directly with both writing
 * and storing user-data from the database.
 */

public class UserData {
    private String nickname;
    private String gender;
    private String birthday;
    private String signUpDate;
    private String lastLoginDate;
    private String bio;
    private int exp;
    private int num_followers;
    private int num_recipes;
    private Map<String, String> following;
    private Map<String, String> followers;
    private Map<String, String> recipes;
    private Map<String, String> favorites;

    public UserData() {
        nickname = new String();
        gender = new String();
        birthday = new String();
        signUpDate = new String();
        lastLoginDate = new String();
        bio = new String();
        exp = -1;
        num_followers = -1;
        num_recipes = -1;
        following = new HashMap<>();
        followers = new HashMap<>();
        recipes = new HashMap<>();
        favorites = new HashMap<>();
    }

    public UserData(String nickname, String gender, String birthday, String signUpDate, String lastLoginDate, String bio, int exp, int num_followers, int num_recipes, Map<String, String> following, Map<String, String> followers, Map<String, String> recipes, Map<String, String> favorites) {
        this.nickname = nickname;
        this.gender = gender;
        this.birthday = birthday;
        this.signUpDate = signUpDate;
        this.lastLoginDate = lastLoginDate;
        this.bio = bio;
        this.exp = exp;
        this.num_followers = num_followers;
        this.num_recipes = num_recipes;
        this.following = following;
        this.followers = followers;
        this.recipes = recipes;
        this.favorites = favorites;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(String signUpDate) {
        this.signUpDate = signUpDate;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public Map<String, String> getFollowing() {
        return following;
    }

    public void setFollowing(Map<String, String> following) {
        this.following = following;
    }

    public Map<String, String> getFollowers() {
        return followers;
    }

    public void setFollowers(Map<String, String> followers) {
        this.followers = followers;
    }

    public Map<String, String> getRecipes() {
        return recipes;
    }

    public void setRecipes(Map<String, String> recipes) {
        this.recipes = recipes;
    }

    public Map<String, String> getFavorites() {
        return favorites;
    }

    public void setFavorites(Map<String, String> favorites) {
        this.favorites = favorites;
    }

    public int getNum_followers() {
        return num_followers;
    }

    public void setNum_followers(int num_followers) {
        this.num_followers = num_followers;
    }

    public int getNum_recipes() {
        return num_recipes;
    }

    public void setNum_recipes(int num_recipes) {
        this.num_recipes = num_recipes;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
