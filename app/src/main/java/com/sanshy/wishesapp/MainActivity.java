package com.sanshy.wishesapp;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MainActivity extends AppCompatActivity {

    public static final String TIMELINE = "Timeline";
    public static final String HOME = "Home";
    public static final String CHAT = "Chat";
    public static final String NOTIFY = "Notify";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CoordinatorLayout mainScreen;
    RelativeLayout splashScreen;
    private AutoCompleteTextView searchText;
    ArrayList<String> hintList = new ArrayList<>();
    ArrayList<singleChat> chatList = new ArrayList<>();
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String CurrentUser;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    private int icons[] = {
            R.drawable.baseline_home_white_24dp,
            R.drawable.baseline_person_white_24dp,
            R.drawable.baseline_chat_white_24dp,
            R.drawable.baseline_notification_important_white_24dp
    };

    Thread mThread;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String ChatOrNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(MainActivity.this,FCMServiceWishApp.class));
        mRootRef.child("update").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    int playVersion = dataSnapshot.getValue(Integer.class);
                    try {
                        PackageInfo pInfo = MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                        String version = pInfo.versionName;
                        int code = pInfo.versionCode;
                        if (playVersion>code)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Update!!")
                                    .setMessage("Wow!! New Version Available!!")
                                    .setPositiveButton("Update Now!!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse("market://details?id=com.sanshy.wishesapp"));
                                            startActivity(intent);
                                        }
                                    })
                                    .create().show();
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }catch (Exception ex){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Intent intent = getIntent();
        ChatOrNotify = intent.getStringExtra("chat");

        try{
            if ((currentUser!=null)&&!(ChatOrNotify.equals("notify"))&&!  (ChatOrNotify.equals("chat"))){
                startService(new Intent(MainActivity.this,MyService.class));
            }
        }catch (Exception ex){
            startService(new Intent(MainActivity.this,MyService.class));
        }

        if (currentUser!=null){
            CurrentUser = currentUser.getUid();
            Thread thread = new Thread(notificationRunnable);
            thread.start();
        }

        searchText = findViewById(R.id.searchText);
        mainScreen = findViewById(R.id.main_screen);

        mThread = new Thread(mRunnable);
        mThread.start();

        splashScreen = findViewById(R.id.splash);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }
    private void setupTabIcons(){
        try{
            tabLayout.getTabAt(0).setIcon(icons[0]);
            tabLayout.getTabAt(1).setIcon(icons[1]);
            tabLayout.getTabAt(2).setIcon(icons[2]);
            tabLayout.getTabAt(3).setIcon(icons[3]);
        }catch (NullPointerException ne){}

    }

    @Override
    protected void onStart() {
        super.onStart();
//        DatabaseReference mHint = mRootRef.child("searches");
//        mHint.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                hintList.clear();
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
//                    hintList.add(dataSnapshot1.child("wish").getValue(String.class));
//                }
//                try{
//                    String hint[] = new String[hintList.size()];
//                    for (int k = 0; k < hintList.size(); k++){
//                        hint[k] = hintList.get(k);
//                    }
//                    ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item, Arrays.asList(hint));
//                    searchText.setAdapter(mAdapter);
//                }catch (Exception ex){
//                    Toast.makeText(MainActivity.this, ""+ex, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Home(),HOME);
        adapter.addFragment(new Timeline(), TIMELINE);
        adapter.addFragment(new Chat(), CHAT);
        adapter.addFragment(new myNotification(), NOTIFY);
        viewPager.setAdapter(adapter);
        try{
            if (ChatOrNotify.equals("chat")){
                viewPager.setCurrentItem(2);
            }
            else if(ChatOrNotify.equals("notify")){
                viewPager.setCurrentItem(3);
            }
        }catch (Exception ex){
            Log.e("Start","Activity Started");
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
    public void searchB(View v){
        String search = searchText.getText().toString();
        if (search.isEmpty()){
            searchText.setError("Write Something!");
            return;
        }
        int check = 0;
        for (int i = 0; i < hintList.size(); i++)
        {
            if (hintList.get(i).equals(search))
            {
                check++;
            }
        }
        if (check == 0)
        {
            DatabaseReference mHint = mRootRef.child("searches");
            String Id = mHint.push().getKey();

            mHint.child(Id).child("id").setValue(Id);
            mHint.child(Id).child("wish").setValue(search);
        }
        Intent intent = new Intent(this,SearchResult.class);
        intent.putExtra("text",search);
        intent.putExtra("check","a");
        startActivity(intent);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainScreen.setVisibility(View.VISIBLE);
                    splashScreen.setVisibility(View.GONE);
                }
            });
        }
    };

    Runnable notificationRunnable= new Runnable() {
        @Override
        public void run() {

            Query query = db.collection("RecentChatRoom").document("UserList").collection(CurrentUser).orderBy("dTime", Query.Direction.DESCENDING);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    if (chatList.size()==1){
                        NotificationManager notif=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        Intent myIntent = new Intent(MainActivity.this,ChatMsg.class);
                        myIntent.putExtra("name",chatList.get(0).getsName());
                        myIntent.putExtra("id",chatList.get(0).getrId());
                        myIntent.putExtra("photoURL",chatList.get(0).getPhotoUrl().toString());
                        PendingIntent lunchIt = PendingIntent.getActivity(MainActivity.this,0,myIntent,FLAG_UPDATE_CURRENT);
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
                        Intent myIntent = new Intent(MainActivity.this,MainActivity.class);
                        myIntent.putExtra("chat","chat");
                        PendingIntent lunchIt = PendingIntent.getActivity(MainActivity.this,0,myIntent,FLAG_UPDATE_CURRENT);
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

}

