package com.uda_movie.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.uda_movie.popularmovies.model.Movie;
import com.uda_movie.popularmovies.model.MovieReview;
import com.uda_movie.popularmovies.model.MovieVideo;
import com.uda_movie.popularmovies.model.SortMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Helper class
 */
public class Utils {

    /**
     * Get formateted date
     *
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    private static Date getFormattedDate(String date, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        return simpleDateFormat.parse(date);
    }

    /**
     * Get Year for string
     *
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static String getYear(String date, String format) throws ParseException {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getFormattedDate(date, format));

        return String.valueOf(cal.get(Calendar.YEAR));
    }

//    /**
//     * Build api url
//     *
//     * @param order
//     * @param apiKey
//     * @return
//     * @throws MalformedURLException
//     */
//    public static URL getApiUrl(SortMethod order, String apiKey) throws MalformedURLException {
//        final String API_KEY_PARAM = "api_key";
//        String endpoint_url = "";
//        if (order == SortMethod.POPULAR) {
//            endpoint_url = TMDB_POPULAR_URL;
//        } else {
//            endpoint_url = TMDB_TOP_URL;
//        }
//
//        Uri builtUri = Uri.parse(endpoint_url).buildUpon()
//                .appendQueryParameter(API_KEY_PARAM, apiKey)
//                .build();
//
//        return new URL(builtUri.toString());
//    }

    /**
     * Parse Movie from Json string
     *
     * @param moviesJsonStr
     * @return
     * @throws JSONException
     */
    public static Movie[] getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
        // JSON tags
        final String TAG_RESULTS = "results";
        final String TAG_ORIGINAL_TITLE = "original_title";
        final String TAG_POSTER_PATH = "poster_path";
        final String TAG_OVERVIEW = "overview";
        final String TAG_VOTE_AVERAGE = "vote_average";
        final String TAG_RELEASE_DATE = "release_date";
        final String TAG_ID = "id";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);
        Movie[] movies = new Movie[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            movies[i] = new Movie();

            JSONObject movieInfo = resultsArray.getJSONObject(i);

            movies[i].setOriginalTitle(movieInfo.getString(TAG_ORIGINAL_TITLE));
            movies[i].setPosterPath(movieInfo.getString(TAG_POSTER_PATH));
            movies[i].setOverview(movieInfo.getString(TAG_OVERVIEW));
            movies[i].setVoteAverage(movieInfo.getDouble(TAG_VOTE_AVERAGE));
            movies[i].setReleaseDate(movieInfo.getString(TAG_RELEASE_DATE));
            movies[i].setId(movieInfo.getLong(TAG_ID));
        }

        return movies;
    }

    public static List<MovieVideo> getMovieVideosFromJson(String videsJsonStr) throws JSONException{
        final String TAG_RESULTS = "results";
        final String TAG_SITE = "site";
        final String TAG_KEY = "key";
        final String TAG_NAME = "name";
        JSONObject resultJson = new JSONObject(videsJsonStr);
        JSONArray resultArray = resultJson.getJSONArray(TAG_RESULTS);
        List<MovieVideo> videos = new ArrayList<MovieVideo>();
        for (int i = 0; i < resultArray.length(); i++){
            MovieVideo video = new MovieVideo();
            JSONObject videoJson = resultArray.getJSONObject(i);
            video.setKey(videoJson.getString(TAG_KEY));
            video.setName(videoJson.getString(TAG_NAME));
            video.setSite(videoJson.getString(TAG_SITE));
            videos.add(video);
        }
        return videos;
    }

    public static List<MovieReview> getMovieReviewsFromJson(String reviewsJsonStr) throws JSONException{
        final String TAG_RESULTS = "results";
        final String TAG_AUTHOR = "author";
        final String TAG_CONTENT = "content";
        JSONObject resultJson = new JSONObject(reviewsJsonStr);
        JSONArray resultArray = resultJson.getJSONArray(TAG_RESULTS);
        List<MovieReview> reviews = new ArrayList<MovieReview>();
        for (int i = 0; i < resultArray.length(); i++){
            MovieReview review = new MovieReview();
            JSONObject reviewJson = resultArray.getJSONObject(i);
            review.setAuthor(reviewJson.getString(TAG_AUTHOR));
            review.setContent(reviewJson.getString(TAG_CONTENT));
            reviews.add(review);
        }
        return reviews;
    }
    /**
     * Check available network
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Get sort method from preference
     *
     * @param context
     * @return
     */
    public static SortMethod getSortMethod(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        return SortMethod.valueOf(pref.getString(context.getString(R.string.sort_by), context.getString(R.string.default_sort)));
    }

    public static void updatePreference(Context c, SortMethod sortMethod) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(c.getString(R.string.sort_by), sortMethod.toString());
        editor.apply();
    }
}