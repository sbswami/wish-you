package com.sanshy.wishesapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMServiceWishApp extends FirebaseMessagingService {
    public FCMServiceWishApp() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        System.out.println("From : "+remoteMessage.getFrom());

        if (remoteMessage.getData().size()>0){
            System.out.println("Message Data Payload "+remoteMessage.getData());
        }

        if (remoteMessage.getNotification()!=null){
            System.out.println("Message Body"+remoteMessage.getNotification().getBody());
        }
    }
}
