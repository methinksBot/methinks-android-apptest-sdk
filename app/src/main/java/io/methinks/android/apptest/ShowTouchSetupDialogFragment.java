package io.methinks.android.apptest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ShowTouchSetupDialogFragment extends DialogFragment {

    private String errorString;

    public ShowTouchSetupDialogFragment() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setTitle(R.string.patcher_req_show_touch)
                .setMessage(R.string.patcher_show_touch_setup)
                .setMessage(R.string.patcher_show_touch_setup_option_first)
                .setMessage(R.string.patcher_show_touch_setup_option_second)
                .setPositiveButton(R.string.patcher_show_touch_option_first, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS), Global.REQUEST_SHOW_TOUCHES);
                    }
                })
                .setPositiveButton(R.string.patcher_show_touch_option_second, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setCancelable(false);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
