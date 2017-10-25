package net.r3dcraft.matbit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by unibl on 22.10.2017.
 */

public final class UserExamples {
    public static UserData user1 () {
        UserData userData = new UserData();
        userData.setNickname("Alvtron");
        userData.setGender("Male");
        userData.setBirthday("1994-01-16 10:00:00");
        userData.setSignUpDate("2017-10-07 10:00:00");
        userData.setLastLoginDate(DateTime.nowString());
        userData.setExp(12345);
        Map<String, String> following = new HashMap<String, String>();
        following.put("k7wRLHlSaRMbHU34LFJw67mqNy82", DateTime.nowString());
        Map<String, String> followers = new HashMap<String, String>();
        followers.put("k7wRLHlSaRMbHU34LFJw67mqNy82", DateTime.nowString());
        Map<String, String> recipes = new HashMap<String, String>();
        recipes.put("123456789", DateTime.nowString());
        Map<String, String> favorites = new HashMap<String, String>();
        favorites.put("123456789", DateTime.nowString());
        userData.setFollowing(following);
        userData.setFollowers(followers);
        userData.setRecipes(recipes);
        userData.setFavorites(favorites);
        return userData;
    }

    public static UserData user2 () {
        UserData userData = new UserData();
        userData.setNickname("Alvtrea");
        userData.setGender("Female");
        userData.setBirthday("1994-02-21 10:00:00");
        userData.setSignUpDate("2017-10-07 10:00:00");
        userData.setLastLoginDate(DateTime.nowString());
        userData.setExp(4321);
        Map<String, String> following = new HashMap<String, String>();
        following.put("dTnPmCyNiTVmcr3rrTAr5Ku6RPo2", DateTime.nowString());
        Map<String, String> followers = new HashMap<String, String>();
        followers.put("dTnPmCyNiTVmcr3rrTAr5Ku6RPo2", DateTime.nowString());
        Map<String, String> recipes = new HashMap<String, String>();
        recipes.put("123456789", DateTime.nowString());
        Map<String, String> favorites = new HashMap<String, String>();
        favorites.put("123456789", DateTime.nowString());
        userData.setFollowing(following);
        userData.setFollowers(followers);
        userData.setRecipes(recipes);
        userData.setFavorites(favorites);
        return userData;
    }
}
