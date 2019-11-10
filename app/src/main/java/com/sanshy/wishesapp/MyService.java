package com.sanshy.wishesapp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.sanshy.wishesapp.FirestoreId.LIKE_COUNT;
import static com.sanshy.wishesapp.FirestoreId.NOTIFICATION_DATE;
import static com.sanshy.wishesapp.FirestoreId.POST_NOTIFICATION;
import static com.sanshy.wishesapp.FirestoreId.SEEN;
import static com.sanshy.wishesapp.FirestoreId.START;

public class MyService extends Service {
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String CurrentUserId;
    String CurrentUserName;
    String CurrentUserPhotoURL;
    int oldCount = 0;
    ArrayList<singleChat> chatList = new ArrayList<>();
    public MyService() {
        CurrentUserId = currentUser.getUid();
        CurrentUserName = currentUser.getDisplayName();
        CurrentUserPhotoURL = currentUser.getPhotoUrl().toString();
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Query queryNotification = db.collection("Notifications").document(POST_NOTIFICATION).collection(CurrentUserId).orderBy(NOTIFICATION_DATE, Query.Direction.DESCENDING);
        queryNotification.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int notificationCounter = 0;
                for (DocumentSnapshot documentSnapshot : documentSnapshots){
                    boolean startChecker = documentSnapshot.getBoolean(START);
                    boolean seenChecker = documentSnapshot.getBoolean(SEEN);
                    if ((!startChecker)||seenChecker){
                        continue;
                    }
                    notificationCounter++;
                }
                if ((notificationCounter>0)&&(oldCount<notificationCounter)){
                    NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    Intent myIntent = new Intent(MyService.this,MainActivity.class);
                    myIntent.putExtra("chat","notify");
                    PendingIntent lunchIt = PendingIntent.getActivity(MyService.this,0,myIntent,FLAG_UPDATE_CURRENT);
                    Notification notify=new Notification.Builder
                            (getApplicationContext())
                            .setContentText(notificationCounter+" Notifications From Wish You")
                            .setContentTitle("Wish You!")
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                            .setContentIntent(lunchIt)
                            .setSmallIcon(R.drawable.nicon)
                            .build();

                    notify.flags |= Notification.FLAG_AUTO_CANCEL;

                    notif.notify(0, notify);
                }
                oldCount = notificationCounter;
            }
        });

        Query query = db.collection("RecentChatRoom").document("UserList").collection(CurrentUserId).orderBy("dTime", Query.Direction.DESCENDING);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (isAppIsInBackground(MyService.this)){
                    chatList.clear();
                    long unSeenCount = 0;
                    for (DocumentSnapshot documentSnapshot : documentSnapshots){
                        if (documentSnapshot.getLong("unSeen")==0){
                            continue;
                        }
                        unSeenCount += documentSnapshot.getLong("unSeen");
                        singleChat chat = new singleChat(
                                documentSnapshot.getString("sName"),
                                documentSnapshot.getString("lastMsg"),
                                documentSnapshot.getString("photoUrl"),
                                documentSnapshot.getDate("dTime"),
                                documentSnapshot.getLong("unSeen"),
                                documentSnapshot.getString("sId"),
                                documentSnapshot.getString("rId")
                        );
                        chatList.add(chat);
                    }
                    if (chatList.size()==1){
                        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent myIntent = new Intent(MyService.this,ChatMsg.class);
                        myIntent.putExtra("name",chatList.get(0).getsName());
                        myIntent.putExtra("id",chatList.get(0).getrId());
                        myIntent.putExtra("photoURL",chatList.get(0).getPhotoUrl().toString());
                        PendingIntent lunchIt = PendingIntent.getActivity(MyService.this,0,myIntent,FLAG_UPDATE_CURRENT);
                        Notification notify = new Notification.Builder
                                (getApplicationContext())
                                .setContentText(unSeenCount+" : "+chatList.get(0).getLastMsg())
                                .setContentTitle(chatList.get(0).getsName())
                                .setContentIntent(lunchIt)
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setSmallIcon(R.drawable.nicon)
                                .build();

                        notify.flags |= Notification.FLAG_AUTO_CANCEL;
                        notif.notify(0, notify);

                    }
                    else if (chatList.size()>1){
                        int sizeOfChatList = chatList.size();

                        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent myIntent = new Intent(MyService.this,MainActivity.class);
                        myIntent.putExtra("chat","chat");
                        PendingIntent lunchIt = PendingIntent.getActivity(MyService.this,0,myIntent,FLAG_UPDATE_CURRENT);
                        Notification notify=new Notification.Builder
                                (getApplicationContext())
                                .setContentText(unSeenCount+" Messages From "+sizeOfChatList+" Chats")
                                .setContentTitle("Wish You!")
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setContentIntent(lunchIt)
                                .setSmallIcon(R.drawable.nicon)
                                .build();

                        notify.flags |= Notification.FLAG_AUTO_CANCEL;

                        notif.notify(0, notify);
                    }
                }
            }
        });


               return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
