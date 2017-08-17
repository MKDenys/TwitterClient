package com.mkdenis.twitterclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
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
            PreferenceSettingsManager.addString("auth_token", authToken.token);
            PreferenceSettingsManager.addString("auth_secret", authToken.secret);
            PreferenceSettingsManager.addString("user_name", session.getUserName());

            TwitterAuthClient authClient = new TwitterAuthClient();
            authClient.requestEmail(session, new Callback<String>() {
                @Override
                public void success(Result<String> result) {
                    // Do something with the result, which provides the email address
                    PreferenceSettingsManager.addString("user_email", result.data);
                }

                @Override
                public void failure(TwitterException exception) {
                    Log.e("get email", exception.getMessage());
                }
            });
            Intent intent = new Intent(LoginActivity.this, UserActivity.class);
            startActivity(intent);
        }

        @Override
        public void failure(TwitterException exception) {
            Log.e("get token", exception.getMessage());
        }
    };
}
