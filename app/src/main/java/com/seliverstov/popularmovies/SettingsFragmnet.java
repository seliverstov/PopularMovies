package com.seliverstov.popularmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by a.g.seliverstov on 15.10.2015.
 */
public class SettingsFragmnet extends PreferenceFragment implements OnPreferenceChangeListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference preference = findPreference(getString(R.string.pref_sort_by_key));
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
        return view;
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
