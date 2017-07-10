package com.uda_movie.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private String originalTitle;
    private String posterPath;
    private String overview;
    private Double voteAverage;
    private String releaseDate;

    final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342";

    public Movie() {
    }


    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }


    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }


    public void setOverview(String overview) {
        if (!overview.equals("null")) {
            this.overview = overview;
        }
    }


    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }


    public void setReleaseDate(String releaseDate) {
        if (!releaseDate.equals("null")) {
            this.releaseDate = releaseDate;
        }
    }


    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return TMDB_POSTER_BASE_URL + posterPath;
    }


    public String getOverview() {
        return overview;
    }


    private Double getVoteAverage() {
        return voteAverage;
    }


    public String getReleaseDate() {
        return releaseDate;
    }


    public String getDetailedVoteAverage() {
        return "rating: " + String.valueOf(getVoteAverage()) + "/10";
    }


    public String getDateFormat() {
        return DATE_FORMAT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeValue(voteAverage);
        dest.writeString(releaseDate);
    }

    private Movie(Parcel in) {
        originalTitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = (Double) in.readValue(Double.class.getClassLoader());
        releaseDate = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
