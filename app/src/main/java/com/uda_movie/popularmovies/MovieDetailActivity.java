package com.uda_movie.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uda_movie.popularmovies.model.Movie;

import java.text.ParseException;


public class MovieDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        TextView originalTitle = (TextView) findViewById(R.id.md_title);
        ImageView poster = (ImageView) findViewById(R.id.md_poster);
        TextView mOverView = (TextView) findViewById(R.id.md_overview);
        TextView rate = (TextView) findViewById(R.id.md_rate);
        TextView year = (TextView) findViewById(R.id.md_year);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Movie movie = null;
        if (extras != null){
            if (extras.containsKey(getString(R.string.movie_parcerable))) {
                movie = extras.getParcelable(getString(R.string.movie_parcerable));
            }
        }

        if (movie != null) {
            originalTitle.setText(movie.getOriginalTitle());

            //TODO
            //it seems .fit() somewhat slower
            Picasso.with(this)
                    .load(movie.getPosterPath())
                    //.resize(getResources().getInteger(R.integer.image_width),
                    //       getResources().getInteger(R.integer.image_height))
                    .fit()
                    .centerInside()
                    .error(R.drawable.clapper_185)
                    .placeholder(R.drawable.clapper_185)
                    .into(poster);

            String overView = movie.getOverview();
            if (overView == null) {
                overView = getResources().getString(R.string.text_no_summary_found);
            }
            mOverView.setText(overView);
            rate.setText(movie.getDetailedVoteAverage());
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
        }
    }
}
