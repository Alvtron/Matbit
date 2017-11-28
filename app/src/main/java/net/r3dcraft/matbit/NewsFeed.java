package net.r3dcraft.matbit;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Thomas Angeland, student at Ostfold University College, on 17.10.2017.
 *
 * The NewsFeed class holds data to be displayed in the NewsFeed item layout that populates the
 * recycler list in MainActivity.
 */

public class NewsFeed {
    private static final String TAG = "NewsFeed";
    private StorageReference storage_reference_thumbnail = null;
    private StorageReference storage_reference_featured_image = null;
    private String title = "";
    private String subtitle = "";
    private String text = "";
    private Intent action;
    private DateTime date = null;

    NewsFeed(){}

    /**
     * @return News feed title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return News feed text
     */
    public String getText() {
        return text;
    }

    /**
     * Compare NewsFeed objects by dates, oldest first
     */
    static final Comparator<NewsFeed> DATE_COMPARATOR_ASC = new Comparator<NewsFeed>() {
        @Override
        public int compare(NewsFeed a, NewsFeed b) {
            if (a.date == null || b.date == null)
                return 0;
            else
                return a.date.compareTo(b.date);
        }
    };

    /**
     * Compare NewsFeed objects by dates, newest first
     */
    static final Comparator<NewsFeed> DATE_COMPARATOR_DESC = new Comparator<NewsFeed>() {
        @Override
        public int compare(NewsFeed a, NewsFeed b) {
            if (a.date == null || b.date == null)
                return 0;
            else
                return b.date.compareTo(a.date);
        }
    };

    /**
     * @return news feed thumbnail photo storage reference
     */
    StorageReference getStorage_reference_thumbnail() {
        return storage_reference_thumbnail;
    }

    /**
     * @return news feed featured photo storage reference
     */
    StorageReference getStorage_reference_featured_image() {
        return storage_reference_featured_image;
    }

    /**
     * @return news feed action intent
     */
    Intent getAction() {
        return action;
    }

    /**
     * @return news feed date
     */
    DateTime getDate() {
        return date;
    }

    /**
     * @return news feed subtitle
     */
    String getSubtitle() {
        return subtitle;
    }

    /**
     *
     * @param storage_reference_thumbnail news feed thumbnail photo storage reference
     */
    public void setStorage_reference_thumbnail(StorageReference storage_reference_thumbnail) {
        this.storage_reference_thumbnail = storage_reference_thumbnail;
    }

    /**
     *
     * @param storage_reference_featured_image news feed featured photo storage reference
     */
    public void setStorage_reference_featured_image(StorageReference storage_reference_featured_image) {
        this.storage_reference_featured_image = storage_reference_featured_image;
    }

    /**
     *
     * @param title news feed title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @param subtitle news feed subtitle
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     *
     * @param text news feed text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     *
     * @param action news feed action (intent)
     */
    public void setAction(Intent action) {
        this.action = action;
    }

    /**
     *
     * @param date news feed date
     */
    public void setDate(DateTime date) {
        this.date = date;
    }
}
