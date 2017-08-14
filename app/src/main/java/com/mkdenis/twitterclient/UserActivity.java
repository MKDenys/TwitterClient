package com.mkdenis.twitterclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

public class UserActivity extends AppCompatActivity {

    TextView user_name, user_email;
    ListView user_timeline;
    Button new_tweet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        user_name = (TextView) findViewById(R.id.textView_user_name);
        user_email = (TextView) findViewById(R.id.textView_user_email);
        user_timeline = (ListView) findViewById(R.id.listView_user_timeline);
        new_tweet = (Button) findViewById(R.id.button_new_tweet);

        user_name.setText(LoginActivity.pref.getString("user_name", ""));
        user_email.setText(LoginActivity.pref.getString("user_email", ""));
        UpdateTimeLine(user_name.getText().toString(), user_timeline);

        View.OnClickListener new_tweet_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                        .getActiveSession();
                final Intent intent = new ComposerActivity.Builder(UserActivity.this)
                        .session(session)
                        .text("")
                        .hashtags("")
                        .createIntent();
                startActivity(intent);
            }
        };
        new_tweet.setOnClickListener(new_tweet_click);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UpdateTimeLine(user_name.getText().toString(), user_timeline);
    }


    private void UpdateTimeLine(String user_name, ListView list_view)
    {
        UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(user_name)
                .build();
        TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();
        list_view.setAdapter(adapter);
    }
}