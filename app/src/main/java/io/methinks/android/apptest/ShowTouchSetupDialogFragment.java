package io.methinks.android.apptest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import java.util.zip.Inflater;

public class ShowTouchSetupDialogFragment extends DialogFragment {

    private String errorString;


    public ShowTouchSetupDialogFragment() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setTitle(R.string.patcher_req_show_touch)
                .setView(inflater.inflate(R.layout.sdk_show_touches_dialog, null))
                .setPositiveButton(R.string.patcher_show_touch_option_first, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /** Enable show touches on user's own */
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS), Global.REQUEST_SHOW_TOUCHES);
                    }
                })
                .setNeutralButton(R.string.patcher_show_touch_option_second, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /** Enable show touches by methinks_show_touches Extension app */

                    }
                })
                .setCancelable(false);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
