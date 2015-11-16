package com.seliverstov.popularmovies.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.seliverstov.popularmovies.db.PopularMoviesContact.*;

/**
 * Created by a.g.seliverstov on 02.11.2015.
 */
public class PopularMoviesDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = PopularMoviesDbHelper.class.getSimpleName();
    public static final int DATABASE_VERSION = 12;
    public static final String DATABASE_NAME = "popular_movies.db";


    public PopularMoviesDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_ADULT + " INTEGER," +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT," +
                MovieEntry.COLUMN_GENRE_IDS + " TEXT," +
                MovieEntry.COLUMN_ORIGINAL_LANGUAGE + " INTEGER," +
                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT," +
                MovieEntry.COLUMN_OVERVIEW + " TEXT," +
                MovieEntry.COLUMN_POPULARITY + " REAL," +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT," +
                MovieEntry.COLUMN_TITLE + " TEXT," +
                MovieEntry.COLUMN_VIDEO + " TEXT," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL," +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER" +
                ");";
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_TMDB_ID+" TEXT NOT NULL,"+
                ReviewEntry.COLUMN_AUTHOR + " TEXT," +
                ReviewEntry.COLUMN_CONTENT + " TEXT," +
                ReviewEntry.COLUMN_URL + " TEXT," +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ")," +
                "UNIQUE ("+ReviewEntry.COLUMN_TMDB_ID+")"+
                ")";
        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VideoEntry.COLUMN_TMDB_ID+" TEXT NOT NULL,"+
                VideoEntry.COLUMN_ISO_639_1 + " TEXT," +
                VideoEntry.COLUMN_KEY + " TEXT," +
                VideoEntry.COLUMN_NAME + " TEXT," +
                VideoEntry.COLUMN_SITE + " TEXT," +
                VideoEntry.COLUMN_SIZE + " INTEGER," +
                VideoEntry.COLUMN_TYPE + " TEXT," +
                VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " + MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ")," +
                "UNIQUE ("+VideoEntry.COLUMN_TMDB_ID+")"+
                ")";
        final String SQL_CREATE_SETTINGS_TABLE = "CREATE TABLE "+ SettingEntry.TABLE_NAME + " ("+
                SettingEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                SettingEntry.COLUMN_NAME+" TEXT NOT NULL,"+
                SettingEntry.COLUMN_VALUE+" TEXT,"+
                "UNIQUE ("+ SettingEntry.COLUMN_NAME+")"+
                ")";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_VIDEOS_TABLE);
        db.execSQL(SQL_CREATE_SETTINGS_TABLE);
        Log.i(LOG_TAG, "Database created!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ SettingEntry.TABLE_NAME);
        onCreate(db);
        Log.i(LOG_TAG, "Database upgraded!");
    }
}
