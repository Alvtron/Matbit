package net.r3dcraft.matbit;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 *
 * The Rating class is a data structure class that represents a rating block in the Firebase
 * Database. Any changes made here will have an impact on the data structure in the database.
 *
 * Since Google Firebase uses clever ClassWrapping, this class can be use directly with both writing
 * and storing rating-data from the database.
 */

public class Rating {
    private String user;
    private boolean thumbsUp;
    private String datetime;

    public Rating() {
        this.user = new String();
        this.thumbsUp = false;
        this.datetime = DateUtility.nowString();
    }

    public Rating(String user, boolean rating, String datetime) {
        this.user = user;
        this.thumbsUp = rating;
        this.datetime = datetime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean getThumbsUp() {
        return thumbsUp;
    }

    public void setThumbsUp(boolean thumbsUp) {
        this.thumbsUp = thumbsUp;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
