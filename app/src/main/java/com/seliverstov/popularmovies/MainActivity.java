package com.seliverstov.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static final String SAVED_SORT_ORDER = MainActivity.class.getSimpleName()+".SAVED_SORT_ORDER";

    private String mSortOrder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState!=null){
                String sortOrder = savedInstanceState.getString(SAVED_SORT_ORDER);
                if (sortOrder!=null) mSortOrder = sortOrder;
                Log.i(LOG_TAG,"Restore saved state: sortOrder = "+sortOrder);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "Save state: sortOrder = "+mSortOrder);
        outState.putString(SAVED_SORT_ORDER, mSortOrder);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = sp.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));

        if (mSortOrder ==null) mSortOrder = sortOrder;

        if (!mSortOrder.equals(sortOrder)){
            mSortOrder = sortOrder;
            MoviesGridFragment fragment = (MoviesGridFragment)getFragmentManager().findFragmentById(R.id.grid_fragment);
            if (fragment!=null) fragment.onSortOrderChanged();
        }
    }
}
