package com.mkdenis.twitterclient;


public final class TwitterAPIURL {
    private static final String BASE = "https://api.twitter.com/1.1/";

    public static final String HOME_TIMELINE = BASE + "statuses/home_timeline.json";
    public static final String USER_TIMELINE = BASE + "statuses/user_timeline.json?screen_name=";
    public static final String PROFILE = BASE + "users/lookup.json?screen_name=";
    public static final String APPLICATION_ONLY_OAUTH = "https://api.twitter.com/oauth2/token";
}
