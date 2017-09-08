package com.mkdenis.twitterclient;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Tweet implements Parcelable{
    private final long id;
    private final Date createdTime;
    private final String text;
    private final int retweetCount;
    private int favoriteCount;
    private final boolean retweeted;
    private boolean favorited;
    private final TwitterUser twitterUser;
    private final List<TwitterMedia> twitterMedia;
    private final List<TweetUrl> tweetUrls;
    private final static String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzzz yyyy";
    private static final String HTML_LINK = "<a href='{0}'>{1}</a>";
    private static final String HTML_NEW_LINE = "<br>";


    public Tweet(long id, String createdTime, String text, int retweetCount, int favoriteCount,
                 boolean retweeted, boolean favorited, TwitterUser twitterUser, List<TwitterMedia> twitterMedia,
                 List<TweetUrl> tweetUrls) throws ParseException {
        this.id = id;
        this.retweetCount = retweetCount;
        this.favoriteCount = favoriteCount;
        this.retweeted = retweeted;
        this.favorited = favorited;
        this.twitterUser = twitterUser;
        this.twitterMedia = twitterMedia;
        this.createdTime = dateFromTwitterFormatString(createdTime);
        this.tweetUrls = tweetUrls;
        this.text = transformToDisplaedText(text);
    }

    protected Tweet(Parcel in) {
        this.id = in.readLong();
        this.createdTime = new Date(in.readLong());
        this.text = in.readString();
        this.retweetCount = in.readInt();
        this.favoriteCount = in.readInt();
        this.retweeted = in.readByte() != 0;
        this.favorited = in.readByte() != 0;
        this.twitterUser = in.readParcelable(TwitterUser.class.getClassLoader());
        this.twitterMedia = in.createTypedArrayList(TwitterMedia.CREATOR);
        this.tweetUrls = in.createTypedArrayList(TweetUrl.CREATOR);
    }

    private String transformToDisplaedText(String text){
        String newText = text;
        newText = removeImageUrl(newText);
        newText = replaceTextLinkToHTMLLink(newText);
        newText = setHtmlNewLine(newText);
        return newText;
    }

    private String removeImageUrl(String text){
        String newText = text;
        for (int i = 0; i < this.twitterMedia.size(); i++) {
            TwitterMedia media =  this.twitterMedia.get(i);
            List<TweetUrl> mediaUrls = media.getUrls();
            TweetUrl url = mediaUrls.get(0);
            newText = newText.replaceAll(url.getUrl(), "");
        }
        return newText;
    }

    private String replaceTextLinkToHTMLLink(String text){
        String newText = text;
        List<TweetUrl> tweetUrl = this.getTweetUrls();
        for (int i = 0; i < tweetUrl.size(); i++) {
            TweetUrl url =  tweetUrl.get(i);
            newText = newText.replaceAll(url.getUrl(), HTML_NEW_LINE +
                    MessageFormat.format(HTML_LINK, url.getExpandedUrl(), url.getDisplayUrl()));
        }
        return newText;
    }

    private String setHtmlNewLine(String text){
        return text.replaceAll("\n", HTML_NEW_LINE);
    }

    private Date dateFromTwitterFormatString(String dateString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat(TWITTER_DATE_FORMAT, Locale.getDefault());
        Date datetime = dateFormat.parse(dateString);
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
        return this.retweetCount;
    }

    public int getFavoriteCount(){
        return this.favoriteCount;
    }

    public void setFavoriteCount(int value){
        this.favoriteCount = value;
    }

    public List<TwitterMedia> getMedia(){
            return this.twitterMedia;
    }

    public long getId(){
        return this.id;
    }

    public boolean getRetweeted(){
        return this.retweeted;
    }

    public boolean getFavorited(){
        return this.favorited;
    }

    public void setFavorited(boolean value){
        this.favorited = value;
    }

    public List<TweetUrl> getTweetUrls() {
        return tweetUrls;
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.createdTime.getTime());
        dest.writeString(this.text);
        dest.writeInt(this.retweetCount);
        dest.writeInt(this.favoriteCount);
        dest.writeByte((byte) (this.retweeted ? 1 : 0));
        dest.writeByte((byte) (this.favorited ? 1 : 0));
        dest.writeParcelable(this.twitterUser, flags);
        dest.writeTypedList(this.twitterMedia);
        dest.writeTypedList(this.tweetUrls);
    }
}
