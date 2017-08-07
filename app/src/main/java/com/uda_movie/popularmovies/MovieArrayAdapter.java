package com.uda_movie.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uda_movie.popularmovies.model.Movie;

import java.util.List;


public class MovieArrayAdapter extends ArrayAdapter<Movie> {
    private static final String LOG_TAG = MovieArrayAdapter.class.getSimpleName();

    public MovieArrayAdapter(@NonNull Context context, @NonNull List<Movie> objects) {

        super(context, 0,  objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Movie movie = getItem(position);
        Log.i(LOG_TAG, "position:" + position + " movie: " + movie.getOriginalTitle());

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(getContext());
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(getContext())
                .load(movie.getPosterUrl())
                .fit()
                .centerInside()
                .error(R.drawable.clapper_185)
                .placeholder(R.drawable.clapper_185)
                .into(imageView);

        return imageView;
    }
}
