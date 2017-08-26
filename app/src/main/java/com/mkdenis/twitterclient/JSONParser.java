package com.mkdenis.twitterclient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    public static String parseAccessToken(String jsonStr){
        JSONObject jsonObject;
        String token;
        try {
            jsonObject = new JSONObject(jsonStr);
            token = jsonObject.getString("access_token");
        } catch (JSONException e) {
            token = "";
        }
        return token;
    }

    public static List<Tweet> parseTimeline(String jsonStr){
        List<Tweet> tweets = new ArrayList<Tweet>();
        List<TwitterMedia> media = new ArrayList<TwitterMedia>();
        try {
            JSONArray tweetsArray = new JSONArray(jsonStr);
            for (int i = 0; i < tweetsArray.length(); i++) {
                media = new ArrayList<TwitterMedia>();
                JSONObject tweetJSONObject = tweetsArray.getJSONObject(i);
                String reply = tweetJSONObject.getString("in_reply_to_status_id");
                if (reply != "null")
                    continue;
                try {
                    JSONObject entities = tweetJSONObject.getJSONObject("extended_entities");
                    JSONArray mediaArray = entities.getJSONArray("media");
                    for (int j = 0; j < mediaArray.length(); j++) {
                        String mediaType = mediaArray.getJSONObject(j).getString("type");
                        String mediaUrl = mediaArray.getJSONObject(j).getString("media_url_https");
                        JSONObject size = mediaArray.getJSONObject(j).getJSONObject("sizes");
                        JSONObject medium = size.getJSONObject("medium");
                        int width = medium.getInt("w");
                        int hight = medium.getInt("h");
                        media.add(j, new TwitterMedia(mediaType, mediaUrl, width, hight));
                    }
                }
                catch (Exception e) {}
                JSONObject userJSONObject = tweetJSONObject.getJSONObject("user");
                long idUser = userJSONObject.getLong("id");
                String name = userJSONObject.getString("name");
                String screenName = userJSONObject.getString("screen_name");
                String profileImageUrl = userJSONObject.getString("profile_image_url_https");
                TwitterUser user = new TwitterUser(idUser, name, screenName, profileImageUrl);
                long idTweet = tweetJSONObject.getLong("id");
                String createdTime = tweetJSONObject.getString("created_at");
                String text = tweetJSONObject.getString("text");
                int retweetCount = tweetJSONObject.getInt("retweet_count");
                int favoriteCount = tweetJSONObject.getInt("favorite_count");
                boolean retweeted = tweetJSONObject.getBoolean("retweeted");
                boolean favorited = tweetJSONObject.getBoolean("favorited");
                tweets.add(tweets.size(), new Tweet(idTweet, createdTime, text, retweetCount, favoriteCount,
                        retweeted, favorited, user, media));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tweets;
    }

    public static TwitterUser parseTwitterUser(String jsonStr){
        TwitterUser user = null;
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObj = jsonArray.getJSONObject(0);
            long id = jsonObj.getLong("id");
            String name = jsonObj.getString("name");
            String screenName = jsonObj.getString("screen_name");
            String profileImageUrl = jsonObj.getString("profile_image_url_https");
            user = new TwitterUser(id, name, screenName, profileImageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
