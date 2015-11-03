package com.seliverstov.popularmovies.db;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
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
        Cursor c = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, c.getCount());
        c.close();

        ContentValues values = TestUtils.getSampleMovieData();

        TestUtils.TestContentObserver tco = TestUtils.TestContentObserver.newInstance();
        getContext().getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);

        Uri newItemUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, values);
        assertNotNull(newItemUri);

        tco.waitForNotificationOrFail();
        getContext().getContentResolver().unregisterContentObserver(tco);

        c = mContext.getContentResolver().query(newItemUri,null,null,null,null);
        assertTrue(c.moveToFirst());
        TestUtils.validateRecord(c, values);

        tco = TestUtils.TestContentObserver.newInstance();
        c.registerContentObserver(tco);

        long id = ContentUris.parseId(newItemUri);
        values.put(MovieEntry._ID,id);
        values.put(MovieEntry.COLUMN_TITLE, "UPDATED!");
        int updCnt = mContext.getContentResolver().update(MovieEntry.CONTENT_URI,values,MovieEntry._ID+" = ?", new String[]{String.valueOf(id)});
        assertEquals(1, updCnt);

        tco.waitForNotificationOrFail();
        c.unregisterContentObserver(tco);
        c.close();

        c = mContext.getContentResolver().query(MovieEntry.CONTENT_URI,null, MovieEntry._ID+" = ?",new String[]{String.valueOf(id)},null);
        tco = TestUtils.TestContentObserver.newInstance();
        c.registerContentObserver(tco);

        assertTrue(c.moveToFirst());
        assertEquals(1, c.getCount());
        TestUtils.validateRecord(c, values);

        int delCnt = mContext.getContentResolver().delete(MovieEntry.CONTENT_URI,MovieEntry._ID+" = ?",new String[]{String.valueOf(id)});
        assertEquals(1, delCnt);

        tco.waitForNotificationOrFail();
        c.unregisterContentObserver(tco);
        c.close();
    }

    public void testReviewQuries(){
        Cursor c = mContext.getContentResolver().query(ReviewEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, c.getCount());
        c.close();

        ContentValues values = TestUtils.getSampleReviewData();

        TestUtils.TestContentObserver tco = TestUtils.TestContentObserver.newInstance();
        getContext().getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);

        Uri newItemUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI, values);
        assertNotNull(newItemUri);

        tco.waitForNotificationOrFail();
        getContext().getContentResolver().unregisterContentObserver(tco);

        c = mContext.getContentResolver().query(newItemUri,null,null,null,null);
        assertTrue(c.moveToFirst());
        TestUtils.validateRecord(c, values);

        tco = TestUtils.TestContentObserver.newInstance();
        c.registerContentObserver(tco);

        long id = ContentUris.parseId(newItemUri);
        values.put(ReviewEntry._ID,id);
        values.put(ReviewEntry.COLUMN_CONTENT, "UPDATED!");
        int updCnt = mContext.getContentResolver().update(ReviewEntry.CONTENT_URI,values,ReviewEntry._ID+" = ?", new String[]{String.valueOf(id)});
        assertEquals(1, updCnt);

        tco.waitForNotificationOrFail();
        c.unregisterContentObserver(tco);
        c.close();

        c = mContext.getContentResolver().query(ReviewEntry.CONTENT_URI,null, ReviewEntry._ID+" = ?",new String[]{String.valueOf(id)},null);
        tco = TestUtils.TestContentObserver.newInstance();
        c.registerContentObserver(tco);

        assertTrue(c.moveToFirst());
        assertEquals(1, c.getCount());
        TestUtils.validateRecord(c, values);

        int delCnt = mContext.getContentResolver().delete(ReviewEntry.CONTENT_URI,ReviewEntry._ID+" = ?",new String[]{String.valueOf(id)});
        assertEquals(1, delCnt);

        tco.waitForNotificationOrFail();
        c.unregisterContentObserver(tco);
        c.close();
    }

    public void testVideoQuries(){
        Cursor c = mContext.getContentResolver().query(VideoEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, c.getCount());
        c.close();

        ContentValues values = TestUtils.getSampleVideoData();

        TestUtils.TestContentObserver tco = TestUtils.TestContentObserver.newInstance();
        getContext().getContentResolver().registerContentObserver(VideoEntry.CONTENT_URI, true, tco);

        Uri newItemUri = mContext.getContentResolver().insert(VideoEntry.CONTENT_URI, values);
        assertNotNull(newItemUri);

        tco.waitForNotificationOrFail();
        getContext().getContentResolver().unregisterContentObserver(tco);

        c = mContext.getContentResolver().query(newItemUri,null,null,null,null);
        assertTrue(c.moveToFirst());
        TestUtils.validateRecord(c, values);

        tco = TestUtils.TestContentObserver.newInstance();
        c.registerContentObserver(tco);

        long id = ContentUris.parseId(newItemUri);
        values.put(VideoEntry._ID,id);
        values.put(VideoEntry.COLUMN_NAME, "UPDATED!");
        int updCnt = mContext.getContentResolver().update(VideoEntry.CONTENT_URI,values,VideoEntry._ID+" = ?", new String[]{String.valueOf(id)});
        assertEquals(1, updCnt);

        tco.waitForNotificationOrFail();
        c.unregisterContentObserver(tco);
        c.close();

        c = mContext.getContentResolver().query(VideoEntry.CONTENT_URI,null, VideoEntry._ID+" = ?",new String[]{String.valueOf(id)},null);
        tco = TestUtils.TestContentObserver.newInstance();
        c.registerContentObserver(tco);

        assertTrue(c.moveToFirst());
        assertEquals(1, c.getCount());
        TestUtils.validateRecord(c, values);

        int delCnt = mContext.getContentResolver().delete(VideoEntry.CONTENT_URI,VideoEntry._ID+" = ?",new String[]{String.valueOf(id)});
        assertEquals(1, delCnt);

        tco.waitForNotificationOrFail();
        c.unregisterContentObserver(tco);
        c.close();
    }
}