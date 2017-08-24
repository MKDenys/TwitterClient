package com.mkdenis.twitterclient;


public class TwitterMedia {
    private String type;
    private String mediaUrl;
    private int width;
    private int hight;

    public TwitterMedia(String type, String mediaUrl, int width, int hight) {
        this.type = type;
        this.mediaUrl = mediaUrl;
        this.width = width;
        this.hight = hight;
    }

    public String getUrl(){
        return this.mediaUrl;
    }
}
