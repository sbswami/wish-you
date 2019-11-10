package com.sanshy.wishesapp;

import android.net.Uri;

import java.util.Date;

/**
 * Created by sbswami on 3/23/2018.
 */

public class singleWish {

    String writerName;
    String wishText;
    long likeCount;
    String photoURL;
    long reportCounter;
    long shareCounter;
    Date date;
    int like;
    int share;

    public singleWish() {
    }

    public singleWish(String writerName, String wishText, long likeCount, String photoURL, long reportCounter, long shareCounter, Date date, int like, int share) {
        this.writerName = writerName;
        this.wishText = wishText;
        this.likeCount = likeCount;
        this.photoURL = photoURL;
        this.reportCounter = reportCounter;
        this.shareCounter = shareCounter;
        this.date = date;
        this.like = like;
        this.share = share;
    }

    public String getWriterName() {
        return writerName;
    }

    public String getWishText() {
        return wishText;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public long getReportCounter() {
        return reportCounter;
    }

    public long getShareCounter() {
        return shareCounter;
    }

    public Date getDate() {
        return date;
    }

    public int getLike() {
        return like;
    }

    public int getShare() {
        return share;
    }
}
