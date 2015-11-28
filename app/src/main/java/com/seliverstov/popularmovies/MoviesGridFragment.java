package com.seliverstov.popularmovies;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seliverstov.popularmovies.db.PopularMoviesContact;
import com.seliverstov.popularmovies.db.PopularMoviesDbHelper;
import com.seliverstov.popularmovies.loader.LoaderUtils;
import com.seliverstov.popularmovies.loader.MoviesLoader;
import com.seliverstov.popularmovies.model.SettingsManager;
import com.seliverstov.popularmovies.rest.model.Movie;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by a.g.seliverstov on 12.10.2015.
 */
public class MoviesGridFragment extends Fragment {
    private static final String LOG_TAG = MoviesGridFragment.class.getSimpleName();


    private boolean loading = false;

    private int TMDB_MOVIES_LOADER_ID = 0;
    private int CURSOR_MOVIES_LOADER_ID = 1;

    private MovieRecyclerViewAdapter mMoviesAdapter;

    private static String[] COLUMNS = {
            PopularMoviesContact.MovieEntry._ID,
            PopularMoviesContact.MovieEntry.COLUMN_POSTER_PATH
    };

    public static int IDX_ID = 0;
    public static int IDX_POSTER_PATH = 1;

    @Bind(R.id.movies_grid) RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.movies_grid_no_movies) TextView mNoMovies;

    private SettingsManager mSettingsManager;

    public interface ItemSelectedCallback{
        void onItemSelected(Uri uri);
    }

    public interface SwipeRefreshListener{
        void onSwipeRefresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = inflater.getContext();

        final View view = inflater.inflate(R.layout.fragment_grid, container, false);

        mMoviesAdapter = new MovieRecyclerViewAdapter(context,null);

        ButterKnife.bind(this, view);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mMoviesAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1) && !loading){
                    SettingsManager settingsManager = new SettingsManager(getActivity());
                    if (!settingsManager.isFavoriteSortOrder() && LoaderUtils.isNetworkAvailable(getActivity())) {
                        loading = true;
                        Log.i(LOG_TAG, "Load additional movies on scroll");
                        loadMovies();
                        Log.i(LOG_TAG, "Database size:" + DatabaseUtils.queryNumEntries((new PopularMoviesDbHelper(getActivity())).getReadableDatabase(), PopularMoviesContact.MovieEntry.TABLE_NAME));
                    }
                }else if (recyclerView.canScrollVertically(1) && loading){
                    loading = false;
                    Log.i(LOG_TAG, "Load on scroll is finished");
                }
            }
        });

        mSwipeRefreshLayout.setProgressViewOffset(true,getResources().getInteger(R.integer.spinner_offset_start),getResources().getInteger(R.integer.spinner_offset_end));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((SwipeRefreshListener)getActivity()).onSwipeRefresh();
            }
        });

        return view;
    }

    private void loadMovies() {
        mNoMovies.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(true);
        getLoaderManager().getLoader(TMDB_MOVIES_LOADER_ID).forceLoad();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSettingsManager = new SettingsManager(getActivity());

        getLoaderManager().initLoader(CURSOR_MOVIES_LOADER_ID, null, new CursorLoaderCallback(getActivity()));

        getLoaderManager().initLoader(TMDB_MOVIES_LOADER_ID, null, new MovieLoaderCallback(getActivity()));

        if (mSettingsManager.getCurrentPage()==0 && !mSettingsManager.isFavoriteSortOrder()){
            loadMovies();
        }
    }

    void onSortOrderChanged(){
        mNoMovies.setVisibility(View.GONE);

        getLoaderManager().restartLoader(CURSOR_MOVIES_LOADER_ID, null, new CursorLoaderCallback(getActivity())).forceLoad();

        if (!mSettingsManager.isFavoriteSortOrder()) {
            if (LoaderUtils.isNetworkAvailable(getActivity())){
                mSwipeRefreshLayout.setRefreshing(true);
                getLoaderManager().getLoader(TMDB_MOVIES_LOADER_ID).forceLoad();
            }else{
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), getString(R.string.cant_load_movies), Toast.LENGTH_SHORT).show();
            }
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    class MovieLoaderCallback implements LoaderManager.LoaderCallbacks<List<Movie>>{
        private Context mContext;

        public MovieLoaderCallback(Context context){
            mContext = context;
        }

        @Override
        public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
            return new MoviesLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<List<Movie>> loader, final List<Movie> data) {
            Log.i(LOG_TAG,"onLoadFinished with result "+data);
            mSwipeRefreshLayout.setRefreshing(false);
            if (data==null){
                loading=false;
                Toast.makeText(getActivity(), getString(R.string.cant_load_movies), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {
            loading=false;
        }
    };

    class CursorLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        private Context mContext;

        public CursorLoaderCallback(Context context){
            mContext = context;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String selectionCriteria = mSettingsManager.isFavoriteSortOrder()?
                    PopularMoviesContact.MovieEntry.COLUMN_FAVORITE+" is not null":
                    PopularMoviesContact.MovieEntry.COLUMN_SORT_ORDER+" = ?";
            String[] selectionArgs = mSettingsManager.isFavoriteSortOrder()?
                    null:
                    new String[]{mSettingsManager.getSortOrderForDb()};

            return new CursorLoader(
                    mContext,
                    PopularMoviesContact.MovieEntry.CONTENT_URI,
                    COLUMNS,
                    selectionCriteria,
                    selectionArgs,
                    mSettingsManager.getSortOrderForDb());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if ((data==null || data.getCount()==0) && !mSwipeRefreshLayout.isRefreshing()){
                mNoMovies.setVisibility(View.VISIBLE);
            }else{
                mNoMovies.setVisibility(View.GONE);
            }
            mMoviesAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mMoviesAdapter.swapCursor(null);
        }
    }
}
