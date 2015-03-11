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
 * Created by Marcus Jacobsson on 2015-01-25.
 */
public class DialogFragmentAppWidgetConfig extends DialogFragment implements DialogInterface.OnClickListener {

    private String choice;
    private DialogFragmentAppWidgetConfigListener mListener;
    private String[] allFolderNames;

    public interface DialogFragmentAppWidgetConfigListener {
        public void onDialogFragmentAppWidgetConfigPositiveClick(String choice);

        public void onDialogFragmentAppWidgetConfigNegativeClick();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Datasource datasource = new Datasource(getActivity());
        ArrayList<Folder> allFolders = new ArrayList<>();
        try {
            datasource.open();
            allFolders = datasource.getAllFolders();
            datasource.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        allFolderNames = new String[allFolders.size()];
        for (Folder tmpFolder : allFolders) {
            allFolderNames[allFolders.indexOf(tmpFolder)] = tmpFolder.getName();
        }

        if (allFolderNames.length != 0)
            choice = allFolderNames[0];

        builder.setPositiveButton(getResources().getString(R.string.dialogfragment_add_folder_pos_btn), this);
        builder.setNegativeButton(getResources().getString(R.string.dialogfragment_add_folder_neg_btn), this);
        builder.setTitle(getResources().getString(R.string.dialogfragment_app_widget_config_title));
        builder.setSingleChoiceItems(allFolderNames, 0, this);
        return builder.create();


    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                mListener.onDialogFragmentAppWidgetConfigPositiveClick(choice);
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                mListener.onDialogFragmentAppWidgetConfigNegativeClick();
                dialog.dismiss();
                break;

            default:
                choice = allFolderNames[which];
                break;
        }
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DialogFragmentAppWidgetConfigListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "Must implement DialogFragmentAppWidgetConfigListener.");
        }
    }
}
