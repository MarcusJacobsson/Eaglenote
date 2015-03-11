package se.ottomatech.marcusjacobsson.eaglenote.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.database.Datasource;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentAbout;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentAddNewFolder;
import se.ottomatech.marcusjacobsson.eaglenote.fragments.NoteDetailsFragment;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Folder;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Note;
import se.ottomatech.marcusjacobsson.eaglenote.preferences.SettingsActivity;

/**
 * Created by Marcus Jacobsson on 2015-01-21.
 */
public class NoteDetailsActivity extends ActionBarActivity implements DialogFragmentAddNewFolder.DialogFragmentAddNewFolderListener {

    private Button btnSaveNote;
    private Datasource datasource;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, we can show the
            // dialog in-line with the list so we don't need this activity.
            finish();
            return;
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            NoteDetailsFragment details = new NoteDetailsFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.note_details, details).commit();
        }

        datasource = new Datasource(this);
    }

    @Override
    public void onAddNewFolderPositiveClick(Folder folder) {
        long success = 0;
        try {
            datasource.open();
            success = datasource.createFolder(folder.getName());
            datasource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (success != -1) {
            Toast.makeText(this, getResources().getString(R.string.dialogfragment_add_folder_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.dialogfragment_add_folder_fail), Toast.LENGTH_SHORT).show();
        }
    }
}
