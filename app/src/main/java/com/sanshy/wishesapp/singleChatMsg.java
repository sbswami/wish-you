package com.sanshy.wishesapp;

public class singleChatMsg {
    String text;
    Integer LorR;

    public singleChatMsg(String text, Integer lorR) {
        this.text = text;
        LorR = lorR;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLorR() {
        return LorR;
    }

    public void setLorR(int lorR) {
        LorR = lorR;
    }
}
