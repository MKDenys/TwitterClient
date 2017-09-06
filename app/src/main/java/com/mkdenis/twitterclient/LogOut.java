package com.mkdenis.twitterclient;


public class LogOut {

    public void logOutUser(){
        PreferenceSettingsManager.removeOAuthToken();
        PreferenceSettingsManager.removeOAuthSecret();
        PreferenceSettingsManager.removeUserName();
    }
}
