package com.seliverstov.popularmovies.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.seliverstov.popularmovies.fragment.DetailsFragment;
import com.seliverstov.popularmovies.R;

/**
 * Created by a.g.seliverstov on 15.10.2015.
 */
public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (savedInstanceState==null){
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailsFragment.MOVIE_DETAILS_URI, getIntent().getData());
            DetailsFragment mdf = new DetailsFragment();
            mdf.setArguments(arguments);
            getFragmentManager().beginTransaction().replace(R.id.details_fragment_container,mdf).commit();
        }
    }
}
