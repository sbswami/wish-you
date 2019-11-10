package com.sanshy.wishesapp;

import java.util.Date;

public class singleChat {
    String sName;
    String lastMsg;
    String photoUrl;
    Date dTime;
    long unSeen;
    String sId;
    String rId;

    public singleChat(String sName, String lastMsg, String photoUrl, Date dTime, long unSeen, String sId, String rId) {
        this.sName = sName;
        this.lastMsg = lastMsg;
        this.photoUrl = photoUrl;
        this.dTime = dTime;
        this.unSeen = unSeen;
        this.sId = sId;
        this.rId = rId;
    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Date getdTime() {
        return dTime;
    }

    public void setdTime(Date dTime) {
        this.dTime = dTime;
    }

    public long getUnSeen() {
        return unSeen;
    }

    public void setUnSeen(long unSeen) {
        this.unSeen = unSeen;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }
}
