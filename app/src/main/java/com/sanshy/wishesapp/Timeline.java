package com.sanshy.wishesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.internal.zzahn.runOnUiThread;
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

public class Timeline extends Fragment{
    private List<singleWish> wishList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayout postLayout;
    private LinearLayout afterSingin;
    private LinearLayout picname;
    private Button signin;
    private Button postB;
    private TextView postText;
    private Button loadWishes;
    private RelativeLayout uWishes;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WishAdapter mAdapter;
    private TextView userName;
    private ImageView userPic;
    private ArrayList<PostData> PostDATA = new ArrayList<>();
    ArrayList<Integer> liker = new ArrayList<>();
    int checker1 = 0;//For Like All
    int checker2 = 0;//For Like UserId
    int checker3 = 0;//For Share All
    int checker4 = 0;//For Share UserId
    boolean PostCheck = true;
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query postRef;
    int check = 0;
    int dataLimit = 10;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    DocumentSnapshot lastDoc;

    String CurrentUserName;
    String CurrentUserId;
    String CurrentUserPhotoURL;

    public Timeline() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_timeline, container, false);

        if (currentUser!=null){
            CurrentUserName = currentUser.getDisplayName();
            CurrentUserId = currentUser.getUid();
            try{
                CurrentUserPhotoURL = currentUser.getPhotoUrl().toString();
            }catch (NullPointerException ex){}
        }

        recyclerView = (RecyclerView) myView.findViewById(R.id.recycler_view);
        afterSingin = myView.findViewById(R.id.afterSignin);
        signin = myView.findViewById(R.id.signin);
        postLayout = myView.findViewById(R.id.postLayout);
        picname = myView.findViewById(R.id.picname);
        loadWishes = myView.findViewById(R.id.loadWishes);
        uWishes = myView.findViewById(R.id.uWishes);
        userName = myView.findViewById(R.id.userName);
        userPic = myView.findViewById(R.id.userPic);
        postB = myView.findViewById(R.id.post);
        postText = myView.findViewById(R.id.selfWish);
        swipeRefreshLayout = myView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addWishData("dateTime");
//                Random rand = new Random();
//                int  n = rand.nextInt(4);
//                switch (n){
//                    case 0 : addWishData("dateTime");
//                    System.out.println("Date Time");
//                    break;
//                    case 1 : addWishData("post");
//                        System.out.println("Post");
//                    break;
//                    case 2 : addWishData("userId");
//                        System.out.println("User id");
//                    break;
//                    case 3 : addWishData("userName");
//                        System.out.println("User Name");
//                    break;
//                    default: addWishData("dateTime");
//                        System.out.println("Date Time");
//                    break;
//                }
            }
        });

        Thread mThread = new Thread(myRunnable);
        mThread.start();

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signinB();
            }
        });
        loadWishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check == 0){
                    loadWishes.setText("Hide Your Wishes");
                    picname.setVisibility(View.GONE);
                    uWishes.setVisibility(View.VISIBLE);
                    check = 1;
                }
                else if (check == 1){
                    loadWishes.setText("Show Your Wishes");
                    picname.setVisibility(View.VISIBLE);
                    uWishes.setVisibility(View.GONE);
                    check = 0;
                }

            }
        });

        postB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null)
                {
                    signinB();
                }
                else if (PostCheck){
                    PostCheck = false;
                    final String PostText = postText.getText().toString();
                    if (PostText.isEmpty())
                    {
                        postText.setError("Please Enter Something!!");
                        return;
                    }try{

                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        final Date date1 = new Date();
                        String date = dateFormat.format(date1);

                        final String postId = date+" "+CurrentUserId;
                        String likes[] = new String[0];
                        String reporters[] = new String[0];

                        Map<String, Object> postData = new HashMap<String, Object>();

                        postData.put("post",PostText);
                        postData.put("userId",CurrentUserId);
                        postData.put("photoURL",CurrentUserPhotoURL);
                        postData.put("userName",CurrentUserName);
                        postData.put("dateTime",date1);
                        postData.put("reportCounter",0);
                        postData.put("likeCounter",0);
                        postData.put("shareCounter",0);
                        postData.put("postId",postId);
                        postData.put("likes",Arrays.asList(likes));
                        postData.put("reporters",Arrays.asList(reporters));
                        postData.put("likeListName",new ArrayList<String>());
                        postData.put("likeListPhoto",new ArrayList<String>());

                        db.collection("All").document(postId).set(postData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Successfully Saved!")
                                                .setMessage("Your Wishes Message Posted to Public.")
                                                .setPositiveButton("OK",null);
                                        builder.create().show();
                                        postText.setText("");
                                        PostCheck = true;

                                        Map<String, Object> notificationMap = new HashMap<>();
                                        String postTextStartPart = PostText;
                                        try{
                                            postTextStartPart = PostText.substring(0,20)+"...";
                                        }catch (Exception ex){

                                        }
                                        notificationMap.put(SEEN,true);
                                        notificationMap.put("start",false);
                                        notificationMap.put("likeCount",0);
                                        notificationMap.put("postId",postId);
                                        notificationMap.put("postDate",date1);
                                        notificationMap.put("postText",postTextStartPart);
                                        notificationMap.put(NOTIFICATION_DATE,date1);

                                        db.collection("Notifications").document("PostNotification").collection(CurrentUserId).document(postId)
                                                .set(notificationMap);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        PostCheck = true;
                                        Toast.makeText(getContext(), "Something is Wrong!\n Please Try Again After Some Time.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        db.collection(CurrentUserId).document(postId).set(postData);


                    }catch (Exception ex){
                        Toast.makeText(getContext(), ""+ex, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mAdapter = new WishAdapter(wishList, new WishAdapter.MyAdapterListener() {
            @Override
            public void writerTimelineClick(View v, int position) {

            }

            @Override
            public void likeClick(View v, int position) {

                if ((liker.get(position) == 0)&&checker1==0&&checker2==0){
                    checker1 = 1;
                    checker2 = 1;
                    long likeCount = PostDATA.get(position).getLikeCounter();
                    String wishText = PostDATA.get(position).getPost();
                    String userId = PostDATA.get(position).getUserId();
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
                            db.collection("Notifications").document(POST_NOTIFICATION).collection(CurrentUserId).document(postId)
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

                                    db.collection("Notifications").document(POST_NOTIFICATION).collection(CurrentUserId).document(postId)
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
                    String userId = PostDATA.get(position).getUserId();
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
                            db.collection("Notifications").document(POST_NOTIFICATION).collection(CurrentUserId).document(postId)
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

                                    db.collection("Notifications").document("PostNotification").collection(CurrentUserId).document(postId)
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

                    String shareText = wishText + "\n --"+CurrentUserName;
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
                if ((PostDATA.size()==1)||(PostDATA.size()==0))
                {
                    wishList.clear();
                    mAdapter.notifyDataSetChanged();
                }
                try{
                    String postId = PostDATA.get(position).getPostId();

                    wishList.remove(position);
                    PostDATA.remove(position);
                    liker.remove(position);
                    mAdapter.notifyDataSetChanged();

                    db.collection("All").document(postId).delete();
                    db.collection(CurrentUserId).document(postId).delete();
                }catch (Exception e){}
            }

            @Override
            public void likesCheckClick(View v, int position) {
                String PostWriterName = PostDATA.get(position).getUserName()+"'s Post Likes";

                final ArrayList<likeList> likerArrayList = PostDATA.get(position).getLikeList();

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                        Intent intent = new Intent(getContext(),WriterData.class);
                        try {
                            intent.putExtra("id", likerArrayList.get(position).getLikerId());
                            intent.putExtra("photo", likerArrayList.get(position).getLikerPhotoURL());
                            intent.putExtra("name", likerArrayList.get(position).getLikerName());
                        }catch (Exception ex){}
                        startActivity(intent);
                    }
                });
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                likeRecyclerView.setLayoutManager(layoutManager);
                likeRecyclerView.setItemAnimator(new DefaultItemAnimator());
                likeRecyclerView.setAdapter(likeAdapter);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
// set the adapter
        recyclerView.setAdapter(mAdapter);



        // Inflate the layout for this fragment
        return myView;
    }

    @Override
    public void onStart() {
        addWishData("dateTime");
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null)
        {
            signin.setVisibility(View.VISIBLE);
            afterSingin.setVisibility(View.GONE);
            signinB();
        }
        else {
            signin.setVisibility(View.GONE);
            afterSingin.setVisibility(View.VISIBLE);
            userName.setText(CurrentUserName);
            try
            {
                Glide.with(getContext())
                        .load(CurrentUserPhotoURL)
                        .into(userPic);

            }catch (NullPointerException ex){
                Toast.makeText(getContext(), "No Photo", Toast.LENGTH_SHORT).show();
            }

        }
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
                signin.setVisibility(View.GONE);
                afterSingin.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Sign In Successfully Done!!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("chat","0");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                userName.setText(user.getDisplayName());
                try
                {
                    Glide.with(getContext())
                            .load(user.getPhotoUrl().toString())
                            .into(userPic);

                }catch (NullPointerException ex){
                    Toast.makeText(getContext(), "No Photo", Toast.LENGTH_SHORT).show();
                }
                // ...
            } else {
                // Sign in failed, check response for error code
                Toast.makeText(getContext(), "Failed To SignIn!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void nextData(String OrderBy){
        if (lastDoc==null){
            return;
        }
        if (currentUser==null){
            return;
        }
        try {
            Query next = db.collection(CurrentUserId).orderBy(OrderBy, Query.Direction.DESCENDING).startAfter(lastDoc).limit(4);
            next.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    if(!(documentSnapshots.isEmpty())){
                        dataLimit += 4;
                        System.out.println("Data Limit : "+dataLimit);
                    }
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        ArrayList<String> reporters = (ArrayList<String>) documentSnapshot.get("reporters");
                        String writerName = documentSnapshot.getString("userName");
                        String wishText = documentSnapshot.getString("post");
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
                                    if (CurrentUserId.equals(L)) {
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

                        lastDoc = documentSnapshot;

                        singleWish sWish = new singleWish(writerName, wishText, likeCounter, photoURL, reportCounter, shareCounter, date, like, 0);
                        wishList.add(sWish);

                        mAdapter.notifyDataSetChanged();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

            mAdapter.notifyDataSetChanged();
        }catch (Exception ex){
            System.out.println("Exception is : "+ex);
        }
    }

    private void addWishData(String OrderBy) {

        if (currentUser==null){
            return;
        }
        try {
            postRef = db.collection(CurrentUserId).orderBy(OrderBy, Query.Direction.DESCENDING).limit(dataLimit);
            postRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot documentSnapshots) {
                    wishList.clear();
                    PostDATA.clear();
                    liker.clear();
                    for (DocumentSnapshot documentSnapshot : documentSnapshots)
                    {
                        ArrayList<String> reporters = (ArrayList<String>) documentSnapshot.get("reporters");
                        String writerName = documentSnapshot.getString("userName");
                        String wishText = documentSnapshot.getString("post");
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
                                    if (CurrentUserId.equals(L)) {
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

                        lastDoc = documentSnapshot;

                        singleWish sWish = new singleWish(writerName, wishText, likeCounter, photoURL, reportCounter, shareCounter, date, like, 0);
                        wishList.add(sWish);

                        mAdapter.notifyDataSetChanged();

                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

            mAdapter.notifyDataSetChanged();
        }catch (Exception ex){
            System.out.println("Exception is : "+ex);
        }
    }
    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nextData("dateTime");
                    }
                });
            }
        }
    };
}
