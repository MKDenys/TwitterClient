package com.mkdenis.twitterclient;


public class TwitterMedia {
    private final String type;
    private final String mediaUrl;
    private final int width;
    private final int hight;

    public TwitterMedia(String type, String mediaUrl, int width, int hight) {
        this.type = type;
        this.mediaUrl = mediaUrl;
        this.width = width;
        this.hight = hight;
    }

    public String getUrl(){
        return mediaUrl;
    }

    public String getType(){
        return type;
    }

    public int getWidth(){
        return width;
    }

    public int getHight(){
        return hight;
    }
}
