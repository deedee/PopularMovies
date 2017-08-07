package com.uda_movie.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.uda_movie.popularmovies.model.FavoriteContract;
import com.uda_movie.popularmovies.model.Movie;
import com.uda_movie.popularmovies.model.SortMethod;
import com.uda_movie.popularmovies.task.GetMovieTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SettingDialogFragment.SettingDialogtListener{

    private GridView gridView;
    private Context context;
    private SortMethod sortMethod;
    private List<Movie> movies;


    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sortMethod = Utils.getSortMethod(context);
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setOnItemClickListener(new GridView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Movie movie = (Movie) adapterView.getItemAtPosition(pos);

                Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                intent.putExtra(getResources().getString(R.string.movie_parcerable), movie);

                startActivity(intent);
            }
        });

        if (savedInstanceState == null || Utils.getSortMethod(context) == SortMethod.FAVORITE) {
            Log.i(LOG_TAG, "OnCreate saveInstance null get data");
            getMoviesFromTMDb(Utils.getSortMethod(context));
        } else {
            Log.i(LOG_TAG, "OnCreate we have instance");
            List<Movie> parcelable = savedInstanceState.
                    getParcelableArrayList(getString(R.string.movie_parcerable));
            Log.i(LOG_TAG, "OnCreate parcel: " + parcelable);
            if (parcelable != null) {
                movies = parcelable;
                gridView.setAdapter(new MovieArrayAdapter(this, parcelable));
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getString(R.string.movie_parcerable),
                (ArrayList<Movie>) movies);
        Log.i(LOG_TAG, "onSaveInstanceState parcel: " + movies);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        List<Movie> parcelable = savedInstanceState.
                getParcelableArrayList(getString(R.string.movie_parcerable));
        Log.i(LOG_TAG, "onRestoreInstanceState parcel: " + parcelable);
        if (parcelable != null) {
            movies = parcelable;
            gridView.setAdapter(new MovieArrayAdapter(this, parcelable));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(LOG_TAG, "menu selected" + item.getTitle());
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            Log.i(LOG_TAG, "action sort");
            SettingDialogFragment settingDialog = new SettingDialogFragment();
            settingDialog.show(getFragmentManager(),"SettingDialog");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Fetch data executor
     *
     * @param sortMethod
     */
    private void getMoviesFromTMDb(SortMethod sortMethod) {
        if (sortMethod == SortMethod.FAVORITE) {
            if (!loadFavoriteFromDb()){
                Toast.makeText(this, getString(R.string.no_favorite), Toast.LENGTH_SHORT).show();
                sortMethod = SortMethod.POPULAR;
                Utils.updatePreference(context, sortMethod);
            } else {
                return;
            }
        }
        if (Utils.isNetworkAvailable(this)) {
            // Key needed to get data from TMDb
            String apiKey = getString(R.string.api_key);

            // Listener for when AsyncTask is ready to update UI
            GetMovieTask.OnTaskCompleted taskCompletedListener = new GetMovieTask.OnTaskCompleted() {
                @Override
                public void onGetMoviesTaskCompleted(Movie[] moviesRes) {
                    Log.i(LOG_TAG, "getMoviesFromTMDb task complete");
                    if (moviesRes != null) {
                        Log.i(LOG_TAG, "getMoviesFromTMDb got result");
                        movies = new ArrayList<Movie>(Arrays.asList(moviesRes));
                        gridView.setAdapter(new MovieArrayAdapter(getApplicationContext(), movies));
                    }
                }
            };

            // Execute task
            GetMovieTask movieTask = new GetMovieTask(taskCompletedListener, apiKey);
            Log.i(LOG_TAG, "getMoviesFromTMDb execute");
            String tmdbTask;
            if (sortMethod == SortMethod.POPULAR)
                tmdbTask = GetMovieTask.GET_POPULAR;
            else{
                tmdbTask = GetMovieTask.GET_TOP_RATE;
            }
            movieTask.execute(tmdbTask);
        } else {
            Toast.makeText(this, getString(R.string.error_internet_required), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSortMethodChange(SortMethod newSortMethod) {
        Log.i(LOG_TAG, sortMethod.toString());
        if (newSortMethod == SortMethod.FAVORITE){
            //load from db
            if (!loadFavoriteFromDb()){
                Toast.makeText(context,R.string.no_favorite,Toast.LENGTH_LONG).show();
            }
        } else {
            getMoviesFromTMDb(newSortMethod);
        }

        Toast.makeText(context,R.string.text_loading,Toast.LENGTH_LONG).show();
    }

    /**
     * load favorite from db
     *
     * @return
     */
    private boolean loadFavoriteFromDb(){
        Cursor result = getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI,
                null, null, null, FavoriteContract.FavoriteEntry.COLUMN_DATE + " DESC");
        if (result.getCount() < 1){
            return false;
        }
        movies = new ArrayList<Movie>();
        for(int i = 0; i < result.getCount(); i++) {
            result.moveToPosition(i);
            Movie movie = new Movie();
            movie.setId(result.getLong(result.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_ID)));
            movie.setOriginalTitle(result.getString(result.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE)));
            movie.setPosterPath(result.getString(result.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH)));
            movie.setReleaseDate(result.getString(result.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_DATE)));
            movie.setOverview(result.getString(result.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW)));
            movie.setVoteAverage(result.getDouble(result.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_VOTE)));
            movies.add(movie);
        }
        gridView.setAdapter(new MovieArrayAdapter(this, movies));
        return true;
    }
}
