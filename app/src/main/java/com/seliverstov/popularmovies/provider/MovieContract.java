package com.seliverstov.popularmovies.provider;

import android.provider.BaseColumns;

/**
 * Created by Alexander on 30.10.2015.
 */
public class MovieContract {
    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_TITLE = "title";
    }

    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "reviews";
    }

    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailers";
    }

}
