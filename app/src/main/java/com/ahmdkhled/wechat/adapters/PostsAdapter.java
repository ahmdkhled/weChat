package com.ahmdkhled.wechat.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ahmdkhled.wechat.R;
import com.ahmdkhled.wechat.activities.CommentsActivity;
import com.ahmdkhled.wechat.model.Post;
import com.ahmdkhled.wechat.model.User;
import com.ahmdkhled.wechat.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ahmed Khaled on 6/3/2018.
 */

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Post> posts;
    private Context context;
    private OnPostCLicked onPostCLicked;

    private static final int POSTS_TYPE=1;
    private static final int AD_TYPE=2;
    private  String adUnitId="";
    private int adEach=10;
    private DatabaseReference root;
    private static final int LIKED_STATE=1;
    private static final int UNLIKED_STATE=2;


    public PostsAdapter(Context context,ArrayList<Post> posts,OnPostCLicked onPostCLicked) {
        this.posts = posts;
        this.context = context;
        this.onPostCLicked = onPostCLicked;
        adUnitId=context.getResources().getString(R.string.adunit_id);
        root = FirebaseDatabase.getInstance().getReference().getRoot();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==POSTS_TYPE){
        View row= LayoutInflater.from(context).inflate(R.layout.post_row,parent,false);
        return  new PostHolder(row);
        }else{
            View row= LayoutInflater.from(context).inflate(R.layout.ad_container,parent,false);
            return new AdHolder(row);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position)==POSTS_TYPE){
            PostHolder postHolder= (PostHolder) holder;
            int pos= mapPosition(position);
            postHolder.populateData(pos,postHolder);
        }else{
            loadAd(holder);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isAdType(position)){
            return AD_TYPE;
        }else {
            return POSTS_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        if(posts==null){
            return 0;}
        if (posts.size()%adEach==0){
            return posts.size()+(posts.size()/adEach)-1;
        }else{
            return posts.size()+(posts.size()/adEach);
        }

    }

    private boolean isAdType(int position){
        int num=position/adEach;
        return position == adEach * num + (num - 1);
    }

    private int mapPosition(int position){
        int adNum=position/adEach;
        int prevNum=adNum-1;
        int nextNum=adNum+1;
        int adNumValue=adNum*adEach+(adNum-1);
        int prevNumValue=prevNum*adEach+(prevNum-1);
        int nextNumValue=nextNum*adEach+(nextNum-1);
        if (position>prevNumValue&&position<adNumValue){
            return position-prevNum;
        }else if (position>adNumValue&&position<nextNumValue){
            return position-adNum;
        }

        return -10;
    }


    class PostHolder extends RecyclerView.ViewHolder{
        TextView postContent,author,commentsCount;
        ImageView profileImg;
        Button like,comment;
        PostHolder(View itemView) {
            super(itemView);
            postContent=itemView.findViewById(R.id.postContent_TV);
            author=itemView.findViewById(R.id.postAuthor_TV);
            commentsCount=itemView.findViewById(R.id.commentsCount);
            profileImg=itemView.findViewById(R.id.postImg_IV);
            like=itemView.findViewById(R.id.like_BU);
            comment=itemView.findViewById(R.id.comment_BU);

            author.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPostCLicked!=null){
                        onPostCLicked.onNameClicked(getAdapterPosition());
                    }
                }
            });

            profileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPostCLicked!=null){
                        onPostCLicked.onImageClicked(getAdapterPosition());
                    }
                }
            });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (posts.get(getAdapterPosition()).getLikeState()==UNLIKED_STATE){
                        likePost(posts.get(getAdapterPosition()).getUid());
                    }else if (posts.get(getAdapterPosition()).getLikeState()==LIKED_STATE){
                        unLikePost(posts.get(getAdapterPosition()).getUid());
                    }
                }
            });

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, CommentsActivity.class);
                    intent.putExtra(CommentsActivity.POST_KEY,posts.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }

        void populateData(int position,PostHolder holder){
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
            String postUid=posts.get(position).getUid();
            handleLikeButton(postUid,position,holder);
            getCommentsCount(postUid,holder,getAdapterPosition());
        }
    }



    class AdHolder extends RecyclerView.ViewHolder{
        NativeContentAdView contentAdView;
        NativeAppInstallAdView appInstallAdView;
        LinearLayout appInstallAd_container;
        LinearLayout contentAd_container;

        AdHolder(View itemView) {
            super(itemView);

            appInstallAd_container=itemView.findViewById(R.id.appInstallAd_container);
            contentAd_container=itemView.findViewById(R.id.contentAd_container);
            appInstallAd_container.setVisibility(View.GONE);
            contentAd_container.setVisibility(View.VISIBLE);

            contentAdView = itemView.findViewById(R.id.nativeContentAdView);

            contentAdView.setHeadlineView(contentAdView.findViewById(R.id.contentad_headline));
            contentAdView.setImageView(contentAdView.findViewById(R.id.contentad_image));
            contentAdView.setBodyView(contentAdView.findViewById(R.id.contentad_body));
            contentAdView.setCallToActionView(contentAdView.findViewById(R.id.contentad_call_to_action));
            contentAdView.setLogoView(contentAdView.findViewById(R.id.contentad_logo));
            contentAdView.setAdvertiserView(contentAdView.findViewById(R.id.contentad_advertiser));

            appInstallAdView =  itemView.findViewById(R.id.nativeAppInstallAdView);

            MediaView mediaView = appInstallAdView.findViewById(R.id.appinstall_media);
            appInstallAdView.setMediaView(mediaView);
            appInstallAdView.setHeadlineView(appInstallAdView.findViewById(R.id.appinstall_headline));
            appInstallAdView.setBodyView(appInstallAdView.findViewById(R.id.appinstall_body));
            appInstallAdView.setCallToActionView(appInstallAdView.findViewById(R.id.appinstall_call_to_action));
            appInstallAdView.setIconView(appInstallAdView.findViewById(R.id.appinstall_app_icon));
            appInstallAdView.setPriceView(appInstallAdView.findViewById(R.id.appinstall_price));
            appInstallAdView.setStarRatingView(appInstallAdView.findViewById(R.id.appinstall_stars));
            appInstallAdView.setStoreView(appInstallAdView.findViewById(R.id.appinstall_store));
        }

        private void populateContentAd(NativeContentAd nativeContentAd){
            appInstallAd_container.setVisibility(View.GONE);
            contentAd_container.setVisibility(View.VISIBLE);

            ((TextView) contentAdView.getHeadlineView()).setText(nativeContentAd.getHeadline());
            ((TextView) contentAdView.getBodyView()).setText(nativeContentAd.getBody());
            ((TextView) contentAdView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
            ((TextView) contentAdView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

            List<NativeAd.Image> images = nativeContentAd.getImages();

            if (images.size() > 0) {
                ((ImageView) contentAdView.getImageView()).setImageDrawable(images.get(0).getDrawable());
            }
            NativeAd.Image logoImage = nativeContentAd.getLogo();
            if (logoImage == null) {
                contentAdView.getLogoView().setVisibility(View.INVISIBLE);
            } else {
                ((ImageView) contentAdView.getLogoView()).setImageDrawable(logoImage.getDrawable());
                contentAdView.getLogoView().setVisibility(View.VISIBLE);
            }

            contentAdView.setNativeAd(nativeContentAd);
        }
        private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd) {
            appInstallAd_container.setVisibility(View.VISIBLE);
            contentAd_container.setVisibility(View.GONE);

            ((ImageView) appInstallAdView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon()
                    .getDrawable());
            ((TextView) appInstallAdView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
            ((TextView) appInstallAdView.getBodyView()).setText(nativeAppInstallAd.getBody());
            ((Button) appInstallAdView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());

            if (nativeAppInstallAd.getPrice() == null) {
                appInstallAdView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                appInstallAdView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) appInstallAdView.getPriceView()).setText(nativeAppInstallAd.getPrice());
            }

            if (nativeAppInstallAd.getStore() == null) {
                appInstallAdView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                appInstallAdView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) appInstallAdView.getStoreView()).setText(nativeAppInstallAd.getStore());
            }

            if (nativeAppInstallAd.getStarRating() == null) {
                appInstallAdView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) appInstallAdView.getStarRatingView())
                        .setRating(nativeAppInstallAd.getStarRating().floatValue());
                appInstallAdView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            appInstallAdView.setNativeAd(nativeAppInstallAd);
        }

    }



    private void loadAd(final RecyclerView.ViewHolder holder){
        AdLoader adLoader=new AdLoader.Builder(context,adUnitId)
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                        Log.d("NATIVEEE","onContentAdLoaded");
                        AdHolder contentAdHolder= (AdHolder) holder;
                        contentAdHolder.populateContentAd(nativeContentAd);

                    }
                }).forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                        Log.d("NATIVEEE","onAppInstallAdLoaded");
                        AdHolder appInstallAdViewHolder= (AdHolder) holder;
                        appInstallAdViewHolder.populateAppInstallAdView(nativeAppInstallAd);

                    }
                }).withAdListener(new AdListener(){
                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        Log.d("NATIVEEE","onAdFailedToLoad "+i);

                    }
                }).build();

        adLoader.loadAd(new AdRequest.Builder().addTestDevice(context.getString(R.string.test_device_hash)).build());
    }

    private void likePost(String postId){
        DatabaseReference postRef=root.child("likes")
                .child(postId).child(getUserUid());
        Map<String,Object> likeMap=new HashMap<>();
        likeMap.put("date",-System.currentTimeMillis());
        postRef.updateChildren(likeMap);
    }

    private void unLikePost(String postUid) {
        DatabaseReference postRef=root.child("likes")
                .child(postUid).child(getUserUid());
        postRef.removeValue();
    }

    private void getCommentsCount(String postUid, final PostHolder holder, final int pos){
        DatabaseReference commentsRef=root.child("comments").child(postUid);
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                posts.get(pos).setCommentsCount(dataSnapshot.getChildrenCount());
                holder.commentsCount.setText(dataSnapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void handleLikeButton(final String postUid, final int pos, final PostHolder holder){
        DatabaseReference likesRf=root.child("likes");
        likesRf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(postUid)&&dataSnapshot.child(postUid).hasChild(getUserUid())){
                    holder.like.setTextColor(Color.parseColor("#03A9F4"));
                    posts.get(pos).setLikeState(LIKED_STATE);
                    //Drawable mDrawable = context.getResources().getDrawable(R.drawable.ic_thumb_up_blue_24dp);
                    Drawable mDrawable = ContextCompat.getDrawable(context,R.drawable.ic_thumb_up_blue_24dp);
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(mDrawable,null,null,null);
                }else {
                    holder.like.setTextColor(Color.parseColor("#FF424242"));
                    posts.get(pos).setLikeState(UNLIKED_STATE);
                    //Drawable mDrawable = context.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp);
                    Drawable mDrawable = ContextCompat.getDrawable(context,R.drawable.ic_thumb_up_black_24dp);
                    holder.like.setCompoundDrawablesWithIntrinsicBounds(mDrawable,null,null,null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getUserUid(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        return currentUser.getUid();
    }

    public interface OnPostCLicked{
        void onImageClicked(int position);
        void onNameClicked(int position);
    }
}
