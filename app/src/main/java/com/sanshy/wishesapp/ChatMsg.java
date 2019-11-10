package com.sanshy.wishesapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class ChatMsg extends AppCompatActivity {

    public static final String MSG = "msg";
    public static final String DATE_TIME = "dateTime";
    public static final String SEEN = "seen";
    public static final String ID = "id";
    public static final String SENDER_ID = "senderId";
    android.support.v7.widget.Toolbar toolbar;
    ListView listView;
    ChatMsgAdapter myAdapter;
    EditText sendText;
    String senderName;
    String senderId;
    String photoURL;
    ArrayList<singleChat> chatList = new ArrayList<>();
    List<singleChatMsg> chatMsgs = new ArrayList<>();
    ArrayList<String> chatTextList = new ArrayList<>();
    ArrayList<Integer> chatCheckList = new ArrayList<>();
    ArrayList<chatDataToCloud> cloudDataList = new ArrayList<>();
    singleChatMsg chat;
    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String CurrentUser;
    String CurrentUserName;
    String CurrentUserPhotoURL;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_msg);

        Intent intent = getIntent();
        senderName = intent.getStringExtra("name");
        senderId = intent.getStringExtra("id");
        photoURL = intent.getStringExtra("photoURL");

        if (currentUser==null){
            finish();
        }
        CurrentUser = currentUser.getUid();
        CurrentUserName = currentUser.getDisplayName();
        CurrentUserPhotoURL = currentUser.getPhotoUrl().toString();

        toolbar = findViewById(R.id.toolbar_chat);
        listView = findViewById(R.id.list_view);
        myAdapter = new ChatMsgAdapter(ChatMsg.this,chatTextList,chatCheckList);
        listView.setAdapter(myAdapter);

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        toolbar.setTitle(senderName);

        sendText = findViewById(R.id.send_msg);



//        Thread mThread = new Thread(runnable);
//        mThread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        addChatMsgs();
        Thread thread = new Thread(runnable);
        thread.start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Query query = db.collection("RecentChatRoom").document("UserList").collection(CurrentUser).orderBy("dTime", Query.Direction.DESCENDING);
            query.addSnapshotListener(ChatMsg.this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
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
                    for (singleChat single : chatList){
                        if (single.getrId().equals(senderId)){
                            chatList.remove(single);
                            return;
                        }
                    }
                    if (chatList.size()==1){
                        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent myIntent = new Intent(ChatMsg.this,ChatMsg.class);
                        myIntent.putExtra("name",chatList.get(0).getsName());
                        myIntent.putExtra("id",chatList.get(0).getrId());
                        myIntent.putExtra("photoURL",chatList.get(0).getPhotoUrl().toString());
                        PendingIntent lunchIt = PendingIntent.getActivity(ChatMsg.this,0,myIntent,FLAG_UPDATE_CURRENT);
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
                        Intent myIntent = new Intent(ChatMsg.this,MainActivity.class);
                        myIntent.putExtra("chat","chat");
                        PendingIntent lunchIt = PendingIntent.getActivity(ChatMsg.this,0,myIntent,FLAG_UPDATE_CURRENT);
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
            });
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        ChatMsg.this.finish();
    }

    public void sendB(View view){

        final String chatText = sendText.getText().toString();
        if (chatText.isEmpty()){
            sendText.setError("Write Something.");
            return;
        }
        if (currentUser==null){
            return;
        }

        final Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String chatId = format.format(date);
        Map<String, Object> chatMap = new HashMap<>();
        chatMap.put(MSG,chatText);
        chatMap.put(DATE_TIME,date);
        chatMap.put(SEEN,0);
        chatMap.put(ID,chatId);
        chatMap.put(SENDER_ID,CurrentUser);

        sendText.setText("");

        db.collection("ChatRooms").document(CurrentUser).collection(senderId).document(chatId)
                .set(chatMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatMsg.this, "Something is Wrong.", Toast.LENGTH_SHORT).show();
                    }
                });
        db.collection("ChatRooms").document(senderId).collection(CurrentUser).document(chatId)
                .set(chatMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatMsg.this, "Something is Wrong.", Toast.LENGTH_SHORT).show();
                    }
                });

        String lastMsgString = chatText;
        try{
            lastMsgString = chatText.substring(0,20)+"...";
        }catch (Exception ex){}

        singleChat recentChatForMe = new singleChat(senderName,lastMsgString,photoURL,date,0,CurrentUser,senderId);
        db.collection("RecentChatRoom").document("UserList").collection(CurrentUser).document(senderId)
                .set(recentChatForMe);

        final String finalLastMsgString = lastMsgString;
        db.collection("RecentChatRoom").document("UserList").collection(senderId).document(CurrentUser).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        long unSeenCount = 0;
                        try{
                            unSeenCount = documentSnapshot.getLong("unSeen");
                        }catch (Exception ex){}
                        unSeenCount++;

                        singleChat recentChatForYou = new singleChat(CurrentUserName, finalLastMsgString,CurrentUserPhotoURL,date,unSeenCount,senderId,CurrentUser);
                        db.collection("RecentChatRoom").document("UserList").collection(senderId).document(CurrentUser)
                                .set(recentChatForYou);
                    }
                });
    }

    public void setSeen(){
        Map<String, Object> seenMap = new HashMap<String, Object>();
        seenMap.put("unSeen",0);
        db.collection("RecentChatRoom").document("UserList").collection(CurrentUser).document(senderId)
                .update(seenMap);
    }

    public void addChatMsgs(){
        setSeen();
        Query firstFetch = db.collection("ChatRooms").document(CurrentUser).collection(senderId).orderBy("dateTime", Query.Direction.ASCENDING);
        firstFetch.addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                chatMsgs.clear();
                chatTextList.clear();
                chatCheckList.clear();
                for (DocumentSnapshot documentSnapshot : documentSnapshots){

                    chatDataToCloud cloudData = new chatDataToCloud(
                            documentSnapshot.getString(MSG),
                            documentSnapshot.getString(ID),
                            documentSnapshot.getDate(DATE_TIME),
                            documentSnapshot.getLong(SEEN),
                            documentSnapshot.getString(SENDER_ID)
                    );
                    cloudDataList.add(cloudData);

                    String senderId = documentSnapshot.getString(SENDER_ID);
                    String text = documentSnapshot.getString(MSG);

                    Integer checker = 1;
                    if (senderId.equals(CurrentUser)){
                        checker = 0;
                    }
                    chatTextList.add(text);
                    chatCheckList.add(checker);
                    setSeen();
                }
                myAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }
        });

    }
    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(listView.getCount() - 1);
            }
        });
    }
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    nextData();
//                }
//            });
//        }
//    };
//    public void nextData(){
//        if (lastDoc==null){
//            return;
//        }
//        Query query = db.collection("ChatRooms").document(CurrentUserId).collection(senderId).orderBy("dateTime", Query.Direction.ASCENDING).startAfter(lastDoc);
//        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
//                for (DocumentSnapshot documentSnapshot : documentSnapshots){
//                    String senderId = documentSnapshot.getString(SENDER_ID);
//                    String text = documentSnapshot.getString(MSG);
//
//                    if (!(senderId.equals(CurrentUserId))){
//                        chat = new singleChatMsg(text,null);
//                        chatMsgs.add(chat);
//                    }
//
//                    lastDoc = documentSnapshot;
//                }
//                myAdapter.notifyDataSetChanged();
//                recyclerView.smoothScrollToPosition(myAdapter.getItemCount());
//            }
//        });
//    }
}
