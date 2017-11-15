package net.r3dcraft.matbit;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 */

public class User {
    private static final String TAG = "User";
    private String id;
    private UserData data;
    private boolean synced = false;

    public User() {
    }

    public User(final DataSnapshot DATA_SNAPSHOT) {
        downloadData(DATA_SNAPSHOT);
    }

    public boolean downloadData(final DataSnapshot DATA_SNAPSHOT) {;
        this.id = DATA_SNAPSHOT.getKey();
        this.data = DATA_SNAPSHOT.getValue(UserData.class);
        return synced = true;
    }

    // GETTERS / SETTERS ---------------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserData getData() {
        return data;
    }

    public void setData(UserData data) {
        this.data = data;
    }

    // ADD & REMOVE --------------------------------------------------------------------------------

    public boolean hasFollower(String followerUID) {
        if (getData().getFollowers().get(followerUID) == null)
            return false;
        else
            return true;
    }

    public void addFollower(String followerUID) {
        if (!hasFollower(followerUID)) {
            String date = DateUtility.nowString();
            getData().getFollowers().put(followerUID, date);
            getData().setNum_followers(getData().getNum_followers() + 1);
            MatbitDatabase.getUserFollowers(id).child(followerUID).setValue(date);
            uploadNumFollowers();
        }
    }

    public void removeFollower(String followerUID) {
        if (hasFollower(followerUID)) {
            getData().getFollowers().remove(followerUID);
            getData().setNum_followers(getData().getNum_followers() - 1);
            MatbitDatabase.getUserFollowers(id).child(followerUID).removeValue();
            uploadNumFollowers();
        }
    }

    // VALIDATION ----------------------------------------------------------------------------------

    public boolean hasData() {
        if (data == null) {
            Log.i(TAG, "hasData: data not initialized.");
            return false;
        }
        else return true;
    }

    public boolean hasCompleteData() {
        if (!hasData() || !hasNickname() || !hasSignUpDate() || !hasLastLoginDate()) {
            Log.i(TAG, "hasCompleteData: Crucial data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasNickname() {
        if (!hasData())
            return false;
        if (data.getNickname() == null) {
            Log.i(TAG, "hasNickname: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasGender() {
        if (!hasData())
            return false;
        if (data.getGender() == null || data.getGender().trim() == "") {
            Log.i(TAG, "hasGender: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasBirthday() {
        if (!hasData())
            return false;
        if (data.getBirthday() == null  || data.getBirthday().trim() == "") {
            Log.i(TAG, "hasBirthday: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasSignUpDate() {
        if (!hasData())
            return false;
        if (data.getSignUpDate() == null  || data.getSignUpDate().trim() == "") {
            Log.i(TAG, "hasSignUpDate: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasLastLoginDate() {
        if (!hasData())
            return false;
        if (data.getLastLoginDate() == null  || data.getLastLoginDate().trim() == "") {
            Log.i(TAG, "hasLastLoginDate: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasBio() {
        if (!hasData())
            return false;
        if (data.getBio() == null  || data.getBio().trim() == "") {
            Log.i(TAG, "hasBio: data not initialized.");
            return false;
        }
        return true;
    }
    public boolean hasExp() {
        if (!hasData())
            return false;
        if (data.getExp() < 0) {
            Log.i(TAG, "hasExp: data not initialized.");
            return false;
        }
        return true;
    }
    public boolean hasNumFollowers() {
        if (!hasData())
            return false;
        if (data.getNum_followers() < 0) {
            Log.i(TAG, "hasNumFollowers: data not initialized.");
            return false;
        }
        return true;
    }
    public boolean hasNumRecipes() {
        if (!hasData())
            return false;
        if (data.getNum_recipes() < 0) {
            Log.i(TAG, "hasNumRecipes: data not initialized.");
            return false;
        }
        return true;
    }
    public boolean hasFollowing() {
        if (!hasData())
            return false;
        if (data.getFollowing() == null || data.getFollowing().size() == 0) {
            Log.i(TAG, "hasFollowing: data not initialized.");
            return false;
        }
        return true;
    }
    public boolean hasFollowers() {
        if (!hasData())
            return false;
        if (data.getFollowers() == null || data.getFollowers().size() == 0) {
            Log.i(TAG, "hasFollowers: data not initialized.");
            return false;
        }
        return true;
    }
    public boolean hasRecipes() {
        if (!hasData())
            return false;
        if (data.getRecipes() == null || data.getRecipes().size() == 0) {
            Log.i(TAG, "hasRecipes: data not initialized.");
            return false;
        }
        return true;
    }

    public boolean hasFavorites() {
        if (!hasData())
            return false;
        if (data.getFavorites() == null || data.getFavorites().size() == 0) {
            Log.i(TAG, "hasFavorites: data not initialized.");
            return false;
        }
        return true;
    }

    // ADDERS --------------------------------------------------------------------------------------

    public void addFollow(String recipeID, String follow_date) {
        data.getFollowing().put(recipeID, follow_date);
    }

    public void addFollower(String recipeID, String follow_date) {
        data.getFollowers().put(recipeID, follow_date);
        data.setNum_followers(data.getNum_followers() + 1);
    }

    public void addRecipe(String recipeID, String upload_date) {
        data.getRecipes().put(recipeID, upload_date);
        data.setNum_recipes(data.getNum_recipes() + 1);
    }

    public void addFavorite(String recipeID, String add_date) {
        data.getFavorites().put(recipeID, add_date);
    }

    // UPLOAD --------------------------------------------------------------------------------------

    public boolean uploadNickname()  {
        if (!hasNickname()) {
            Log.e(TAG, "uploadNickname: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserNickname(id).setValue(data.getNickname());
            return true;
        }

    }

    public static void uploadNickname(String id, String nickname)  {
        MatbitDatabase.getUserNickname(id).setValue(nickname);
    }

    public boolean uploadGender()  {
        if (!hasGender()) {
            Log.e(TAG, "uploadGender: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserGender(id).setValue(data.getGender());
            return true;
        }
    }

    public static void uploadGender(String id, String gender)  {
        MatbitDatabase.getUserGender(id).setValue(gender);
    }

    public boolean uploadBirthday()  {
        if (!hasBirthday()) {
            Log.e(TAG, "uploadBirthday: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserBirthday(id).setValue(data.getBirthday());
            return true;
        }
    }

    public static void uploadBirthday(String id, String birthday)  {
        MatbitDatabase.getUserBirthday(id).setValue(birthday);
    }

    public boolean uploadSignUpDate()  {
        if (!hasSignUpDate()) {
            Log.e(TAG, "uploadSignUpDate: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserSignUpDate(id).setValue(data.getSignUpDate());
            return true;
        }
    }

    public static void uploadSignUpDate(String id, String signUpDate)  {
        MatbitDatabase.getUserSignUpDate(id).setValue(signUpDate);
    }

    public boolean uploadLastLoginDate()  {
        if (!hasLastLoginDate()) {
            Log.e(TAG, "uploadLastLoginDate: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserLastLoginDate(id).setValue(data.getLastLoginDate());
            return true;
        }
    }

    public static void uploadLastLoginDate(String id, String lastLoginDate)  {
        MatbitDatabase.getUserLastLoginDate(id).setValue(lastLoginDate);
    }

    public boolean uploadBio()  {
        if (!hasBio()) {
            Log.e(TAG, "uploadBio: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserBio(id).setValue(data.getBio());
            return true;
        }
    }

    public static void uploadBio(String id, String bio)  {
        MatbitDatabase.getUserBio(id).setValue(bio);
    }

    public boolean uploadExp()  {
        if (!hasExp()) {
            Log.e(TAG, "uploadExp: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserExp(id).setValue(data.getExp());
            return true;
        }
    }

    public static void uploadExp(String id, int exp)  {
        MatbitDatabase.getUserExp(id).setValue(exp);
    }

    public boolean uploadNumFollowers()  {
        if (!hasNumFollowers()) {
            Log.e(TAG, "uploadNumFollowers: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserNumFollowers(id).setValue(data.getNum_followers());
            return true;
        }
    }

    public static void uploadNumFollowers(String id, int num_followers)  {
        MatbitDatabase.getUserNumFollowers(id).setValue(num_followers);
    }

    public boolean uploadNumRecipes()  {
        if (!hasNumRecipes()) {
            Log.e(TAG, "uploadNumRecipes: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserNumRecipes(id).setValue(data.getNum_recipes());
            return true;
        }
    }

    public static void uploadNumRecipes(String id, int num_recipes)  {
        MatbitDatabase.getUserNumRecipes(id).setValue(num_recipes);
    }

    public boolean uploadFollowing() {
        if (!hasFollowing()) {
            Log.e(TAG, "uploadFollowing: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserFollowing(id).setValue(data.getFollowing());
            return true;
        }
    }

    public static void uploadFollowing(String id, String userID, String date)  {
        MatbitDatabase.getUserFollowing(id).child(userID).setValue(date);
    }

    public boolean uploadFollowers()  {
        if (!hasFollowers()) {
            Log.e(TAG, "uploadFollowers: Upload failed. No data found");
            return false;
        } else if (!uploadNumFollowers()) {
            Log.e(TAG, "uploadFollowers: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserFollowers(id).setValue(data.getFollowers());
            return true;
        }
    }

    public static void uploadFollowers(String id, String userID, String date)  {
        MatbitDatabase.getUserFollowers(id).child(userID).setValue(date);
    }

    public boolean uploadRecipes()  {
        if (!hasRecipes()) {
            Log.e(TAG, "uploadRecipes: Upload failed. No data found");
            return false;
        } else if (!uploadNumRecipes()) {
            Log.e(TAG, "uploadRecipes: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserRecipes(id).setValue(data.getRecipes());
            return true;
        }
    }

    public static void uploadRecipes(String id, String userID, String date)  {
        MatbitDatabase.getUserRecipes(id).child(userID).setValue(date);
    }

    public boolean uploadFavorites()  {
        if (!hasFavorites()) {
            Log.e(TAG, "uploadFavorites: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserFavorites(id).setValue(data.getFavorites());
            return true;
        }
    }

    public static void uploadFavorites(String id, String userID, String date)  {
        MatbitDatabase.getUserFavorites(id).child(userID).setValue(date);
    }

    public boolean uploadAll() {
        if (!hasData()) {
            Log.e(TAG, "uploadAll: Upload failed. No or not enough data");
            return false;
        } else {
            MatbitDatabase.getUser(id).setValue(data);
            return true;
        }
    }
}