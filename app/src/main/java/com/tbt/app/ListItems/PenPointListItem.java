package com.tbt.app.ListItems;

/**
 * Created by bradley on 07-03-2017.
 */

public class PenPointListItem {
    private String id, title, preview, authorName, authorImgURL;
    public PenPointListItem() {}

    public void setAuthorImgURL(String authorImgURL) {
        this.authorImgURL = authorImgURL;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorImgURL() {
        return authorImgURL;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getId() {
        return id;
    }

    public String getPreview() {
        return preview;
    }

    public String getTitle() {
        return title;
    }
}
