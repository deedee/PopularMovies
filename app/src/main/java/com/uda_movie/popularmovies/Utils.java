package com.uda_movie.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.uda_movie.popularmovies.model.Movie;
import com.uda_movie.popularmovies.model.SortMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Helper class
 */
public class Utils {

    public static final String TMDB_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?";
    public static final String TMDB_TOP_URL = "https://api.themoviedb.org/3/movie/top_rated?";


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

    /**
     * Build api url
     *
     * @param order
     * @param apiKey
     * @return
     * @throws MalformedURLException
     */
    public static URL getApiUrl(SortMethod order, String apiKey) throws MalformedURLException {
        final String API_KEY_PARAM = "api_key";
        String endpoint_url = "";
        if (order == SortMethod.POPULAR) {
            endpoint_url = TMDB_POPULAR_URL;
        } else {
            endpoint_url = TMDB_TOP_URL;
        }

        Uri builtUri = Uri.parse(endpoint_url).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        return new URL(builtUri.toString());
    }

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
        }

        return movies;
    }

    /**
     * Check availabale network
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
}