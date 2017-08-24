package com.mkdenis.twitterclient;


public class TwitterUser {
    private long id;
    private String name;
    private String screenName;
    private String profileImageUrl;

    public TwitterUser (long id, String name, String screenName, String profileImageUrl){
        this.id = id;
        this.name = name;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName(){
        return this.name;
    }

    public String getScreenName(){
        return this.screenName;
    }

    public String getProfileImageUrl(){
        return this.profileImageUrl;
    }
}
