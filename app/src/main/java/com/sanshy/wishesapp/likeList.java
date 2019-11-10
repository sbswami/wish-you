package com.sanshy.wishesapp;

public class likeList {

    String likerName;
    String likerPhotoURL;
    String likerId;

    public likeList(String likerName, String likerPhotoURL, String likerId) {
        this.likerName = likerName;
        this.likerPhotoURL = likerPhotoURL;
        this.likerId = likerId;
    }

    public String getLikerName() {
        return likerName;
    }

    public void setLikerName(String likerName) {
        this.likerName = likerName;
    }

    public String getLikerPhotoURL() {
        return likerPhotoURL;
    }

    public void setLikerPhotoURL(String likerPhotoURL) {
        this.likerPhotoURL = likerPhotoURL;
    }

    public String getLikerId() {
        return likerId;
    }

    public void setLikerId(String likerId) {
        this.likerId = likerId;
    }
}
