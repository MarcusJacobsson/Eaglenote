package se.ottomatech.marcusjacobsson.eaglenote.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.purplebrain.adbuddiz.sdk.AdBuddizDelegate;
import com.purplebrain.adbuddiz.sdk.AdBuddizError;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentAddNewFolder;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentAbout;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentDeleteFolder;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentMoveNote;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentRemoveNote;
import se.ottomatech.marcusjacobsson.eaglenote.fragments.NoteDetailsFragment;
import se.ottomatech.marcusjacobsson.eaglenote.fragments.NotesFragment;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Folder;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Note;
import se.ottomatech.marcusjacobsson.eaglenote.preferences.SettingsActivity;

public class MainActivity extends ActionBarActivity implements DialogFragmentMoveNote.DialogFragmentMoveNoteListener,
        DialogFragmentRemoveNote.DialogFragmentRemoveNoteListener, DialogFragmentAddNewFolder.DialogFragmentAddNewFolderListener,
        DialogFragmentDeleteFolder.DialogFragmentDeleteFolderListener {

    public AdBuddizDelegate delegate;

    private AdBuddizDelegate adDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setUpAds();

        int sender = getIntent().getIntExtra("widget_sender", 0);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String noteId = prefs.getString("appwidget_id_note_id" + sender, "");

        getIntent().removeExtra("widget_sender");

        if (!noteId.equals("")) {
            displayNoteDetailsFromWidget(noteId);
        }
    }

    private void setUpAds(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long last_shown_ad = prefs.getLong("last_shown_ad", System.currentTimeMillis());
        boolean first = prefs.getBoolean("first", true);
        AdBuddiz.setPublisherKey("ef3bdcd2-613b-4467-a0ea-565dca037716");
        AdBuddiz.cacheAds(this);
        if(first){
            AdBuddiz.showAd(this);
        }
        //Show ad maximum every five minutes
        if (last_shown_ad + 300000 < System.currentTimeMillis()){
            AdBuddiz.showAd(this);
        }
        delegate = new AdBuddizDelegate() {
            @Override
            public void didCacheAd() {
            }

            @Override
            public void didShowAd() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
                SharedPreferences.Editor edit = prefs.edit();
                edit.putLong("last_shown_ad", System.currentTimeMillis());
                edit.putBoolean("first", false);
                edit.apply();
            }

            @Override
            public void didFailToShowAd(AdBuddizError adBuddizError) {
            }

            @Override
            public void didClick() {
            }

            @Override
            public void didHideAd() {
            }
        };

        AdBuddiz.setDelegate(delegate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

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
                    intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject_first)); //header
                    intent.putExtra(Intent.EXTRA_TEXT, tmpNote.getNote());

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
    public void onAddNewFolderPositiveClick(Folder folder) {
        NotesFragment fragment = (NotesFragment) getSupportFragmentManager().findFragmentById(R.id.note_title_fragment);
        fragment.addFolder(folder);
    }

    @Override
    public void onRemoveNotesPositiveClick() {
        NotesFragment fragment = (NotesFragment) getSupportFragmentManager().findFragmentById(R.id.note_title_fragment);
        fragment.deleteNotes();
        fragment.disableCAB();
    }

    @Override
    public void onMoveNotePositiveClick(String chosenFolder) {
        NotesFragment fragment = (NotesFragment) getSupportFragmentManager().findFragmentById(R.id.note_title_fragment);
        fragment.moveNotes(chosenFolder);
        fragment.disableCAB();
    }

    @Override
    public void onDeleteFolderPositiveClick() {
        NotesFragment fragment = (NotesFragment) getSupportFragmentManager().findFragmentById(R.id.note_title_fragment);
        fragment.disableCAB();
        fragment.deleteFolder();
    }

    private void displayNoteDetailsFromWidget(String noteId) {
        NotesFragment fragment = (NotesFragment) getSupportFragmentManager().findFragmentById(R.id.note_title_fragment);
        fragment.displayNoteDetailsFromWidget(noteId);
    }
}