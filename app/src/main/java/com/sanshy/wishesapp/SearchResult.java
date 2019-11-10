package com.sanshy.wishesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sanshy.wishesapp.FirestoreId.LAST_LIKER_ID;
import static com.sanshy.wishesapp.FirestoreId.LAST_LIKER_NAME;
import static com.sanshy.wishesapp.FirestoreId.LAST_LIKER_PHOTO;
import static com.sanshy.wishesapp.FirestoreId.LIKE_COUNT;
import static com.sanshy.wishesapp.FirestoreId.NOTIFICATION_DATE;
import static com.sanshy.wishesapp.FirestoreId.POST_NOTIFICATION;
import static com.sanshy.wishesapp.FirestoreId.SECOND_LAST_LIKER_ID;
import static com.sanshy.wishesapp.FirestoreId.SECOND_LAST_LIKER_NAME;
import static com.sanshy.wishesapp.FirestoreId.SECOND_LAST_LIKER_PHOTO;
import static com.sanshy.wishesapp.FirestoreId.SEEN;
import static com.sanshy.wishesapp.FirestoreId.START;

public class SearchResult extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private List<singleWish> wishList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayout postLayout;
    private WishAdapter mAdapter;
    private AutoCompleteTextView searchText;
    private String text;
    private String sCheck;

    private ArrayList<PostData> PostDATA = new ArrayList<>();
    ArrayList<Integer> liker = new ArrayList<>();
    int checker1 = 0;//For Like All
    int checker2 = 0;//For Like UserId
    int checker3 = 0;//For Share All
    int checker4 = 0;//For Share UserId

    String CurrentUserName;
    String CurrentUserId;
    String CurrentUserPhotoURL;

    ArrayList<String> hintList = new ArrayList<>();
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        if (currentUser!=null){
            CurrentUserName = currentUser.getDisplayName();
            CurrentUserId = currentUser.getUid();
            try{
                CurrentUserPhotoURL = currentUser.getPhotoUrl().toString();
            }catch (NullPointerException ex){}
        }

        Intent intent = getIntent();
        text = intent.getStringExtra("text");
        String temp = intent.getStringExtra("check");
        if (temp.equals("a")){
            sCheck = "All";
        }else {
            sCheck = temp;
        }
        searchText = findViewById(R.id.searchText);
        searchText.setText(text);
        recyclerView = findViewById(R.id.recycler_view);
        postLayout = findViewById(R.id.postLayout);
        mAdapter = new WishAdapter(wishList, new WishAdapter.MyAdapterListener() {
            @Override
            public void writerTimelineClick(View v, int position) {
                Intent intent = new Intent(SearchResult.this,WriterData.class);
                try {
                    intent.putExtra("id", PostDATA.get(position).getUserId());
                    intent.putExtra("photo", PostDATA.get(position).getPhotoURL());
                    intent.putExtra("name", PostDATA.get(position).getUserName());
                }catch (Exception ex){}
                startActivity(intent);
            }

            @Override
            public void likeClick(View v, int position) {
                if (currentUser==null){
                    signinB();
                    return;
                }
                if ((liker.get(position) == 0)&&checker1==0&&checker2==0){
                    checker1 = 1;
                    checker2 = 1;
                    long likeCount = PostDATA.get(position).getLikeCounter();
                    String wishText = PostDATA.get(position).getPost();
                    final String userId = PostDATA.get(position).getUserId();
                    String photoURL = PostDATA.get(position).getPhotoURL();
                    String name = PostDATA.get(position).getUserName();
                    Date date1 = PostDATA.get(position).getDateTime();
                    long reportCount = PostDATA.get(position).getReportCounter();
                    long shareCount = PostDATA.get(position).getShareCounter();
                    final String postId = PostDATA.get(position).getPostId();

                    ArrayList<String> likesL = PostDATA.get(position).getLikes();
                    ArrayList<likeList> likeListLike = PostDATA.get(position).getLikeList();
                    likeList tempList = new likeList(CurrentUserName,CurrentUserPhotoURL,CurrentUserId);

                    likeCount++;
                    PostDATA.get(position).setLikeCounter(likeCount);
                    likesL.add(CurrentUserId);
                    likeListLike.add(tempList);
                    PostDATA.get(position).setLikes(likesL);
                    PostDATA.get(position).setLikeList(likeListLike);

                    ArrayList<String> likeListName = new ArrayList<>();
                    ArrayList<String> likeListPhoto = new ArrayList<>();

                    for (int r = 0; r<likeListLike.size(); r++){
                        likeListName.add(likeListLike.get(r).getLikerName());
                        likeListPhoto.add(likeListLike.get(r).getLikerPhotoURL());
                    }

                    Map<String, Object> postData = new HashMap<String, Object>();
                    postData.put("likeCounter",likeCount);
                    postData.put("likes", likesL);
                    postData.put("likeListName",likeListName);
                    postData.put("likeListPhoto",likeListPhoto);

                    liker.set(position,1);
                    singleWish sWishT = new singleWish(name,wishText,likeCount,photoURL,reportCount,shareCount,date1,1,0);
                    wishList.set(position,sWishT);
                    mAdapter.notifyDataSetChanged();

                    final long finalLikeCount = likeCount;
                    db.collection("All").document(postId).update(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checker1 = 0;
                            db.collection("Notifications").document(POST_NOTIFICATION).collection(userId).document(postId)
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String secondLastUserName = "";
                                    String secondLastUserId = "";
                                    String secondLastUserPhoto = "";
                                    try{
                                        secondLastUserName = documentSnapshot.getString(LAST_LIKER_NAME);
                                        secondLastUserId = documentSnapshot.getString(LAST_LIKER_ID);
                                        secondLastUserPhoto = documentSnapshot.getString(LAST_LIKER_PHOTO);
                                    }catch (Exception ex){}

                                    Map<String, Object> notificationMap = new HashMap<>();

                                    Date nowDate = new Date();
                                    notificationMap.put(SEEN,false);
                                    notificationMap.put(START,true);
                                    notificationMap.put(LIKE_COUNT, finalLikeCount);
                                    notificationMap.put(NOTIFICATION_DATE,nowDate);
                                    notificationMap.put(LAST_LIKER_NAME,CurrentUserName);
                                    notificationMap.put(LAST_LIKER_ID,CurrentUserId);
                                    notificationMap.put(LAST_LIKER_PHOTO,CurrentUserPhotoURL);
                                    notificationMap.put(SECOND_LAST_LIKER_NAME,secondLastUserName);
                                    notificationMap.put(SECOND_LAST_LIKER_ID,secondLastUserId);
                                    notificationMap.put(SECOND_LAST_LIKER_PHOTO,secondLastUserPhoto);

                                    db.collection("Notifications").document(POST_NOTIFICATION).collection(userId).document(postId)
                                            .update(notificationMap);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            checker1 = 0;
                        }
                    });
                    db.collection(userId).document(postId).update(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checker2 = 0;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            checker2 = 0;
                        }
                    });
                }
                else if ((liker.get(position) == 1)&&checker1==0&&checker2==0){
                    checker1 = 1;
                    checker2 = 1;
                    long likeCount = PostDATA.get(position).getLikeCounter();
                    String wishText = PostDATA.get(position).getPost();
                    final String userId = PostDATA.get(position).getUserId();
                    String photoURL = PostDATA.get(position).getPhotoURL();
                    String name = PostDATA.get(position).getUserName();
                    Date date1 = PostDATA.get(position).getDateTime();
                    long reportCount = PostDATA.get(position).getReportCounter();
                    long shareCount = PostDATA.get(position).getShareCounter();
                    final String postId = PostDATA.get(position).getPostId();

                    ArrayList<String> likesL = PostDATA.get(position).getLikes();
                    ArrayList<likeList> likeListLike = PostDATA.get(position).getLikeList();

                    likeCount--;
                    PostDATA.get(position).setLikeCounter(likeCount);
                    likesL.remove(CurrentUserId);
                    PostDATA.get(position).setLikes(likesL);
                    int removingIndex = -1;
                    for (int k = 0; k<likeListLike.size();k++){
                        if (likeListLike.get(k).getLikerId().equals(CurrentUserId)){
                            removingIndex = k;
                            break;
                        }
                    }
                    System.out.println(removingIndex+" Index of Removing..........");
                    likeListLike.remove(removingIndex);
                    PostDATA.get(position).setLikeList(likeListLike);

                    ArrayList<String> likeListName = new ArrayList<>();
                    ArrayList<String> likeListPhoto = new ArrayList<>();

                    for (int r = 0; r<likeListLike.size(); r++){
                        likeListName.add(likeListLike.get(r).getLikerName());
                        likeListPhoto.add(likeListLike.get(r).getLikerPhotoURL());
                    }

                    Map<String, Object> postData = new HashMap<String, Object>();
                    postData.put("likeCounter",likeCount);
                    postData.put("likes", likesL);
                    postData.put("likeListName",likeListName);
                    postData.put("likeListPhoto",likeListPhoto);


                    liker.set(position,0);
                    singleWish sWishT = new singleWish(name,wishText,likeCount,photoURL,reportCount,shareCount,date1,0,0);
                    wishList.set(position,sWishT);
                    mAdapter.notifyDataSetChanged();

                    final long finalLikeCount = likeCount;

                    db.collection("All").document(postId).update(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checker1 = 0;
                            db.collection("Notifications").document(POST_NOTIFICATION).collection(userId).document(postId)
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String lastUserName = "";
                                    String lastUserId = "";
                                    String lastUserPhoto = "";
                                    try{
                                        lastUserName = documentSnapshot.getString(SECOND_LAST_LIKER_NAME);
                                        lastUserId = documentSnapshot.getString(SECOND_LAST_LIKER_ID);
                                        lastUserPhoto = documentSnapshot.getString(SECOND_LAST_LIKER_PHOTO);
                                    }catch (Exception ex){}

                                    Map<String, Object> notificationMap = new HashMap<>();

                                    Date nowDate = new Date();
                                    notificationMap.put(SEEN,true);
                                    notificationMap.put(START,true);
                                    notificationMap.put(LIKE_COUNT, finalLikeCount);
                                    notificationMap.put(NOTIFICATION_DATE,nowDate);
                                    notificationMap.put(LAST_LIKER_NAME,lastUserName);
                                    notificationMap.put(LAST_LIKER_ID,lastUserId);
                                    notificationMap.put(LAST_LIKER_PHOTO,lastUserPhoto);
                                    notificationMap.put(SECOND_LAST_LIKER_NAME,"");
                                    notificationMap.put(SECOND_LAST_LIKER_ID,"");
                                    notificationMap.put(SECOND_LAST_LIKER_PHOTO,"");

                                    db.collection("Notifications").document("PostNotification").collection(userId).document(postId)
                                            .update(notificationMap);
                                }
                            });                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            checker1 = 0;
                        }
                    });
                    db.collection(userId).document(postId).update(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checker2 = 0;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            checker2 = 0;
                        }
                    });
                }

            }

            @Override
            public void shareClick(View v, int position) {
                if (checker3==0&&checker4==0){
                    checker3 = 1;
                    checker4 = 1;
                    long likeCount = PostDATA.get(position).getLikeCounter();
                    String wishText = PostDATA.get(position).getPost();
                    String userId = PostDATA.get(position).getUserId();
                    String photoURL = PostDATA.get(position).getPhotoURL();
                    String name = PostDATA.get(position).getUserName();
                    Date date1 = PostDATA.get(position).getDateTime();
                    long reportCount = PostDATA.get(position).getReportCounter();
                    long shareCount = PostDATA.get(position).getShareCounter();
                    String postId = PostDATA.get(position).getPostId();

                    shareCount++;
                    PostDATA.get(position).setShareCounter(shareCount);

                    String shareText = wishText + "\n --"+currentUser.getDisplayName();
                    String shareSubject ="Wish You!!";
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT,shareText);
                    intent.putExtra(Intent.EXTRA_SUBJECT,shareSubject);
                    startActivity(Intent.createChooser(intent, "Share Your Feeling Via.."));

                    Map<String, Object> postData = new HashMap<String, Object>();
                    postData.put("shareCounter",shareCount);

                    singleWish sWishT = new singleWish(name,wishText,likeCount,photoURL,reportCount,shareCount,date1,liker.get(position),0);
                    wishList.set(position,sWishT);
                    mAdapter.notifyDataSetChanged();

                    db.collection("All").document(postId).update(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checker3 = 0;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            checker3 = 0;
                        }
                    });
                    db.collection(userId).document(postId).update(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checker4 = 0;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            checker4 = 0;
                        }
                    });

                }
            }

            @Override
            public void reportClick(View v, int position) {
                if (currentUser==null){
                    signinB();
                    return;
                }
                if ((PostDATA.size()==1)||(PostDATA.size()==0))
                {
                    wishList.clear();
                    mAdapter.notifyDataSetChanged();
                }
                try {
                    String userId = PostDATA.get(position).getUserId();
                    long reportCount = PostDATA.get(position).getReportCounter();
                    String postId = PostDATA.get(position).getPostId();
                    ArrayList<String> reporterR = PostDATA.get(position).getReporters();

                    reporterR.add(currentUser.getUid());
                    PostDATA.get(position).setReporters(reporterR);

                    reportCount++;

                    Map<String, Object> postData = new HashMap<String, Object>();
                    postData.put("reporters", reporterR);
                    postData.put("reportCounter", reportCount);

                    wishList.remove(position);
                    PostDATA.remove(position);
                    liker.remove(position);
                    mAdapter.notifyDataSetChanged();

                    if (reportCount >= 10) {
                        db.collection("All").document(postId).delete();
                    } else {
                        db.collection("All").document(postId).update(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                    db.collection(userId).document(postId).update(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }catch (Exception ex){}
            }

            @Override
            public void likesCheckClick(View v, int position) {
                String PostWriterName = PostDATA.get(position).getUserName()+"'s Post Likes";

                final ArrayList<likeList> likerArrayList = PostDATA.get(position).getLikeList();

                final AlertDialog.Builder builder = new AlertDialog.Builder(SearchResult.this);
                LayoutInflater layoutInflater = (LayoutInflater) SearchResult.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.likes_list,null);
                builder.setView(view)
                        .setPositiveButton("Close",null);

                TextView titleLike;
                titleLike = view.findViewById(R.id.title_likes);
                titleLike.setText(PostWriterName);

                RecyclerView likeRecyclerView = view.findViewById(R.id.like_recycler_view);
                likeListAdapter likeAdapter = new likeListAdapter(likerArrayList, new likeListAdapter.MyAdapterListener() {
                    @Override
                    public void likerTimelineClick(View v, int position) {
                        Intent intent = new Intent(SearchResult.this,WriterData.class);
                        try {
                            intent.putExtra("id", likerArrayList.get(position).getLikerId());
                            intent.putExtra("photo", likerArrayList.get(position).getLikerPhotoURL());
                            intent.putExtra("name", likerArrayList.get(position).getLikerName());
                        }catch (Exception ex){}
                        startActivity(intent);
                    }
                });
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchResult.this);
                likeRecyclerView.setLayoutManager(layoutManager);
                likeRecyclerView.setItemAnimator(new DefaultItemAnimator());
                likeRecyclerView.setAdapter(likeAdapter);

                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchResult.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
// set the adapter
        recyclerView.setAdapter(mAdapter);

        addWishData();
    }
    private void addWishData() {
        try {
            Query postRef = db.collection(sCheck).orderBy("dateTime", Query.Direction.DESCENDING).limit(30);
            postRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    wishList.clear();
                    PostDATA.clear();
                    liker.clear();
                    for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                        String wishText = documentSnapshot.getString("post");
                        boolean found = wishText.contains(text);
                        if (!found){
                            continue;
                        }
                        ArrayList<String> reporters = (ArrayList<String>) documentSnapshot.get("reporters");
                        String writerName = documentSnapshot.getString("userName");
                        long likeCounter = (long) documentSnapshot.get("likeCounter");
                        String photoURL = documentSnapshot.getString("photoURL");
                        Date date = (Date) documentSnapshot.get("dateTime");
                        long reportCounter = (long) documentSnapshot.get("reportCounter");
                        long shareCounter = (long) documentSnapshot.get("shareCounter");
                        String userid = documentSnapshot.getString("userId");
                        String postid = documentSnapshot.getString("postId");
                        ArrayList<String> likes = (ArrayList<String>) documentSnapshot.get("likes");
                        ArrayList<String> likeName = (ArrayList<String>) documentSnapshot.get("likeListName");
                        ArrayList<String> likePhoto = (ArrayList<String>) documentSnapshot.get("likeListPhoto");

                        ArrayList<likeList> likeListCloud = new ArrayList<>();

                        for (int p = 0; p < likes.size(); p++){
                            likeList tempList = new likeList(likeName.get(p),likePhoto.get(p),likes.get(p));
                            likeListCloud.add(tempList);
                        }
                        PostData PD = new PostData(wishText, userid, photoURL, writerName, date, reportCounter, likeCounter, shareCounter, postid, likes, reporters,likeListCloud);
                        PostDATA.add(PD);
                        int like = 0;
                        try {
                            if (!(likes.size() == 0)) {

                                for (String L : likes) {
                                    if (currentUser.getUid().equals(L)) {
                                        like = 1;
                                        liker.add(1);
                                        break;
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            System.out.println("Exception Is : " + ex);
                        }
                        if (like == 0) {
                            liker.add(0);
                        }

                        singleWish sWish = new singleWish(writerName, wishText, likeCounter, photoURL, reportCounter, shareCounter, date, like, 0);
                        wishList.add(sWish);

                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

            mAdapter.notifyDataSetChanged();
        }catch (Exception ex){
            System.out.println("Exception is : "+ex);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference mHint = mRootRef.child("searches");
        mHint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hintList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    hintList.add(dataSnapshot1.child("wish").getValue(String.class));
                }
                try{
                    String hint[] = new String[hintList.size()];
                    for (int k = 0; k < hintList.size(); k++){
                        hint[k] = hintList.get(k);
                    }
                    ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(SearchResult.this,android.R.layout.simple_spinner_dropdown_item, Arrays.asList(hint));
                    searchText.setAdapter(mAdapter);
                }catch (Exception ex){
                    Toast.makeText(SearchResult.this, ""+ex, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void signinB(){

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_launcher_foreground)      // Set logo drawable
                        .setTheme(R.style.AppTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Sign In Successfully Done!!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SearchResult.this, SearchResult.class);
                intent.putExtra("text",text);
                intent.putExtra("check",sCheck);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // ...
            } else {
                // Sign in failed, check response for error code
                Toast.makeText(this, "Failed To SignIn!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void searchB(View v){
        String search = searchText.getText().toString();
        if (search.isEmpty()){
            searchText.setError("Write Anything");
            return;
        }
        Intent intent = new Intent(this,SearchResult.class);
        intent.putExtra("text",search);
        intent.putExtra("check",sCheck);
        startActivity(intent);
    }
}
