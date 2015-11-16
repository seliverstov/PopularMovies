package com.seliverstov.popularmovies;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.seliverstov.popularmovies.db.PopularMoviesContact;
import com.seliverstov.popularmovies.db.PopularMoviesDbHelper;
import com.seliverstov.popularmovies.rest.TMDBClient;
import com.seliverstov.popularmovies.rest.model.Movie;

import java.io.IOException;
import java.util.List;

/**
 * Created by a.g.seliverstov on 06.11.2015.
 */
public class MoviesLoader extends AsyncTaskLoader<Void> {
    private static final String LOG_TAG = MoviesLoader.class.getSimpleName();
    private static final String PAGE_SETTING = "PAGE";
    private Context mContext;
    private String mSortOrder;
    private Integer mPage;

    public MoviesLoader(Context context, String sortOrder, Integer page) {
        super(context);
        mContext = context;
        mSortOrder = sortOrder;
        mPage = page;
    }

    @Override
    public Void loadInBackground() {
        Log.i(LOG_TAG,"Start load in background with params: "+mSortOrder+", "+mPage);
        /*Cursor c = mContext.getContentResolver().query(PopularMoviesContact.SettingEntry.CONTENT_URI.buildUpon().appendPath(PAGE_SETTING).build(),new String[]{PopularMoviesContact.SettingEntry.COLUMN_VALUE},null,null,null);
        String storedPage = null;
        if (c.moveToFirst()){
            storedPage = c.getString(0);
            Log.i(LOG_TAG,"Stored page: "+storedPage);
        }else{
            Log.i(LOG_TAG,"Stored page not found!");
        }
        int page = 0;
        if (storedPage!=null){
            page = Integer.valueOf(storedPage);
        }
        page++;*/
        try {
            List<Movie> movies = new TMDBClient().listMovies(mSortOrder, mPage);
            ContentValues[] cvs = new ContentValues[movies.size()];
            for(int i=0;i<movies.size();i++){
                ContentValues cv = new ContentValues();
                Movie m = movies.get(i);
                cv.put(PopularMoviesContact.MovieEntry._ID,m.getId());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_TITLE,m.getTitle());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_VOTE_COUNT,m.getVoteCount());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_VOTE_AVERAGE,m.getVoteAverage());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_ADULT,m.getAdult()?1:0);
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_BACKDROP_PATH,m.getBackdropPath());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_GENRE_IDS,m.getGenreIds().toString());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_ORIGINAL_LANGUAGE,m.getOriginalLanguage());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_ORIGINAL_TITLE,m.getOriginalTitle());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_OVERVIEW,m.getOverview());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_POPULARITY,m.getPopularity());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_POSTER_PATH,m.getPosterPath());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_RELEASE_DATE,m.getReleaseDate());
                cv.put(PopularMoviesContact.MovieEntry.COLUMN_VIDEO,m.getVideo()?1:0);
                cvs[i]=cv;
            }
            int insCnt = mContext.getContentResolver().bulkInsert(PopularMoviesContact.MovieEntry.CONTENT_URI,cvs);
            Log.i(LOG_TAG, insCnt + " was loaded to database; page = " + mPage);

            /*ContentValues sPage = new ContentValues();
            sPage.put(PopularMoviesContact.SettingEntry.COLUMN_NAME,PAGE_SETTING);
            sPage.put(PopularMoviesContact.SettingEntry.COLUMN_VALUE, String.valueOf(page));
            mContext.getContentResolver().insert(PopularMoviesContact.SettingEntry.CONTENT_URI, sPage);
            Log.i(LOG_TAG, "Stored page updated to "+page);*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
