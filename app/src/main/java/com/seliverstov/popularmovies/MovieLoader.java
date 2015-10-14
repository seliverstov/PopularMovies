package com.seliverstov.popularmovies;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.seliverstov.popularmovies.rest.TMDBClient;
import com.seliverstov.popularmovies.rest.model.Movie;

import java.io.IOException;
import java.util.List;

/**
 * Created by a.g.seliverstov on 13.10.2015.
 */
public class MovieLoader {
    private LoadMoviesTask currentLoad;
    private Context context;
    private int page = 0;

    public MovieLoader(Context c){
        context = c;
    }

    public class LoadMoviesTask extends AsyncTask<String,Void,List<Movie>> {
        private ArrayAdapter<Movie> mAdapter;
        private Exception exeption;

        public LoadMoviesTask(ArrayAdapter<Movie> adapter){
            mAdapter = adapter;
        }

        @Override
        protected List<Movie> doInBackground (String...params){
            String sortBy = params[0];
            page++;
            try {
                List<Movie> result = new TMDBClient().listMovies(sortBy, page);
                return result;
            }catch(IOException ex){
                exeption = ex;
            }
            return null;
        }

        @Override
        protected void onPostExecute (List < Movie > movies) {
            if (exeption!=null){
                Toast.makeText(context, R.string.cant_load_movies, Toast.LENGTH_SHORT).show();
                return;
            }
            if (movies!=null) {
                mAdapter.addAll(movies);
            }
        }
    }

    public void loadMoreMovies(ArrayAdapter<Movie> adapter, String... params){
        if (currentLoad == null || currentLoad.getStatus()== AsyncTask.Status.FINISHED){
            currentLoad = new LoadMoviesTask(adapter);
            currentLoad.execute(params);
        }
    }
}
