package com.seliverstov.popularmovies.db;

import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import junit.framework.TestCase;

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

        Uri newItemUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI,TestUtils.getSampleMovieData());
        assertNotNull(newItemUri);

        c = mContext.getContentResolver().query(newItemUri,null,null,null,null);
        assertTrue(c.moveToFirst());
        TestUtils.validateRecord(c, TestUtils.getSampleMovieData());

        int delCnt = mContext.getContentResolver().delete(MovieEntry.CONTENT_URI,null,null);
        assertEquals(1,delCnt);

        c.close();

    }

    public void testReviewQuries(){
        Cursor c = mContext.getContentResolver().query(ReviewEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, c.getCount());
        c.close();

        Uri newItemUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI, TestUtils.getSampleReviewData());
        assertNotNull(newItemUri);

        c = mContext.getContentResolver().query(newItemUri,null,null,null,null);
        assertTrue(c.moveToFirst());
        TestUtils.validateRecord(c,TestUtils.getSampleReviewData());

        int delCnt = mContext.getContentResolver().delete(ReviewEntry.CONTENT_URI,null,null);
        assertEquals(1,delCnt);

        c.close();

    }

    public void testVideoQuries(){
        Cursor c = mContext.getContentResolver().query(VideoEntry.CONTENT_URI, null, null, null, null);
        assertEquals(0, c.getCount());
        c.close();

        Uri newItemUri = mContext.getContentResolver().insert(VideoEntry.CONTENT_URI, TestUtils.getSampleVideoData());
        assertNotNull(newItemUri);

        c = mContext.getContentResolver().query(newItemUri,null,null,null,null);
        assertTrue(c.moveToFirst());
        TestUtils.validateRecord(c,TestUtils.getSampleVideoData());

        int delCnt = mContext.getContentResolver().delete(VideoEntry.CONTENT_URI,null,null);
        assertEquals(1,delCnt);

        c.close();

    }
}