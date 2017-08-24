package com.mkdenis.twitterclient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Tweet {
    private long id;
    private String createdTime;
    private String text;
    private int retweet_count;
    private int favorite_count;
    private boolean retweeted;
    private boolean favorited;
    private TwitterUser twitterUser;
    private List<TwitterMedia> twitterMedia;

    public Tweet(long id, String createdTime, String text, int retweet_count, int favorite_count,
                 boolean retweeted, boolean favorited, TwitterUser twitterUser, List<TwitterMedia> twitterMedia){
        this.id = id;
        this.createdTime = createdTime;
        this.text = text;
        this.retweet_count = retweet_count;
        this.favorite_count = favorite_count;
        this.retweeted = retweeted;
        this.favorited = favorited;
        this.twitterUser = twitterUser;
        this.twitterMedia = twitterMedia;
    }

    public TwitterUser getUser(){
        return this.twitterUser;
    }

    public String getCreatedTime(){
        DateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzzz yyyy");
        Date datetime = null;
        String timeStr = "";
        try {
            datetime = dateFormat.parse(this.createdTime);
            dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yy");
            timeStr = dateFormat.format(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStr;
    }

    public String getText(){
        return this.text;
    }

    public int getRetweetCount(){
        return this.retweet_count;
    }

    public int getFavoriteCount(){
        return this.favorite_count;
    }

    public TwitterMedia getMedia(){
        if (twitterMedia.size() != 0)
            return this.twitterMedia.get(0);
        else
            return null;
    }

    public long getId(){
        return this.id;
    }
}
