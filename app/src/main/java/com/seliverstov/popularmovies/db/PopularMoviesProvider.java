package com.seliverstov.popularmovies.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.seliverstov.popularmovies.db.PopularMoviesContact.*;

/**
 * Created by a.g.seliverstov on 03.11.2015.
 */
public class PopularMoviesProvider extends ContentProvider {
    private static final String LOG_TAG = PopularMoviesProvider.class.getSimpleName();

    private static final UriMatcher sUriMatcher;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;

    static final int REVIEW = 200;
    static final int REVIEW_WITH_ID = 201;

    static final int VIDEO = 300;
    static final int VIDEO_WITH_ID = 301;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;
        sUriMatcher.addURI(authority, PATH_MOVIE, MOVIE);
        sUriMatcher.addURI(authority, PATH_MOVIE+"/#", MOVIE_WITH_ID);

        sUriMatcher.addURI(authority, PATH_REVIEW, REVIEW);
        sUriMatcher.addURI(authority, PATH_REVIEW+"/#", REVIEW_WITH_ID);

        sUriMatcher.addURI(authority, PATH_VIDEO, VIDEO);
        sUriMatcher.addURI(authority, PATH_VIDEO+"/#", VIDEO_WITH_ID);
    }

    private PopularMoviesDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new PopularMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db =mDbHelper.getReadableDatabase();
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                retCursor = db.query(MovieEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case MOVIE_WITH_ID:{
                String id = String.valueOf(ContentUris.parseId(uri));
                retCursor = db.query(MovieEntry.TABLE_NAME,projection,MovieEntry._ID+" = ?",new String[]{id},null,null,sortOrder);
                break;
            }
            case REVIEW:
            case REVIEW_WITH_ID: {
                retCursor = db.query(ReviewEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            case VIDEO:
            case VIDEO_WITH_ID: {
                retCursor = db.query(VideoEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unsupported uri: "+uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)){
            case MOVIE:
                return MovieEntry.CONTENT_DIR_TYPE;
            case MOVIE_WITH_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEW:
                return ReviewEntry.CONTENT_DIR_TYPE;
            case REVIEW_WITH_ID:
                return ReviewEntry.CONTENT_ITEM_TYPE;
            case VIDEO:
                return VideoEntry.CONTENT_DIR_TYPE;
            case VIDEO_WITH_ID:
                return VideoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unsupported uri: "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case MOVIE: {
                long id = db.insertWithOnConflict(MovieEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_NONE);
                if (id != -1)
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long id = db.insertWithOnConflict(ReviewEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_NONE);
                if (id != -1)
                    returnUri = ContentUris.withAppendedId(ReviewEntry.CONTENT_URI, id);
                else
                    throw new UnsupportedOperationException("Failed to insert row into " + uri);
                break;
            }
            case VIDEO: {
                long id = db.insertWithOnConflict(VideoEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_NONE);
                if (id != -1)
                    returnUri = ContentUris.withAppendedId(VideoEntry.CONTENT_URI, id);
                else
                    throw new UnsupportedOperationException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unsupported uri: "+uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db =mDbHelper.getWritableDatabase();
        int deletedRows;

        switch(sUriMatcher.match(uri)){
            case MOVIE: {
                deletedRows = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                deletedRows = db.delete(ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case VIDEO: {
                deletedRows = db.delete(VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unsupported uri: "+uri);
        }

        if (deletedRows>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return deletedRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int updatedRows;

        switch(sUriMatcher.match(uri)){
            case MOVIE:{
                updatedRows = db.update(MovieEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            case REVIEW:{
                updatedRows = db.update(ReviewEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            case VIDEO:{
                updatedRows = db.update(VideoEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unsupported uri: "+uri);
        }

        if (updatedRows>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return updatedRows;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String table;
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                table = MovieEntry.TABLE_NAME;
                break;
            case REVIEW:
                table = ReviewEntry.TABLE_NAME;
                break;
            case VIDEO:
                table = VideoEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported uri:" + uri);
        }
        int insertedRows = 0;
        int updatedRows = 0;
        db.beginTransaction();
        try {
            for (ContentValues v : values) {
                long id = db.insertWithOnConflict(table, null, v, SQLiteDatabase.CONFLICT_IGNORE);
                if (id != -1) insertedRows++;
                else if (v.containsKey(BaseColumns._ID)) {
                    db.update(table,v, BaseColumns._ID+" = ?",new String[]{String.valueOf(v.get(BaseColumns._ID))});
                    updatedRows++;
                }
            }
            db.setTransactionSuccessful();
            Log.i(LOG_TAG, insertedRows+" rows were inserted, "+ updatedRows + " rows were updated");
        } finally {
            db.endTransaction();
        }
        if (insertedRows > 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return insertedRows;
    }

    @Override
    public void shutdown() {
        mDbHelper.close();
        super.shutdown();
    }
}
