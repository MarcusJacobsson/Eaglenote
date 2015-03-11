package se.ottomatech.marcusjacobsson.eaglenote.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentAbout;
import se.ottomatech.marcusjacobsson.eaglenote.fragments.NoteDetailsFragment;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Note;
import se.ottomatech.marcusjacobsson.eaglenote.preferences.SettingsActivity;

/**
 * Created by Marcus Jacobsson on 2015-01-24.
 * Editor lol
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            Intent prefIntent = new Intent(this, SettingsActivity.class);
            startActivity(prefIntent);
        } else if (item.getItemId() == R.id.action_share) {
            NoteDetailsFragment fragment = (NoteDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.note_details);
            if (fragment != null) {
                fragment.saveCurrentNote();
                Note tmpNote = fragment.getShownNote();
                if (tmpNote != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject_first)); //header
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, tmpNote.getNote());

                    Intent chooser = Intent.createChooser(intent, getResources().getString(R.string.share_chooser_title));

                    // Verify the intent will resolve to at least one activity
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(chooser);
                    } else {
                        Toast.makeText(this, getResources().getString(R.string.user_feedback_no_share_app_found), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getResources().getString(R.string.user_feedback_select_note_to_share), Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, getResources().getString(R.string.user_feedback_select_note_to_share), Toast.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.action_about) {
            DialogFragmentAbout dialogAbout = new DialogFragmentAbout();
            dialogAbout.show(getSupportFragmentManager(), "dialogAbout");
        }

        return super.onOptionsItemSelected(item);
    }

}
