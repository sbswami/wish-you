package com.sanshy.wishesapp;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sanshy.wishesapp.FirestoreId.LAST_LIKER_ID;
import static com.sanshy.wishesapp.FirestoreId.LAST_LIKER_NAME;
import static com.sanshy.wishesapp.FirestoreId.LAST_LIKER_PHOTO;
import static com.sanshy.wishesapp.FirestoreId.LIKE_COUNT;
import static com.sanshy.wishesapp.FirestoreId.NOTIFICATION_DATE;
import static com.sanshy.wishesapp.FirestoreId.POST_DATE;
import static com.sanshy.wishesapp.FirestoreId.POST_ID;
import static com.sanshy.wishesapp.FirestoreId.POST_NOTIFICATION;
import static com.sanshy.wishesapp.FirestoreId.POST_TEXT;
import static com.sanshy.wishesapp.FirestoreId.SECOND_LAST_LIKER_ID;
import static com.sanshy.wishesapp.FirestoreId.SECOND_LAST_LIKER_NAME;
import static com.sanshy.wishesapp.FirestoreId.SECOND_LAST_LIKER_PHOTO;
import static com.sanshy.wishesapp.FirestoreId.SEEN;
import static com.sanshy.wishesapp.FirestoreId.START;

public class myNotification extends Fragment {
    ListView notificationListView;
    SwipeRefreshLayout swipeRefreshLayout;
    notificationAdapter adapter;
    ArrayList<singleNotificationData> notificationListCloud = new ArrayList<>();
    ArrayList<String> duplicateUserList = new ArrayList<>();


    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String CurrentUserName;
    String CurrentUserId;
    String CurrentUserPhotoURL;

    String notificationLastUserName;
    String notificationLastUserPhotoURL;
    String notificationLastUserId;
    String notificationSecondLastUserName;
    String notificationSecondLastUserPhotoURL;
    String notificationSecondLastUserId;
    long notificationLikeCount;
    String notificationPostId;
    String notificationPostText;
    Date notificationDateTime;
    Date postDateTime;
    boolean notificationSeen;

    private TextView writerName,dateTime,mainWishText,likeCount,shareCount;
    private Button likeB,share;
    private ImageView writerPic,report;

    public myNotification() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_notification, container, false);

        if (currentUser!=null){
            CurrentUserName = currentUser.getDisplayName();
            CurrentUserId = currentUser.getUid();
            try{
                CurrentUserPhotoURL = currentUser.getPhotoUrl().toString();
            }catch (NullPointerException ex){}
        }

        notificationListView = myView.findViewById(R.id.notification_list);
        swipeRefreshLayout = myView.findViewById(R.id.notification_swipe);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addNotification();
            }
        });

        adapter = new notificationAdapter(getActivity(),duplicateUserList,notificationListCloud);
        notificationListView.setAdapter(adapter);

        notificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                notificationLastUserId = notificationListCloud.get(position).getNotificationLastUserId();
                notificationLastUserName = notificationListCloud.get(position).getNotificationLastUserName();
                notificationLastUserPhotoURL = notificationListCloud.get(position).getNotificationLastUserPhotoURL();
                notificationSecondLastUserId = notificationListCloud.get(position).getNotificationSecondLastUserId();
                notificationSecondLastUserName = notificationListCloud.get(position).getNotificationSecondLastUserName();
                notificationSecondLastUserPhotoURL = notificationListCloud.get(position).getNotificationSecondLastUserPhotoURL();
                notificationDateTime = notificationListCloud.get(position).getNotificationDateTime();
                notificationLikeCount = notificationListCloud.get(position).getNotificationLikeCount();
                notificationPostText = notificationListCloud.get(position).getNotificationPostText();
                notificationPostId = notificationListCloud.get(position).getNotificationPostId();
                notificationSeen = true;
                postDateTime = notificationListCloud.get(position).getPostDateTime();


                singleNotificationData singleData = new singleNotificationData(notificationLastUserName,notificationLastUserPhotoURL,notificationLastUserId,notificationSecondLastUserName,notificationSecondLastUserPhotoURL,notificationSecondLastUserId,notificationLikeCount,notificationPostId,notificationPostText,notificationDateTime,postDateTime,notificationSeen);
                notificationListCloud.set(position,singleData);
                adapter.notifyDataSetChanged();


                db.collection(CurrentUserId).document(notificationPostId);
                db.collection(CurrentUserId).document(notificationPostId).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if (!(documentSnapshot.exists())){
                                    Toast.makeText(getContext(), "Post Was Deleted...", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ArrayList<String> reporters = (ArrayList<String>) documentSnapshot.get("reporters");
                                String writerNameString = documentSnapshot.getString("userName");
                                String wishText = documentSnapshot.getString("post");
                                long likeCounter = (long)documentSnapshot.get("likeCounter");
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

                                PostData PD = new PostData(wishText,userid,photoURL,writerNameString,date,reportCounter,likeCounter,shareCounter,postid,likes,reporters,likeListCloud);

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View SinglePostView = layoutInflater.inflate(R.layout.single_wish_list,null);

                                writerName = SinglePostView.findViewById(R.id.writerName);
                                dateTime = SinglePostView.findViewById(R.id.dateTime);
                                mainWishText = SinglePostView.findViewById(R.id.mainWishText);
                                likeCount = SinglePostView.findViewById(R.id.likeCount);
                                likeB = SinglePostView.findViewById(R.id.like);
                                share = SinglePostView.findViewById(R.id.share);
                                writerPic = SinglePostView.findViewById(R.id.writerPic);
                                report = SinglePostView.findViewById(R.id.report);
                                shareCount = SinglePostView.findViewById(R.id.shareCount);

                                writerName.setText(PD.getUserName());
                                mainWishText.setText(PD.getPost());
                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                String datetime = format.format(date);
                                dateTime.setText(datetime);
                                shareCount.setText(PD.getShareCounter()+"");
                                try
                                {
                                    Glide.with(getContext())
                                            .load(PD.getPhotoURL())
                                            .into(writerPic);

                                }catch (NullPointerException ex){
                                    Toast.makeText(getContext(), "No Photo", Toast.LENGTH_SHORT).show();
                                }
                                boolean check = likes.contains(CurrentUserId);
                                long tempCount = PD.getLikeCounter()-1;
                                String likeCountText = "You and "+tempCount+" Other Likes";
                                String likeCountTextForZeroLike = tempCount+" Other Likes";
                                if (tempCount == 0){
                                    likeCountText = "You like this Post";
                                }
                                if (tempCount==(-1)){
                                    likeCountTextForZeroLike = "Be First Like";
                                }

                                if (check){
                                    likeB.setText("Liked");
                                    likeCount.setText(likeCountText);
                                }else{
                                    likeB.setText("Like");
                                    likeCount.setText(likeCountTextForZeroLike);
                                }
                                builder.setView(SinglePostView)
                                        .create()
                                        .show();

                            }
                        });

                Map<String, Object> seenChange = new HashMap<>();
                seenChange.put("seen",true);

                db.collection("Notifications").document("PostNotification").collection(CurrentUserId).document(notificationPostId)
                        .update(seenChange);
            }
        });

        addNotification();

        return myView;
    }

    public void addNotification(){
        Query query = db.collection("Notifications").document(POST_NOTIFICATION).collection(CurrentUserId).orderBy(NOTIFICATION_DATE, Query.Direction.DESCENDING);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                notificationListCloud.clear();
                duplicateUserList.clear();
                for (DocumentSnapshot documentSnapshot : documentSnapshots){
                    boolean startChecker = documentSnapshot.getBoolean(START);
                    if (!startChecker){
                        continue;
                    }
                    notificationLastUserId = documentSnapshot.getString(LAST_LIKER_ID);
                    notificationLastUserName = documentSnapshot.getString(LAST_LIKER_NAME);
                    notificationLastUserPhotoURL = documentSnapshot.getString(LAST_LIKER_PHOTO);
                    notificationSecondLastUserId = documentSnapshot.getString(SECOND_LAST_LIKER_ID);
                    notificationSecondLastUserName = documentSnapshot.getString(SECOND_LAST_LIKER_NAME);
                    notificationSecondLastUserPhotoURL = documentSnapshot.getString(SECOND_LAST_LIKER_PHOTO);
                    notificationDateTime = documentSnapshot.getDate(NOTIFICATION_DATE);
                    notificationLikeCount = documentSnapshot.getLong(LIKE_COUNT);
                    notificationPostText = documentSnapshot.getString(POST_TEXT);
                    notificationPostId = documentSnapshot.getString(POST_ID);
                    notificationSeen = documentSnapshot.getBoolean(SEEN);
                    postDateTime = documentSnapshot.getDate(POST_DATE);

                    singleNotificationData singleData = new singleNotificationData(notificationLastUserName,notificationLastUserPhotoURL,notificationLastUserId,notificationSecondLastUserName,notificationSecondLastUserPhotoURL,notificationSecondLastUserId,notificationLikeCount,notificationPostId,notificationPostText,notificationDateTime,postDateTime,notificationSeen);
                    notificationListCloud.add(singleData);
                    duplicateUserList.add(notificationLastUserName);
                }
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
