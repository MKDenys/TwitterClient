package com.mkdenis.twitterclient;

import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TweetViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewName;
    public TextView textViewScreenName;
    public TextView textViewCreatedTime;
    public TextView textViewTweetText;
    public TextView textViewRetweetCount;
    public TextView textViewFavoriteCount;
    public ImageView imageViewUserProfileImage, imageViewFavorite;
    public LinearLayout imageContainer;

    public TweetViewHolder(View view) {
        super(view);
        this.textViewName = (TextView) view.findViewById(R.id.textView_name);
        this.textViewScreenName = (TextView) view.findViewById(R.id.textView_screen_name);
        this.textViewCreatedTime = (TextView) view.findViewById(R.id.textView_created_time);
        this.textViewTweetText = (TextView) view.findViewById(R.id.textView_tweet_text);
        this.textViewTweetText.setMovementMethod(LinkMovementMethod.getInstance());
        this.textViewRetweetCount = (TextView) view.findViewById(R.id.textView_retweet_count);
        this.textViewFavoriteCount = (TextView) view.findViewById(R.id.textView_favorite_count);
        this.imageViewUserProfileImage = (ImageView) view.findViewById(R.id.imageView_profile_image);
        this.imageContainer = (LinearLayout) view.findViewById(R.id.image_container);
        this.imageViewFavorite = (ImageView) view.findViewById(R.id.imageView_favorite);
    }
}
