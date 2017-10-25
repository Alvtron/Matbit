package net.r3dcraft.matbit;

/**
 * Created by unibl on 21.10.2017.
 */

public class Comment {
    private String user;
    private String comment;
    private String datetime;

    public Comment() {
        user = new String();
        comment = new String();
        datetime = DateTime.nowString();
    }

    public Comment(String user, String comment, String datetime) {
        this.user = user;
        this.comment = comment;
        this.datetime = datetime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
