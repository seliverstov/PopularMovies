package com.seliverstov.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.seliverstov.popularmovies.loader.ReviewsLoader;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import static com.seliverstov.popularmovies.db.PopularMoviesContact.*;

/**
 * Created by a.g.seliverstov on 15.10.2015.
 */
public class MovieDetailsFragment extends Fragment {
    public static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    public static final String MOVIE_DETAILS_URI = "MOVIE_DETAILS_URI";


    private static String[] MOVIE_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_FAVORITE
    };

    private static String[] REVIEW_COLUMNS = {
            ReviewEntry._ID,
            ReviewEntry.COLUMN_AUTHOR,
            ReviewEntry.COLUMN_CONTENT,
            ReviewEntry.COLUMN_URL
    };

    private static final int CURSOR_MOVIE_DETAILS_LOADER = 2;
    private static final int CURSOR_MOVIE_REVIEWS_LOADER = 3;

    private static final int TMDB_MOVIE_REVIEWS_LOADER = 4;
    private static final int TMDB_MOVIE_VIDEOS_LOADER = 5;

    private static int IDX_ID = 0;
    private static int IDX_ORIGINAL_TITLE = 1;
    private static int IDX_RELEASE_DATE = 2;
    private static int IDX_VOTE_AVERAGE = 3;
    private static int IDX_OVERVIEW = 4;
    private static int IDX_POSTER_PATH = 5;
    private static int IDX_FAVORITE = 6;

    private static int IDX_REVIEW_AUTHOR = 1;
    private static int IDX_REVIEW_CONTENT = 2;
    private static int IDX_REVIEW_URL = 3;

    private Uri mUri;
    private TextView mTitle;
    private TextView mYear;
    private TextView mRating;
    private TextView mOverview;
    private ImageView mPoster;
    private Button mFavorite;
    private ListView mReviews;

    public Uri getMovieUri(){
        return mUri;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments  = getArguments();
        if (arguments!=null){
            mUri = arguments.getParcelable(MOVIE_DETAILS_URI);
        }

        if (mUri==null) return null;

        final View view = inflater.inflate(R.layout.movie_details, container, false);

        mTitle = (TextView)view.findViewById(R.id.movie_title);
        mYear = (TextView)view.findViewById(R.id.movie_year);
        mRating = (TextView)view.findViewById(R.id.movie_rating);
        mOverview = (TextView)view.findViewById(R.id.movie_overview);
        mPoster = (ImageView)view.findViewById(R.id.movie_poster);
        mFavorite = (Button)view.findViewById(R.id.favorite);


        final View rootView = inflater.inflate(R.layout.fragment_details,container,false);
        mReviews = (ListView)rootView.findViewById(R.id.movie_reviews);
        mReviews.setAdapter(new ReviewAdapter(getActivity(),null,0));
        mReviews.addHeaderView(view);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CURSOR_MOVIE_DETAILS_LOADER,null,new CursorDetailsCallback()).forceLoad();
        getLoaderManager().initLoader(CURSOR_MOVIE_REVIEWS_LOADER, null, new CursorReviewsCallback()).forceLoad();
        getLoaderManager().initLoader(TMDB_MOVIE_REVIEWS_LOADER,null, new TMDBReviewsCallback()).forceLoad();
    }

    public void onSortOrderChange(){

    }

    class CursorDetailsCallback implements LoaderManager.LoaderCallbacks<Cursor>{
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mUri!=null)
                return new CursorLoader(getActivity(),mUri, MOVIE_COLUMNS,null,null,null);
            else
                return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
            if (c.moveToFirst()){
                String originalTitle = c.getString(IDX_ORIGINAL_TITLE);
                mTitle.setText(originalTitle);

                String rd = c.getString(IDX_RELEASE_DATE);
                rd = (rd !=null && rd.length()>=4)? rd.substring(0,4): "";

                mYear.setText(rd);

                mRating.setText(new DecimalFormat("#.#").format(c.getDouble(IDX_VOTE_AVERAGE)) + "/10");

                mOverview.setText(c.getString(IDX_OVERVIEW));

                String posterPath = c.getString(IDX_POSTER_PATH);
                if (posterPath!=null) {
                    Uri url = Uri.parse(getString(R.string.movie_poster_base_url))
                            .buildUpon()
                            .appendPath(getString(R.string.big_movie_poster_size))
                            .appendEncodedPath(posterPath)
                            .build();
                    Log.i(LOG_TAG, "Get poster for movie " + originalTitle + ": " + url.toString());
                    Picasso.with(getActivity()).load(url).placeholder(R.drawable.loading_big).into(mPoster);
                }else{
                    mPoster.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    mPoster.setImageResource(R.drawable.noposter);
                }


                final int f = c.getInt(IDX_FAVORITE);
                if (f == 0){
                    mFavorite.setText("Add to Favorite");
                }else{
                    mFavorite.setText("Remove from Favorite");
                }
                final int id = c.getInt(IDX_ID);
                mFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues cv = new ContentValues();
                        if (f == 0) {
                            cv.put(MovieEntry.COLUMN_FAVORITE, 1);
                            mFavorite.setText("Remove from Favorite");
                        } else {
                            cv.put(MovieEntry.COLUMN_FAVORITE, (Integer) null);
                            mFavorite.setText("Add to Favorite");
                        }
                        long u = getActivity().getContentResolver().update(MovieEntry.CONTENT_URI, cv, MovieEntry._ID + " = ?", new String[]{String.valueOf(id)});
                        Log.i(LOG_TAG, "Movie " + id + " was updated: " + u);
                    }
                });
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    class TMDBReviewsCallback implements LoaderManager.LoaderCallbacks<Void>{

        @Override
        public Loader<Void> onCreateLoader(int id, Bundle args) {
            if (mUri!=null) {
                String movieId = String.valueOf(ContentUris.parseId(mUri));
                return new ReviewsLoader(getActivity(), movieId);
            }else
                return null;
        }

        @Override
        public void onLoadFinished(Loader<Void> loader, Void data) {

        }

        @Override
        public void onLoaderReset(Loader<Void> loader) {

        }
    }

    private class CursorReviewsCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mUri!=null) {
                long movieId = ContentUris.parseId(mUri);
                return new CursorLoader(getActivity(), ReviewEntry.CONTENT_URI, REVIEW_COLUMNS, ReviewEntry.COLUMN_MOVIE_ID+" = ?", new String[]{String.valueOf(movieId)}, null);
            }else
                return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.i(LOG_TAG,"Reviews cursor size is: "+data.getCount());
            ((CursorAdapter)((HeaderViewListAdapter)mReviews.getAdapter()).getWrappedAdapter()).swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            ((CursorAdapter)((HeaderViewListAdapter)mReviews.getAdapter()).getWrappedAdapter()).swapCursor(null);
        }
    }

    private class ReviewAdapter extends CursorAdapter {
        private class ViewHolder{
            public TextView mAuthor;
            public TextView mContent;
            public TextView mUrl;

            public ViewHolder(View view){
                mAuthor = (TextView)view.findViewById(R.id.review_item_author);
                mContent = (TextView)view.findViewById(R.id.review_item_content);
                mUrl = (TextView)view.findViewById(R.id.review_item_url);
            }
        }
        public ReviewAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final View view = LayoutInflater.from(context).inflate(R.layout.review_item,parent,false);
            ViewHolder vh = new ViewHolder(view);
            view.setTag(vh);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder vh = (ViewHolder)view.getTag();
            vh.mAuthor.setText(cursor.getString(IDX_REVIEW_AUTHOR));
            vh.mContent.setText(cursor.getString(IDX_REVIEW_CONTENT));
            vh.mUrl.setText(cursor.getString(IDX_REVIEW_URL));
        }
    }
}
