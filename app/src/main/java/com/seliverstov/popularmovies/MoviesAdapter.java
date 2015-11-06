package com.seliverstov.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by a.g.seliverstov on 05.11.2015.
 */
public class MoviesAdapter extends CursorAdapter {
    public MoviesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView newView = new ImageView(context);
        newView.setLayoutParams(new GridView.LayoutParams(
                (int)context.getResources().getDimension(R.dimen.small_movie_poster_width),
                (int)context.getResources().getDimension(R.dimen.small_movie_poster_height)
        ));
        newView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return newView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String poster = cursor.getString(MoviesGridFragment.IDX_POSTER_PATH);
        if (poster!=null) {
            Uri url = Uri.parse(context.getString(R.string.movie_poster_base_url))
                    .buildUpon()
                    .appendPath(context.getString(R.string.small_movie_poster_size))
                    .appendEncodedPath(poster)
                    .build();
            Picasso.with(context).load(url).placeholder(R.drawable.loading_small).into((ImageView)view);
        }else{
            ((ImageView)view).setScaleType(ImageView.ScaleType.CENTER_CROP);
            ((ImageView)view).setImageResource(R.drawable.noposter);
        }
    }
}
