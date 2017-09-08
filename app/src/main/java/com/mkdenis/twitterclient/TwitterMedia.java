package com.mkdenis.twitterclient;


import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class TwitterMedia implements Parcelable {
    private final String type;
    private final String mediaUrl;
    private final int width;
    private final int height;
    private final List<TweetUrl> urls;


    public TwitterMedia(String type, String mediaUrl, int width, int height, List<TweetUrl> urls) {
        this.type = type;
        this.mediaUrl = mediaUrl;
        this.width = width;
        this.height = height;
        this.urls = urls;
    }

    public String getUrl(){
        return this.mediaUrl;
    }

    public String getType(){
        return this.type;
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public List<TweetUrl> getUrls() {
        return urls;
    }

    protected TwitterMedia(Parcel in) {
        this.type = in.readString();
        this.mediaUrl = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.urls = in.createTypedArrayList(TweetUrl.CREATOR);
    }

    public static final Creator<TwitterMedia> CREATOR = new Creator<TwitterMedia>() {
        @Override
        public TwitterMedia createFromParcel(Parcel in) {
            return new TwitterMedia(in);
        }

        @Override
        public TwitterMedia[] newArray(int size) {
            return new TwitterMedia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.mediaUrl);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeTypedList(this.urls);
    }
}
