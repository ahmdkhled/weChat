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
import com.ahmdkhled.wechat.model.Post;
import com.ahmdkhled.wechat.model.User;
import com.ahmdkhled.wechat.utils.Utils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Ahmed Khaled on 6/24/2018.
 */

public class FragPostsAdapter extends RecyclerView.Adapter<FragPostsAdapter.PostHolder>{
    private ArrayList<Post> posts;
    private Context context;

    public FragPostsAdapter(ArrayList<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row= LayoutInflater.from(context).inflate(R.layout.post_row,parent,false);
        return new PostHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.populateData(position);
    }

    @Override
    public int getItemCount() {
        if (posts==null) return 0;
        return posts.size();
    }


    class PostHolder extends RecyclerView.ViewHolder{
        TextView postContent,author;
        ImageView profileImg;
        PostHolder(View itemView) {
            super(itemView);
            postContent=itemView.findViewById(R.id.postContent_TV);
            author=itemView.findViewById(R.id.postAuthor_TV);
            profileImg=itemView.findViewById(R.id.postImg_IV);


        }

        void populateData(int position){
            postContent.setText(posts.get(position).getContent());
            if (posts.get(position).getUser()!=null){
                User user=posts.get(position).getUser();
                author.setText(posts.get(position).getUser().getName());
                if (Utils.isEmpty(user.getProfileImg())){
                    profileImg.setImageResource(R.drawable.user);
                }else{
                    Glide.with(context).load(user.getProfileImg()).into(profileImg);
                }
            }
        }
    }

}
