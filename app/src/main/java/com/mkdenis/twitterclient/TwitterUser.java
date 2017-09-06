package com.mkdenis.twitterclient;


import java.io.Serializable;

public class TwitterUser implements Serializable {
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
        return this.name;
    }

    public String getScreenName(){
        return this.screenName;
    }

    public String getProfileImageUrl(){
        return this.profileImageUrl;
    }

    public long getId(){
        return this.id;
    }
}
