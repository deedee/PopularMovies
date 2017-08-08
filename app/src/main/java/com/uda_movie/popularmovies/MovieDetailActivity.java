package com.uda_movie.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.uda_movie.popularmovies.model.FavoriteContract;
import com.uda_movie.popularmovies.model.Movie;

import com.uda_movie.popularmovies.model.MovieReview;
import com.uda_movie.popularmovies.model.MovieVideo;
import com.uda_movie.popularmovies.task.GetMovieTask;

import java.text.ParseException;

import butterknife.ButterKnife;
import butterknife.BindView;

public class MovieDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private final String MOVIE_KEY = "movie";
    private final String YOUTUBE = "youtube";
    private final String YOUTUBE_THUMB_URL = "https://img.youtube.com/vi/%s/sddefault.jpg";
    private final String YOUTUBE_WATCH_URL = "https://youtube.com/watch?v=%s";
    private final int RESULT_CODE = 1;

    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.md_title) TextView originalTitle;
    @BindView(R.id.md_poster) ImageView poster;
    @BindView(R.id.md_overview) TextView mOverView;
    @BindView(R.id.md_rate) TextView rate;
    @BindView(R.id.md_year) TextView year;
    @BindView(R.id.rating) RatingBar rating;
    @BindView(R.id.add_to_fav) Button btnFavorite;
    @BindView(R.id.trailers) LinearLayout trailerContainer;
    @BindView(R.id.reviews) LinearLayout reviewContainer;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);
        ButterKnife.bind(this);

        btnFavorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (isFavorite()){
                    removeFavorite();
                } else {
                    setAsFavorite();
                }
            }
        });
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();

            if (extras != null){
                if (extras.containsKey(getString(R.string.movie_parcerable))) {
                    movie = extras.getParcelable(getString(R.string.movie_parcerable));
                }
            }
        } else {
            Log.i(LOG_TAG, "OnCreate we have instance");
            Movie parcelable = savedInstanceState.getParcelable(getString(R.string.movie_parcerable));
            if (parcelable != null) {
                movie = parcelable;
            }
        }

        if (movie != null) {
            originalTitle.setText(movie.getOriginalTitle());

            //TODO
            //it seems .fit() somewhat slower
            Picasso.with(this)
                    .load(movie.getPosterUrl())
                    //.resize(getResources().getInteger(R.integer.image_width),
                    //       getResources().getInteger(R.integer.image_height))
                    .fit()
                    //.centerInside()
                    .centerCrop()
                    .error(R.drawable.clapper_185)
                    .placeholder(R.drawable.clapper_185)
                    .into(poster);

            String overView = movie.getOverview();
            if (overView == null) {
                overView = getResources().getString(R.string.text_no_summary_found);
            }
            mOverView.setText(overView);
            rate.setText(movie.getDetailedVoteAverage());
            rating.setRating(movie.getVoteAverage().floatValue()/10 * 5);
            String releaseDate = movie.getReleaseDate();
            if (releaseDate != null) {
                try {
                    //just need show the year
                    releaseDate = Utils.getYear(releaseDate, movie.getDateFormat());
                } catch (ParseException e) {
                    Log.e(LOG_TAG, "Error with parsing movie release date", e);
                }
            } else {
                releaseDate = getResources().getString(R.string.text_na);
            }
            year.setText(releaseDate);

            setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            collapsingToolbarLayout.setTitle(movie.getOriginalTitle());
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

            if (isFavorite()){
                btnFavorite.setBackgroundColor(getResources().getColor(R.color.btn_disable));
                btnFavorite.setText(R.string.added_to_fav);
            } else {
                btnFavorite.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                btnFavorite.setText(R.string.add_to_fav);
            }

            if (movie.getVideos() == null) {
                fetchVideos();
            } else {
                fillVideos();
            }
            if (movie.getReviews() == null) {
                fetchReviews();
            } else {
                fillReviews();
            }
        } else {
            finish();
        }
    }

    private boolean isFavorite(){
        Cursor result = getContentResolver().query(FavoriteContract.FavoriteEntry.CONTENT_URI, null, FavoriteContract.FavoriteEntry.COLUMN_ID + " = ?", new String[] {movie.getId().toString()}, null);
        return result.getCount() > 0;
    }
    private void setAsFavorite(){
        ContentValues values = new ContentValues();
        values.put(FavoriteContract.FavoriteEntry.COLUMN_ID, movie.getId());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, movie.getOriginalTitle());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_DATE, movie.getReleaseDate());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_OVERVIEW, movie.getOverview());
        values.put(FavoriteContract.FavoriteEntry.COLUMN_VOTE, movie.getVoteAverage());

        Uri uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, values);

        if(uri != null)
        {
            btnFavorite.setBackgroundColor(getResources().getColor(R.color.btn_disable));
            btnFavorite.setText(R.string.added_to_fav);
            Toast.makeText(this, R.string.added_to_fav, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.error_occur, Toast.LENGTH_SHORT).show();
        }
    }
    private void removeFavorite(){
        try {
            Uri uri = FavoriteContract.FavoriteEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movie.getId().toString()).build();

            getContentResolver().delete(uri, FavoriteContract.FavoriteEntry.COLUMN_ID + " = ?", new String[]{movie.getId().toString()});

            btnFavorite.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btnFavorite.setText(R.string.add_to_fav);
            Toast.makeText(this, R.string.face_removed, Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void fetchVideos(){
        if (Utils.isNetworkAvailable(this)) {
            String apiKey = getString(R.string.api_key);

            GetMovieTask.OnTaskCompleted taskCompletedListener = new GetMovieTask.OnTaskCompleted() {
                @Override
                public void onGetMoviesTaskCompleted(Movie[] moviesRes) {
                    Log.i(LOG_TAG, "ddd v:" + moviesRes.length);
                    if (moviesRes != null && moviesRes.length > 0) {
                        Log.i(LOG_TAG, "getMoviesFromTMDb got result video");
                        Log.i(LOG_TAG, "ddd v2:" + moviesRes[0].getVideos().size());
                        movie.setVideos(moviesRes[0].getVideos());
                        fillVideos();
                    }
                }
            };

            // Execute task
            GetMovieTask movieTask = new GetMovieTask(taskCompletedListener, apiKey);
            Log.i(LOG_TAG, "getMoviesFromTMDb execute");

            movieTask.execute(GetMovieTask.GET_VIDEO, movie.getId().toString());
        } else {
            Toast.makeText(this, getString(R.string.error_internet_required), Toast.LENGTH_LONG).show();
        }
    }

    private void fillVideos(){
        trailerContainer.removeAllViews();
        if (movie.getVideos().size() > 0){
            for (final MovieVideo video : movie.getVideos()){
                Log.i(LOG_TAG, "fill v:" + video.getKey());
                Log.i(LOG_TAG, "fill v:" + video.getSite());
                if (!YOUTUBE.equals(video.getSite())){
                    Log.i(LOG_TAG, "fill v:NOOOOO");
                    continue;
                }
                LayoutInflater inflater = LayoutInflater.from(this);
                View item = inflater.inflate(R.layout.vide_item, trailerContainer, false);

                ImageView thumb = (ImageView) item.findViewById(R.id.video_thumb);


                Picasso.with(this)
                        .load(String.format(YOUTUBE_THUMB_URL, video.getKey()))
                        .fit()
                        .centerCrop()
                        .error(R.drawable.clapper_185)
                        .placeholder(R.drawable.clapper_185)
                        .into(thumb);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String youtubeUrl = String.format(YOUTUBE_WATCH_URL, video.getKey());
                        Uri uri = Uri.parse(youtubeUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                        if(intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
                trailerContainer.addView(item);
                Log.i(LOG_TAG, video.getKey() + " - " + video.getName());
            }
        }
    }

    private void fetchReviews(){
        if (Utils.isNetworkAvailable(this)) {
            String apiKey = getString(R.string.api_key);

            GetMovieTask.OnTaskCompleted taskCompletedListener = new GetMovieTask.OnTaskCompleted() {
                @Override
                public void onGetMoviesTaskCompleted(Movie[] moviesRes) {
                    Log.i(LOG_TAG, "ddd r:" + moviesRes.length);
                    if (moviesRes != null && moviesRes.length > 0) {
                        Log.i(LOG_TAG, "getMoviesFromTMDb got result");
                        movie.setReviews(moviesRes[0].getReviews());
                        fillReviews();
                    }
                }
            };

            // Execute task
            GetMovieTask movieTask = new GetMovieTask(taskCompletedListener, apiKey);
            Log.i(LOG_TAG, "getMoviesFromTMDb execute");

            movieTask.execute(GetMovieTask.GET_REVIEW, movie.getId().toString());
        } else {
            Toast.makeText(this, getString(R.string.error_internet_required), Toast.LENGTH_LONG).show();
        }
    }

    private void fillReviews(){
        reviewContainer.removeAllViews();


        for(MovieReview review : movie.getReviews()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View item = inflater.inflate(R.layout.review_item, reviewContainer, false);

            TextView author = (TextView) item.findViewById(R.id.review_author);
            TextView content = (TextView) item.findViewById(R.id.review_content);

            author.setText(review.getAuthor());
            content.setText(review.getContent());

            reviewContainer.addView(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "save Instance");
        outState.putParcelable(getString(R.string.movie_parcerable), movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "Restor Instance");
        Movie parcelable = savedInstanceState.getParcelable(getString(R.string.movie_parcerable));
        if (parcelable != null) {
            movie = parcelable;
            //fillVideos();
            //fillReviews();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CODE,  getIntent());
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CODE,  getIntent());
        finish();
    }
}
