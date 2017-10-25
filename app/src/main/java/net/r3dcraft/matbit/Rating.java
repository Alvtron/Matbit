package net.r3dcraft.matbit;

/**
 * Created by unibl on 21.10.2017.
 */

public class Rating {
    private String user;
    private int rating;
    private String datetime;

    public Rating() {
        this.user = new String();
        this.rating = -1;
        this.datetime = DateTime.nowString();
    }

    public Rating(String user, int rating, String datetime) {
        this.user = user;
        this.rating = rating;
        this.datetime = datetime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
