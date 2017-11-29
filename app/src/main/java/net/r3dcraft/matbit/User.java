package net.r3dcraft.matbit;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 *
 * The User Class represents a user in the Matbit Database. It is meant to be used when loading
 * users from the database. It stores the user's unique ID/key as a String and the data on that
 * key as a UserData object.
 *
 * This class also includes some functions:
 * Functions for adding and removing followers
 * Functions for uploading specific data of this user to the database, or everything.
 */

public class User {
    private static final String TAG = "User";
    private String id;
    private UserData data;

    /**
     * Default Constructor
     */
    public User() {}

    /**
     * Constructor
     * @param DATA_SNAPSHOT data snapshot of specific user in Matbit database
     */
    public User(final DataSnapshot DATA_SNAPSHOT) {
        downloadData(DATA_SNAPSHOT);
    }

    /**
     * Download user data with data snapshot of specific user in Matbit database
     * @param DATA_SNAPSHOT data snapshot of specific user in Matbit database
     */
    public void downloadData(final DataSnapshot DATA_SNAPSHOT) {;
        this.id = DATA_SNAPSHOT.getKey();
        this.data = DATA_SNAPSHOT.getValue(UserData.class);
    }

    // GETTERS / SETTERS ---------------------------------------------------------------------------

    /**
     * @return User ID/KEY
     */
    public String getId() {
        return id;
    }

    /**
     * @param id User ID/KEY
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return user data
     */
    public UserData getData() {
        return data;
    }

    /**
     * @param data user data
     */
    public void setData(UserData data) {
        this.data = data;
    }

    // ADD & REMOVE --------------------------------------------------------------------------------

    /**
     * Check whether the user has a specific follower or not.
     * @param followerUID ID/KEY of follower
     * @return true if user has follower, false if not
     */
    public boolean hasFollower(String followerUID) {
        return getData().getFollowers().get(followerUID) != null;
    }

    /**
     * Add new follower to user
     * @param followerUID ID/KEY of follower
     */
    public void addFollower(String followerUID) {
        if (!hasFollower(followerUID)) {
            String date = DateUtility.nowString();
            getData().getFollowers().put(followerUID, date);
            getData().setNum_followers(getData().getNum_followers() + 1);
            MatbitDatabase.getUserFollowers(id).child(followerUID).setValue(date);
            uploadNumFollowers();
        }
    }

    /**
     * Remove specific follower from user
     * @param followerUID ID/KEY of follower
     */
    public void removeFollower(String followerUID) {
        if (hasFollower(followerUID)) {
            getData().getFollowers().remove(followerUID);
            getData().setNum_followers(getData().getNum_followers() - 1);
            MatbitDatabase.getUserFollowers(id).child(followerUID).removeValue();
            uploadNumFollowers();
        }
    }

    /**
     * Add recipe to follower
     * @param recipeID ID/KEY of recipe
     * @param upload_date recipe upload date as string
     */
    public void addRecipe(String recipeID, String upload_date) {
        data.getRecipes().put(recipeID, upload_date);
        data.setNum_recipes(data.getNum_recipes() + 1);
    }

    // VALIDATION ----------------------------------------------------------------------------------

    /**
     * Check whether user has data or not.
     * @return true if user has data, false if not.
     */
    public boolean hasData() {
        if (data == null) {
            Log.i(TAG, "hasData: data not initialized.");
            return false;
        }
        else return true;
    }

    /**
     * Check whether user has nickname or not.
     * @return true if user has nickname, false if not.
     */
    public boolean hasNickname() {
        if (!hasData())
            return false;
        if (data.getNickname() == null) {
            Log.i(TAG, "hasNickname: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has gender or not.
     * @return true if user has gender, false if not.
     */
    public boolean hasGender() {
        if (!hasData())
            return false;
        if (data.getGender() == null || data.getGender().trim() == "") {
            Log.i(TAG, "hasGender: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has birthday or not.
     * @return true if user has birthday, false if not.
     */
    public boolean hasBirthday() {
        if (!hasData())
            return false;
        if (data.getBirthday() == null  || data.getBirthday().trim() == "") {
            Log.i(TAG, "hasBirthday: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has signed up date or not.
     * @return true if user has data, signed up date if not.
     */
    public boolean hasSignUpDate() {
        if (!hasData())
            return false;
        if (data.getSignUpDate() == null  || data.getSignUpDate().trim() == "") {
            Log.i(TAG, "hasSignUpDate: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has last login date or not.
     * @return true if user has last login date, false if not.
     */
    public boolean hasLastLoginDate() {
        if (!hasData())
            return false;
        if (data.getLastLoginDate() == null  || data.getLastLoginDate().trim() == "") {
            Log.i(TAG, "hasLastLoginDate: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has bio or not.
     * @return true if user has bio, false if not.
     */
    public boolean hasBio() {
        if (!hasData())
            return false;
        if (data.getBio() == null  || data.getBio().trim() == "") {
            Log.i(TAG, "hasBio: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has exp or not.
     * @return true if user has exp, false if not.
     */
    public boolean hasExp() {
        if (!hasData())
            return false;
        if (data.getExp() < 0) {
            Log.i(TAG, "hasExp: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has number of followers or not.
     * @return true if user has number of followers, false if not.
     */
    public boolean hasNumFollowers() {
        if (!hasData())
            return false;
        if (data.getNum_followers() < 0) {
            Log.i(TAG, "hasNumFollowers: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has number of recipes or not.
     * @return true if user has number of recipes, false if not.
     */
    public boolean hasNumRecipes() {
        if (!hasData())
            return false;
        if (data.getNum_recipes() < 0) {
            Log.i(TAG, "hasNumRecipes: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has following or not.
     * @return true if user has following, false if not.
     */
    public boolean hasFollowing() {
        if (!hasData())
            return false;
        if (data.getFollowing() == null || data.getFollowing().size() == 0) {
            Log.i(TAG, "hasFollowing: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has followers or not.
     * @return true if user has followers, false if not.
     */
    public boolean hasFollowers() {
        if (!hasData())
            return false;
        if (data.getFollowers() == null || data.getFollowers().size() == 0) {
            Log.i(TAG, "hasFollowers: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has recipes or not.
     * @return true if user has recipes, false if not.
     */
    public boolean hasRecipes() {
        if (!hasData())
            return false;
        if (data.getRecipes() == null || data.getRecipes().size() == 0) {
            Log.i(TAG, "hasRecipes: data not initialized.");
            return false;
        }
        return true;
    }

    /**
     * Check whether user has favorites or not.
     * @return true if user has favorites, false if not.
     */
    public boolean hasFavorites() {
        if (!hasData())
            return false;
        if (data.getFavorites() == null || data.getFavorites().size() == 0) {
            Log.i(TAG, "hasFavorites: data not initialized.");
            return false;
        }
        return true;
    }

    // UPLOAD --------------------------------------------------------------------------------------

    /**
     * Upload user nickname to Matbit database
     * @return true if data is valid
     */
    public boolean uploadNickname()  {
        if (!hasNickname()) {
            Log.e(TAG, "uploadNickname: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserNickname(id).setValue(data.getNickname());
            return true;
        }

    }

    /**
     * Upload user gender to Matbit database
     * @return true if data is valid
     */
    public boolean uploadGender()  {
        if (!hasGender()) {
            Log.e(TAG, "uploadGender: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserGender(id).setValue(data.getGender());
            return true;
        }
    }

    /**
     * Upload user birthday to Matbit database
     * @return true if data is valid
     */
    public boolean uploadBirthday()  {
        if (!hasBirthday()) {
            Log.e(TAG, "uploadBirthday: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserBirthday(id).setValue(data.getBirthday());
            return true;
        }
    }

    /**
     * Upload user sign up date to Matbit database
     * @return true if data is valid
     */
    public boolean uploadSignUpDate()  {
        if (!hasSignUpDate()) {
            Log.e(TAG, "uploadSignUpDate: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserSignUpDate(id).setValue(data.getSignUpDate());
            return true;
        }
    }

    /**
     * Upload user last login date to Matbit database
     * @return true if data is valid
     */
    public boolean uploadLastLoginDate()  {
        if (!hasLastLoginDate()) {
            Log.e(TAG, "uploadLastLoginDate: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserLastLoginDate(id).setValue(data.getLastLoginDate());
            return true;
        }
    }

    /**
     * Upload user bio to Matbit database
     * @return true if data is valid
     */
    public boolean uploadBio()  {
        if (!hasBio()) {
            Log.e(TAG, "uploadBio: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserBio(id).setValue(data.getBio());
            return true;
        }
    }

    /**
     * Upload user exp to Matbit database
     * @return true if data is valid
     */
    public boolean uploadExp()  {
        if (!hasExp()) {
            Log.e(TAG, "uploadExp: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserExp(id).setValue(data.getExp());
            return true;
        }
    }

    /**
     * Upload user number of followers to Matbit database
     * @return true if data is valid
     */
    public boolean uploadNumFollowers()  {
        if (!hasNumFollowers()) {
            Log.e(TAG, "uploadNumFollowers: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserNumFollowers(id).setValue(data.getNum_followers());
            return true;
        }
    }

    /**
     * Upload user number of recipes to Matbit database
     * @return true if data is valid
     */
    public boolean uploadNumRecipes()  {
        if (!hasNumRecipes()) {
            Log.e(TAG, "uploadNumRecipes: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserNumRecipes(id).setValue(data.getNum_recipes());
            return true;
        }
    }

    /**
     * Upload user following to Matbit database
     * @return true if data is valid
     */
    public boolean uploadFollowing() {
        if (!hasFollowing()) {
            Log.e(TAG, "uploadFollowing: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserFollowing(id).setValue(data.getFollowing());
            return true;
        }
    }

    /**
     * Upload user followers to Matbit database
     * @return true if data is valid
     */
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

    /**
     * Upload user recipes to Matbit database
     * @return true if data is valid
     */
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

    /**
     * Upload user favorites to Matbit database
     * @return true if data is valid
     */
    public boolean uploadFavorites()  {
        if (!hasFavorites()) {
            Log.e(TAG, "uploadFavorites: Upload failed. No data found");
            return false;
        } else {
            MatbitDatabase.getUserFavorites(id).setValue(data.getFavorites());
            return true;
        }
    }

    /**
     * Upload all user data to Matbit database
     * @return true if data is valid
     */
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