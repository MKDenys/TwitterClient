package com.mkdenis.twitterclient;


import java.io.Serializable;

public class TweetUrl implements Serializable{
    private final String url;
    private final String expandedUrl;
    private final String displayUrl;

    public TweetUrl(String url, String expandedUrl, String displayUrl) {
        this.url = url;
        this.expandedUrl = expandedUrl;
        this.displayUrl = displayUrl;
    }

    public String getUrl(){
        return this.url;
    }

    public String getExpandedUrl() {
        return this.expandedUrl;
    }

    public String getDisplayUrl() {
        return this.displayUrl;
    }


}
