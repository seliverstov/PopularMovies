package com.seliverstov.popularmovies;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.seliverstov.popularmovies.rest.TMDBClient;
import com.seliverstov.popularmovies.rest.model.Movie;

import java.util.List;

/**
 * Created by a.g.seliverstov on 13.10.2015.
 */
public class MovieLoader {
    private LoadMoviesTask currentLoad;
    private int page = 0;

    public class LoadMoviesTask extends AsyncTask<String,Void,List<Movie>> {
        private ArrayAdapter<Movie> mAdapter;

        public LoadMoviesTask(ArrayAdapter<Movie> adapter){
            mAdapter = adapter;
        }

        @Override
        protected List<Movie> doInBackground (String...params){
            String sortBy = params[0];
            page++;
            Log.i(this.getClass().getSimpleName(),"Load page "+page);
            List<Movie> result = new TMDBClient().listMovies(sortBy, page);
            Log.i(MoviesGridFragment.class.toString(), "Get " + result.size() + " movies!");
            return result;
        }

        @Override
        protected void onPostExecute (List < Movie > movies) {
            mAdapter.addAll(movies);
        }
    }

    public synchronized void loadMoreMovies(ArrayAdapter<Movie> adapter, String... params){
        if (currentLoad == null || currentLoad.getStatus()== AsyncTask.Status.FINISHED){
            currentLoad = new LoadMoviesTask(adapter);
            currentLoad.execute(params);
        }
    }
}
