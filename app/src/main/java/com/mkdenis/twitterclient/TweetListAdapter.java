package com.mkdenis.twitterclient;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class TweetListAdapter extends RecyclerView.Adapter<TweetListAdapter.ViewHolder> {
    private final List<Tweet> tweets;
    private final Context context;
    private final int VIEW_ITEM = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewScreenName;
        private TextView textViewCreatedTime;
        private TextView textViewTweetText;
        private TextView textViewRetweetCount;
        private TextView textViewFavoriteCount;
        private ImageView imageViewUserProfileImage;
        private LinearLayout linearLayoutImageParent;

        public ViewHolder(View view) {
            super(view);
            textViewName = (TextView) view.findViewById(R.id.textView_name);
            textViewScreenName = (TextView) view.findViewById(R.id.textView_screen_name);
            textViewCreatedTime = (TextView) view.findViewById(R.id.textView_created_time);
            textViewTweetText = (TextView) view.findViewById(R.id.textView_tweet_text);
            textViewRetweetCount = (TextView) view.findViewById(R.id.textView_retweet_count);
            textViewFavoriteCount = (TextView) view.findViewById(R.id.textView_favorite_count);
            imageViewUserProfileImage = (ImageView) view.findViewById(R.id.imageView_profile_image);
            linearLayoutImageParent = (LinearLayout) view.findViewById(R.id.image_parent_layout);
        }
    }

    public static class ProgressViewHolder extends TweetListAdapter.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar)view.findViewById(R.id.more_progress);
        }
    }

    public TweetListAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    public TweetListAdapter(Context context){
        this.context = context;
        this.tweets = null;
    }

    @Override
    public int getItemViewType(int position) {
        if (tweets != null)
            if (position == tweets.size() - 1)
                return 1;
            else
                return 0;
        else
            return 1;
    }

    public void addListToBottom(List<Tweet> newTweets){
        tweets.addAll(newTweets);
        this.notifyDataSetChanged();
    }

    public void addListToTop(List<Tweet> newTweets){
        if (tweets != null)
            tweets.addAll(0, newTweets);
        this.notifyDataSetChanged();
    }

    public long getNewestTweetId(){
        if (tweets != null)
            return tweets.get(0).getId();
        else
            return 0;
    }

    public long getOldestTweetId(){
        if (tweets != null)
            return tweets.get(tweets.size() - 1).getId();
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        if (tweets != null)
            return tweets.size();
        else
            return 1;
    }

    @Override
    public long getItemId(int position) {
        return tweets.get(position).getId();
    }

    @Override
    public TweetListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TweetListAdapter.ViewHolder viewHolder;
        if(viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet, parent, false);
            viewHolder = new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetListAdapter.ViewHolder holder, int position) {

        if(holder instanceof ProgressViewHolder){
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }else{
            Tweet tweet = tweets.get(position);
            TwitterUser user = tweet.getUser();
            List<TwitterMedia> media = tweet.getMedia();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yy");
            String tweetCreatedTime = dateFormat.format(tweet.getCreatedTime());
            holder.textViewName.setText(user.getName());
            holder.textViewScreenName.setText("@" + user.getScreenName());
            holder.textViewCreatedTime.setText(tweetCreatedTime);
            holder.textViewTweetText.setText(tweet.getText());
            holder.textViewRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
            holder.textViewFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
            ImageLazyLoader imageLazyLoader = new ImageLazyLoaderAdapter();
            imageLazyLoader.loadImageFromUrl(context, user.getProfileImageUrl(),
                    holder.imageViewUserProfileImage);

            holder.linearLayoutImageParent.removeAllViews();
            for (int i = 0; i < media.size(); i++) {
                ImageView imageView = new ImageView(context);
                imageLazyLoader.loadImageFromUrl(context, media.get(i).getUrl(),
                        imageView);
                holder.linearLayoutImageParent.addView(imageView);
            }
        }
    }
}
