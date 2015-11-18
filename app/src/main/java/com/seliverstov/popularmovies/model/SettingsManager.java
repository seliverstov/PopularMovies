package com.seliverstov.popularmovies.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.seliverstov.popularmovies.R;
import com.seliverstov.popularmovies.db.PopularMoviesContact;
import com.seliverstov.popularmovies.rest.TMDBClient;

/**
 * Created by a.g.seliverstov on 18.11.2015.
 */
public class SettingsManager {
    private Context mContext;
    private SharedPreferences mSp;

    public static final int SORT_ORDER_POPULARITY = 0;
    public static final int SORT_ORDER_RATING = 1;
    public static final int SORT_ORDER_FAVORITE = 2;


    public SettingsManager(Context context){
        mContext = context;
        mSp = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public int getCurrentSortOrder(){
        int sortOrder = Integer.valueOf(mSp.getString(mContext.getString(R.string.pref_sort_by_key), String.valueOf(SORT_ORDER_POPULARITY)));
        return sortOrder;
    }

    public String getSortOrderForDb(){
        switch (getCurrentSortOrder()){
            case SORT_ORDER_FAVORITE:
            case SORT_ORDER_POPULARITY: return PopularMoviesContact.MovieEntry.COLUMN_POPULARITY+" DESC";
            case SORT_ORDER_RATING: return PopularMoviesContact.MovieEntry.COLUMN_VOTE_AVERAGE+" DESC";
            default: return null;
        }
    }

    public String getSortOrderForWeb(){
        switch (getCurrentSortOrder()){
            case SORT_ORDER_FAVORITE: return null;
            case SORT_ORDER_POPULARITY: return TMDBClient.SORT_ORDER_POPULARIRY_DESC;
            case SORT_ORDER_RATING: return TMDBClient.SORT_ORDER_VOTE_AVERAGE_DESC;
            default: return null;
        }
    }

    public boolean isFavoriteSortOrder(){
        return getCurrentSortOrder() == SORT_ORDER_FAVORITE;
    }

    public int getCurrentPage(){
        return mSp.getInt(mContext.getString(R.string.pref_page_key),0);
    }

    public void setCurrentPage(int page){
        mSp.edit().putInt(mContext.getString(R.string.pref_page_key),page).apply();
    }
}
