package com.mkdenis.twitterclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TweetListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tweet> tweets;
    private final Context context;
    private final static int VIEW_TWEET = 0;
    private final static int VIEW_PROGRESS_BAR = 1;

    public TweetListAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    @Override
    public int getItemViewType(int position) {
        if (this.tweets.size() > 0)
            if (position == this.tweets.size() - 1)
                return VIEW_PROGRESS_BAR;
            else
                return VIEW_TWEET;
        else
            return VIEW_PROGRESS_BAR;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = new ArrayList<>();
        this.tweets.addAll(tweets);
        this.notifyDataSetChanged();
    }

    public void addListToBottom(List<Tweet> newTweets){
        this.tweets.addAll(newTweets);
        this.notifyDataSetChanged();
    }

    public void addListToTop(List<Tweet> newTweets){
        this.tweets.addAll(0, newTweets);
        this.notifyDataSetChanged();
    }

    public long getNewestTweetId(){
        if (this.tweets.size() > 0)
            return this.tweets.get(0).getId();
        else
            return 0;
    }

    public long getOldestTweetId(){
        if (this.tweets.size() > 0)
            return this.tweets.get(this.tweets.size() - 1).getId();
        else
            return 0;
    }

    public List<Tweet> getTweets(){
        return tweets;
    }

    @Override
    public int getItemCount() {
        if (this.tweets.size() > 0)
            return this.tweets.size();
        else
            return 1;
    }

    @Override
    public long getItemId(int position) {
        return this.tweets.get(position).getId();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType == VIEW_TWEET) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet, parent, false);
            viewHolder = new TweetViewHolder(view, this.context);
        } else if (viewType == VIEW_PROGRESS_BAR){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }else {
            Tweet tweet = tweets.get(position);
            ((TweetViewHolder) holder).bind(tweet);
        }
    }
}
