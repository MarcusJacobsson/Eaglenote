package se.ottomatech.marcusjacobsson.eaglenote.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.activities.NoteDetailsActivity;
import se.ottomatech.marcusjacobsson.eaglenote.arrayadapters.NotesArrayAdapter;
import se.ottomatech.marcusjacobsson.eaglenote.database.Datasource;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentAddNewFolder;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentDeleteFolder;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentMoveNote;
import se.ottomatech.marcusjacobsson.eaglenote.dialogfragments.DialogFragmentRemoveNote;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Folder;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Note;
import se.ottomatech.marcusjacobsson.eaglenote.preferences.SettingsActivity;

/**
 * Created by Marcus Jacobsson on 2015-01-21.
 */

public class NotesFragment extends Fragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener, View.OnClickListener, NotesArrayAdapter.NotesAdapterCallback {

    boolean mDualPane;
    int mCurCheckPosition = -1;
    private ArrayList<Note> allNotes;
    private NotesArrayAdapter notesAdapter;
    private Datasource datasource;
    private Spinner spinnerFolders;
    private ListView lwNotes;
    private ArrayAdapter<String> spinnerAdapter;
    private ActionMode mActionMode;
    private ArrayList<String> checkedNotes;
    private String curFolder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        setUpComponents(rootView);
        SharedPreferences pref = getActivity().getSharedPreferences("curFolder", Context.MODE_PRIVATE);
        int curFolderPos = pref.getInt("curFolderPos", -1);
        if (curFolderPos != -1) {
            spinnerFolders.setSelection(curFolderPos);
        }

        curFolder = spinnerFolders.getSelectedItem().toString();

        return rootView;
    }

    @Override
    public void onDestroy() {
        SharedPreferences pref = getActivity().getSharedPreferences("curFolder", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("curFolderPos", spinnerFolders.getSelectedItemPosition());
        edit.apply();
        super.onDestroy();
    }

    private void setUpComponents(View rootView) {
        datasource = new Datasource(rootView.getContext());
        spinnerFolders = (Spinner) rootView.findViewById(R.id.spinner_folders);
        lwNotes = (ListView) rootView.findViewById(R.id.lw_notes);
        LinearLayout btnAddNote = (LinearLayout) rootView.findViewById(R.id.btn_add_note);
        ImageButton btnDeleteFolder = (ImageButton) rootView.findViewById(R.id.btn_delete_folder);
        ImageButton btnAddFolder = (ImageButton) rootView.findViewById(R.id.btn_add_folder);
        btnAddFolder.setOnClickListener(this);
        btnDeleteFolder.setOnClickListener(this);
        btnAddNote.setOnClickListener(this);
        spinnerFolders.setOnItemSelectedListener(this);
        lwNotes.setOnItemClickListener(this);

        setUpSpinner();
        getAllNotes();

        notesAdapter = new NotesArrayAdapter(getActivity(), allNotes);
        lwNotes.setAdapter(notesAdapter);
        notesAdapter.setCallback(this);
    }

    private void setUpSpinner() {
        ArrayList<String> folderNames = getAllFolderNames();
        spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, folderNames);

        spinnerFolders.setAdapter(spinnerAdapter);
    }

    private ArrayList<String> getAllFolderNames() {
        ArrayList<Folder> allFolders = new ArrayList<Folder>();
        try {
            datasource.open();
            allFolders = datasource.getAllFolders();
            datasource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        ArrayList<String> folderNames = new ArrayList<String>();
        for (Folder tmpFolder : allFolders) {
            folderNames.add(tmpFolder.getName());
        }
        return folderNames;
    }

    public void updateSpinner() {
        ArrayList<String> folderNames = getAllFolderNames();
        spinnerAdapter.clear();
        spinnerAdapter.addAll(folderNames);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        getAllNotes();
        if (mActionMode != null)
            mActionMode.finish();

        if (!curFolder.equals(spinnerFolders.getSelectedItem().toString())) {
            curFolder = spinnerFolders.getSelectedItem().toString();
            lwNotes.setItemChecked(-1, true);
            NoteDetailsFragment details = (NoteDetailsFragment)
                    getFragmentManager().findFragmentById(R.id.note_details);
            if (details != null)
                getFragmentManager().beginTransaction().remove(details).commit();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }

    private void getAllNotes() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = pref.getString(SettingsActivity.pref_key_sort_order, "");
        if (lwNotes != null) {
            String currentFolder = spinnerFolders.getSelectedItem().toString();
            try {
                datasource.open();
                allNotes = datasource.getAllNotes(currentFolder);
                datasource.close();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }

            //sort prefs
            switch (sortOrder) {
                case "titleASC":
                    Collections.sort(allNotes, compNoteNoteAsc);
                    break;

                case "titleDSC":
                    Collections.sort(allNotes, compNoteNoteDesc);
                    break;

                case "timeASC":
                    Collections.sort(allNotes, compNoteTimeAsc);
                    break;

                case "timeDSC":
                    Collections.sort(allNotes, compNoteTimeDesc);
                    break;
            }

            if (notesAdapter != null) {
                notesAdapter.clear();
                notesAdapter.addAll(allNotes);
            }
        }
        setSelectionHighlight();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showDetails(position);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_note) {
            addNote();
        } else if (v.getId() == R.id.btn_delete_folder) {
            String currentFolder = spinnerFolders.getSelectedItem().toString();
            if (!currentFolder.equals("MAIN")) {
                DialogFragmentDeleteFolder dialog = new DialogFragmentDeleteFolder();
                Bundle args = new Bundle();
                args.putString("delete_folder_name", currentFolder);
                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(), "dialog");
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_can_not_delete_main), Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btn_add_folder) {
            DialogFragment dialog = new DialogFragmentAddNewFolder();
            dialog.show(getActivity().getSupportFragmentManager(), "add_folder_dialog");
        }

    }

    @Override
    public void onPause() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        View detailsFrame = getActivity().findViewById(R.id.note_details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        if (!mDualPane) {
            updateSpinner();
            lwNotes.setChoiceMode(ListView.CHOICE_MODE_NONE);
        } else {
            lwNotes.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        getAllNotes();
        super.onResume();
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        mCurCheckPosition = index;
        SharedPreferences prefs = getActivity().getSharedPreferences("curNoteId", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("curNoteId", allNotes.get(index).getId());
        edit.commit();

        View detailsFrame = getActivity().findViewById(R.id.note_details);
        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
        if (mDualPane) {

            // Check what fragment is currently shown, replace if needed.
            NoteDetailsFragment details = (NoteDetailsFragment)
                    getFragmentManager().findFragmentById(R.id.note_details);

            if (allNotes.size() != 0) {
                if (details == null || !details.getShownNoteId().equals(String.valueOf(allNotes.get(index).getId()))) {
                    // Make new fragment to show this selection.

                    details = NoteDetailsFragment.newInstance(allNotes.get(index));

                    // Execute a transaction, replacing any existing fragment
                    // with this one inside the frame.
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.note_details, details);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();

                }
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), NoteDetailsActivity.class);
            intent.putExtra("noteId", String.valueOf(allNotes.get(index).getId()));
            intent.putExtra("noteTitle", allNotes.get(index).getTitle());
            intent.putExtra("noteNote", allNotes.get(index).getNote());
            intent.putExtra("noteTimeStamp", allNotes.get(index).getTimestamp());
            intent.putExtra("noteFolder", allNotes.get(index).getFolder());
            startActivity(intent);
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items

            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context, menu);
            checkedNotes = new ArrayList<String>();

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.context_delete) {
                DialogFragmentRemoveNote dialog = new DialogFragmentRemoveNote();
                Bundle bundle = new Bundle();
                bundle.putString("number_of_notes", String.valueOf(checkedNotes.size()));
                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "dialog");
                return true;
            } else if (item.getItemId() == R.id.context_move) {
                DialogFragmentMoveNote moveDialog = new DialogFragmentMoveNote();
                moveDialog.show(getActivity().getSupportFragmentManager(), "moveDialog");
                return true;
            } else {
                return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            getAllNotes();
        }
    };


    @Override
    public void checkBoxChecked(Note which) {
        if (mActionMode == null)
            mActionMode = getActivity().startActionMode(mActionModeCallback);

        checkedNotes.add(String.valueOf(which.getId()));

        if (checkedNotes.size() == 1) {
            mActionMode.setTitle(checkedNotes.size() + " " + getResources().getString(R.string.action_mode_title_note));

        } else {
            mActionMode.setTitle(checkedNotes.size() + " " + getResources().getString(R.string.action_mode_title_notes));
        }
    }

    @Override
    public void checkBoxUnchecked(Note which) {
        if (mActionMode != null) {
            //Note clickedNote = (Note) which.getTag(R.id.cb_notes);
            checkedNotes.remove(String.valueOf(which.getId()));

            if (checkedNotes.size() == 1) {
                mActionMode.setTitle(checkedNotes.size() + " " + getResources().getString(R.string.action_mode_title_note));
            } else {
                mActionMode.setTitle(checkedNotes.size() + " " + getResources().getString(R.string.action_mode_title_notes));
            }
            if (checkedNotes.size() == 0) {
                mActionMode.finish();
            }
        }
    }

    private void addNote() {
        long success = 0;
        String currentFolder = spinnerFolders.getSelectedItem().toString();
        //Create the note
        try {
            datasource.open();
            success = datasource.createNote("", "", currentFolder);
            datasource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (success != -1) {
            Note newNote = null;
                    /*
                    Get the created note. If the insert was successful, the success variable will
                    hold a reference to the ID of the created row.
                     */
            try {
                datasource.open();
                newNote = datasource.getNote((int) success);
                datasource.close();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }

            if (newNote != null) {

                allNotes.add(newNote);
                if (mDualPane) {
                    notesAdapter.clear();
                    notesAdapter.addAll(allNotes);
                }
                showDetails(allNotes.indexOf(newNote));
                setSelectionHighlight();

            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_could_not_add_folder), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_could_not_add_folder), Toast.LENGTH_SHORT).show();
        }

    }

    public void addFolder(Folder folder) {
        long success = 0;
        try {
            datasource.open();
            success = datasource.createFolder(folder.getName());
            datasource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (success != -1) {
            Toast.makeText(getActivity(), getResources().getString(R.string.dialogfragment_add_folder_success), Toast.LENGTH_SHORT).show();
            NotesFragment fragment = (NotesFragment) getFragmentManager().findFragmentById(R.id.note_title_fragment);
            fragment.updateSpinner();
            ArrayList<String> allFolders = getAllFolderNames();
            spinnerFolders.setSelection(allFolders.size() - 1);
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.dialogfragment_add_folder_fail), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteNotes() {
        SharedPreferences pref = getActivity().getSharedPreferences("curNoteId", Context.MODE_PRIVATE);
        int curNoteId = pref.getInt("curNoteId", -1);
        try {
            datasource.open();
            for (String noteId : checkedNotes) {
                if (noteId.equals(String.valueOf(curNoteId)) && mDualPane) {
                    NoteDetailsFragment details = (NoteDetailsFragment)
                            getFragmentManager().findFragmentById(R.id.note_details);
                    if (details != null)
                        getFragmentManager().beginTransaction().remove(details).commit();
                }

                datasource.removeNote(noteId);
            }
            datasource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (checkedNotes.size() != 1) {
            Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_notes_deleted), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_note_deleted), Toast.LENGTH_SHORT).show();
        }
        getAllNotes();
    }

    public void moveNotes(String chosenFolder) {
        SharedPreferences pref = getActivity().getSharedPreferences("curNoteId", Context.MODE_PRIVATE);
        int curNoteId = pref.getInt("curNoteId", -1);
        try {
            datasource.open();
            for (String noteId : checkedNotes) {
                if (noteId.equals(String.valueOf(curNoteId)) && mDualPane) {
                    NoteDetailsFragment details = (NoteDetailsFragment)
                            getFragmentManager().findFragmentById(R.id.note_details);
                    if (details != null)
                        getFragmentManager().beginTransaction().remove(details).commit();
                }
                datasource.updateNoteFolder(noteId, chosenFolder);
            }
            datasource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (checkedNotes.size() != 1) {
            Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_notes_moved), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_note_moved), Toast.LENGTH_SHORT).show();
        }
        getAllNotes();
    }

    public void deleteFolder() {
        String currentFolder = spinnerFolders.getSelectedItem().toString();
        long success = 0;
        if (!currentFolder.equals("MAIN")) {
            try {
                datasource.open();
                if (allNotes.size() != 0) {
                    for (Note tmpNote : allNotes) {
                        datasource.removeNote(String.valueOf(allNotes.get(allNotes.indexOf(tmpNote)).getId()));
                    }
                }
                success = datasource.removeFolder(currentFolder);
                datasource.close();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            if (success != 0) {
                if (mDualPane) {
                    NoteDetailsFragment details = (NoteDetailsFragment)
                            getFragmentManager().findFragmentById(R.id.note_details);
                    if (details != null)
                        details.clearNoteDisplay();
                }
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_can_not_delete_main), Toast.LENGTH_SHORT).show();
        }
        if (success != 0) {
            getAllNotes();
            updateSpinner();
            spinnerFolders.setSelection(0);
            Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_folder_deleted), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.user_feedback_could_not_delete_folder), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateListView() {
        if (mDualPane) {
            getAllNotes();
        }

    }

    public void disableCAB() {
        if (mActionMode != null)
            mActionMode.finish();
    }

    public void displayNoteDetailsFromWidget(String noteId) {
        Note note = null;
        ArrayList<String> allFolderNames = getAllFolderNames();
        try {
            datasource.open();
            note = datasource.getNote(Integer.parseInt(noteId));
            datasource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        if (note != null) {
            if (note.getFolder().equals(spinnerFolders.getSelectedItem().toString())) {
                showDetails(allNotes.indexOf(note));
            } else {
                spinnerFolders.setSelection(allFolderNames.indexOf(note.getFolder()));
                getAllNotes();
                showDetails(allNotes.indexOf(note));
            }
        }
    }

    public void setSelectionHighlight() {
        if (mDualPane) {
            SharedPreferences pref = getActivity().getSharedPreferences("curNoteId", Context.MODE_PRIVATE);
            NoteDetailsFragment noteDetailsFragment = (NoteDetailsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.note_details);

            int id = pref.getInt("curNoteId", -1);
            if (id != -1) {
                Note note = null;
                try {
                    datasource.open();
                    note = datasource.getNote(id);
                    datasource.close();
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
                int curPos = allNotes.indexOf(note);
                if (note != null && noteDetailsFragment != null) {
                    lwNotes.setItemChecked(curPos, true);
                    if (note.getFolder().equals(spinnerFolders.getSelectedItem().toString()))
                        showDetails(curPos);
                }else if(note != null){
                    if (note.getFolder().equals(spinnerFolders.getSelectedItem().toString()))
                        showDetails(curPos);
                }
            }
        }
    }

    private Comparator<Note> compNoteNoteAsc = new Comparator<Note>() {
        @Override
        public int compare(Note lhs, Note rhs) {
            return lhs.getNote().compareTo(rhs.getNote());
        }
    };

    private Comparator<Note> compNoteNoteDesc = new Comparator<Note>() {
        @Override
        public int compare(Note lhs, Note rhs) {
            return rhs.getNote().compareTo(lhs.getNote());
        }
    };

    private Comparator<Note> compNoteTimeAsc = new Comparator<Note>() {
        @Override
        public int compare(Note lhs, Note rhs) {
            return lhs.getTimestamp().compareTo(rhs.getTimestamp());
        }
    };

    private Comparator<Note> compNoteTimeDesc = new Comparator<Note>() {
        @Override
        public int compare(Note lhs, Note rhs) {
            return rhs.getTimestamp().compareTo(lhs.getTimestamp());
        }
    };
}
