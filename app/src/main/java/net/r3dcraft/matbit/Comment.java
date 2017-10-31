package net.r3dcraft.matbit;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 */

public class Comment {
    private String user;
    private String comment;
    private String datetimeCreated;
    private String datetimeUpdated;

    public Comment() {
        user = "";
        comment = "";
        datetimeCreated = "";
        this.datetimeUpdated = "";
    }

    public Comment(String user, String comment, String datetimeCreated, String datetimeUpdated) {
        this.user = user;
        this.comment = comment;
        this.datetimeCreated = datetimeCreated;
        this.datetimeUpdated = datetimeUpdated;
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

    public String getDatetimeCreated() {
        return datetimeCreated;
    }

    public void setDatetimeCreated(String datetimeCreated) {
        this.datetimeCreated = datetimeCreated;
    }

    public String getDatetimeUpdated() {
        return datetimeUpdated;
    }

    public void setDatetimeUpdated(String datetimeUpdated) {
        this.datetimeUpdated = datetimeUpdated;
    }
}
