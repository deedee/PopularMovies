package com.uda_movie.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;


public class MovieVideo implements Parcelable {
    private String site;
    private String key;
    private String name;

    public MovieVideo(){

    }

    protected MovieVideo(Parcel in) {
        site = in.readString();
        key = in.readString();
        name = in.readString();
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site.toLowerCase();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(site);
        dest.writeString(key);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieVideo> CREATOR = new Parcelable.Creator<MovieVideo>() {
        @Override
        public MovieVideo createFromParcel(Parcel in) {
            return new MovieVideo(in);
        }

        @Override
        public MovieVideo[] newArray(int size) {
            return new MovieVideo[size];
        }
    };
}