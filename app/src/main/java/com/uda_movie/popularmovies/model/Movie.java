package com.uda_movie.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class Movie implements Parcelable {
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private Long id;
    private String originalTitle;
    private String posterPath;
    private String overview;
    private Double voteAverage;
    private String releaseDate;
    private List<MovieVideo> videos;
    private List<MovieReview> reviews;

    final String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getPosterUrl() {
        return TMDB_POSTER_BASE_URL + posterPath;
    }


    public String getOverview() {
        return overview;
    }


    public Double getVoteAverage() {
        return voteAverage;
    }


    public String getReleaseDate() {
        return releaseDate;
    }

    public List<MovieVideo> getVideos() {
        return videos;
    }

    public void setVideos(List<MovieVideo> videos) {
        this.videos = videos;
    }

    public List<MovieReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<MovieReview> reviews) {
        this.reviews = reviews;
    }

    public String getDetailedVoteAverage() {
        return String.valueOf(getVoteAverage()) + "/10";
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
        dest.writeLong(id);
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeValue(voteAverage);
        dest.writeString(releaseDate);
        dest.writeList(videos);
        dest.writeList(reviews);
    }

    private Movie(Parcel in) {
        id = in.readLong();
        originalTitle = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        voteAverage = (Double) in.readValue(Double.class.getClassLoader());
        releaseDate = in.readString();
        videos = (List<MovieVideo>) in.readArrayList(ArrayList.class.getClassLoader());
        reviews = (List<MovieReview>) in.readArrayList(ArrayList.class.getClassLoader());
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
