package se.ottomatech.marcusjacobsson.eaglenote.fragments;

import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.database.Datasource;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Note;
import se.ottomatech.marcusjacobsson.eaglenote.preferences.SettingsActivity;

/**
 * Created by Marcus Jacobsson on 2015-01-21.
 */
public class NoteDetailsFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private String noteId, noteTitle, noteNote, noteTimeStamp, noteFolder;
    private Datasource datasource;
    private EditText etNote;
    private boolean dirty;

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static NoteDetailsFragment newInstance(Note note) {
        NoteDetailsFragment f = new NoteDetailsFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("noteId", String.valueOf(note.getId()));
        args.putString("noteTitle", note.getTitle());
        args.putString("noteNote", note.getNote());
        args.putString("noteTimeStamp", note.getTimestamp());
        args.putString("noteFolder", note.getFolder());
        f.setArguments(args);

        return f;
    }

    public String getShownNoteId() {
        return getArguments().getString("noteId");
    }

    public Note getShownNote() {
        if (noteId != null) {
            Note tmpNote = new Note();
            tmpNote.setFolder(noteFolder);
            tmpNote.setTimestamp(noteTimeStamp);
            tmpNote.setNote(noteNote);
            tmpNote.setId(Integer.parseInt(noteId));
            tmpNote.setTitle(noteTitle);
            return tmpNote;
        } else {
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_details, container, false);
        datasource = new Datasource(getActivity());

        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

        noteId = getArguments().getString("noteId");
        noteTitle = getArguments().getString("noteTitle");
        noteNote = getArguments().getString("noteNote");
        noteTimeStamp = getArguments().getString("noteTimeStamp");
        noteFolder = getArguments().getString("noteFolder");

        etNote = (EditText) rootView.findViewById(R.id.etNote);
        etNote.addTextChangedListener(this);

        etNote.setText(noteNote);

        //Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String textStyle = sharedPref.getString(SettingsActivity.pref_key_general_text_style, "");
        String textSize = sharedPref.getString(SettingsActivity.pref_key_general_text_size, "");
        if (textStyle.equals("Default")) {
            etNote.setTypeface(Typeface.DEFAULT);
        } else {
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), textStyle);
            etNote.setTypeface(typeface);
        }
        etNote.setTextSize(Float.parseFloat(textSize));

        etNote.setSelection(etNote.getText().toString().length());

        LinearLayout btnSaveNote = (LinearLayout) rootView.findViewById(R.id.btn_save_note);
        btnSaveNote.setOnClickListener(this);

        dirty = false;

        return rootView;
    }

    @Override
    public void onPause() {
        saveCurrentNote();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_save_note){
            saveCurrentNote();
        }
    }

    public void saveCurrentNote() {
        if (dirty) {
            noteNote = etNote.getText().toString();
            long success = 0;
            try {
                datasource.open();
                success = datasource.updateNoteNote(noteId, etNote.getText().toString());
                datasource.close();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            if (success == 1) {
                NotesFragment fragment = (NotesFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.note_title_fragment);
                if (fragment != null) {
                    fragment.updateListView();
                    fragment.disableCAB();
                }
                Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_saved_note), Toast.LENGTH_SHORT).show();
                updateWidgetIfNeeded();
                dirty = false;
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_could_not_save_note), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        dirty = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void updateWidgetIfNeeded() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String appWidgetId = prefs.getString("appwidget_note_id" + noteId, "default_val");
        if (!appWidgetId.equals("default_val")) {

            RemoteViews views = new RemoteViews(getActivity().getPackageName(), R.layout.app_widget);
            views.setTextViewText(R.id.tv_app_widget_note, etNote.getText().toString());


            SharedPreferences.Editor edit = prefs.edit();
            edit.putString("appwidget_id_note" + appWidgetId, etNote.getText().toString()).commit();

            AppWidgetManager.getInstance(getActivity()).updateAppWidget(Integer.parseInt(appWidgetId), views);
        }
    }

    public void clearNoteDisplay() {
        this.etNote.setText("");
        dirty = false;
    }

    public void resetNoteDisplay() {

        etNote.setText(noteNote);

        //Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String textStyle = sharedPref.getString(SettingsActivity.pref_key_general_text_style, "");
        String textSize = sharedPref.getString(SettingsActivity.pref_key_general_text_size, "");
        if (textStyle.equals("Default")) {
            etNote.setTypeface(Typeface.DEFAULT);
        } else {
            Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), textStyle);
            etNote.setTypeface(typeface);
        }
        etNote.setTextSize(Float.parseFloat(textSize));


        etNote.setSelection(etNote.getText().toString().length());
        dirty = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Uppdatera eventuella preferences
        if (etNote != null)
            resetNoteDisplay();

    }
}
