package com.seliverstov.popularmovies;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.seliverstov.popularmovies.rest.TMDBClient;
import com.seliverstov.popularmovies.rest.model.Movie;

import java.util.List;

/**
 * Created by a.g.seliverstov on 13.10.2015.
 */
public class LoadMoviesTask extends AsyncTask<Integer,Void,List<Movie>> {
    private ArrayAdapter<Movie> mAdapter;

    public LoadMoviesTask(ArrayAdapter<Movie> adapter){
        mAdapter=adapter;
    }

    @Override
    protected List<Movie> doInBackground(Integer... params) {
        int page = (params.length > 0 && params[0] > 0) ? params[0] : 1;
        List<Movie> result = new TMDBClient().listMostPopularMovies(page);
        Log.i(MoviesGridFragment.class.toString(), "Get " + result.size() + " movies!");
        return result;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onPostExecute(List<Movie> movies) {
        mAdapter.addAll(movies);
    }
}
