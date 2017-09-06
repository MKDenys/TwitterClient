package com.mkdenis.twitterclient;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceSettingsManager {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static final String KEY_OAUTH_TOKEN= "oauth_token";
    private static final String KEY_OAUTH_SECRET = "oauth_secret";
    private static final String KEY_USER_NAME = "user_name";
    private static final String PREFERENCES_NAME = "pref";

    public static void init(Context context){
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void setOAuthToken(String token){
        editor.putString(KEY_OAUTH_TOKEN, token);
        editor.commit();
    }

    public static String getOAuthToken(){
        return sharedPreferences.getString(KEY_OAUTH_TOKEN, null);
    }

    public static void removeOAuthToken() {
        editor.remove(KEY_OAUTH_TOKEN);
        editor.commit();
    }

    public static void setOAuthSecret(String secret){
        editor.putString(KEY_OAUTH_SECRET, secret);
        editor.commit();
    }

    public static String getOAuthSecret(){
        return sharedPreferences.getString(KEY_OAUTH_SECRET, null);
    }

    public static void removeOAuthSecret() {
        editor.remove(KEY_OAUTH_SECRET);
        editor.commit();
    }

    public static void setUserName(String userName){
        editor.putString(KEY_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(){
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public static void removeUserName() {
        editor.remove(KEY_USER_NAME);
        editor.commit();
    }
}
