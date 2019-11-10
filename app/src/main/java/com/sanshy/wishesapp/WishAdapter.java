package com.sanshy.wishesapp;

/**
 * Created by sbswami on 3/23/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WishAdapter extends RecyclerView.Adapter<WishAdapter.MyViewHolder> {

    public MyAdapterListener onClickListener;
    public interface MyAdapterListener {

        void writerTimelineClick(View v, int position);
        void likeClick(View v, int position);
        void shareClick(View v, int position);
        void reportClick(View v, int position);
        void likesCheckClick(View v, int position);
    }

    Context myContext;

    private List<singleWish> wishListArray;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView writerName,dateTime,mainWishText,likeCount,shareCount;
        public Button like,share;
        public ImageView writerPic,report;

        public MyViewHolder(View view) {
            super(view);
            writerName = view.findViewById(R.id.writerName);
            dateTime = view.findViewById(R.id.dateTime);
            mainWishText = view.findViewById(R.id.mainWishText);
            likeCount = view.findViewById(R.id.likeCount);
            like = view.findViewById(R.id.like);
            share = view.findViewById(R.id.share);
            writerPic = view.findViewById(R.id.writerPic);
            report = view.findViewById(R.id.report);
            shareCount = view.findViewById(R.id.shareCount);

            writerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.writerTimelineClick(view, getAdapterPosition());
                }
            });
            writerPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.writerTimelineClick(view, getAdapterPosition());
                }
            });
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.likeClick(view, getAdapterPosition());
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.shareClick(view, getAdapterPosition());
                }
            });
            report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.reportClick(view, getAdapterPosition());
                }
            });
            likeCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.likesCheckClick(view, getAdapterPosition());
                }
            });
        }
    }


    public WishAdapter(List<singleWish> wishListArray) {
        this.wishListArray = wishListArray;
    }
    public WishAdapter(List<singleWish> newRows, MyAdapterListener listener) {

        this.wishListArray = newRows;
        onClickListener = listener;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_wish_list, parent, false);
        myContext = parent.getContext();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        singleWish wishlist = wishListArray.get(position);
        holder.writerName.setText(wishlist.getWriterName());
        holder.mainWishText.setText(wishlist.getWishText());
        Date date = wishlist.getDate();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = format.format(date);
        holder.dateTime.setText(datetime);
        holder.shareCount.setText(wishlist.getShareCounter()+"");
        try
        {
            Glide.with(myContext)
                    .load(wishlist.getPhotoURL())
                    .into(holder.writerPic);

        }catch (NullPointerException ex){
            Toast.makeText(myContext, "No Photo", Toast.LENGTH_SHORT).show();
        }

        long tempCount = wishlist.getLikeCount()-1;
        String likeCountText = "You and "+tempCount+" Other Likes";
        String likeCountTextForZeroLike = wishlist.getLikeCount()+" Other Likes";
        if (tempCount == 0){
            likeCountText = "You like this Post";
        }
        if (tempCount==(-1)){
            likeCountTextForZeroLike = "Be First Like";
        }

        if (wishlist.getLike() == 1){
            holder.like.setText("Liked");
            holder.likeCount.setText(likeCountText);
        }else{
            holder.like.setText("Like");
            holder.likeCount.setText(likeCountTextForZeroLike);
        }
    }

    @Override
    public int getItemCount() {
        return wishListArray.size();
    }
}

