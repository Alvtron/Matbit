package net.r3dcraft.matbit;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 17.10.2017.
 */

public class NewsFeed {
    private String url_thumbnail = "";
    private String url_featured_image = "";
    private String title = "";
    private String text = "";
    private Date date = null;

    public NewsFeed(){}

    public NewsFeed(String url_thumbnail, String url_featured_image, String title, String text, Date date) {
        this.url_thumbnail = url_thumbnail;
        this.url_featured_image = url_featured_image;
        this.title = title;
        this.text = text;
        this.date = date;
    }

    public String getUrl_thumbnail() {
        return url_thumbnail;
    }

    public void setUrl_thumbnail(String url_thumbnail) {
        this.url_thumbnail = url_thumbnail;
    }

    public String getUrl_featured_image() {
        return url_featured_image;
    }

    public void setUrl_featured_image(String url_featured_image) {
        this.url_featured_image = url_featured_image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static final Comparator<NewsFeed> DATE_COMPARATOR_ASC = new Comparator<NewsFeed>() {
        @Override
        public int compare(NewsFeed a, NewsFeed b) {
            if (a.date == null || b.date == null)
                return 0;
            else
                return a.date.compareTo(b.date);
        }
    };

    public static final Comparator<NewsFeed> DATE_COMPARATOR_DESC = new Comparator<NewsFeed>() {
        @Override
        public int compare(NewsFeed a, NewsFeed b) {
            if (a.date == null || b.date == null)
                return 0;
            else
                return b.date.compareTo(a.date);
        }
    };
}
