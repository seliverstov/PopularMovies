package com.seliverstov.popularmovies;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.seliverstov.popularmovies.rest.model.Movie;
import com.squareup.picasso.Picasso;


/**
 * Created by a.g.seliverstov on 12.10.2015.
 */
public class MoviesGridFragment extends Fragment {
    private static final String LOG_TAG = MoviesGridFragment.class.getSimpleName();
    private int VISIBLE_TRESHOLD = 2;
    private MovieLoader movieLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = inflater.getContext();
        movieLoader = new MovieLoader(context);
        View view = inflater.inflate(R.layout.fragment_movies_grid,container,false);
        final GridView gv = (GridView)view.findViewById(R.id.movies_grid);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        final ArrayAdapter<Movie> adapter = new ArrayAdapter<Movie>(context,android.R.layout.simple_list_item_1){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ImageView imageView;
                if (convertView==null) {
                    imageView = new ImageView(context);
                    imageView.setLayoutParams(
                            new GridView.LayoutParams(
                                    (int)getResources().getDimension(R.dimen.small_movie_poster_width),
                                    (int)getResources().getDimension(R.dimen.big_movie_poster_height)));
                    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                }else{
                    imageView = (ImageView)convertView;
                }
                Movie m = getItem(position);
                if (m.getPosterPath()!=null) {
                    Uri url = Uri.parse(getString(R.string.movie_poster_base_url))
                            .buildUpon()
                            .appendPath(getString(R.string.small_movie_poster_size))
                            .appendEncodedPath(m.getPosterPath())
                            .build();
                    Log.i(LOG_TAG, "Get image for position "+position+": "+url.toString());
                    Picasso.with(context).load(url).into(imageView);
                }else{
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageResource(R.drawable.noposter);
                }
                return imageView;
            }
        };

        gv.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - visibleItemCount <= (firstVisibleItem + VISIBLE_TRESHOLD)) {
                    loadMoviesToAdapter(adapter);
                }
            }
        });

        gv.setAdapter(adapter);
        loadMoviesToAdapter(adapter);
        return view;
    }

    private void loadMoviesToAdapter(ArrayAdapter<Movie> adapter){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sp.getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_default));
        movieLoader.loadMoreMovies(adapter,sortBy);
    }
}
