package com.tbt.app.ListItems;

/**
 * Created by bradley on 23-03-2017.
 */

public class UserSubmissionListItem {
    private String id, userId, time, originalFileName, type, baseLocation, status;

    public UserSubmissionListItem() {}

    public String getId() {
        return id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getBaseLocation() {
        return baseLocation;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBaseLocation(String baseLocation) {
        this.baseLocation = baseLocation;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
