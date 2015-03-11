package se.ottomatech.marcusjacobsson.eaglenote.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;
import com.google.android.vending.licensing.ServerManagedPolicy;

import java.util.UUID;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentAbout;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentAddNewFolder;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentDeleteFolder;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentLicenseFailed;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentLicenseRetry;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentMoveNote;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentRemoveNote;
import se.ottomatech.marcusjacobsson.eaglenote.fragments.NoteDetailsFragment;
import se.ottomatech.marcusjacobsson.eaglenote.fragments.NotesFragment;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Folder;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Note;
import se.ottomatech.marcusjacobsson.eaglenote.preferences.SettingsActivity;

public class MainActivity extends ActionBarActivity implements DialogFragmentMoveNote.DialogFragmentMoveNoteListener,
        DialogFragmentRemoveNote.DialogFragmentRemoveNoteListener, DialogFragmentAddNewFolder.DialogFragmentAddNewFolderListener,
        DialogFragmentDeleteFolder.DialogFragmentDeleteFolderListener, DialogFragmentLicenseRetry.LicenceRetryDialogPositiveClick {

    private ProgressDialog progress;
    private static final byte[] SALT = new byte[]{
            -46, 65, 30, -128, -103, -57, 74, -64, 51, 88, -95,
            -45, 77, -117, -36, -113, -11, 32, -64, 89
    };
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private Handler mHandler;
    private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArJefLNKn6SYzfwoPowIjPjfw00j/KStcmD2DZYz9oxCBRfZ6ve5/273tYXs7pdQ/lemsZ2MVidSeT1sZB9miIglsWXh5hgaSGI2sY2QW+bQ0mCpv9JxUy+Ml68Fy79JRF6I9p2SEEWW1gBHyIIgdmP0xEHOTshRvuh8DHpqe754khHVlznpky2gsNsuPB1lMcJLMC3FbKgLW3KeumkgvKqyOEExi+Wo32gtAcU2GbaMwTpomtyn3tihpB0vxTdYlf7UKe89V3VK0AU7d9Wz6fQu8LW9OFNVyyp7fsFT5FVXYyrBAOwIVc/o1LPltjm3eZed13KjTfQk5aA95uqYukwIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        id(this);
        mHandler = new Handler();
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();
        mChecker = new LicenseChecker(
                this, new ServerManagedPolicy(this,
                new AESObfuscator(SALT, getPackageName(), uniqueID)),
                BASE64_PUBLIC_KEY  // Your public licensing key.
        );
        doCheck();

        int sender = getIntent().getIntExtra("widget_sender", 0);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String noteId = prefs.getString("appwidget_id_note_id" + sender, "");

        getIntent().removeExtra("widget_sender");

        if (!noteId.equals("")) {
            displayNoteDetailsFromWidget(noteId);
        }
    }

    private void doCheck() {
        progress = ProgressDialog.show(this, getResources().getString(R.string.progress_bar_license_title),
                getResources().getString(R.string.progress_bar_license_msg), true);
        mChecker.checkAccess(mLicenseCheckerCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChecker.onDestroy();
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

    /* Licence check */

    private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
        public void allow(int reason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should allow user access.
            displayResult(reason);
        }

        public void dontAllow(int reason) {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            displayResult(reason);

            if (reason == Policy.RETRY) {
                // If the reason received from the policy is RETRY, it was probably
                // due to a loss of connection with the service, so we should give the
                // user a chance to retry. So show a dialog to retry.

                DialogFragmentLicenseRetry licenseRetry = new DialogFragmentLicenseRetry();
                licenseRetry.show(getSupportFragmentManager(), "licenseRetry");
            } else {
                // Otherwise, the user is not licensed to use this app.
                // Your response should always inform the user that the application
                // is not licensed, but your behavior at that point can vary. You might
                // provide the user a limited access version of your app or you can
                // take them to Google Play to purchase the app.

                DialogFragmentLicenseFailed licenseFailed = new DialogFragmentLicenseFailed();
                licenseFailed.show(getSupportFragmentManager(), "licenceFailed");
            }
        }

        @Override
        public void applicationError(int errorCode) {
            dontAllow(Policy.NOT_LICENSED);
        }
    }

    private void displayResult(final int reason) {
        mHandler.post(new Runnable() {
            public void run() {
                progress.dismiss();
            }
        });
    }

    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }
        return uniqueID;
    }

    @Override
    public void onLicenceRetryPositiveClick() {
        doCheck();
    }
}