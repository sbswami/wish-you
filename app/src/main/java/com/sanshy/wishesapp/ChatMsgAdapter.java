package com.sanshy.wishesapp;

/**
 * Created by sbswami on 3/23/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatMsgAdapter extends ArrayAdapter<String> {

    Activity mContext;
    ArrayList<String> text = new ArrayList<>();
    ArrayList<Integer> Checker = new ArrayList<>();

    public ChatMsgAdapter(Activity context, ArrayList<String> text, ArrayList<Integer> checker){
        super(context,R.layout.single_chat_msg, text);
        this.mContext = context;
        this.text = text;
        this.Checker = checker;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View mChatView = inflater.inflate(R.layout.single_chat_msg,null,true);

        TextView textView = mChatView.findViewById(R.id.text_view);
        RelativeLayout layout = mChatView.findViewById(R.id.left_layout);

        if (Checker.get(position)==0){
            layout.setGravity(Gravity.END);
            textView.setBackgroundResource(R.drawable.right_text);
            textView.setPadding(11,11,11,11);
            layout.setPadding(25,0,0,0);
        }

        textView.setText(text.get(position));

        return mChatView;
    }
}

