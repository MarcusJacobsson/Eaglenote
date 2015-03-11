package se.ottomatech.marcusjacobsson.eaglenote.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.ArrayList;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.database.Datasource;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Folder;

/**
 * Created by Marcus Jacobsson on 2015-01-23.
 */
public class DialogFragmentMoveNote extends DialogFragment implements DialogInterface.OnClickListener {

    private DialogFragmentMoveNoteListener mListener;
    private String chosenFolder;
    private String[] allFolderNames;

    public interface DialogFragmentMoveNoteListener {
        public void onMoveNotePositiveClick(String chosenFolder);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Datasource datasource = new Datasource(getActivity());
        ArrayList<Folder> allFolders = new ArrayList<Folder>();
        chosenFolder = "MAIN";

        try {
            datasource.open();
            allFolders = datasource.getAllFolders();
            datasource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        allFolderNames = new String[allFolders.size()];
        for (int i = 0; i < allFolders.size(); i++) {
            allFolderNames[i] = allFolders.get(i).getName();
        }

        builder.setSingleChoiceItems(allFolderNames, 0, this);
        builder.setTitle(getResources().getString(R.string.dialogfragment_move_note));
        builder.setPositiveButton(getResources().getString(R.string.dialogfragment_move_note_pos_btn), this);
        builder.setNegativeButton(getResources().getString(R.string.dialogfragment_move_note_neg_btn), this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                mListener.onMoveNotePositiveClick(chosenFolder);
                break;
            default:
                chosenFolder = allFolderNames[which];
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogFragmentMoveNoteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "Must implement DialogFragmentMoveNoteListener.");
        }
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }
}
