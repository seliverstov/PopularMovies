package com.seliverstov.popularmovies;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seliverstov.popularmovies.loader.ReviewsLoader;
import com.seliverstov.popularmovies.loader.VideosLoader;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import static com.seliverstov.popularmovies.db.PopularMoviesContact.*;

/**
 * Created by a.g.seliverstov on 15.10.2015.
 */
public class MovieDetailsFragment extends Fragment {
    public static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    public static final String MOVIE_DETAILS_URI = "MOVIE_DETAILS_URI";
    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    public static final String YOUTUBE = "youtube";


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

    private static String[] VIDEO_COLUMNS = {
            VideoEntry._ID,
            VideoEntry.COLUMN_NAME,
            VideoEntry.COLUMN_SITE,
            VideoEntry.COLUMN_KEY
    };

    private static final int CURSOR_MOVIE_DETAILS_LOADER = 2;
    private static final int CURSOR_MOVIE_REVIEWS_LOADER = 3;
    private static final int CURSOR_MOVIE_VIDEOS_LOADER = 4;

    private static final int TMDB_MOVIE_REVIEWS_LOADER = 5;
    private static final int TMDB_MOVIE_VIDEOS_LOADER = 6;

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

    private static int IDX_VIDEO_NAME = 1;
    private static int IDX_VIDEO_SITE = 2;
    private static int IDX_VIDEO_KEY = 3;

    private Uri mUri;
    private TextView mTitle;
    private TextView mYear;
    private TextView mRating;
    private TextView mOverview;
    private ImageView mPoster;
    private Button mFavorite;
    private LinearLayout mReviews;
    private LinearLayout mVideos;

    private String mTrailerLink;

    public Uri getMovieUri(){
        return mUri;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments  = getArguments();
        if (arguments!=null){
            mUri = arguments.getParcelable(MOVIE_DETAILS_URI);
        }

        if (mUri==null) return null;

        final View view = inflater.inflate(R.layout.fragment_details, container, false);

        mTitle = (TextView)view.findViewById(R.id.movie_title);
        mYear = (TextView)view.findViewById(R.id.movie_year);
        mRating = (TextView)view.findViewById(R.id.movie_rating);
        mOverview = (TextView)view.findViewById(R.id.movie_overview);
        mPoster = (ImageView)view.findViewById(R.id.movie_poster);
        mFavorite = (Button)view.findViewById(R.id.favorite);

        mReviews = (LinearLayout)view.findViewById(R.id.movie_reviews);
        mVideos = (LinearLayout)view.findViewById(R.id.movie_videos);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CURSOR_MOVIE_DETAILS_LOADER, null, new CursorDetailsCallback());

        getLoaderManager().initLoader(CURSOR_MOVIE_REVIEWS_LOADER, null, new CursorReviewsCallback()).forceLoad();
        getLoaderManager().initLoader(TMDB_MOVIE_REVIEWS_LOADER, null, new TMDBReviewsCallback()).forceLoad();

        getLoaderManager().initLoader(CURSOR_MOVIE_VIDEOS_LOADER, null, new CursorVideosCallback()).forceLoad();
        getLoaderManager().initLoader(TMDB_MOVIE_VIDEOS_LOADER, null, new TMDBVideosCallback()).forceLoad();
    }

    public void onSortOrderChange(){

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.details_menu,menu);
        MenuItem shareItem =  menu.findItem(R.id.action_share);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,mTrailerLink);
        ((ShareActionProvider) MenuItemCompat.getActionProvider(shareItem)).setShareIntent(Intent.createChooser(intent,getString(R.string.share_trailer)));
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
        public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
            if (c.moveToFirst()) {
                do {
                    View r = getActivity().getLayoutInflater().inflate(R.layout.review_item, null);
                    ((TextView) r.findViewById(R.id.review_item_content)).setText(c.getString(IDX_REVIEW_CONTENT));
                    ((TextView) r.findViewById(R.id.review_item_author)).setText(c.getString(IDX_REVIEW_AUTHOR));
                    ((TextView) r.findViewById(R.id.review_item_url)).setText(c.getString(IDX_REVIEW_URL));
                    mReviews.addView(r);
                } while (c.moveToNext());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mReviews.removeAllViews();
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

    class TMDBVideosCallback implements LoaderManager.LoaderCallbacks<Void>{

        @Override
        public Loader<Void> onCreateLoader(int id, Bundle args) {
            if (mUri!=null) {
                String movieId = String.valueOf(ContentUris.parseId(mUri));
                return new VideosLoader(getActivity(), movieId);
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

    private class CursorVideosCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mUri!=null) {
                long movieId = ContentUris.parseId(mUri);
                return new CursorLoader(getActivity(), VideoEntry.CONTENT_URI, VIDEO_COLUMNS, VideoEntry.COLUMN_MOVIE_ID+" = ?", new String[]{String.valueOf(movieId)}, null);
            }else
                return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
            if (c.moveToFirst()) {
                do {
                    View v = getActivity().getLayoutInflater().inflate(R.layout.video_item, null);
                    TextView video = (TextView) v.findViewById(R.id.video_item_name);
                    video.setText(c.getString(IDX_VIDEO_NAME));
                    final String key = c.getString(IDX_VIDEO_KEY);
                    final String site = c.getString(IDX_VIDEO_SITE);
                    if (mTrailerLink==null){
                        mTrailerLink=YOUTUBE_BASE_URL + key;
                    }
                    video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (YOUTUBE.equalsIgnoreCase(site))
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + key)));
                            else
                                Toast.makeText(getActivity(), "Sorry :( I cant' play video from " + site + ". Only youtube is supported now.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mVideos.addView(v);
                } while (c.moveToNext());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mVideos.removeAllViews();
        }
    }

    private class VideoAdapter extends CursorAdapter {
        private class ViewHolder{
            public TextView mName;


            public ViewHolder(View view){
                mName = (TextView)view.findViewById(R.id.video_item_name);
            }
        }
        public VideoAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            final View view = LayoutInflater.from(context).inflate(R.layout.video_item,parent,false);
            ViewHolder vh = new ViewHolder(view);
            view.setTag(vh);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder vh = (ViewHolder)view.getTag();
            vh.mName.setText(cursor.getString(IDX_VIDEO_NAME));
        }
    }
}
