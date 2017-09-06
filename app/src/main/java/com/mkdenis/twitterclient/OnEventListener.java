package com.mkdenis.twitterclient;

public interface OnEventListener {
    void onSuccess(Object object);
    void onFailure(Exception e);
}
