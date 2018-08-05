package com.ahmdkhled.wechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.model.Comment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 8/5/2018.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentHolder>{

    private Context context;
    private ArrayList<Comment> commentsList;

    public CommentsAdapter(Context context, ArrayList<Comment> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_row,parent,false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
        holder.populateComment(position);
    }

    @Override
    public int getItemCount() {
        if (commentsList==null)
            return 0;
        return commentsList.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder{
        ImageView authorImg;
        TextView author,content;
        public CommentHolder(View itemView) {
            super(itemView);
            author=itemView.findViewById(R.id.commentAuthor);
            authorImg=itemView.findViewById(R.id.commentAuthorImg);
            content=itemView.findViewById(R.id.commentContent);
        }

        void populateComment(int position){
            if (commentsList.get(position).getContent()!=null){
                content.setText(commentsList.get(position).getContent());
            }
            if (commentsList.get(position).getUser()!=null) {
                author.setText(commentsList.get(position).getUser().getName());
                Glide.with(context).load(commentsList.get(position).getUser().getProfileImg()).into(authorImg);
            }
        }
    }
}
