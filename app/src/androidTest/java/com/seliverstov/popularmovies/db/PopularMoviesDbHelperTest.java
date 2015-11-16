package com.seliverstov.popularmovies.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.seliverstov.popularmovies.db.PopularMoviesContact.*;

import java.util.HashSet;


/**
 * Created by a.g.seliverstov on 02.11.2015.
 */
public class PopularMoviesDbHelperTest extends AndroidTestCase  {
    public final String LOG_TAG = this.getClass().getSimpleName();

    public void setUp(){
        mContext.deleteDatabase(PopularMoviesDbHelper.DATABASE_NAME);
    }

    public void testCreateDatabase(){
        SQLiteDatabase db  = (new PopularMoviesDbHelper(mContext)).getWritableDatabase();
        assertTrue(db.isOpen());
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null);
        assertTrue(c.moveToFirst());

        HashSet tableNames = new HashSet();
        tableNames.add(PopularMoviesContact.MovieEntry.TABLE_NAME);
        tableNames.add(PopularMoviesContact.ReviewEntry.TABLE_NAME);
        tableNames.add(PopularMoviesContact.VideoEntry.TABLE_NAME);

        do{
            tableNames.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue(tableNames.isEmpty());
        c.close();
        db.close();
    }

    public void testMovieTable(){
        SQLiteDatabase db = (new PopularMoviesDbHelper(mContext)).getWritableDatabase();
        Cursor c = db.rawQuery("PRAGMA table_info("+ MovieEntry.TABLE_NAME+")",null);
        assertTrue(c.moveToFirst());

        HashSet columns = new HashSet();
        columns.add(MovieEntry._ID);
        columns.add(MovieEntry.COLUMN_ADULT);
        columns.add(MovieEntry.COLUMN_BACKDROP_PATH);
        columns.add(MovieEntry.COLUMN_GENRE_IDS);
        columns.add(MovieEntry.COLUMN_ORIGINAL_LANGUAGE);
        columns.add(MovieEntry.COLUMN_ORIGINAL_TITLE);
        columns.add(MovieEntry.COLUMN_OVERVIEW);
        columns.add(MovieEntry.COLUMN_POPULARITY);
        columns.add(MovieEntry.COLUMN_POSTER_PATH);
        columns.add(MovieEntry.COLUMN_RELEASE_DATE);
        columns.add(MovieEntry.COLUMN_TITLE);
        columns.add(MovieEntry.COLUMN_VIDEO);
        columns.add(MovieEntry.COLUMN_VOTE_AVERAGE);
        columns.add(MovieEntry.COLUMN_VOTE_COUNT);

        int columnIndex = c.getColumnIndex("name");

        do{
            columns.remove(c.getString(columnIndex));
        }while(c.moveToNext());
        c.close();

        assertTrue(columns.isEmpty());

        ContentValues row = TestUtils.getSampleMovieData();

        long rowId = db.insert(MovieEntry.TABLE_NAME,null,row);

        assertTrue(rowId != -1);

        c = db.query(MovieEntry.TABLE_NAME,null,null,null,null,null,null);

        assertTrue(c.moveToFirst());

        TestUtils.validateRecord(c, row);

        assertFalse(c.moveToNext());
        c.close();
        db.close();
    }

    public void testReviewTable(){
        SQLiteDatabase db = (new PopularMoviesDbHelper(mContext)).getWritableDatabase();
        Cursor c = db.rawQuery("PRAGMA table_info("+ ReviewEntry.TABLE_NAME+")",null);
        assertTrue(c.moveToFirst());

        HashSet columns = new HashSet();
        columns.add(ReviewEntry._ID);
        columns.add(ReviewEntry.COLUMN_AUTHOR);
        columns.add(ReviewEntry.COLUMN_CONTENT);
        columns.add(ReviewEntry.COLUMN_MOVIE_ID);
        columns.add(ReviewEntry.COLUMN_URL);
        columns.add(ReviewEntry.COLUMN_TMDB_ID);

        int columnIndex = c.getColumnIndex("name");

        do{
            columns.remove(c.getString(columnIndex));
        }while(c.moveToNext());
        c.close();

        assertTrue(columns.isEmpty());

        ContentValues row = TestUtils.getSampleReviewData();

        long rowId = db.insert(ReviewEntry.TABLE_NAME,null,row);

        assertTrue(rowId != -1);

        c = db.query(ReviewEntry.TABLE_NAME,null,null,null,null,null,null);

        assertTrue(c.moveToFirst());

        TestUtils.validateRecord(c, row);

        assertFalse(c.moveToNext());
        c.close();
        db.close();
    }

    public void testVideoTable(){
        SQLiteDatabase db = (new PopularMoviesDbHelper(mContext)).getWritableDatabase();
        Cursor c = db.rawQuery("PRAGMA table_info("+ VideoEntry.TABLE_NAME+")",null);
        assertTrue(c.moveToFirst());

        HashSet columns = new HashSet();
        columns.add(VideoEntry._ID);


        int columnIndex = c.getColumnIndex("name");

        do{
            columns.remove(c.getString(columnIndex));
        }while(c.moveToNext());
        c.close();

        assertTrue(columns.isEmpty());

        ContentValues row = TestUtils.getSampleVideoData();

        long rowId = db.insert(VideoEntry.TABLE_NAME,null,row);

        assertTrue(rowId != -1);

        c = db.query(VideoEntry.TABLE_NAME,null,null,null,null,null,null);

        assertTrue(c.moveToFirst());

        TestUtils.validateRecord(c, row);

        assertFalse(c.moveToNext());
        c.close();
        db.close();
    }

    public void tesSettingTable(){
        SQLiteDatabase db = (new PopularMoviesDbHelper(mContext)).getWritableDatabase();
        Cursor c = db.rawQuery("PRAGMA table_info("+ SettingEntry.TABLE_NAME+")",null);
        assertTrue(c.moveToFirst());

        HashSet columns = new HashSet();
        columns.add(SettingEntry._ID);


        int columnIndex = c.getColumnIndex("name");

        do{
            columns.remove(c.getString(columnIndex));
        }while(c.moveToNext());
        c.close();

        assertTrue(columns.isEmpty());

        ContentValues row = TestUtils.getSampleSettingData();

        long rowId = db.insert(SettingEntry.TABLE_NAME,null,row);

        assertTrue(rowId != -1);

        c = db.query(SettingEntry.TABLE_NAME,null,null,null,null,null,null);

        assertTrue(c.moveToFirst());

        TestUtils.validateRecord(c, row);

        assertFalse(c.moveToNext());
        c.close();
        db.close();
    }

}