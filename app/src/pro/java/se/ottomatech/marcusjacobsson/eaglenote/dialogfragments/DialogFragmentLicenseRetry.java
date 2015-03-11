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
 * Created by Marcus Jacobsson on 2015-02-11.
 */
public class DialogFragmentLicenseRetry extends DialogFragment implements DialogInterface.OnClickListener{

    private LicenceRetryDialogPositiveClick mListener;

    public interface LicenceRetryDialogPositiveClick{
        public void onLicenceRetryPositiveClick();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.dialogfragment_license_retry_title));
        builder.setMessage(getResources().getString(R.string.dialogfragment_license_retry_msg));
        builder.setPositiveButton(getResources().getString(R.string.dialogfragment_license_retry_pos_btn), this);
        builder.setNegativeButton(getResources().getString(R.string.dialogfragment_license_retry_neg_btn), this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_NEGATIVE:
                dialog.dismiss();
                getActivity().finish();
                break;

            case DialogInterface.BUTTON_POSITIVE:
                mListener.onLicenceRetryPositiveClick();
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LicenceRetryDialogPositiveClick) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + "Must implement LicenceRetryDialogPositiveClick.");
        }
    }
}
