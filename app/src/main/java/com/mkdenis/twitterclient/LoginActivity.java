package com.mkdenis.twitterclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginActivity extends AppCompatActivity {

    private  TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_login);
        PreferenceSettingsManager.init(this);
        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(callbackTwitterSession);
        if (PreferenceSettingsManager.getString("oauth_token") != null)
            showActivity(UserActivity.class);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    private final Callback<TwitterSession> callbackTwitterSession = new Callback<TwitterSession>(){
        @Override
        public void success(Result<TwitterSession> result) {
            // Do something with result, which provides a TwitterSession for making API calls
            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
            TwitterAuthToken authToken = session.getAuthToken();
            PreferenceSettingsManager.addString("oauth_token", authToken.token);
            PreferenceSettingsManager.addString("oauth_secret", authToken.secret);
            PreferenceSettingsManager.addString("user_name", session.getUserName());
            showActivity(UserActivity.class);
        }

        @Override
        public void failure(TwitterException exception) {
            Log.d("get token", exception.getMessage());
        }
    };

    private void showActivity(Class activityClass){
        Intent intent = new Intent(LoginActivity.this, activityClass);
        startActivity(intent);
    }
}
