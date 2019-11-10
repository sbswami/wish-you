package com.sanshy.wishesapp;

/**
 * Created by sbswami on 3/23/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class likeListAdapter extends RecyclerView.Adapter<likeListAdapter.MyViewHolder> {

    public MyAdapterListener onClickListener;
    public interface MyAdapterListener {

        void likerTimelineClick(View v, int position);
    }

    Context myContext;

    private List<likeList> likeUserListArray;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView likerName;
        public ImageView likerImage;

        public MyViewHolder(View view) {
            super(view);

            likerName = view.findViewById(R.id.liker_name);
            likerImage = view.findViewById(R.id.liker_image);

            likerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.likerTimelineClick(v, getAdapterPosition());
                }
            });
            likerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.likerTimelineClick(v, getAdapterPosition());
                }
            });

        }
    }


    public likeListAdapter(List<likeList> likeUserListArray) {
        this.likeUserListArray = likeUserListArray;
    }
    public likeListAdapter(List<likeList> newRows, MyAdapterListener listener) {

        this.likeUserListArray = newRows;
        onClickListener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_like, parent, false);
        myContext = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        likeList likeList = likeUserListArray.get(position);
        holder.likerName.setText(likeList.getLikerName());
        try
        {
            Glide.with(myContext)
                    .load(likeList.getLikerPhotoURL())
                    .into(holder.likerImage);

        }catch (NullPointerException ex){
            Toast.makeText(myContext, "No Photo", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return likeUserListArray.size();
    }
}

