package com.seliverstov.popularmovies.loader;

import android.support.v4.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.seliverstov.popularmovies.db.PopularMoviesContact;
import com.seliverstov.popularmovies.rest.TMDBClient;
import com.seliverstov.popularmovies.rest.model.Video;

import java.util.List;

/**
 * Created by a.g.seliverstov on 18.11.2015.
 */
public class VideosLoader extends AsyncTaskLoader<List<Video>> {
    private static final String LOG_TAG = ReviewsLoader.class.getSimpleName();
    private String mMovieId;
    public VideosLoader(Context context, String movieId) {
        super(context);
        mMovieId = movieId;
    }

    @Override
    public List<Video> loadInBackground() {
        if (!LoaderUtils.isNetworkAvailable(getContext())) return null;
        try{
            List<Video> videos = new TMDBClient().listVideos(mMovieId);
            int insCnt = 0;
            if (videos!=null  && videos.size()>0){
                ContentValues[] cvs = new ContentValues[videos.size()];
                for(int i=0;i<videos.size();i++){
                    ContentValues cv = new ContentValues();
                    Video v = videos.get(i);
                    cv.put(PopularMoviesContact.VideoEntry.COLUMN_NAME,v.getName());
                    cv.put(PopularMoviesContact.VideoEntry.COLUMN_ISO_639_1, v.getIso6391());
                    cv.put(PopularMoviesContact.VideoEntry.COLUMN_SITE,v.getSite());
                    cv.put(PopularMoviesContact.VideoEntry.COLUMN_TYPE,v.getType());
                    cv.put(PopularMoviesContact.VideoEntry.COLUMN_SIZE,v.getSize());
                    cv.put(PopularMoviesContact.VideoEntry.COLUMN_KEY,v.getKey());
                    cv.put(PopularMoviesContact.VideoEntry.COLUMN_MOVIE_ID,mMovieId);
                    cv.put(PopularMoviesContact.VideoEntry.COLUMN_TMDB_ID,v.getId());

                    cvs[i]=cv;
                }
                insCnt = getContext().getContentResolver().bulkInsert(PopularMoviesContact.VideoEntry.CONTENT_URI,cvs);
            }
            Log.i(LOG_TAG, insCnt + " videos for movie "+mMovieId+" was loaded to database");
            return videos;
        }catch(Exception e){
            Log.e(LOG_TAG,e.getMessage(),e);
            return null;
        }
    }
}
