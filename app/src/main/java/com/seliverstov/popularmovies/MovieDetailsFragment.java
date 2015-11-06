package com.seliverstov.popularmovies;

import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import static com.seliverstov.popularmovies.db.PopularMoviesContact.*;

/**
 * Created by a.g.seliverstov on 15.10.2015.
 */
public class MovieDetailsFragment extends Fragment {
    public static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private static String[] COLUMNS = {
            MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_POSTER_PATH
    };

    private static int IDX_ORIGINAL_TITLE = 0;
    private static int IDX_RELEASE_DATE = 1;
    private static int IDX_VOTE_AVERAGE = 2;
    private static int IDX_OVERVIEW = 3;
    private static int IDX_POSTER_PATH = 4;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        Uri uri = getActivity().getIntent().getData();
        Cursor c = getActivity().getContentResolver().query(uri,COLUMNS,null,null,null);
        if (c.moveToFirst()){
            TextView title = (TextView)view.findViewById(R.id.movie_title);
            TextView year = (TextView)view.findViewById(R.id.movie_year);
            TextView rating = (TextView)view.findViewById(R.id.movie_rating);
            TextView overview = (TextView)view.findViewById(R.id.movie_overview);

            String originalTitle = c.getString(IDX_ORIGINAL_TITLE);
            title.setText(originalTitle);
            String rd = c.getString(IDX_RELEASE_DATE);
            rd = (rd !=null && rd.length()>=4)? rd.substring(0,4): "";

            year.setText(rd);
            rating.setText(new DecimalFormat("#.#").format(c.getDouble(IDX_VOTE_AVERAGE))+"/10");
            overview.setText(c.getString(IDX_OVERVIEW));

            ImageView poster = (ImageView)view.findViewById(R.id.movie_poster);

            String posterPath = c.getString(IDX_POSTER_PATH);
            if (posterPath!=null) {
                Uri url = Uri.parse(getString(R.string.movie_poster_base_url))
                        .buildUpon()
                        .appendPath(getString(R.string.big_movie_poster_size))
                        .appendEncodedPath(posterPath)
                        .build();
                Log.i(LOG_TAG, "Get poster for movie " + originalTitle + ": " + url.toString());
                Picasso.with(getActivity()).load(url).placeholder(R.drawable.loading_big).into(poster);
            }else{
                poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
                poster.setImageResource(R.drawable.noposter);
            }
        }else{
            Toast.makeText(getActivity(),"Can't find movie details :(",Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}
