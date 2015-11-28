package com.seliverstov.popularmovies.loader;

import android.support.v4.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.seliverstov.popularmovies.db.PopularMoviesContact;
import com.seliverstov.popularmovies.model.SettingsManager;
import com.seliverstov.popularmovies.rest.TMDBClient;
import com.seliverstov.popularmovies.rest.model.Movie;

import java.io.IOException;
import java.util.List;

/**
 * Created by a.g.seliverstov on 06.11.2015.
 */
public class MoviesLoader extends AsyncTaskLoader<List<Movie>> {
    private static final String LOG_TAG = MoviesLoader.class.getSimpleName();

    public MoviesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {
        if (!LoaderUtils.isNetworkAvailable(getContext())) return null;
        try {
            SettingsManager settingsManager = new SettingsManager(getContext());
            String sortOrder = settingsManager.getSortOrderForWeb();
            int page = settingsManager.getCurrentPage()+1;
            Log.i(LOG_TAG,"Start load in background with params: "+sortOrder+", "+page);
            List<Movie> movies = new TMDBClient().listMovies(sortOrder,page);
            int insCnt = 0;
            if (movies!=null && movies.size()>0) {
                ContentValues[] cvs = new ContentValues[movies.size()];
                for (int i = 0; i < movies.size(); i++) {
                    ContentValues cv = new ContentValues();
                    Movie m = movies.get(i);
                    cv.put(PopularMoviesContact.MovieEntry._ID, m.getId());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_TITLE, m.getTitle());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_VOTE_COUNT, m.getVoteCount());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_VOTE_AVERAGE, m.getVoteAverage());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_ADULT, m.getAdult() ? 1 : 0);
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_BACKDROP_PATH, m.getBackdropPath());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_GENRE_IDS, m.getGenreIds().toString());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, m.getOriginalLanguage());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_ORIGINAL_TITLE, m.getOriginalTitle());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_OVERVIEW, m.getOverview());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_POPULARITY, m.getPopularity());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_POSTER_PATH, m.getPosterPath());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_RELEASE_DATE, m.getReleaseDate());
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_VIDEO, m.getVideo() ? 1 : 0);
                    cv.put(PopularMoviesContact.MovieEntry.COLUMN_SORT_ORDER, settingsManager.getSortOrderForDb());
                    cvs[i] = cv;
                }
                insCnt = getContext().getContentResolver().bulkInsert(PopularMoviesContact.MovieEntry.CONTENT_URI, cvs);
                settingsManager.setCurrentPage(page);
            }
            Log.i(LOG_TAG, insCnt + " movies was loaded to database; page = " + page);
            return movies;
        } catch (IOException e) {
            Log.e(LOG_TAG,e.getMessage(),e);
            return null;
        }
    }

}
