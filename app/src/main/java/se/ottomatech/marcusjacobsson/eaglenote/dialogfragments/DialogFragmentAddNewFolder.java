package se.ottomatech.marcusjacobsson.eaglenote.dialogfragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import se.ottomatech.marcusjacobsson.eaglenote.R;
import se.ottomatech.marcusjacobsson.eaglenote.pojos.Folder;

/**
 * Created by Marcus Jacobsson on 2015-01-20.
 */
public class DialogFragmentAddNewFolder extends DialogFragment implements DialogInterface.OnClickListener {

    private EditText etName;
    private DialogFragmentAddNewFolderListener mListener;

    public interface DialogFragmentAddNewFolderListener {
        public void onAddNewFolderPositiveClick(Folder folder);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View ourView = inflater.inflate(
                R.layout.dialogfragment_add_folder, null);
        builder.setView(ourView);
        etName = (EditText) ourView.findViewById(R.id.etAddFolder);

        builder.setTitle(R.string.dialogfragment_add_folder_title);

        builder.setPositiveButton(
                R.string.dialogfragment_add_folder_pos_btn, this);
        builder.setNegativeButton(
                R.string.dialogfragment_add_folder_neg_btn, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                String name = etName.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.dialogfragment_add_folder_feedback), Toast.LENGTH_SHORT).show();
                } else {
                    Folder folder = new Folder();
                    folder.setName(name);
                    mListener.onAddNewFolderPositiveClick(folder);
                }
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogFragmentAddNewFolderListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "Must implement DialogFragmentAddNewFolderListener.");
        }
    }
}
