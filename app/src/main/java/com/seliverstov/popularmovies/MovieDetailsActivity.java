package com.seliverstov.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by a.g.seliverstov on 15.10.2015.
 */
public class MovieDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState==null){
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.MOVIE_DETAILS_URI, getIntent().getData());
            MovieDetailsFragment mdf = new MovieDetailsFragment();
            mdf.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(R.id.details_fragment_container,mdf).commit();
        }
    }
}
