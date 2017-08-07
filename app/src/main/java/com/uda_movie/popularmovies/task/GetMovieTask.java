package com.uda_movie.popularmovies.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.uda_movie.popularmovies.Utils;
import com.uda_movie.popularmovies.model.Movie;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetMovieTask extends AsyncTask<String, Void, Movie[]> {
    /**
     * For logging purposes
     */
    private final String LOG_TAG = GetMovieTask.class.getSimpleName();

    /**
     * TMDb API key
     */
    private final String apiKey;

    public static final String GET_POPULAR = "Get Popular";
    public static final String GET_TOP_RATE = "Get Top Rate";
    public static final String GET_VIDEO = "Get Video";
    public static final String GET_REVIEW = "Get Review";
    private final String TMDB_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular?";
    private final String TMDB_TOP_URL = "https://api.themoviedb.org/3/movie/top_rated?";
    private final String TMDB_VIDEO = "https://api.themoviedb.org/3/movie/%s/videos?";
    private final String TMDB_REVIEW = "https://api.themoviedb.org/3/movie/%s/reviews?";
    private final String API_KEY_PARAM = "api_key";

    /**
     * Interface / listener
     */
    private final OnTaskCompleted taskCompletedListener;

    public interface OnTaskCompleted{
        void onGetMoviesTaskCompleted(Movie[] movies);
    }

    /**
     * Constructor
     *
     * @param listener UI listener
     * @param apiKey TMDb API key
     */
    public GetMovieTask(final OnTaskCompleted listener, String apiKey) {
        super();
        taskCompletedListener = listener;
        this.apiKey = apiKey;
    }

    @Override
    protected Movie[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Holds data returned from the API
        String moviesJsonStr = null;
        String endpoint_url = null;
        try {
            switch (params[0]){
                case GetMovieTask.GET_POPULAR:
                    endpoint_url = TMDB_POPULAR_URL;
                    break;
                case GetMovieTask.GET_TOP_RATE:
                    endpoint_url = TMDB_TOP_URL;
                    break;
                case GetMovieTask.GET_VIDEO:
                    endpoint_url = String.format(TMDB_VIDEO, params[1]);
                    break;
                case GetMovieTask.GET_REVIEW:
                    endpoint_url = String.format(TMDB_REVIEW, params[1]);
                    break;
                default:
                    throw new UnsupportedOperationException("Wrong task");
            }
            URL url = new URL(Uri.parse(endpoint_url).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .build().toString());

            // Start connecting to get JSON
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                return null;
            }

            moviesJsonStr = builder.toString();
            Log.i(LOG_TAG, "response: " + moviesJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            // Tidy up: release url connection and buffered reader
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            // Make sense of the JSON data
            Movie movie = new Movie();
            switch (params[0]){
                case GetMovieTask.GET_VIDEO:
                    movie.setVideos(Utils.getMovieVideosFromJson(moviesJsonStr));
                    return new Movie[]{movie};
                case GetMovieTask.GET_REVIEW:
                    movie.setReviews(Utils.getMovieReviewsFromJson(moviesJsonStr));
                    return new Movie[]{movie};
                default:
                    return Utils.getMoviesDataFromJson(moviesJsonStr);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie[] movies) {
        super.onPostExecute(movies);

        // Notify UI
        taskCompletedListener.onGetMoviesTaskCompleted(movies);
    }
}
