package com.mkdenis.twitterclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserActivity extends AppCompatActivity{

    private TextView textViewUserName;
    private Button buttonNewTweet, buttonLogOut;
    private ImageView imageViewProfileImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewTimeline;
    private JSONParser jsonParser;
    private static final String KEY_TWEET_LIST = "TWEET_LIST";
    private static final String KEY_PROFILE_IMAGE = "PROFILE_IMAGE";
    private TweetListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        findView();
        this.buttonNewTweet.setOnClickListener(this.onClickListener);
        this.textViewUserName.setText(PreferenceSettingsManager.getUserName());
        this.jsonParser = new JSONParser();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.recyclerViewTimeline.setLayoutManager(layoutManager);
        if (savedInstanceState == null){
            this.adapter = new TweetListAdapter(this, Collections.EMPTY_LIST);
            loadTweets();
            setProfileImage();
        } else {
            List<Tweet> tweets = savedInstanceState.getParcelableArrayList(KEY_TWEET_LIST);
            this.adapter = new TweetListAdapter(this, tweets);
            byte[] profileImageByteArray = savedInstanceState.getByteArray(KEY_PROFILE_IMAGE);
            this.imageViewProfileImage.setImageBitmap(byteArrayToBitmap(profileImageByteArray));
        }
        this.recyclerViewTimeline.setAdapter(this.adapter);
        this.recyclerViewTimeline.setHasFixedSize(true);
        this.swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        this.swipeRefreshLayout.setOnRefreshListener(this.onRefreshListener);
        this.recyclerViewTimeline.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(RecyclerView view) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        loadPreviousTweets();
                    }
                });
            }
        });
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_TWEET_LIST, (ArrayList<Tweet>) this.adapter.getTweets());
        byte[] profileImageByteArray = bitmapToByteArray(
                ((BitmapDrawable)imageViewProfileImage.getDrawable()).getBitmap());
        outState.putByteArray(KEY_PROFILE_IMAGE, profileImageByteArray);
        super.onSaveInstanceState(outState);
    }

    private void findView() {
        this.textViewUserName = (TextView) findViewById(R.id.textView_user_name);
        this.recyclerViewTimeline = (RecyclerView) findViewById(R.id.recycler_view_timeline);
        this.buttonNewTweet = (Button) findViewById(R.id.button_new_tweet);
        this.imageViewProfileImage = (ImageView) findViewById(R.id.imageView_main_profile_image);
        this.buttonLogOut = (Button) findViewById(R.id.button_logout);
        this.buttonLogOut.setOnClickListener(onClickListener);
        this.swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.user_activity_swipe_refresh_layout);
    }

    private byte[] bitmapToByteArray(Bitmap bitmap){
        int qualityPersent = 100;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, qualityPersent, stream);
        return stream.toByteArray();
    }

    private Bitmap byteArrayToBitmap(byte[] byteArray){
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    private void setProfileImage(){
        try {
            HttpURLConnection connection = TwitterRestAPIManager.getInstance().httpRequestTwitterAPI(HTTPMethods.GET,
                            TwitterAPIURL.PROFILE + PreferenceSettingsManager.getUserName());
            ReadResponse readResponse = new ReadResponse(connection, this.onLoadUserProfileImage);
            readResponse.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTweets(){
        try {
            HttpURLConnection connection = TwitterRestAPIManager.getInstance().
                    httpRequestTwitterAPI(HTTPMethods.GET,TwitterAPIURL.HOME_TIMELINE);
            ReadResponse readResponse = new ReadResponse(connection, this.onLoadTweets);
            readResponse.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPreviousTweets(){
        long maxId = this.adapter.getOldestTweetId();
        if (maxId > 0) {
            String url = TwitterAPIURL.HOME_TIMELINE + "?max_id=" + maxId;
            try {
                HttpURLConnection connection = TwitterRestAPIManager.getInstance().
                        httpRequestTwitterAPI(HTTPMethods.GET,url);
                ReadResponse readResponse = new ReadResponse(connection, this.onLoadPreviousTweets);
                readResponse.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadNewTweets(){
        long sinceId = this.adapter.getNewestTweetId();
        if (sinceId > 0) {
            String url = TwitterAPIURL.HOME_TIMELINE + "?since_id=" + sinceId;
            try {
                HttpURLConnection connection = TwitterRestAPIManager.getInstance().
                        httpRequestTwitterAPI(HTTPMethods.GET, url);
                ReadResponse readResponse = new ReadResponse(connection, this.onLoadNewTweets);
                readResponse.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            this.swipeRefreshLayout.setRefreshing(false);
    }

    private final OnEventListener onLoadTweets = new OnEventListener() {
        @Override
        public void onSuccess(Object object) {
            List<Tweet> tweets = jsonParser.parseTimeline((String) object);
            adapter.setTweets(tweets);
        }

        @Override
        public void onFailure(Exception e) {

        }
    };

    private final OnEventListener onLoadNewTweets = new OnEventListener() {
        @Override
        public void onSuccess(Object object) {
            List<Tweet> tweets = jsonParser.parseTimeline((String) object);
            if (tweets.size() > 0) {
                adapter.addListToTop(tweets);
            }
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(Exception e) {

        }
    };

    private final OnEventListener onLoadPreviousTweets = new OnEventListener() {
        @Override
        public void onSuccess(Object object) {
            List<Tweet> tweets = jsonParser.parseTimeline((String) object);
            if (tweets.size() > 0) {
                adapter.addListToBottom(tweets);
            }
        }

        @Override
        public void onFailure(Exception e) {

        }
    };

    private final OnEventListener onLoadUserProfileImage = new OnEventListener() {
        @Override
        public void onSuccess(Object object) {
            TwitterUser user = jsonParser.parseTwitterUser((String) object);
            ImageLazyLoader imageLazyLoader = new PicassoImageLazyLoader(UserActivity.this);
            imageLazyLoader.loadImageFromUrl(user.getProfileImageUrl(), imageViewProfileImage);
        }

        @Override
        public void onFailure(Exception e) {

        }
    };

    private final SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    loadNewTweets();
                }
            });
        }
    };

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_new_tweet: {
                TwitterSession session = TwitterCore.getInstance().getSessionManager()
                        .getActiveSession();
                Intent intent = new ComposerActivity.Builder(UserActivity.this)
                        .session(session)
                        .text("")
                        .hashtags("")
                        .createIntent();
                startActivity(intent);
                break;
            }
            case R.id.button_logout: {
                LogOut logOut = new LogOut();
                logOut.logOutUser();
                finish();
                break;
            }
        }
        }
    };
}