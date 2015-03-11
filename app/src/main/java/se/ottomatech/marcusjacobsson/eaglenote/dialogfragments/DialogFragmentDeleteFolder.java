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
 * Created by Marcus Jacobsson on 2015-01-27.
 */
public class DialogFragmentDeleteFolder extends DialogFragment implements DialogInterface.OnClickListener {

    private DialogFragmentDeleteFolderListener mListener;

    public interface DialogFragmentDeleteFolderListener{
        public void onDeleteFolderPositiveClick();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String folderName = getArguments().getString("delete_folder_name");

        builder.setTitle(getResources().getString(R.string.dialogfragment_remove_folder_title));
        builder.setMessage(getResources().getString(R.string.dialogfragment_remove_folder_msg) + " " + folderName + getResources().getString(R.string.dialogfragment_remove_folder_msg2));
        builder.setPositiveButton(getResources().getString(R.string.dialogfragment_remove_folder_pos_btn), this);
        builder.setNegativeButton(getResources().getString(R.string.dialogfragment_remove_folder_neg_btn), this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;

            case DialogInterface.BUTTON_POSITIVE:
                mListener.onDeleteFolderPositiveClick();
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogFragmentDeleteFolderListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "Must implement DialogFragmentDeleteFolderListener.");
        }
    }
}
