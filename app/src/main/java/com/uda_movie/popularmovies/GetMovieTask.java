package com.uda_movie.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import com.uda_movie.popularmovies.model.Movie;
import com.uda_movie.popularmovies.model.SortMethod;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by deedee on 7/8/17.
 */

public class GetMovieTask extends AsyncTask<SortMethod, Void, Movie[]> {
    /**
     * For logging purposes
     */
    private final String LOG_TAG = GetMovieTask.class.getSimpleName();

    /**
     * TMDb API key
     */
    private final String apiKey;

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
    protected Movie[] doInBackground(SortMethod... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Holds data returned from the API
        String moviesJsonStr = null;

        try {
            URL url = Utils.getApiUrl(params[0], apiKey);

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
                // Adds '\n' at last line if not already there.
                // This supposedly makes it easier to debug.
                builder.append(line).append("\n");
            }

            if (builder.length() == 0) {
                // No data found. Nothing more to do here.
                return null;
            }

            moviesJsonStr = builder.toString();
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
            return Utils.getMoviesDataFromJson(moviesJsonStr);
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
