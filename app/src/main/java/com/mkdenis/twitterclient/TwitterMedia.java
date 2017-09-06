package com.mkdenis.twitterclient;


import java.io.Serializable;
import java.util.List;

public class TwitterMedia implements Serializable {
    private final String type;
    private final String mediaUrl;
    private final int width;
    private final int height;
    private final List<TweetUrl> urls;


    public TwitterMedia(String type, String mediaUrl, int width, int height, List<TweetUrl> urls) {
        this.type = type;
        this.mediaUrl = mediaUrl;
        this.width = width;
        this.height = height;
        this.urls = urls;
    }

    public String getUrl(){
        return this.mediaUrl;
    }

    public String getType(){
        return this.type;
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public List<TweetUrl> getUrls() {
        return urls;
    }
}
