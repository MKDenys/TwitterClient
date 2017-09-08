package com.mkdenis.twitterclient;


import android.os.Parcel;
import android.os.Parcelable;

public class TweetUrl implements Parcelable{
    private final String url;
    private final String expandedUrl;
    private final String displayUrl;

    public TweetUrl(String url, String expandedUrl, String displayUrl) {
        this.url = url;
        this.expandedUrl = expandedUrl;
        this.displayUrl = displayUrl;
    }

    public String getUrl(){
        return this.url;
    }

    public String getExpandedUrl() {
        return this.expandedUrl;
    }

    public String getDisplayUrl() {
        return this.displayUrl;
    }

    protected TweetUrl(Parcel in) {
        this.url = in.readString();
        this.expandedUrl = in.readString();
        this.displayUrl = in.readString();
    }

    public static final Creator<TweetUrl> CREATOR = new Creator<TweetUrl>() {
        @Override
        public TweetUrl createFromParcel(Parcel in) {
            return new TweetUrl(in);
        }

        @Override
        public TweetUrl[] newArray(int size) {
            return new TweetUrl[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.expandedUrl);
        dest.writeString(this.displayUrl);
    }
}
