package com.mkdenis.twitterclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Tweet> tweets;
    private final Context context;
    private final static int VIEW_TWEET = 0;
    private final static int VIEW_PROGRESS_BAR = 1;
    private final static String DISPLAY_DATE_FORMAT = "HH:mm:ss dd.MM.yy";

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
        if (this.tweets != null)
            if (position == this.tweets.size() - 1)
                return VIEW_PROGRESS_BAR;
            else
                return VIEW_TWEET;
        else
            return VIEW_PROGRESS_BAR;
    }

    public void addListToBottom(List<Tweet> newTweets){
        this.tweets.addAll(newTweets);
        this.notifyDataSetChanged();
    }

    public void addListToTop(List<Tweet> newTweets){
        if (this.tweets != null)
            this.tweets.addAll(0, newTweets);
        this.notifyDataSetChanged();
    }

    public long getNewestTweetId(){
        if (this.tweets != null)
            return this.tweets.get(0).getId();
        else
            return 0;
    }

    public long getOldestTweetId(){
        if (this.tweets != null)
            return this.tweets.get(this.tweets.size() - 1).getId();
        else
            return 0;
    }

    public List<Tweet> getTweets(){
        return tweets;
    }

    @Override
    public int getItemCount() {
        if (this.tweets != null)
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
        RecyclerView.ViewHolder viewHolder;
        if(viewType == VIEW_TWEET) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tweet, parent, false);
            viewHolder = new TweetViewHolder(view);
        }else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if(holder instanceof ProgressViewHolder){
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }else{
            final TweetViewHolder tweetHolder = (TweetViewHolder) holder;
            final Tweet tweet = tweets.get(position);
            TwitterUser user = tweet.getUser();
            DateFormat dateFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault());
            String tweetCreatedTime = dateFormat.format(tweet.getCreatedTime());
            tweetHolder.textViewName.setText(user.getName());
            String screenName = "@" + user.getScreenName();
            tweetHolder.textViewScreenName.setText(screenName);
            tweetHolder.textViewCreatedTime.setText(tweetCreatedTime);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tweetHolder.textViewTweetText.setText(Html.fromHtml(tweet.getText(), Html. FROM_HTML_MODE_LEGACY));
            } else {
                tweetHolder.textViewTweetText.setText(Html.fromHtml(tweet.getText()));
            }
            tweetHolder.textViewRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
            tweetHolder.textViewFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
            ImageLazyLoader imageLazyLoader = new PicassoImageLazyLoader(this.context);
            imageLazyLoader.loadImageFromUrl(user.getProfileImageUrl(), tweetHolder.imageViewUserProfileImage);

            List<TwitterMedia> mediaList = tweet.getMedia();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tweetHolder.imageContainer.removeAllViews();
            for (int i = 0; i < mediaList.size(); i++) {
                TwitterMedia twitterMedia =  mediaList.get(i);
                layoutParams.setMargins(20, 20, 20, 20);
                layoutParams.gravity = Gravity.CENTER;
                ImageView imageView = new ImageView(this.context);
                imageLazyLoader.loadImageFromUrl(twitterMedia.getUrl(), imageView);
                imageView.setLayoutParams(layoutParams);
                tweetHolder.imageContainer.addView(imageView);
            }
            if (tweet.getFavorited())
                tweetHolder.imageViewFavorite.setImageResource(R.mipmap.favorited);
            else
                tweetHolder.imageViewFavorite.setImageResource(R.mipmap.favorite);
            tweetHolder.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String url = "";
                        if (tweet.getFavorited())
                            url = TwitterAPIURL.FAVORITES_DESTROY + getItemId(position);
                        else
                            url = TwitterAPIURL.FAVORITES_CREATE + getItemId(position);
                        final HttpURLConnection connection = TwitterRestAPIManager.getInstance().
                                httpRequestTwitterAPI(HTTPMethods.POST, url);
                        WriteRequest writeRequest = new WriteRequest(connection, "", new OnEventListener() {
                            @Override
                            public void onSuccess(Object object) {
                                ReadResponse readResponse = new ReadResponse(connection, new OnEventListener() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        if (tweet.getFavorited()) {
                                            tweetHolder.imageViewFavorite.setImageResource(R.mipmap.favorite);
                                            tweetHolder.textViewFavoriteCount.setText(
                                                    String.valueOf(tweet.getFavoriteCount() - 1));
                                            tweets.get(position).setFavorited(false);
                                            tweets.get(position).setFavoriteCount(tweet.getFavoriteCount() - 1);
                                        } else {
                                            tweetHolder.imageViewFavorite.setImageResource(R.mipmap.favorited);
                                            tweetHolder.textViewFavoriteCount.setText(
                                                    String.valueOf(tweet.getFavoriteCount() + 1));
                                            tweets.get(position).setFavorited(true);
                                            tweets.get(position).setFavoriteCount(tweet.getFavoriteCount() + 1);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {

                                    }
                                });
                                readResponse.execute();
                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });
                        writeRequest.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
