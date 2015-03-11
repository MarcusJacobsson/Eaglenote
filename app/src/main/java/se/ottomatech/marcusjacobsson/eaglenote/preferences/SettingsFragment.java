package se.ottomatech.marcusjacobsson.eaglenote.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import se.ottomatech.marcusjacobsson.eaglenote.R;

/**
 * Created by Marcus Jacobsson on 2015-01-27.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference txtSize = (ListPreference) findPreference(SettingsActivity.pref_key_general_text_size);
        txtSize.setSummary(txtSize.getEntry());

        ListPreference txtStyle = (ListPreference) findPreference(SettingsActivity.pref_key_general_text_style);
        txtStyle.setSummary(txtStyle.getEntry());

        ListPreference sortOrder = (ListPreference) findPreference(SettingsActivity.pref_key_sort_order);
        sortOrder.setSummary(sortOrder.getEntry());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
