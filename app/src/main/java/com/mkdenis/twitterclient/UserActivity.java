package com.mkdenis.twitterclient;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    private TextView textViewUserName;
    private ListView listViewUserTimeLine;
    private Button buttonNewTweet, buttonLogOut;
    private ImageView imageViewProfileImage;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        findView();

        textViewUserName.setText(PreferenceSettingsManager.getString("user_name"));
        TwitterUser user = TwitterRestAPIManager.getInstance().
                getUser(PreferenceSettingsManager.getString("user_name"));
        Picasso.with(this).load(user.getProfileImageUrl()).into(imageViewProfileImage);

        ArrayList<Tweet> tweets = TwitterRestAPIManager.getInstance().getTimeline(
                getString(R.string.HOME_TIMELINE_URL));
        TweetListAdapter adapter = new TweetListAdapter(this, tweets);
        listViewUserTimeLine.setAdapter(adapter);
        listViewUserTimeLine.setOnScrollListener(new EndlessScrollListener(3, this));
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

    }

    private void findView() {
        textViewUserName = (TextView) findViewById(R.id.textView_user_name);
        listViewUserTimeLine = (ListView) findViewById(R.id.listView_user_timeline);
        buttonNewTweet = (Button) findViewById(R.id.button_new_tweet);
        buttonNewTweet.setOnClickListener(onClickListener);
        imageViewProfileImage = (ImageView) findViewById(R.id.imageView_main_profile_image);
        buttonLogOut = (Button) findViewById(R.id.button_logout);
        buttonLogOut.setOnClickListener(onClickListener);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.user_activity_swipe_refresh_layout);
    }

    private void loadNewTweets(){
        TweetListAdapter adapter = (TweetListAdapter) listViewUserTimeLine.getAdapter();
        long sinceId = adapter.getItemId(0);
        String url = getString(R.string.HOME_TIMELINE_URL) + "?since_id=" + sinceId;
        ArrayList<Tweet> tweets = TwitterRestAPIManager.getInstance().getTimeline(url);
        if (tweets.size() > 0)
            adapter.addArrayListToTop(tweets);
    }

    private final SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    loadNewTweets();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    };

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
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