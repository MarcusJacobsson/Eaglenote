package se.ottomatech.marcusjacobsson.eaglenote.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import se.ottomatech.marcusjacobsson.eaglenote.R;

/**
 * Created by Marcus Jacobsson on 2015-01-23.
 */
public class DialogFragmentRemoveNote extends DialogFragment implements DialogInterface.OnClickListener {

    private DialogFragmentRemoveNoteListener mListener;

    public interface DialogFragmentRemoveNoteListener{
        public void onRemoveNotesPositiveClick();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String numberOfNotes = getArguments().getString("number_of_notes");

        if(numberOfNotes.equals("1")){
            builder.setTitle(getResources().getString(R.string.dialogfragment_remove_note));
            builder.setMessage(getResources().getString(R.string.dialogfragment_remove_note_msg));
        }else{
            builder.setTitle(getResources().getString(R.string.dialogfragment_remove_notes));
            builder.setMessage(getResources().getString(R.string.dialogfragment_remove_notes_msg) + " " + numberOfNotes + " " + getResources().getString(R.string.dialogfragment_remove_notes_msg2));
        }

        builder.setNegativeButton(getResources().getString(R.string.dialogfragment_remove_note_neg_btn), this);
        builder.setPositiveButton(getResources().getString(R.string.dialogfragment_remove_note_pos_btn), this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_NEGATIVE:
            dialog.dismiss();
            break;

            case DialogInterface.BUTTON_POSITIVE:
            mListener.onRemoveNotesPositiveClick();
            break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogFragmentRemoveNoteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "Must implement DialogFragmentRemoveNoteListener.");
        }
    }

}
