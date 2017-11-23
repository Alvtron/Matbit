package net.r3dcraft.matbit;

import com.google.firebase.database.Exclude;

import java.util.Comparator;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 21.10.2017.
 *
 * The Comment class is a data structure class that represents a comment block in the Firebase
 * Database. Any changes made here will have an impact on the data structure in the database.
 *
 * Since Google Firebase uses clever ClassWrapping, this class can be use directly with both writing
 * and storing comment-data from the database.
 */

public class Comment {
    private String user;
    private String comment;
    private String datetimeCreated;
    private String datetimeUpdated;

    /**
     * Default Comment constructor
     */
    public Comment() {
        user = "";
        comment = "";
        datetimeCreated = "";
        this.datetimeUpdated = "";
    }

    /**
     * Comment Constructor
     * @param user - Author of comment
     * @param comment - Comment text
     * @param datetimeCreated - String of date/time created, formatted by DateUtility
     * @param datetimeUpdated - String of date/time updated, formatted by DateUtility
     */
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

    /**
     * Sort comment dates from oldest to newest with this static comparator.
     */
    @Exclude
    public static final Comparator<Comment> DATE_COMPARATOR_ASC = new Comparator<Comment>() {
        @Override
        public int compare(Comment a, Comment b) {
            return DateUtility.stringToDate(a.getDatetimeCreated())
                    .compareTo(DateUtility.stringToDate(b.getDatetimeCreated()));
        }
    };

    /**
     * Sort comment dates from newest to oldest with this static comparator.
     */
    @Exclude
    public static final Comparator<Comment> DATE_COMPARATOR_DESC = new Comparator<Comment>() {
        @Override
        public int compare(Comment a, Comment b) {
            return DateUtility.stringToDate(b.getDatetimeCreated())
                    .compareTo(DateUtility.stringToDate(a.getDatetimeCreated()));
        }
    };
}
