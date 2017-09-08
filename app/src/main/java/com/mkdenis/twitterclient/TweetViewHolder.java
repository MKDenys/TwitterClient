package com.mkdenis.twitterclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetViewHolder extends RecyclerView.ViewHolder {
    private TextView textViewName;
    private TextView textViewScreenName;
    private TextView textViewCreatedTime;
    private TextView textViewTweetText;
    private TextView textViewRetweetCount;
    private TextView textViewFavoriteCount;
    private ImageView imageViewUserProfileImage, imageViewFavorite;
    private LinearLayout imageContainer;
    private Context context;
    private final static String DISPLAY_DATE_FORMAT = "HH:mm:ss dd.MM.yy";
    private ImageLazyLoader imageLazyLoader;
    private Tweet tweet;

    public TweetViewHolder(View view, Context context) {
        super(view);
        this.context = context;
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
        this.imageLazyLoader = new PicassoImageLazyLoader(this.context);
    }

    public void bind(Tweet tweet){
        this.tweet = tweet;
        TwitterUser user = tweet.getUser();
        DateFormat dateFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault());
        String tweetCreatedTime = dateFormat.format(tweet.getCreatedTime());
        this.textViewCreatedTime.setText(tweetCreatedTime);
        this.textViewName.setText(user.getName());
        String screenName = "@" + user.getScreenName();
        this.textViewScreenName.setText(screenName);
        setTweetText(this.tweet.getText());
        this.textViewRetweetCount.setText(String.valueOf(this.tweet.getRetweetCount()));
        this.textViewFavoriteCount.setText(String.valueOf(this.tweet.getFavoriteCount()));
        imageLazyLoader.loadImageFromUrl(user.getProfileImageUrl(), this.imageViewUserProfileImage);
        setTweetMedia(this.tweet.getMedia());
        if (this.tweet.getFavorited())
            this.imageViewFavorite.setImageResource(R.drawable.favorited);
        else
            this.imageViewFavorite.setImageResource(R.drawable.favorite);
        this.imageViewFavorite.setOnClickListener(onClickListener);
    }

    private void setTweetText(String text){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            this.textViewTweetText.setText(Html.fromHtml(text, Html. FROM_HTML_MODE_LEGACY));
        } else {
            this.textViewTweetText.setText(Html.fromHtml(text));
        }
    }

    private void setTweetMedia(List<TwitterMedia> twitterMediaList){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.imageContainer.removeAllViews();
        for (int i = 0; i < twitterMediaList.size(); i++) {
            TwitterMedia twitterMedia =  twitterMediaList.get(i);
            layoutParams.setMargins(this.context.getResources().getDimensionPixelOffset(R.dimen.tweet_image_margin),
                    this.context.getResources().getDimensionPixelOffset(R.dimen.tweet_image_margin),
                    this.context.getResources().getDimensionPixelOffset(R.dimen.tweet_image_margin),
                    this.context.getResources().getDimensionPixelOffset(R.dimen.tweet_image_margin));
            layoutParams.gravity = Gravity.CENTER;
            ImageView imageView = new ImageView(this.context);
            imageLazyLoader.loadImageFromUrl(twitterMedia.getUrl(), imageView);
            imageView.setLayoutParams(layoutParams);
            this.imageContainer.addView(imageView);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String url = "";
                if (tweet.getFavorited())
                    url = TwitterAPIURL.FAVORITES_DESTROY + tweet.getId();
                else
                    url = TwitterAPIURL.FAVORITES_CREATE + tweet.getId();
                final HttpURLConnection connection = TwitterRestAPIManager.getInstance().
                        httpRequestTwitterAPI(HTTPMethods.POST, url);
                WriteRequest writeRequest = new WriteRequest(connection, "", new OnEventListener() {
                    @Override
                    public void onSuccess(Object object) {
                        ReadResponse readResponse = new ReadResponse(connection, onReadResponseListener);
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
    };

    private OnEventListener onReadResponseListener = new OnEventListener() {
        @Override
        public void onSuccess(Object object) {
            if (tweet.getFavorited()) {
                imageViewFavorite.setImageResource(R.drawable.favorite);
                textViewFavoriteCount.setText(
                        String.valueOf(tweet.getFavoriteCount() - 1));
                tweet.setFavorited(false);
                tweet.setFavoriteCount(tweet.getFavoriteCount() - 1);
            } else {
                imageViewFavorite.setImageResource(R.drawable.favorited);
                textViewFavoriteCount.setText(
                        String.valueOf(tweet.getFavoriteCount() + 1));
                tweet.setFavorited(true);
                tweet.setFavoriteCount(tweet.getFavoriteCount() + 1);
            }
        }

        @Override
        public void onFailure(Exception e) {

        }
    };
}
