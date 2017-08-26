package com.mkdenis.twitterclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import java.net.HttpURLConnection;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private TextView textViewUserName;
    private Button buttonNewTweet, buttonLogOut;
    private ImageView imageViewProfileImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewTimeline;
    private RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        findView();
        buttonNewTweet.setOnClickListener(onClickListener);
        textViewUserName.setText(PreferenceSettingsManager.getString("user_name"));
        setProfileImage();
        layoutManager = new LinearLayoutManager(this);
        recyclerViewTimeline.setLayoutManager(layoutManager);
        TweetListAdapter adapterEmpty = new TweetListAdapter(this);
        recyclerViewTimeline.setAdapter(adapterEmpty);
        recyclerViewTimeline.addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) layoutManager) {
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
        loadTweets();
        recyclerViewTimeline.setHasFixedSize(true);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    private void findView() {
        textViewUserName = (TextView) findViewById(R.id.textView_user_name);
        recyclerViewTimeline = (RecyclerView) findViewById(R.id.recycler_view_timeline);
        buttonNewTweet = (Button) findViewById(R.id.button_new_tweet);
        imageViewProfileImage = (ImageView) findViewById(R.id.imageView_main_profile_image);
        buttonLogOut = (Button) findViewById(R.id.button_logout);
        buttonLogOut.setOnClickListener(onClickListener);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.user_activity_swipe_refresh_layout);
    }

    private void setProfileImage(){
        try {
            HttpURLConnection connection = TwitterRestAPIManager.getInstance().httpGetTwitterAPI(TwitterAPIURL.PROFILE +
                    PreferenceSettingsManager.getString("user_name"));
            ReadResponse readResponse = new ReadResponse(connection, onEventListener,
                    OnEventListener.LOAD_USER_PROFILE_IMAGE);
            readResponse.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTweets(){
        try {
            HttpURLConnection connection = TwitterRestAPIManager.getInstance().httpGetTwitterAPI(TwitterAPIURL.HOME_TIMELINE);
            ReadResponse readResponse = new ReadResponse(connection, onEventListener,
                    OnEventListener.LOAD_TWEETS);
            readResponse.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPreviousTweets(){
        TweetListAdapter adapter = (TweetListAdapter) recyclerViewTimeline.getAdapter();
        long maxId = adapter.getOldestTweetId();
        if (maxId > 0) {
            String url = TwitterAPIURL.HOME_TIMELINE + "?max_id=" + maxId;
            try {
                HttpURLConnection connection = TwitterRestAPIManager.getInstance().httpGetTwitterAPI(url);
                ReadResponse readResponse = new ReadResponse(connection, onEventListener,
                        OnEventListener.LOAD_PREVIOUS_TWEETS);
                readResponse.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadNewTweets(){
        TweetListAdapter adapter = (TweetListAdapter) recyclerViewTimeline.getAdapter();
        long sinceId = adapter.getNewestTweetId();
        if (sinceId > 0) {
            String url = TwitterAPIURL.HOME_TIMELINE + "?since_id=" + sinceId;
            try {
                HttpURLConnection connection = TwitterRestAPIManager.getInstance().httpGetTwitterAPI(url);
                ReadResponse readResponse = new ReadResponse(connection, onEventListener,
                        OnEventListener.LOAD_NEW_TWEETS);
                readResponse.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            swipeRefreshLayout.setRefreshing(false);
    }

    private final OnEventListener onEventListener = new OnEventListener() {
        @Override
        public void onSuccess(Object object, int eventType) {
            switch (eventType) {
                case 0: {
                    List<Tweet> tweets = JSONParser.parseTimeline((String) object);
                    TweetListAdapter adapter = new TweetListAdapter(UserActivity.this, tweets);
                    recyclerViewTimeline.setAdapter(adapter);
                    break;
                }
                case 1: {
                    List<Tweet> tweets = JSONParser.parseTimeline((String) object);
                    if (tweets.size() > 0) {
                        TweetListAdapter adapter = (TweetListAdapter) recyclerViewTimeline.getAdapter();
                        adapter.addListToTop(tweets);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                }
                case 2:{
                    List<Tweet> tweets = JSONParser.parseTimeline((String) object);
                    if (tweets.size() > 0) {
                        TweetListAdapter adapter = (TweetListAdapter) recyclerViewTimeline.getAdapter();
                        adapter.addListToBottom(tweets);
                    }
                    break;
                }
                case 3:{
                    TwitterUser user = JSONParser.parseTwitterUser((String) object);
                    ImageLazyLoader imageLazyLoader = new ImageLazyLoaderAdapter();
                    imageLazyLoader.loadImageFromUrl(UserActivity.this, user.getProfileImageUrl(),
                            imageViewProfileImage);
                    break;
                }
            }
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
                    PreferenceSettingsManager.clear();
                    finish();
                    break;
                }
            }
        }
    };
}