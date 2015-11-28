package com.seliverstov.popularmovies;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.seliverstov.popularmovies.db.PopularMoviesContact;
import com.squareup.picasso.Picasso;

/**
 * Created by alexander on 28.11.2015.
 */
public class MovieRecyclerViewAdapter extends CursorRecyclerViewAdapter<MovieViewHolder> {
    private Context mContext;
    public MovieRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext=context;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder viewHolder, Cursor cursor) {
        final String poster = cursor.getString(MoviesGridFragment.IDX_POSTER_PATH);
        final long id =  cursor.getLong(MoviesGridFragment.IDX_ID);
        if (poster!=null) {
            Uri url = Uri.parse(mContext.getString(R.string.movie_poster_base_url))
                    .buildUpon()
                    .appendPath(mContext.getString(R.string.small_movie_poster_size))
                    .appendEncodedPath(poster)
                    .build();
            Picasso.with(mContext).load(url).placeholder(R.drawable.loading_small).error(R.drawable.no_poster).into(viewHolder.poster);
        }else{
            viewHolder.poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder.poster.setImageResource(R.drawable.no_poster);
        }
        viewHolder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoviesGridFragment.ItemSelectedCallback collback = (MoviesGridFragment.ItemSelectedCallback) mContext;
                collback.onItemSelected(ContentUris.withAppendedId(PopularMoviesContact.MovieEntry.CONTENT_URI, id));
            }
        });
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_item, parent, false);
        MovieViewHolder vh = new MovieViewHolder(itemView);
        return vh;
    }
}
