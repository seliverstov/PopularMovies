package com.seliverstov.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.seliverstov.popularmovies.rest.model.Movie;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String SAVED_SORT_ORDER = MainActivity.class.getSimpleName()+".SAVED_SORT_ORDER";
    public static final String SAVED_MOVIES = MainActivity.class.getSimpleName()+".SAVED_MOVIES";

    private MovieLoader movieLoader;

    public void setCurrentSortOrder(String currentSortOrder) {
        this.currentSortOrder = currentSortOrder;
    }

    private String currentSortOrder;


    public MovieLoader getMovieLoader() {
        return movieLoader;
    }

    public String getCurrentSortOrder() {
        return currentSortOrder;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (movieLoader==null){
            movieLoader = new MovieLoader(this);
        }

        if (savedInstanceState!=null){
            Log.i(LOG_TAG,"Restore saved state:");
            if (savedInstanceState!=null){
                String sortOrder = savedInstanceState.getString(SAVED_SORT_ORDER);
                ArrayList<Movie> movies = (ArrayList<Movie>)savedInstanceState.getSerializable(SAVED_MOVIES);
                if (sortOrder!=null) this.currentSortOrder=sortOrder;
                if (movies!=null) this.movieLoader.setMovies(movies);
                Log.i(LOG_TAG,"\tsortOrder: "+sortOrder);
                Log.i(LOG_TAG,"\tmovies: "+movies);
            }
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = sp.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));

        if (currentSortOrder==null){
            currentSortOrder = sortOrder;
        }else if (!currentSortOrder.equals(sortOrder)){
            movieLoader.reset();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "Save state:");
        outState.putString(SAVED_SORT_ORDER, currentSortOrder);
        outState.putSerializable(SAVED_MOVIES, (ArrayList<Movie>) movieLoader.getMovies());
        Log.i(LOG_TAG, "\tsortOrder: " + currentSortOrder);
        Log.i(LOG_TAG, "\tmovies: " + movieLoader.getMovies());
    }
}
