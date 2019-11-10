package com.sanshy.wishesapp;

import java.util.Date;

public class singleNotificationData {

    String notificationLastUserName;
    String notificationLastUserPhotoURL;
    String notificationLastUserId;
    String notificationSecondLastUserName;
    String notificationSecondLastUserPhotoURL;
    String notificationSecondLastUserId;
    long notificationLikeCount;
    String notificationPostId;
    String notificationPostText;
    Date notificationDateTime;
    Date postDateTime;
    boolean notificationSeen;

    public singleNotificationData(String notificationLastUserName, String notificationLastUserPhotoURL, String notificationLastUserId, String notificationSecondLastUserName, String notificationSecondLastUserPhotoURL, String notificationSecondLastUserId, long notificationLikeCount, String notificationPostId, String notificationPostText, Date notificationDateTime, Date postDateTime, boolean notificationSeen) {
        this.notificationLastUserName = notificationLastUserName;
        this.notificationLastUserPhotoURL = notificationLastUserPhotoURL;
        this.notificationLastUserId = notificationLastUserId;
        this.notificationSecondLastUserName = notificationSecondLastUserName;
        this.notificationSecondLastUserPhotoURL = notificationSecondLastUserPhotoURL;
        this.notificationSecondLastUserId = notificationSecondLastUserId;
        this.notificationLikeCount = notificationLikeCount;
        this.notificationPostId = notificationPostId;
        this.notificationPostText = notificationPostText;
        this.notificationDateTime = notificationDateTime;
        this.postDateTime = postDateTime;
        this.notificationSeen = notificationSeen;
    }

    public String getNotificationSecondLastUserPhotoURL() {
        return notificationSecondLastUserPhotoURL;
    }

    public void setNotificationSecondLastUserPhotoURL(String notificationSecondLastUserPhotoURL) {
        this.notificationSecondLastUserPhotoURL = notificationSecondLastUserPhotoURL;
    }

    public String getNotificationSecondLastUserId() {
        return notificationSecondLastUserId;
    }

    public void setNotificationSecondLastUserId(String notificationSecondLastUserId) {
        this.notificationSecondLastUserId = notificationSecondLastUserId;
    }

    public String getNotificationLastUserName() {
        return notificationLastUserName;
    }

    public void setNotificationLastUserName(String notificationLastUserName) {
        this.notificationLastUserName = notificationLastUserName;
    }

    public String getNotificationLastUserPhotoURL() {
        return notificationLastUserPhotoURL;
    }

    public void setNotificationLastUserPhotoURL(String notificationLastUserPhotoURL) {
        this.notificationLastUserPhotoURL = notificationLastUserPhotoURL;
    }

    public String getNotificationLastUserId() {
        return notificationLastUserId;
    }

    public void setNotificationLastUserId(String notificationLastUserId) {
        this.notificationLastUserId = notificationLastUserId;
    }

    public String getNotificationSecondLastUserName() {
        return notificationSecondLastUserName;
    }

    public void setNotificationSecondLastUserName(String notificationSecondLastUserName) {
        this.notificationSecondLastUserName = notificationSecondLastUserName;
    }

    public long getNotificationLikeCount() {
        return notificationLikeCount;
    }

    public void setNotificationLikeCount(long notificationLikeCount) {
        this.notificationLikeCount = notificationLikeCount;
    }

    public String getNotificationPostId() {
        return notificationPostId;
    }

    public void setNotificationPostId(String notificationPostId) {
        this.notificationPostId = notificationPostId;
    }

    public String getNotificationPostText() {
        return notificationPostText;
    }

    public void setNotificationPostText(String notificationPostText) {
        this.notificationPostText = notificationPostText;
    }

    public Date getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(Date notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    public Date getPostDateTime() {
        return postDateTime;
    }

    public void setPostDateTime(Date postDateTime) {
        this.postDateTime = postDateTime;
    }

    public boolean isNotificationSeen() {
        return notificationSeen;
    }

    public void setNotificationSeen(boolean notificationSeen) {
        this.notificationSeen = notificationSeen;
    }
}
