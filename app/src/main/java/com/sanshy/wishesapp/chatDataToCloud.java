package com.sanshy.wishesapp;

import java.util.Date;

public class chatDataToCloud {
    String msg;
    String id;
    Date dateTime;
    long seen;
    String senderId;

    public chatDataToCloud(String msg, String id, Date dateTime, long seen, String senderId) {
        this.msg = msg;
        this.id = id;
        this.dateTime = dateTime;
        this.seen = seen;
        this.senderId = senderId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public long getSeen() {
        return seen;
    }

    public void setSeen(long seen) {
        this.seen = seen;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
