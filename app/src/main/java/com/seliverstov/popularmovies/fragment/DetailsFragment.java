package com.seliverstov.popularmovies.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seliverstov.popularmovies.R;
import com.seliverstov.popularmovies.activity.DetailsActivity;
import com.seliverstov.popularmovies.loader.ReviewsLoader;
import com.seliverstov.popularmovies.loader.VideosLoader;
import com.seliverstov.popularmovies.model.SettingsManager;
import com.seliverstov.popularmovies.rest.model.Review;
import com.seliverstov.popularmovies.rest.model.Video;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.seliverstov.popularmovies.db.PopularMoviesContact.*;

/**
 * Created by a.g.seliverstov on 15.10.2015.
 */
public class DetailsFragment extends Fragment {
    public static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    public static final String MOVIE_DETAILS_URI = "MOVIE_DETAILS_URI";
    public static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    public static final String YOUTUBE_IMAGE_URL = "http://img.youtube.com/vi/%s/default.jpg";

    public static final String YOUTUBE = "youtube";


    private static String[] MOVIE_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_FAVORITE,
            MovieEntry.COLUMN_BACKDROP_PATH
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
    private static int IDX_BACKDROP_PATH = 7;

    private static int IDX_REVIEW_AUTHOR = 1;
    private static int IDX_REVIEW_CONTENT = 2;
    private static int IDX_REVIEW_URL = 3;

    private static int IDX_VIDEO_NAME = 1;
    private static int IDX_VIDEO_SITE = 2;
    private static int IDX_VIDEO_KEY = 3;

    private Uri mUri;
    @Bind(R.id.movie_title) TextView mTitle;
    @Bind(R.id.movie_year) TextView mYear;
    @Bind(R.id.movie_rating) TextView mRating;
    @Bind(R.id.movie_overview) TextView mOverview;
    @Bind(R.id.movie_poster) ImageView mPoster;
    @Bind(R.id.favorite) FloatingActionButton mFavorite;
    @Bind(R.id.movie_reviews) LinearLayout mReviews;
    @Bind(R.id.movie_videos) LinearLayout mVideos;
    @Bind(R.id.movie_videos_progressbar) ProgressBar mVideosProgressBar;
    @Bind(R.id.movie_reviews_progressbar) ProgressBar mReviewsProgressBar;
    @Bind(R.id.movie_has_no_videos) TextView mNoVideos;
    @Bind(R.id.movie_has_no_reviews) TextView mNoReviews;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbar;

    private MenuItem mShareItem;
    private String mVideoUrl;
    private final String INTENT_TYPE = "text/plain";

    public Uri getMovieUri() {
        return mUri;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MOVIE_DETAILS_URI);
        }
        View view;
        if (mUri == null) {
            view = inflater.inflate(R.layout.no_movie,container,false);
        }else {
            view = inflater.inflate(R.layout.fragment_details, container, false);
            ButterKnife.bind(this, view);
            if (getActivity() instanceof DetailsActivity) {
                ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CURSOR_MOVIE_DETAILS_LOADER, null, new CursorDetailsCallback());

        getLoaderManager().initLoader(CURSOR_MOVIE_REVIEWS_LOADER, null, new CursorReviewsCallback());
        getLoaderManager().initLoader(TMDB_MOVIE_REVIEWS_LOADER, null, new TMDBReviewsCallback());

        getLoaderManager().initLoader(CURSOR_MOVIE_VIDEOS_LOADER, null, new CursorVideosCallback());
        getLoaderManager().initLoader(TMDB_MOVIE_VIDEOS_LOADER, null, new TMDBVideosCallback());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.details_menu, menu);
        mShareItem = menu.findItem(R.id.action_share);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(INTENT_TYPE);
        intent.putExtra(Intent.EXTRA_TEXT, mVideoUrl==null?getActivity().getString(R.string.no_videos):mVideoUrl);
        ((ShareActionProvider) MenuItemCompat.getActionProvider(mShareItem)).setShareIntent(intent);
    }

    class CursorDetailsCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mUri != null)
                return new CursorLoader(getActivity(), mUri, MOVIE_COLUMNS, null, null, null);
            else
                return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
            if (c.moveToFirst()) {
                String originalTitle = c.getString(IDX_ORIGINAL_TITLE);

                mCollapsingToolbar.setTitle(originalTitle);
                String rd = c.getString(IDX_RELEASE_DATE);
                /*rd = (rd != null && rd.length() >= 4) ? rd.substring(0, 4) : "";*/

                mTitle.setText(originalTitle);
                mYear.setText("Release date: "+rd);

                mRating.setText("Rating: "+new DecimalFormat("#.#").format(c.getDouble(IDX_VOTE_AVERAGE)) + "/10");

                String overview = c.getString(IDX_OVERVIEW);
                mOverview.setText(overview==null?getString(R.string.no_overview):overview);

                String backdropPath = c.getString(IDX_BACKDROP_PATH);
                String posterPath = c.getString(IDX_POSTER_PATH);
                if (backdropPath != null || posterPath!=null) {
                    Uri url = Uri.parse(getString(R.string.movie_poster_base_url))
                            .buildUpon()
                            .appendPath(getString(R.string.backdrop_size))
                            .appendEncodedPath(backdropPath==null?posterPath:backdropPath)
                            .build();
                    Log.i(LOG_TAG, "Get poster for movie " + originalTitle + ": " + url.toString());
                    Picasso.with(getActivity()).load(url).error(R.drawable.no_poster).into(mPoster);
                } else {
                    mPoster.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    mPoster.setImageResource(R.drawable.no_poster);
                }


                final int f = c.getInt(IDX_FAVORITE);

                if (f == 0) {
                    mFavorite.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.btn_star_big_off));
                } else {
                    mFavorite.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.btn_star_big_on));
                }

                final int id = c.getInt(IDX_ID);
                mFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues cv = new ContentValues();
                        if (f == 0) {
                            cv.put(MovieEntry.COLUMN_FAVORITE, 1);
                            mFavorite.setImageDrawable(ContextCompat.getDrawable(getActivity(),android.R.drawable.btn_star_big_on));
                        } else {
                            cv.put(MovieEntry.COLUMN_FAVORITE, (Integer) null);
                            mFavorite.setImageDrawable(ContextCompat.getDrawable(getActivity(),android.R.drawable.btn_star_big_off));
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

    class TMDBReviewsCallback implements LoaderManager.LoaderCallbacks<List<Review>> {

        @Override
        public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
            if (mUri != null) {
                String movieId = String.valueOf(ContentUris.parseId(mUri));
                return new ReviewsLoader(getActivity(), movieId);
            } else
                return null;
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, final List<Review> data) {
            mReviewsProgressBar.setVisibility(View.GONE);
            if (data==null){
                Toast.makeText(getActivity(), getString(R.string.cant_load_reviews), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {

        }
    }

    private class CursorReviewsCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mUri != null) {
                long movieId = ContentUris.parseId(mUri);
                return new CursorLoader(getActivity(), ReviewEntry.CONTENT_URI, REVIEW_COLUMNS, ReviewEntry.COLUMN_MOVIE_ID + " = ?", new String[]{String.valueOf(movieId)}, null);
            } else
                return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
            if (c.moveToFirst()) {
                mNoReviews.setVisibility(View.GONE);
                mReviews.removeAllViews();
                do {
                    View r = getActivity().getLayoutInflater().inflate(R.layout.review_item, null);
                    ((TextView) r.findViewById(R.id.review_item_content)).setText(c.getString(IDX_REVIEW_CONTENT));
                    ((TextView) r.findViewById(R.id.review_item_author)).setText(c.getString(IDX_REVIEW_AUTHOR));
                    mReviews.addView(r);
                } while (c.moveToNext());
            }else{
                mNoReviews.setVisibility(View.VISIBLE);
                if (!new SettingsManager(getActivity()).isFavoriteSortOrder()) {
                    mReviewsProgressBar.setVisibility(View.VISIBLE);
                    getLoaderManager().getLoader(TMDB_MOVIE_REVIEWS_LOADER).forceLoad();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mReviews.removeAllViews();
        }
    }

    class TMDBVideosCallback implements LoaderManager.LoaderCallbacks<List<Video>> {

        @Override
        public Loader<List<Video>> onCreateLoader(int id, Bundle args) {
            if (mUri != null) {
                String movieId = String.valueOf(ContentUris.parseId(mUri));
                return new VideosLoader(getActivity(), movieId);
            } else
                return null;
        }

        @Override
        public void onLoadFinished(Loader<List<Video>> loader,final List<Video> data) {
            mVideosProgressBar.setVisibility(View.GONE);
            if (data==null){
                Toast.makeText(getActivity(), getString(R.string.cant_load_trailers), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Video>> loader) {

        }
    }

    private class CursorVideosCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (mUri != null) {
                long movieId = ContentUris.parseId(mUri);
                return new CursorLoader(getActivity(), VideoEntry.CONTENT_URI, VIDEO_COLUMNS, VideoEntry.COLUMN_MOVIE_ID + " = ?", new String[]{String.valueOf(movieId)}, null);
            } else
                return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
            if (c.moveToFirst()) {
                mNoVideos.setVisibility(View.GONE);
                mVideos.removeAllViews();
                do {
                    View v = getActivity().getLayoutInflater().inflate(R.layout.video_item, null);
                    ImageView video = (ImageView) v.findViewById(R.id.video_item_image);
                    final String key = c.getString(IDX_VIDEO_KEY);
                    final String site = c.getString(IDX_VIDEO_SITE);
                    Uri url = Uri.parse(String.format(YOUTUBE_IMAGE_URL, key));
                    Log.i(LOG_TAG, url.toString());
                    Picasso.with(getActivity()).load(url).placeholder(R.drawable.loading_small).error(R.drawable.no_poster).into(video);
                    if (c.isFirst()) {
                        mVideoUrl = YOUTUBE_BASE_URL + key;
                        if (mShareItem != null) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType(INTENT_TYPE);
                            intent.putExtra(Intent.EXTRA_TEXT, mVideoUrl);
                            ((ShareActionProvider) MenuItemCompat.getActionProvider(mShareItem)).setShareIntent(intent);
                        }
                    }
                    video.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (YOUTUBE.equalsIgnoreCase(site))
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + key)));
                            else
                                Toast.makeText(getActivity(), String.format(getString(R.string.no_youtube_video),site), Toast.LENGTH_SHORT).show();
                        }
                    });
                    mVideos.addView(v);
                } while (c.moveToNext());
            }else{
                mNoVideos.setVisibility(View.VISIBLE);
                if (!new SettingsManager(getActivity()).isFavoriteSortOrder()){
                    mVideosProgressBar.setVisibility(View.VISIBLE);
                    getLoaderManager().getLoader(TMDB_MOVIE_VIDEOS_LOADER).forceLoad();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mVideos.removeAllViews();
        }
    }
}
