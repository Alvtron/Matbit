package net.r3dcraft.matbit;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 22.10.2017.
 */

public class UserData {
    public String nickname;
    public String gender;
    public String birthday;
    public String signUpDate;
    public String lastLoginDate;
    public int exp;
    private Map<String, String> following = new HashMap<String, String>();
    private Map<String, String> followers = new HashMap<String, String>();
    private Map<String, String> recipes = new HashMap<String, String>();
    private Map<String, String> favorites = new HashMap<String, String>();

    public UserData() {
    }

    public UserData(String nickname, String gender, String birthday, String signUpDate, String lastLoginDate, int exp, Map<String, String> following, Map<String, String> followers, Map<String, String> recipes, Map<String, String> favorites) {
        this.nickname = nickname;
        this.gender = gender;
        this.birthday = birthday;
        this.signUpDate = signUpDate;
        this.lastLoginDate = lastLoginDate;
        this.exp = exp;
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
}
