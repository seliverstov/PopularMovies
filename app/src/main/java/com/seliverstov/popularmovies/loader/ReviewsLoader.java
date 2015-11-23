package com.seliverstov.popularmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.seliverstov.popularmovies.db.PopularMoviesContact;
import com.seliverstov.popularmovies.rest.TMDBClient;
import com.seliverstov.popularmovies.rest.model.Review;

import java.util.List;

/**
 * Created by a.g.seliverstov on 18.11.2015.
 */
public class ReviewsLoader extends AsyncTaskLoader<List<Review>> {
    private static final String LOG_TAG = ReviewsLoader.class.getSimpleName();
    private String mMovieId;
    public ReviewsLoader(Context context, String movieId) {
        super(context);
        mMovieId = movieId;
    }

    @Override
    public List<Review> loadInBackground() {
        try{
            List<Review> reviews = new TMDBClient().listReviews(mMovieId);
            int insCnt = 0;
            if (reviews!=null && reviews.size()>0){
                ContentValues[] cvs = new ContentValues[reviews.size()];
                for(int i=0;i<reviews.size();i++){
                    ContentValues cv = new ContentValues();
                    Review r = reviews.get(i);
                    cv.put(PopularMoviesContact.ReviewEntry.COLUMN_AUTHOR,r.getAuthor());
                    cv.put(PopularMoviesContact.ReviewEntry.COLUMN_CONTENT, r.getContent());
                    cv.put(PopularMoviesContact.ReviewEntry.COLUMN_TMDB_ID, r.getId());
                    cv.put(PopularMoviesContact.ReviewEntry.COLUMN_MOVIE_ID, mMovieId);
                    cv.put(PopularMoviesContact.ReviewEntry.COLUMN_URL, r.getUrl());
                    cvs[i]=cv;
                }
                insCnt = getContext().getContentResolver().bulkInsert(PopularMoviesContact.ReviewEntry.CONTENT_URI,cvs);
            }
            Log.i(LOG_TAG, insCnt + " reviews for movie "+mMovieId+" was loaded to database");
            return reviews;
        }catch(Exception e){
            Log.e(LOG_TAG,e.getMessage(),e);
            return null;
        }
    }
}
