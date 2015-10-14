package com.seliverstov.popularmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by a.g.seliverstov on 13.10.2015.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference preference = findPreference(getString(R.string.pref_sort_by_key));
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String strValue = newValue.toString();
        if (preference instanceof ListPreference){
            ListPreference lp = (ListPreference)preference;
            int i = lp.findIndexOfValue(strValue);
            if (i>=0){
                preference.setSummary(lp.getEntries()[i]);
            }
        }else{
            preference.setSummary(strValue);
        }
        return true;
    }


}
