package se.ottomatech.marcusjacobsson.eaglenote.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Marcus Jacobsson on 2015-01-27.
 */
public class SettingsActivity extends PreferenceActivity {

    public static final String pref_key_show_timestamp_list = "pref_key_show_timestamp_list";
    public static final String pref_key_general_text_size = "pref_key_general_text_size";
    public static final String pref_key_general_text_style = "pref_key_general_text_style";
    public static final String pref_key_sort_order = "pref_key_list_sort_order";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}
