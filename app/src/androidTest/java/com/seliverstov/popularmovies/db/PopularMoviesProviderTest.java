package com.seliverstov.popularmovies.db;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.test.AndroidTestCase;

import static com.seliverstov.popularmovies.db.PopularMoviesContact.*;

/**
 * Created by a.g.seliverstov on 03.11.2015.
 */
public class PopularMoviesProviderTest extends AndroidTestCase {
    public void setUp() throws Exception {
        super.setUp();
        mContext.getContentResolver().delete(ReviewEntry.CONTENT_URI,null,null);
        mContext.getContentResolver().delete(VideoEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(MovieEntry.CONTENT_URI,null,null);

        Cursor c = mContext.getContentResolver().query(ReviewEntry.CONTENT_URI,null,null,null,null);
        assertEquals(0, c.getCount());
        c.close();

        c = mContext.getContentResolver().query(VideoEntry.CONTENT_URI,null,null,null,null);
        assertEquals(0,c.getCount());
        c.close();

        c = mContext.getContentResolver().query(MovieEntry.CONTENT_URI,null,null,null,null);
        assertEquals(0,c.getCount());
        c.close();
    }

    public void testProviderRegistry(){
        PackageManager pm = mContext.getPackageManager();
        ComponentName cName = new ComponentName(mContext.getPackageName(),PopularMoviesProvider.class.getName());
        try{
            ProviderInfo pi = pm.getProviderInfo(cName,0);
            assertEquals(CONTENT_AUTHORITY,pi.authority);
        }catch(PackageManager.NameNotFoundException ex){
            assertTrue(false);
        }
    }

    public void testGetType(){
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        assertEquals(MovieEntry.CONTENT_DIR_TYPE,type);
        type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI.buildUpon().appendPath("145").build());
        assertEquals(MovieEntry.CONTENT_ITEM_TYPE,type);

        type = mContext.getContentResolver().getType(ReviewEntry.CONTENT_URI);
        assertEquals(ReviewEntry.CONTENT_DIR_TYPE,type);
        type = mContext.getContentResolver().getType(ReviewEntry.CONTENT_URI.buildUpon().appendPath("145").build());
        assertEquals(ReviewEntry.CONTENT_ITEM_TYPE,type);

        type = mContext.getContentResolver().getType(VideoEntry.CONTENT_URI);
        assertEquals(VideoEntry.CONTENT_DIR_TYPE,type);
        type = mContext.getContentResolver().getType(VideoEntry.CONTENT_URI.buildUpon().appendPath("145").build());
        assertEquals(VideoEntry.CONTENT_ITEM_TYPE,type);
    }

    public void testMovieQuries(){
        testBasicQueries(MovieEntry.CONTENT_URI,TestUtils.getSampleMovieData(), MovieEntry.COLUMN_TITLE);
        testBulkInsert(MovieEntry.CONTENT_URI, TestUtils.getSampleMovies(100));

    }

    public void testReviewQuries(){
        testBasicQueries(ReviewEntry.CONTENT_URI,TestUtils.getSampleReviewData(), ReviewEntry.COLUMN_CONTENT);
        testBulkInsert(ReviewEntry.CONTENT_URI,TestUtils.getSampleReviews(100));
    }

    public void testVideoQuries(){
        testBasicQueries(VideoEntry.CONTENT_URI,TestUtils.getSampleVideoData(), VideoEntry.COLUMN_NAME);
        testBulkInsert(VideoEntry.CONTENT_URI,TestUtils.getSampleVideos(100));
    }

    protected void testBulkInsert(Uri contentUri, ContentValues[] values){
        TestUtils.TestContentObserver tco = TestUtils.TestContentObserver.newInstance();
        mContext.getContentResolver().registerContentObserver(contentUri, true, tco);
        int insCnt = mContext.getContentResolver().bulkInsert(contentUri, values);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);
        assertEquals(values.length, insCnt);

        Cursor c = mContext.getContentResolver().query(contentUri, null, null, null, null);
        assertEquals(values.length, c.getCount());
        c.moveToFirst();
        for(int i=0;i<values.length;i++){
            TestUtils.validateRecord(c,values[i]);
            c.moveToNext();
        }
        c.close();
    }

    protected void testBasicQueries(Uri contentUri, ContentValues values, String updatedColumn){
        Cursor c = mContext.getContentResolver().query(contentUri, null, null, null, null);
        assertEquals(0, c.getCount());
        c.close();

        TestUtils.TestContentObserver tco = TestUtils.TestContentObserver.newInstance();
        getContext().getContentResolver().registerContentObserver(contentUri, true, tco);

        Uri newItemUri = mContext.getContentResolver().insert(contentUri, values);
        assertNotNull(newItemUri);

        tco.waitForNotificationOrFail();
        getContext().getContentResolver().unregisterContentObserver(tco);

        c = mContext.getContentResolver().query(newItemUri,null,null,null,null);
        assertTrue(c.moveToFirst());
        TestUtils.validateRecord(c, values);

        tco = TestUtils.TestContentObserver.newInstance();
        c.registerContentObserver(tco);

        long id = ContentUris.parseId(newItemUri);
        values.put(BaseColumns._ID,id);
        values.put(updatedColumn, "UPDATED!");
        int updCnt = mContext.getContentResolver().update(contentUri,values,BaseColumns._ID+" = ?", new String[]{String.valueOf(id)});
        assertEquals(1, updCnt);

        tco.waitForNotificationOrFail();
        c.unregisterContentObserver(tco);
        c.close();

        c = mContext.getContentResolver().query(contentUri, null, BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null);
        tco = TestUtils.TestContentObserver.newInstance();
        c.registerContentObserver(tco);

        assertTrue(c.moveToFirst());
        assertEquals(1, c.getCount());
        TestUtils.validateRecord(c, values);

        int delCnt = mContext.getContentResolver().delete(contentUri,BaseColumns._ID+" = ?",new String[]{String.valueOf(id)});
        assertEquals(1, delCnt);

        tco.waitForNotificationOrFail();
        c.unregisterContentObserver(tco);
        c.close();
    }
}