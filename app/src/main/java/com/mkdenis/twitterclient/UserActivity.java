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

    private TextView userName, userEmail;
    private ListView userTimeLine;
    private Button newTweetButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        findView();

        userName.setText(PreferenceSettingsManager.getString("user_name"));
        userEmail.setText(PreferenceSettingsManager.getString("user_email"));
        updateTimeLine(userName.getText().toString(), userTimeLine);
        try {
            String response = TwitterRestAPIManager.getUserTimeline("KirichDev");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTimeLine(userName.getText().toString(), userTimeLine);
    }

    private void findView() {
        userName = (TextView) findViewById(R.id.textView_user_name);
        userEmail = (TextView) findViewById(R.id.textView_user_email);
        userTimeLine = (ListView) findViewById(R.id.listView_user_timeline);
        newTweetButton = (Button) findViewById(R.id.button_new_tweet);
        newTweetButton.setOnClickListener(onClickListener);
    }

    private void updateTimeLine(String userName, ListView listView)
    {
        UserTimeline userTimeline = new UserTimeline.Builder()
                .screenName(userName)
                .build();
        TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(this)
                .setTimeline(userTimeline)
                .build();
        listView.setAdapter(adapter);
    }

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
            }
        }
    };
}