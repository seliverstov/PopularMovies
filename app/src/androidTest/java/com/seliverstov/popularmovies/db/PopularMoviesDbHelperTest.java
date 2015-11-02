package com.seliverstov.popularmovies.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.seliverstov.popularmovies.db.PopularMoviesContact.MovieEntry;
import com.seliverstov.popularmovies.db.PopularMoviesContact.ReviewEntry;
import com.seliverstov.popularmovies.db.PopularMoviesContact.VideoEntry;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        ContentValues row = new ContentValues();
        row.put(MovieEntry._ID, 135397);
        row.put(MovieEntry.COLUMN_ADULT,0);
        row.put(MovieEntry.COLUMN_BACKDROP_PATH,"/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");
        row.put(MovieEntry.COLUMN_GENRE_IDS,"[28,12,878,53]");
        row.put(MovieEntry.COLUMN_ORIGINAL_LANGUAGE,"en");
        row.put(MovieEntry.COLUMN_ORIGINAL_TITLE,"Jurassic World");
        row.put(MovieEntry.COLUMN_OVERVIEW,"Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        row.put(MovieEntry.COLUMN_RELEASE_DATE,"2015-06-12");
        row.put(MovieEntry.COLUMN_POSTER_PATH,"/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg");
        row.put(MovieEntry.COLUMN_POPULARITY, 50.908661);
        row.put(MovieEntry.COLUMN_TITLE, "Jurassic World");
        row.put(MovieEntry.COLUMN_VIDEO, 0);
        row.put(MovieEntry.COLUMN_VOTE_AVERAGE, 6.9);
        row.put(MovieEntry.COLUMN_VOTE_COUNT, 2824);

        long rowId = db.insert(MovieEntry.TABLE_NAME,null,row);

        assertTrue(rowId != -1);

        c = db.query(MovieEntry.TABLE_NAME,null,null,null,null,null,null);

        assertTrue(c.moveToFirst());

        validateRecord(c, row);

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

        ContentValues row = new ContentValues();
        row.put(ReviewEntry.COLUMN_TMDB_ID,"55910381c3a36807f900065d");
        row.put(ReviewEntry.COLUMN_AUTHOR,"jonlikesmoviesthatdontsuck");
        row.put(ReviewEntry.COLUMN_CONTENT,"I was a huge fan of the original 3 movies, they were out when I was younger, and I grew up loving dinosaurs because of them. This movie was awesome, and I think it can stand as a testimonial piece towards the capabilities that Christopher Pratt has. He nailed it. The graphics were awesome, the supporting cast did great and the t rex saved the child in me. 10\\5 stars, four thumbs up, and I hope that star wars episode VII doesn't disappoint,");
        row.put(ReviewEntry.COLUMN_URL,"http://j.mp/1GHgSxi");
        row.put(ReviewEntry.COLUMN_MOVIE_ID,135397);

        long rowId = db.insert(ReviewEntry.TABLE_NAME,null,row);

        assertTrue(rowId != -1);

        c = db.query(ReviewEntry.TABLE_NAME,null,null,null,null,null,null);

        assertTrue(c.moveToFirst());

        validateRecord(c, row);

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

        ContentValues row = new ContentValues();
        row.put(VideoEntry.COLUMN_TMDB_ID,"5576eac192514111e4001b03");
        row.put(VideoEntry.COLUMN_ISO_639_1,"en");
        row.put(VideoEntry.COLUMN_KEY,"lP-sUUUfamw");
        row.put(VideoEntry.COLUMN_NAME,"Official Trailer 3");
        row.put(VideoEntry.COLUMN_SITE,"YouTube");
        row.put(VideoEntry.COLUMN_SIZE,720);
        row.put(VideoEntry.COLUMN_TYPE,"Trailer");
        row.put(VideoEntry.COLUMN_MOVIE_ID,135397);

        long rowId = db.insert(VideoEntry.TABLE_NAME,null,row);

        assertTrue(rowId != -1);

        c = db.query(VideoEntry.TABLE_NAME,null,null,null,null,null,null);

        assertTrue(c.moveToFirst());

        validateRecord(c, row);

        assertFalse(c.moveToNext());
        c.close();
        db.close();
    }

    private void validateRecord(Cursor c, ContentValues row) {
        Set<Map.Entry<String, Object>> vs = row.valueSet();
        for(Map.Entry<String, Object> e:vs){
            int colIndex = c.getColumnIndex(e.getKey());
            int colType = c.getType(colIndex);
            switch (colType){
                case Cursor.FIELD_TYPE_FLOAT:
                    Double expDouble = (Double)e.getValue();
                    Double curDouble = c.getDouble(colIndex);
                    assertEquals(expDouble,curDouble);
                    break;
                default:
                    String expectedValue = String.valueOf(e.getValue());
                    String currentValue = c.getString(colIndex);
                    assertEquals(expectedValue, currentValue);
            }
        }
    }
}