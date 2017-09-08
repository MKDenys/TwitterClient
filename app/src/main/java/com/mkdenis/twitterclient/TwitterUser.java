package com.mkdenis.twitterclient;


import android.os.Parcel;
import android.os.Parcelable;

public class TwitterUser implements Parcelable {
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

    protected TwitterUser(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
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

    public static final Creator<TwitterUser> CREATOR = new Creator<TwitterUser>() {
        @Override
        public TwitterUser createFromParcel(Parcel in) {
            return new TwitterUser(in);
        }

        @Override
        public TwitterUser[] newArray(int size) {
            return new TwitterUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(screenName);
        dest.writeString(profileImageUrl);
    }
}
