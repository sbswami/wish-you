package com.sanshy.wishesapp;

/**
 * Created by sbswami on 3/23/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class notificationAdapter extends ArrayAdapter<String> {

    Activity mContext;
    ArrayList<singleNotificationData> notificationDataArrayList = new ArrayList<>();
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    public notificationAdapter(Activity context, ArrayList<String> CurrentUserName,ArrayList<singleNotificationData> notificationDataArrayList){
        super(context,R.layout.single_chat_msg, CurrentUserName);
        this.mContext = context;
        this.notificationDataArrayList = notificationDataArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View myNotificationView = inflater.inflate(R.layout.single_notification,null,true);

        CardView notificationCard = myNotificationView.findViewById(R.id.notification_card);
        TextView notificationLikeText = myNotificationView.findViewById(R.id.like_text);
        TextView notificationPostText = myNotificationView.findViewById(R.id.post_text);
        TextView notificationTimeAgo = myNotificationView.findViewById(R.id.time_ago);
        ImageView notificationImage = myNotificationView.findViewById(R.id.notify_image);

        String CurrentUserName = currentUser.getDisplayName();
        singleNotificationData notificationList = notificationDataArrayList.get(position);

        String PostText = notificationList.getNotificationPostText();
        Date notificationDate = notificationList.getNotificationDateTime();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String notifDate = format.format(notificationDate);
        boolean seenCheck = notificationList.isNotificationSeen();
        if (seenCheck){
            notificationCard.setCardBackgroundColor(Color.WHITE);
        }
        long likeCount = notificationList.getNotificationLikeCount();

        String likeText;

        if (likeCount==1){
            String lastUserName = notificationList.getNotificationLastUserName();
           try{
               if (CurrentUserName.equals(lastUserName)){
                   lastUserName = "You";
               }
           }catch (NullPointerException ex){}
            likeText = lastUserName+" Likes, Post";
        }else if (likeCount==2){
            String lastUserName = notificationList.getNotificationLastUserName();
            String secondLastUserName = notificationList.getNotificationSecondLastUserName();
            try{
                if (CurrentUserName.equals(lastUserName)){
                    lastUserName = "You";
                }else if (CurrentUserName.equals(secondLastUserName)){
                    secondLastUserName="You";
                }
            }catch (NullPointerException ex){}
            likeText = lastUserName+" and "+secondLastUserName+" like, Post";
        }else if (likeCount==0){
            likeText = "Someone Will Like this Post";
        }
        else {
            String lastUserName = notificationList.getNotificationLastUserName();
            String secondLastUserName = notificationList.getNotificationSecondLastUserName();
            try{
                if (CurrentUserName.equals(lastUserName)){
                    lastUserName = "You";
                }else if (CurrentUserName.equals(secondLastUserName)){
                    secondLastUserName="You";
                }
            }catch (NullPointerException ex){}
            long tempCount = likeCount-2;
            likeText = lastUserName+","+secondLastUserName+" and "+tempCount+" Other like, Post";
        }
        notificationLikeText.setText(likeText);
        notificationPostText.setText(PostText);
        notificationTimeAgo.setText(notifDate);

        try
        {
            Glide.with(mContext)
                    .load(notificationList.getNotificationLastUserPhotoURL())
                    .into(notificationImage);

        }catch (NullPointerException ex){
            Toast.makeText(mContext, "No Photo", Toast.LENGTH_SHORT).show();
        }

        return myNotificationView;

    }
}

