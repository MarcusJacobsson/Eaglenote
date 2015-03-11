package se.ottomatech.marcusjacobsson.eaglenote.dialogfragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import se.ottomatech.marcusjacobsson.eaglenote.R;

/**
 * Created by Marcus Jacobsson on 2015-02-04.
 */
public class DialogFragmentAbout extends DialogFragment implements DialogInterface.OnClickListener {

    private TextView tvVersion, tvDevBy, tvDevByEmail, tvThanks;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View ourView = inflater.inflate(
                R.layout.dialogfragment_about, null);


        tvVersion = (TextView) ourView.findViewById(R.id.tv_about_version);
        tvDevBy = (TextView) ourView.findViewById(R.id.tv_about_developed_by);
        tvDevByEmail = (TextView) ourView.findViewById(R.id.tv_about_developed_by_email);
        tvThanks = (TextView) ourView.findViewById(R.id.tv_about_thanks);

        String version = "";

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tvVersion.setText(getResources().getString(R.string.dialogfragment_about_version) + " " + version);
        tvDevBy.setText(getResources().getString(R.string.dialogfragment_about_msg_developed_by));
        tvDevByEmail.setText(getResources().getString(R.string.dialogfragment_about_msg_developed_by_email));
        tvThanks.setText(getResources().getString(R.string.dialogfragment_about_msg_thanks));

        builder.setTitle(getResources().getString(R.string.dialogfragment_about_title));

        builder.setPositiveButton(getResources().getString(R.string.dialogfragment_about_pos_btn), this);

        builder.setView(ourView);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE){
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getResources().getString(R.string.dialogfragment_about_url)));
            startActivity(i);
        }
    }
}
