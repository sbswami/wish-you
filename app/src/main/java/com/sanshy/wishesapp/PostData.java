package com.sanshy.wishesapp;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by sbswami on 5/17/2018.
 */

public class PostData {

    public String post;
    public String userId;
    public String photoURL;
    public String userName;
    public Date dateTime;
    public long reportCounter;
    public long likeCounter;
    public long shareCounter;
    public String postId;
    public ArrayList<String> likes;
    public ArrayList<String> reporters;
    public ArrayList<likeList> likeList;


    public PostData(String post, String userId, String photoURL, String userName, Date dateTime, long reportCounter, long likeCounter, long shareCounter, String postId, ArrayList<String> likes, ArrayList<String> reporters, ArrayList<com.sanshy.wishesapp.likeList> likeList) {
        this.post = post;
        this.userId = userId;
        this.photoURL = photoURL;
        this.userName = userName;
        this.dateTime = dateTime;
        this.reportCounter = reportCounter;
        this.likeCounter = likeCounter;
        this.shareCounter = shareCounter;
        this.postId = postId;
        this.likes = likes;
        this.reporters = reporters;
        this.likeList = likeList;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public String getPost() {
        return post;
    }

    public String getUserId() {
        return userId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public long getReportCounter() {
        return reportCounter;
    }

    public long getLikeCounter() {
        return likeCounter;
    }

    public long getShareCounter() {
        return shareCounter;
    }

    public String getPostId() {
        return postId;
    }

    public ArrayList<String> getReporters() {
        return reporters;
    }

    public void setReporters(ArrayList<String> reporters) {
        this.reporters = reporters;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setReportCounter(long reportCounter) {
        this.reportCounter = reportCounter;
    }

    public void setLikeCounter(long likeCounter) {
        this.likeCounter = likeCounter;
    }

    public void setShareCounter(long shareCounter) {
        this.shareCounter = shareCounter;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public ArrayList<com.sanshy.wishesapp.likeList> getLikeList() {
        return likeList;
    }

    public void setLikeList(ArrayList<com.sanshy.wishesapp.likeList> likeList) {
        this.likeList = likeList;
    }
}
