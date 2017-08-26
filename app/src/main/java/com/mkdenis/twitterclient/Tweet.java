package com.mkdenis.twitterclient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Tweet {
    private final long id;
    private final Date createdTime;
    private final String text;
    private final int retweet_count;
    private final int favorite_count;
    private final boolean retweeted;
    private final boolean favorited;
    private final TwitterUser twitterUser;
    private final List<TwitterMedia> twitterMedia;

    public Tweet(long id, String createdTime, String text, int retweet_count, int favorite_count,
                 boolean retweeted, boolean favorited, TwitterUser twitterUser, List<TwitterMedia> twitterMedia){
        this.id = id;
        this.text = text;
        this.retweet_count = retweet_count;
        this.favorite_count = favorite_count;
        this.retweeted = retweeted;
        this.favorited = favorited;
        this.twitterUser = twitterUser;
        this.twitterMedia = twitterMedia;
        this.createdTime = dateFromTwitterFormatString(createdTime);
    }

    private Date dateFromTwitterFormatString(String dateStr){
        DateFormat dateFormat = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss zzzz yyyy");
        Date datetime = null;
        try {
            datetime = dateFormat.parse(dateStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return datetime;
    }

    public TwitterUser getUser(){
        return this.twitterUser;
    }

    public Date getCreatedTime(){
        return this.createdTime;
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

    public List<TwitterMedia> getMedia(){
            return this.twitterMedia;
    }

    public long getId(){
        return this.id;
    }
}
