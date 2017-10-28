package net.r3dcraft.matbit;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 */

public class Rating {
    private String user;
    private boolean thumbsUp;
    private String datetime;

    public Rating() {
        this.user = new String();
        this.thumbsUp = false;
        this.datetime = DateTime.nowString();
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
