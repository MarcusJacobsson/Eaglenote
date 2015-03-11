package se.ottomatech.marcusjacobsson.eaglenote.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import se.ottomatech.marcusjacobsson.eaglenote.R;

/**
 * Created by Marcus Jacobsson on 2015-02-11.
 */
public class DialogFragmentLicenseFailed extends DialogFragment implements DialogInterface.OnClickListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.dialogfragment_license_failed_title));
        builder.setMessage(getResources().getString(R.string.dialogfragment_license_failed_msg));
        builder.setPositiveButton(getResources().getString(R.string.dialogfragment_license_failed_pos_btn), this);
        builder.setNegativeButton(getResources().getString(R.string.dialogfragment_license_failed_neg_btn), this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                getActivity().finish();
                break;

            case DialogInterface.BUTTON_POSITIVE:
                getActivity().finish();
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.https://play.google.com/store/apps/details?id=se.ottomatech.marcusjacobsson.eaglenote.pro"));
                startActivity(intent);
                break;

        }
    }
}
