package com.seliverstov.popularmovies.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.seliverstov.popularmovies.fragment.DetailsFragment;
import com.seliverstov.popularmovies.fragment.GridFragment;
import com.seliverstov.popularmovies.R;
import com.seliverstov.popularmovies.db.PopularMoviesContact;
import com.seliverstov.popularmovies.model.SettingsManager;

public class MainActivity extends AppCompatActivity implements GridFragment.ItemSelectedCallback, GridFragment.SwipeRefreshListener{
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String SAVED_SORT_ORDER = MainActivity.class.getSimpleName()+".SAVED_SORT_ORDER";
    public static final String SAVED_MOVIE_URI = MainActivity.class.getSimpleName()+".SAVED_MOVIE_URI";

    private static final String MOVIE_DETAILS_FRAGMENT_TAG = "MOVIE_DETAILS_FRAGMENT_TAG";

    private Integer mSortOrder;
    private boolean mTwoPane;
    private Uri mMovieUri;
    private SettingsManager mSettingsManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState!=null){
            if (savedInstanceState.containsKey(SAVED_SORT_ORDER)) mSortOrder = savedInstanceState.getInt(SAVED_SORT_ORDER);
            if (savedInstanceState.containsKey(SAVED_MOVIE_URI)) mMovieUri = savedInstanceState.getParcelable(SAVED_MOVIE_URI);
        }

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);



        if (findViewById(R.id.details_fragment_container)!=null){
            mTwoPane = true;


            if (savedInstanceState==null || getFragmentManager().findFragmentByTag(MOVIE_DETAILS_FRAGMENT_TAG)==null){
                getFragmentManager().beginTransaction().replace(R.id.details_fragment_container,new DetailsFragment(),MOVIE_DETAILS_FRAGMENT_TAG).commit();
            }
        }else{
            mTwoPane = false;
        }

        mSettingsManager = new SettingsManager(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_share);
        if (item != null && (!mTwoPane || mMovieUri==null)){
            menu.removeItem(item.getItemId());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int oldSortOrder = mSettingsManager.getCurrentSortOrder();
        switch (item.getItemId()){
            case R.id.action_show_most_popular: {
                mSettingsManager.setSortOrder(SettingsManager.SORT_ORDER_POPULARITY);
                break;
            }
            case R.id.action_show_most_rated: {
                mSettingsManager.setSortOrder(SettingsManager.SORT_ORDER_RATING);
                break;
            }
            case R.id.action_show_favorite: {
                mSettingsManager.setSortOrder(SettingsManager.SORT_ORDER_FAVORITE);
                break;
            }
            default: return super.onOptionsItemSelected(item);
        }
        if (oldSortOrder!=mSettingsManager.getCurrentSortOrder()) refresh();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_SORT_ORDER, mSortOrder);
        outState.putParcelable(SAVED_MOVIE_URI, mMovieUri);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSortOrder ==null) mSortOrder = mSettingsManager.getCurrentSortOrder();

        if (!mSortOrder.equals(mSettingsManager.getCurrentSortOrder())){
            refresh();
        }

        if (mTwoPane){
            DetailsFragment mdf = (DetailsFragment)getFragmentManager().findFragmentByTag(MOVIE_DETAILS_FRAGMENT_TAG);
            Uri oldUri = null;
            if (mdf!=null) oldUri = mdf.getMovieUri();
            if (mMovieUri!=oldUri){
                Bundle argumants = new Bundle();
                argumants.putParcelable(DetailsFragment.MOVIE_DETAILS_URI, mMovieUri);
                DetailsFragment newFragment = new DetailsFragment();
                newFragment.setArguments(argumants);
                getFragmentManager().beginTransaction().replace(R.id.details_fragment_container, newFragment, MOVIE_DETAILS_FRAGMENT_TAG).commit();
            }
        }
    }

    @Override
    public void onItemSelected(Uri uri) {
        mMovieUri = uri;
        if (mTwoPane){
            Bundle argumants = new Bundle();
            argumants.putParcelable(DetailsFragment.MOVIE_DETAILS_URI,uri);
            DetailsFragment mdf = new DetailsFragment();
            mdf.setArguments(argumants);
            getFragmentManager().beginTransaction().replace(R.id.details_fragment_container,mdf,MOVIE_DETAILS_FRAGMENT_TAG).commit();
        }else{
            Intent intent = new Intent(this,DetailsActivity.class);
            intent.setData(uri);
            startActivity(intent);
        }
    }

    protected void refresh(){
        mSortOrder = mSettingsManager.getCurrentSortOrder();

        mMovieUri = null;

        mSettingsManager.setCurrentPage(0);

        Log.i(LOG_TAG, "Refresh database");
        long d = getContentResolver().delete(PopularMoviesContact.MovieEntry.CONTENT_URI, PopularMoviesContact.MovieEntry.COLUMN_FAVORITE + " is null", null);
        Log.i(LOG_TAG, d+" records were deleted");

        ContentValues cv = new ContentValues();
        cv.put(PopularMoviesContact.MovieEntry.COLUMN_SORT_ORDER, (String) null);
        long u = getContentResolver().update(PopularMoviesContact.MovieEntry.CONTENT_URI, cv, PopularMoviesContact.MovieEntry.COLUMN_FAVORITE + " is not null", null);
        Log.i(LOG_TAG, u+" records were updated");

        GridFragment fragment = (GridFragment)getFragmentManager().findFragmentById(R.id.grid_fragment);
        if (fragment!=null) fragment.onSortOrderChanged();

        if (mTwoPane){
            onItemSelected(mMovieUri);
        }
    }

    @Override
    public void onSwipeRefresh() {
        refresh();
    }
}
