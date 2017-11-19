package net.r3dcraft.matbit;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 *
 *
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

    @Exclude
    public static final Comparator<Comment> DATE_COMPARATOR_ASC = new Comparator<Comment>() {
        @Override
        public int compare(Comment a, Comment b) {
            return DateUtility.stringToDate(a.getDatetimeCreated())
                    .compareTo(DateUtility.stringToDate(b.getDatetimeCreated()));
        }
    };

    @Exclude
    public static final Comparator<Comment> DATE_COMPARATOR_DESC = new Comparator<Comment>() {
        @Override
        public int compare(Comment a, Comment b) {
            return DateUtility.stringToDate(b.getDatetimeCreated())
                    .compareTo(DateUtility.stringToDate(a.getDatetimeCreated()));
        }
    };
}
