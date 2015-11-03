package com.seliverstov.popularmovies.db;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by a.g.seliverstov on 03.11.2015.
 */
public class TestUtils {
    public static ContentValues getSampleMovieData(){
        ContentValues row = new ContentValues();
        row.put(PopularMoviesContact.MovieEntry._ID, 135397);
        row.put(PopularMoviesContact.MovieEntry.COLUMN_ADULT,0);
        row.put(PopularMoviesContact.MovieEntry.COLUMN_BACKDROP_PATH,"/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");
        row.put(PopularMoviesContact.MovieEntry.COLUMN_GENRE_IDS,"[28,12,878,53]");
        row.put(PopularMoviesContact.MovieEntry.COLUMN_ORIGINAL_LANGUAGE,"en");
        row.put(PopularMoviesContact.MovieEntry.COLUMN_ORIGINAL_TITLE,"Jurassic World");
        row.put(PopularMoviesContact.MovieEntry.COLUMN_OVERVIEW,"Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        row.put(PopularMoviesContact.MovieEntry.COLUMN_RELEASE_DATE,"2015-06-12");
        row.put(PopularMoviesContact.MovieEntry.COLUMN_POSTER_PATH,"/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg");
        row.put(PopularMoviesContact.MovieEntry.COLUMN_POPULARITY, 50.908661);
        row.put(PopularMoviesContact.MovieEntry.COLUMN_TITLE, "Jurassic World");
        row.put(PopularMoviesContact.MovieEntry.COLUMN_VIDEO, 0);
        row.put(PopularMoviesContact.MovieEntry.COLUMN_VOTE_AVERAGE, 6.9);
        row.put(PopularMoviesContact.MovieEntry.COLUMN_VOTE_COUNT, 2824);
        return row;
    }

    public static ContentValues getSampleReviewData(){
        ContentValues row = new ContentValues();
        row.put(PopularMoviesContact.ReviewEntry.COLUMN_TMDB_ID,"55910381c3a36807f900065d");
        row.put(PopularMoviesContact.ReviewEntry.COLUMN_AUTHOR,"jonlikesmoviesthatdontsuck");
        row.put(PopularMoviesContact.ReviewEntry.COLUMN_CONTENT,"I was a huge fan of the original 3 movies, they were out when I was younger, and I grew up loving dinosaurs because of them. This movie was awesome, and I think it can stand as a testimonial piece towards the capabilities that Christopher Pratt has. He nailed it. The graphics were awesome, the supporting cast did great and the t rex saved the child in me. 10\\5 stars, four thumbs up, and I hope that star wars episode VII doesn't disappoint,");
        row.put(PopularMoviesContact.ReviewEntry.COLUMN_URL,"http://j.mp/1GHgSxi");
        row.put(PopularMoviesContact.ReviewEntry.COLUMN_MOVIE_ID,135397);
        return row;
    }

    public static ContentValues getSampleVideoData(){
        ContentValues row = new ContentValues();
        row.put(PopularMoviesContact.VideoEntry.COLUMN_TMDB_ID,"5576eac192514111e4001b03");
        row.put(PopularMoviesContact.VideoEntry.COLUMN_ISO_639_1,"en");
        row.put(PopularMoviesContact.VideoEntry.COLUMN_KEY,"lP-sUUUfamw");
        row.put(PopularMoviesContact.VideoEntry.COLUMN_NAME,"Official Trailer 3");
        row.put(PopularMoviesContact.VideoEntry.COLUMN_SITE,"YouTube");
        row.put(PopularMoviesContact.VideoEntry.COLUMN_SIZE,720);
        row.put(PopularMoviesContact.VideoEntry.COLUMN_TYPE,"Trailer");
        row.put(PopularMoviesContact.VideoEntry.COLUMN_MOVIE_ID,135397);
        return row;
    }

    public static void validateRecord(Cursor c, ContentValues row) {
        Set<Map.Entry<String, Object>> vs = row.valueSet();
        for(Map.Entry<String, Object> e:vs){
            int colIndex = c.getColumnIndex(e.getKey());
            assertTrue(colIndex >= 0);
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

    static class TestContentObserver extends ContentObserver{
        final HandlerThread mHandlerThread;
        boolean mContentChanged;

        public static TestContentObserver newInstance(){
            HandlerThread ht = new HandlerThread("TestContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHandlerThread = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange,null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHandlerThread.quit();
        }
    }

}
