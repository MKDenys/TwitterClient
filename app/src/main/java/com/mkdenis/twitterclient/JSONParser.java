package com.mkdenis.twitterclient;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    private static final String KEY_IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
    private static final String KEY_EXTENDED_ENTITIES = "extended_entities";
    private static final String KEY_ENTITIES = "entities";
    private static final String KEY_MEDIA = "media";
    private static final String KEY_TYPE = "type";
    private static final String KEY_MEDIA_URL_HTTPS = "media_url_https";
    private static final String KEY_SIZES = "sizes";
    private static final String KEY_MEDIUM = "medium";
    private static final String KEY_WIDTH = "w";
    private static final String KEY_HEIGHT = "h";
    private static final String KEY_USER = "user";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SCREEN_NAME = "screen_name";
    private static final String KEY_PROFILE_IMAGE_URL_HTTPS = "profile_image_url_https";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_TEXT = "text";
    private static final String KEY_RETWEET_COUNT = "retweet_count";
    private static final String KEY_FAVORITE_COUNT = "favorite_count";
    private static final String KEY_RETWEETED = "retweeted";
    private static final String KEY_FAVORITED = "favorited";
    private static final String KEY_URLS = "urls";
    private static final String KEY_URL = "url";
    private static final String KEY_EXPANDED_URL = "expanded_url";
    private static final String KEY_DISPLAY_URL = "display_url";
    private static final String NULL = "null";

    private List<TweetUrl> parseUrls(JSONArray jsonArray){
        List<TweetUrl> tweetUrlList = new ArrayList<TweetUrl>();
        try {
            for (int j = 0; j < jsonArray.length(); j++) {
                String url = jsonArray.getJSONObject(j).getString(KEY_URL);
                String expandedUrl = jsonArray.getJSONObject(j).getString(KEY_EXPANDED_URL);
                String displayUrl = jsonArray.getJSONObject(j).getString(KEY_DISPLAY_URL);
                tweetUrlList.add(new TweetUrl(url, expandedUrl, displayUrl));
            }
        }
        catch (Exception e){
            Log.d("!!!!!!!!!!!!!!!", "getUrls: " + e.toString());
        }
        return tweetUrlList;
    }

    public List<Tweet> parseTimeline(String jsonStr){
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TwitterMedia> mediaList;
        List<TweetUrl> tweetUrlList;
        List<TweetUrl> mediaUrlList;
        try {
            JSONArray tweetsArray = new JSONArray(jsonStr);
            for (int i = 0; i < tweetsArray.length(); i++) {
                mediaList = new ArrayList<TwitterMedia>();
                tweetUrlList = new ArrayList<TweetUrl>();
                JSONObject tweetJSONObject = tweetsArray.getJSONObject(i);
                String reply = tweetJSONObject.getString(KEY_IN_REPLY_TO_STATUS_ID);
                if (reply != NULL)
                    continue;
                try {
                    JSONObject entities = tweetJSONObject.getJSONObject(KEY_ENTITIES);
                    JSONArray urlArray = entities.getJSONArray(KEY_URLS);
                    tweetUrlList = parseUrls(urlArray);
                    JSONObject extendedEntities = tweetJSONObject.getJSONObject(KEY_EXTENDED_ENTITIES);
                    JSONArray mediaArray = extendedEntities.getJSONArray(KEY_MEDIA);
                    for (int j = 0; j < mediaArray.length(); j++) {
                        mediaUrlList = parseUrls(mediaArray);
                        String mediaType = mediaArray.getJSONObject(j).getString(KEY_TYPE);
                        String mediaUrl = mediaArray.getJSONObject(j).getString(KEY_MEDIA_URL_HTTPS);
                        JSONObject size = mediaArray.getJSONObject(j).getJSONObject(KEY_SIZES);
                        JSONObject medium = size.getJSONObject(KEY_MEDIUM);
                        int width = medium.getInt(KEY_WIDTH);
                        int height = medium.getInt(KEY_HEIGHT);
                        mediaList.add(new TwitterMedia(mediaType, mediaUrl, width, height, mediaUrlList));
                    }
                }
                catch (Exception e) {}
                JSONObject userJSONObject = tweetJSONObject.getJSONObject(KEY_USER);
                long idUser = userJSONObject.getLong(KEY_ID);
                String name = userJSONObject.getString(KEY_NAME);
                String screenName = userJSONObject.getString(KEY_SCREEN_NAME);
                String profileImageUrl = userJSONObject.getString(KEY_PROFILE_IMAGE_URL_HTTPS);
                TwitterUser user = new TwitterUser(idUser, name, screenName, profileImageUrl);
                long idTweet = tweetJSONObject.getLong(KEY_ID);
                String createdTime = tweetJSONObject.getString(KEY_CREATED_AT);
                String text = tweetJSONObject.getString(KEY_TEXT);
                int retweetCount = tweetJSONObject.getInt(KEY_RETWEET_COUNT);
                int favoriteCount = tweetJSONObject.getInt(KEY_FAVORITE_COUNT);
                boolean retweeted = tweetJSONObject.getBoolean(KEY_RETWEETED);
                boolean favorited = tweetJSONObject.getBoolean(KEY_FAVORITED);
                tweets.add(new Tweet(idTweet, createdTime, text, retweetCount, favoriteCount,
                        retweeted, favorited, user, mediaList, tweetUrlList));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tweets;
    }

    public TwitterUser parseTwitterUser(String jsonStr){
        TwitterUser user = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            long id = jsonObj.getLong(KEY_ID);
            String name = jsonObj.getString(KEY_NAME);
            String screenName = jsonObj.getString(KEY_SCREEN_NAME);
            String profileImageUrl = jsonObj.getString(KEY_PROFILE_IMAGE_URL_HTTPS);
            user = new TwitterUser(id, name, screenName, profileImageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
