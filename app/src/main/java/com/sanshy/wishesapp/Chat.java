package com.sanshy.wishesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.Executor;


public class Chat extends Fragment {

    private List<singleChat> chatList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;

    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String CurrentUser;
    String CurrentUserName;
    String CurrentUserPhotoURL;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Chat() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (currentUser!=null){
            CurrentUser = currentUser.getUid();
            CurrentUserName = currentUser.getDisplayName();
            try{
                CurrentUserPhotoURL = currentUser.getPhotoUrl().toString();
            }catch (NullPointerException ex){
                CurrentUserPhotoURL = "";
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = myView.findViewById(R.id.recycler_view);

        mAdapter = new ChatAdapter(chatList,new ChatAdapter.MyAdapterListener(){
            @Override
            public void mainCardOnClick(View v, int position) {
                Intent intent = new Intent(getContext(),ChatMsg.class);
                intent.putExtra("name",chatList.get(position).getsName());
                intent.putExtra("id",chatList.get(position).getrId());
                intent.putExtra("photoURL",chatList.get(position).getPhotoUrl());
                startActivity(intent);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        if (currentUser!=null){
            addChatList();
        }

        return myView;
    }

    public void addChatList(){

        Query query = db.collection("RecentChatRoom").document("UserList").collection(CurrentUser).orderBy("dTime", Query.Direction.DESCENDING);
        query.addSnapshotListener(new com.google.firebase.firestore.EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                chatList.clear();
                for (DocumentSnapshot documentSnapshot : documentSnapshots){
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
                mAdapter.notifyDataSetChanged();
            }
        });

        mAdapter.notifyDataSetChanged();
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
