package com.seliverstov.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by alexander on 28.11.2015.
 */
public class MovieViewHolder extends RecyclerView.ViewHolder {
    public ImageView poster;
    public MovieViewHolder(View itemView) {
        super(itemView);
        poster = (ImageView)itemView.findViewById(R.id.movie_grid_item);
    }
}
