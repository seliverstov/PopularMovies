package com.seliverstov.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.seliverstov.popularmovies.db.PopularMoviesContact;

public class MainActivity extends AppCompatActivity implements MoviesGridFragment.ItemSelectedCallback{
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String SAVED_SORT_ORDER = MainActivity.class.getSimpleName()+".SAVED_SORT_ORDER";
    public static final String SAVED_MOVIE_URI = MainActivity.class.getSimpleName()+".SAVED_MOVIE_URI";
    public static final String SAVED_PAGE = MainActivity.class.getSimpleName()+".SAVED_PAGE";

    private static final String MOVIE_DETAILS_FRAGMENT_TAG = "MOVIE_DETAILS_FRAGMENT_TAG";

    private String mSortOrder;
    private boolean mTwoPane;
    private Uri mMovieUri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.details_fragment_container)!=null){
            mTwoPane = true;
            if (savedInstanceState==null){
                getFragmentManager().beginTransaction().replace(R.id.details_fragment_container,new MovieDetailsFragment(),MOVIE_DETAILS_FRAGMENT_TAG).commit();
            }
        }else{
            mTwoPane = false;
        }

        if (savedInstanceState!=null){
            if (savedInstanceState.containsKey(SAVED_SORT_ORDER)) mSortOrder = savedInstanceState.getString(SAVED_SORT_ORDER);
            if (savedInstanceState.containsKey(SAVED_MOVIE_URI)) mMovieUri = savedInstanceState.getParcelable(SAVED_MOVIE_URI);
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
            case R.id.action_refresh:
                refresh();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_SORT_ORDER, mSortOrder);
        outState.putParcelable(SAVED_MOVIE_URI, mMovieUri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = sp.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));

        if (mSortOrder ==null) mSortOrder = sortOrder;

        if (!mSortOrder.equals(sortOrder)){
            refresh();
        }

        if (mTwoPane && mMovieUri !=null){
            MovieDetailsFragment mdf = (MovieDetailsFragment)getFragmentManager().findFragmentByTag(MOVIE_DETAILS_FRAGMENT_TAG);
            Uri oldUri = null;
            if (mdf!=null) oldUri = mdf.getMovieUri();
            if (oldUri==null || mMovieUri!=oldUri){
                Bundle argumants = new Bundle();
                argumants.putParcelable(MovieDetailsFragment.MOVIE_DETAILS_URI, mMovieUri);
                MovieDetailsFragment newFragment = new MovieDetailsFragment();
                newFragment.setArguments(argumants);
                getFragmentManager().beginTransaction().replace(R.id.details_fragment_container,newFragment,MOVIE_DETAILS_FRAGMENT_TAG).commit();
            }
        }
    }

    @Override
    public void onItemSelected(Uri uri) {
        mMovieUri = uri;
        if (mTwoPane){
            Bundle argumants = new Bundle();
            argumants.putParcelable(MovieDetailsFragment.MOVIE_DETAILS_URI,uri);
            MovieDetailsFragment mdf = new MovieDetailsFragment();
            mdf.setArguments(argumants);
            getFragmentManager().beginTransaction().replace(R.id.details_fragment_container,mdf,MOVIE_DETAILS_FRAGMENT_TAG).commit();
        }else{
            Intent intent = new Intent(this,MovieDetailsActivity.class);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    protected void refresh(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = sp.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));
        mSortOrder = sortOrder;
        mMovieUri = null;
        sp.edit().putInt("pref_page",0).apply();

        Log.i(LOG_TAG, "Refresh database");
        long d = getContentResolver().delete(PopularMoviesContact.MovieEntry.CONTENT_URI, PopularMoviesContact.MovieEntry.COLUMN_FAVORITE + " is null", null);
        Log.i(LOG_TAG, d+" records were deleted");

        ContentValues cv = new ContentValues();
        cv.put(PopularMoviesContact.MovieEntry.COLUMN_SORT_ORDER, (String) null);
        long u = getContentResolver().update(PopularMoviesContact.MovieEntry.CONTENT_URI, cv, PopularMoviesContact.MovieEntry.COLUMN_FAVORITE + " is not null", null);
        Log.i(LOG_TAG, u+" records were updated");
        
        MoviesGridFragment fragment = (MoviesGridFragment)getFragmentManager().findFragmentById(R.id.grid_fragment);
        if (fragment!=null) fragment.onSortOrderChanged();
    }
}
