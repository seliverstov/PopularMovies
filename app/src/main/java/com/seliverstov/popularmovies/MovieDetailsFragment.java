package com.seliverstov.popularmovies;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.seliverstov.popularmovies.rest.model.Movie;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

/**
 * Created by a.g.seliverstov on 15.10.2015.
 */
public class MovieDetailsFragment extends Fragment {
    public static final String EXTRA_MOVIE_OBJECT = "MOVIE_OBJECT";
    public static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        Movie m = (Movie)getActivity().getIntent().getSerializableExtra(EXTRA_MOVIE_OBJECT);
        TextView title = (TextView)view.findViewById(R.id.movie_title);
        TextView year = (TextView)view.findViewById(R.id.movie_year);
        TextView rating = (TextView)view.findViewById(R.id.movie_rating);
        TextView overview = (TextView)view.findViewById(R.id.movie_overview);

        title.setText(m.getOriginalTitle());
        String rd = m.getReleaseDate();
        rd = (rd !=null && rd.length()>=4)? rd.substring(0,4): "";

        year.setText(rd);
        rating.setText(new DecimalFormat("#.#").format(m.getVoteAverage())+"/10");
        overview.setText(m.getOverview());

        ImageView poster = (ImageView)view.findViewById(R.id.movie_poster);

        if (m.getPosterPath()!=null) {
            Uri url = Uri.parse(getString(R.string.movie_poster_base_url))
                    .buildUpon()
                    .appendPath(getString(R.string.big_movie_poster_size))
                    .appendEncodedPath(m.getPosterPath())
                    .build();
            Log.i(LOG_TAG, "Get poster for movie " + m.getTitle() + ": " + url.toString());
            Picasso.with(getActivity()).load(url).into(poster);
        }else{
            poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            poster.setImageResource(R.drawable.noposter);
        }
        return view;
    }
}
