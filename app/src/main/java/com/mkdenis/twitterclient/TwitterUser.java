package com.mkdenis.twitterclient;


public class TwitterUser {
    private final long id;
    private final String name;
    private final String screenName;
    private final String profileImageUrl;

    public TwitterUser (long id, String name, String screenName, String profileImageUrl){
        this.id = id;
        this.name = name;
        this.screenName = screenName;
        this.profileImageUrl = profileImageUrl;
    }

    public String getName(){
        return name;
    }

    public String getScreenName(){
        return screenName;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }

    public long getId(){
        return id;
    }
}
