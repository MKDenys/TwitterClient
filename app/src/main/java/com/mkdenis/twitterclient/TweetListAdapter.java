package com.mkdenis.twitterclient;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TweetListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Tweet> tweets = null;
    private LayoutInflater layoutInflater;
    private ImageView userProfileImage, tweetImage;

    public TweetListAdapter(Context context, ArrayList<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addArrayListToBottom(ArrayList<Tweet> newTweets){
        tweets.addAll(newTweets);
        this.notifyDataSetChanged();
    }

    public void addArrayListToTop(ArrayList<Tweet> newTweets){
        tweets.addAll(0, newTweets);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (tweets != null)
            return tweets.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return tweets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tweets.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.tweet, parent, false);
        }

        Tweet tweet = tweets.get(position);
        TwitterUser user = tweet.getUser();
        TwitterMedia media = tweet.getMedia();
        ((TextView) view.findViewById(R.id.textView_name)).setText(user.getName());
        ((TextView) view.findViewById(R.id.textView_screen_name)).setText("@" + user.getScreenName());
        ((TextView) view.findViewById(R.id.textView_created_time)).setText(tweet.getCreatedTime());
        ((TextView) view.findViewById(R.id.textView_tweet_text)).setText(tweet.getText());
        ((TextView) view.findViewById(R.id.textView_retweet_count)).setText(String.valueOf(tweet.getRetweetCount()));
        ((TextView) view.findViewById(R.id.textView_favorite_count)).setText(String.valueOf(tweet.getFavoriteCount()));
        userProfileImage = (ImageView) view.findViewById(R.id.imageView_profile_image);
        tweetImage = (ImageView) view.findViewById(R.id.imageView_tweet_image);
        Picasso.with(context).load(user.getProfileImageUrl()).into(userProfileImage);
        if (media != null) {
            tweetImage.setVisibility(View.VISIBLE);
            Picasso.with(context).load(media.getUrl()).into(tweetImage);
        } else
            tweetImage.setVisibility(View.GONE);
        return view;
    }
}
