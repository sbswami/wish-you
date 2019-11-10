package com.sanshy.wishesapp;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder>  {

    FirebaseAuth mAuth= FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    public ChatAdapter.MyAdapterListener onClickListener;
    public interface MyAdapterListener {

        void mainCardOnClick(View v, int position);
    }

    Context myContext;

    private List<singleChat> chatListArray;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView senderName,lastMsg,dateTime,unseenCount;
        public ImageView senderPic;
        public CardView mainCard;

        public MyViewHolder(View view) {
            super(view);
            senderName = view.findViewById(R.id.sender_name);
            dateTime = view.findViewById(R.id.date_time);
            lastMsg = view.findViewById(R.id.last_msg);
            unseenCount = view.findViewById(R.id.unseen_count);
            senderPic = view.findViewById(R.id.sender_pic);
            mainCard = view.findViewById(R.id.main_card);

            mainCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.mainCardOnClick(view, getAdapterPosition());
                }
            });
        }
    }


    public ChatAdapter(List<singleChat> chatListArray) {
        this.chatListArray = chatListArray;
    }
    public ChatAdapter(List<singleChat> newRows, ChatAdapter.MyAdapterListener listener) {

        this.chatListArray = newRows;
        onClickListener = listener;
    }
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_chat_list, parent, false);
        myContext = parent.getContext();
        return new ChatAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.MyViewHolder holder, int position) {
        singleChat chatlist = chatListArray.get(position);
        Date date = chatlist.getdTime();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
        Date date1 = new Date();
        String currentDate = format.format(date1);
        String datetime = format.format(date);
        String finalDate = datetime;
        if (currentDate.equals(datetime)){
            finalDate = format1.format(date);
        }
        holder.senderName.setText(chatlist.getsName());

        String preText = "";
        try{
            if (currentUser.getUid().equals(chatlist.getsId())){
                preText = "You : ";
            }
        }catch (Exception e){}
        holder.lastMsg.setText(preText+chatlist.getLastMsg());
        holder.unseenCount.setText(chatlist.getUnSeen()+"");
        holder.dateTime.setText(finalDate);
        try
        {
            Glide.with(myContext)
                    .load(chatlist.getPhotoUrl())
                    .into(holder.senderPic);

        }catch (NullPointerException ex){

        }
    }

    @Override
    public int getItemCount() {
        return chatListArray.size();
    }
}
