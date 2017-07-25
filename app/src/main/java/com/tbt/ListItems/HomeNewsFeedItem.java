package com.tbt.ListItems;

/**
 * Created by bradley on 04-04-2017.
 */

public class HomeNewsFeedItem {
    private String postId, userId, location, name, profilePicURL, caption, like;

    public HomeNewsFeedItem() {}

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public String getCaption() {
        return caption;
    }

    public String getLike() {
        return like;
    }
}
