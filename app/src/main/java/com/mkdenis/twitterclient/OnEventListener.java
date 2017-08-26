package com.mkdenis.twitterclient;

public interface OnEventListener {
    void onSuccess(Object object, int eventType);
    void onFailure(Exception e);
    int LOAD_TWEETS = 0;
    int LOAD_NEW_TWEETS = 1;
    int LOAD_PREVIOUS_TWEETS = 2;
    int LOAD_USER_PROFILE_IMAGE = 3;
    int LOAD_ACCESS_TOKEN = 4;
}
