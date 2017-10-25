package net.r3dcraft.matbit;

/**
 * Created by thomas on 17.10.2017.
 */

public class NewsFeed {
    private String mainline = "Default Mainline";
    private String underline = "Default Underline";
    private String photo_url = "";

    public NewsFeed(){}

    public NewsFeed(String mainline, String underline, String photo_url){
        this.mainline = mainline;
        this.underline = underline;
        this.photo_url = photo_url;
    }

    public String getMainline() {
        return mainline;
    }

    public void setMainline(String mainline) {
        this.mainline = mainline;
    }

    public String getUnderline() {
        return underline;
    }

    public void setUnderline(String underline) {
        this.underline = underline;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
}
