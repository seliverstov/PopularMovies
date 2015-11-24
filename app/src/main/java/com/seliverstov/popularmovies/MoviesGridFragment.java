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

    private int VISIBLE_THRESHOLD = 2;
    private boolean loading = true;
    private int previousTotalItemCount = 0;

    private int TMDB_MOVIES_LOADER_ID = 0;
    private int CURSOR_MOVIES_LOADER_ID = 1;

    private MoviesAdapter mMoviesAdapter;

    private static String[] COLUMNS = {
            PopularMoviesContact.MovieEntry._ID,
            PopularMoviesContact.MovieEntry.COLUMN_POSTER_PATH
    };

    public static int IDX_ID = 0;
    public static int IDX_POSTER_PATH = 1;

    @Bind(R.id.movies_grid) GridView mGridView;
    @Bind(R.id.movies_grid_progressbar) ProgressBar mProgressBar;
    @Bind(R.id.movies_grid_no_movies) TextView mNoMovies;

    private SettingsManager mSettingsManager;

    public interface ItemSelectedCallback{
        void onItemSelected(Uri uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = inflater.getContext();

        final View view = inflater.inflate(R.layout.fragment_grid, container, false);

        mMoviesAdapter = new MoviesAdapter(context, null, 0);

        ButterKnife.bind(this,view);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) mMoviesAdapter.getItem(position);
                if (c != null && c.getCount() > 0) {
                    ItemSelectedCallback collback = (ItemSelectedCallback) getActivity();
                    collback.onItemSelected(ContentUris.withAppendedId(PopularMoviesContact.MovieEntry.CONTENT_URI, c.getLong(IDX_ID)));
                }
            }
        });

        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                SettingsManager settingsManager = new SettingsManager(getActivity());
                if (totalItemCount < previousTotalItemCount) {
                    previousTotalItemCount = totalItemCount;
                    if (totalItemCount == 0) loading = true;
                }

                if (loading && totalItemCount > previousTotalItemCount) {
                    loading = false;
                    previousTotalItemCount = totalItemCount;
                    Log.i(LOG_TAG, "Load on scroll is finished");
                }
                if (!settingsManager.isFavoriteSortOrder() && LoaderUtils.isNetworkAvailable(getActivity())) {
                    if (!loading) {
                        if (totalItemCount - visibleItemCount <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
                            loading = true;
                            Log.i(LOG_TAG, "Load additional movies on scroll");
                            getLoaderManager().getLoader(TMDB_MOVIES_LOADER_ID).forceLoad();
                            Log.i(LOG_TAG, "Database size:" + DatabaseUtils.queryNumEntries((new PopularMoviesDbHelper(getActivity())).getReadableDatabase(), PopularMoviesContact.MovieEntry.TABLE_NAME));
                        }
                    }
                }
            }
        });

        mGridView.setAdapter(mMoviesAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSettingsManager = new SettingsManager(getActivity());

        getLoaderManager().initLoader(CURSOR_MOVIES_LOADER_ID, null, new CursorLoaderCallback(getActivity()));

        getLoaderManager().initLoader(TMDB_MOVIES_LOADER_ID, null, new MovieLoaderCallback(getActivity()));

        if (mSettingsManager.getCurrentPage()==0 && !mSettingsManager.isFavoriteSortOrder()){
            mProgressBar.setVisibility(View.VISIBLE);
            getLoaderManager().getLoader(TMDB_MOVIES_LOADER_ID).forceLoad();
        }
    }

    void onSortOrderChanged(){
        mGridView.clearChoices();

        getLoaderManager().restartLoader(CURSOR_MOVIES_LOADER_ID, null, new CursorLoaderCallback(getActivity())).forceLoad();

        if (!mSettingsManager.isFavoriteSortOrder()) {
            if (LoaderUtils.isNetworkAvailable(getActivity())){
                mProgressBar.setVisibility(View.VISIBLE);
                getLoaderManager().getLoader(TMDB_MOVIES_LOADER_ID).forceLoad();
            }else{
                Toast.makeText(getActivity(), getString(R.string.cant_load_movies), Toast.LENGTH_SHORT).show();
            }
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
            mProgressBar.setVisibility(View.GONE);
            if (data==null){
                loading=false;
                Toast.makeText(getActivity(), getString(R.string.cant_load_movies), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Movie>> loader) {}
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
            if (data==null || data.getCount()==0){
                mNoMovies.setVisibility(View.VISIBLE);
            }else{
                mNoMovies.setVisibility(View.GONE);
            }
            mMoviesAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) { mMoviesAdapter.swapCursor(null); }
    }
}
