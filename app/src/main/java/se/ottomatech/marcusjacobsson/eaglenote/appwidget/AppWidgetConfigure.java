package se.ottomatech.marcusjacobsson.eaglenote.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.activities.MainActivity;
import se.ottomatech.marcusjacobsson.eaglenote.database.Datasource;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Folder;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Note;
import se.ottomatech.marcusjacobsson.eaglenote.util.QwkNoteTimeUtil;


/**
 * Created by Marcus Jacobsson on 2015-01-25.
 */

public class AppWidgetConfigure extends FragmentActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Button btnDone;
    private Spinner spinnerFolder, spinnerNote;
    private String chosenFolder, chosenNoteId, chosenNoteTimestamp, chosenNoteNote;
    private Datasource datasource;
    private ArrayList<Folder> allFolders;
    private ArrayList<Note> allNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.app_widget_configure);
        datasource = new Datasource(this);
        setUpComponents();
        chosenFolder = "MAIN";

    }

    private void setUpComponents() {
        btnDone = (Button) findViewById(R.id.btn_widget_config_done);
        spinnerFolder = (Spinner) findViewById(R.id.spinner_widget_config_folders);
        spinnerNote = (Spinner) findViewById(R.id.spinner_widget_config_notes);

        btnDone.setOnClickListener(this);
        spinnerFolder.setOnItemSelectedListener(this);
        spinnerNote.setOnItemSelectedListener(this);

        try {
            datasource.open();
            allFolders = datasource.getAllFolders();
            datasource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        String[] allFolderNames = new String[allFolders.size()];
        for (Folder tmpFolder : allFolders) {
            allFolderNames[allFolders.indexOf(tmpFolder)] = tmpFolder.getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, allFolderNames);
        spinnerFolder.setAdapter(adapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.spinner_widget_config_folders) {
            chosenNoteId = "";
            chosenFolder = spinnerFolder.getSelectedItem().toString();
            allNotes = new ArrayList<>();
            try {
                datasource.open();
                allNotes = datasource.getAllNotes(chosenFolder);
                datasource.close();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }

            ArrayList<String> allNotesNotesArray = new ArrayList<>();

            if (allNotes.size() > 0) {
                for (Note tmpNote : allNotes) {
                    allNotesNotesArray.add(tmpNote.getNote());
                }
                btnDone.setEnabled(true);
            } else {
                allNotesNotesArray.add(getResources().getString(R.string.user_feedback_appwidget_empty_folder));
                btnDone.setEnabled(false);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, allNotesNotesArray);
            spinnerNote.setAdapter(adapter);
        } else if (parent.getId() == R.id.spinner_widget_config_notes) {
            if(btnDone.isEnabled()){
                chosenNoteId = String.valueOf(allNotes.get(spinnerNote.getSelectedItemPosition()).getId());
                chosenNoteTimestamp = allNotes.get(spinnerNote.getSelectedItemPosition()).getTimestamp();
                chosenNoteNote = allNotes.get(spinnerNote.getSelectedItemPosition()).getNote();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_widget_config_done) {
            if (!chosenNoteId.equals("")) {
                int mAppWidgetId = 0;

                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    mAppWidgetId = extras.getInt(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID);
                }

                QwkNoteTimeUtil timeUtil = new QwkNoteTimeUtil();
                String formatedTimeStamp = timeUtil.makeTime(chosenNoteTimestamp);

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.app_widget);
                views.setTextViewText(R.id.tv_app_widget_timestamp, formatedTimeStamp);
                views.setTextViewText(R.id.tv_app_widget_note, chosenNoteNote);


                Intent showNote = new Intent(this, MainActivity.class);
                showNote.putExtra("widget_sender", mAppWidgetId);
                showNote.setData(Uri.parse(showNote.toUri(Intent.URI_INTENT_SCHEME)));
                showNote.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent notesPendingIntent = PendingIntent.getActivity(this, mAppWidgetId, showNote, PendingIntent.FLAG_UPDATE_CURRENT);

                views.setOnClickPendingIntent(R.id.app_widget_layout_id, notesPendingIntent);

                AppWidgetManager.getInstance(this).updateAppWidget(mAppWidgetId, views);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("appwidget_note_id" + chosenNoteId, String.valueOf(mAppWidgetId)).commit();
                edit.putString("appwidget_id_timestamp" + mAppWidgetId, chosenNoteTimestamp).commit();
                edit.putString("appwidget_id_note" + mAppWidgetId, chosenNoteNote).commit();
                edit.putString("appwidget_id_note_id" + mAppWidgetId, chosenNoteId).commit();

                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();
            } else {
                Toast.makeText(this, getResources().getString(R.string.user_feedback_select_note), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
