package io.methinks.android.apptest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


public class ShowTouchSetupDialogFragment extends DialogFragment {

    private Context context;

    @SuppressLint("ValidFragment")
    public ShowTouchSetupDialogFragment(Context context) {
        this();
        this.context = context;
    }
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
                        /** Check developer mode enabled   1 = dev on , 0 = dev off   */
                        boolean devCheck = Settings.Secure.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) != 0;

                        if (devCheck) {
                            /** Enable show touches on user's own */
                            getActivity().startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS), Global.REQUEST_SHOW_TOUCHES);

                        } else {
                            Snackbar.make(getActivity().findViewById(R.id.devModeCoordinatorLayout), R.string.patcher_set_devmode, Snackbar.LENGTH_LONG)
                                    .setActionTextColor(getResources().getColor(R.color.cornflower))
                                    .setAction(R.string.patcher_set_devmode_guide, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            redirectToGuide();
                                        }
                                    })
                                    .show();
                            getActivity().startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS), Global.REQUEST_SHOW_TOUCHES);
                        }

                    }
                })
                .setNeutralButton(R.string.patcher_show_touch_option_second, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /** Enable show touches by methinks_show_touches Extension app */

                    }
                })
                .setCancelable(false);

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void redirectToGuide() {
        Uri uri = Uri.parse(Global.isDebugMode ? Global.DEV_METHINKS_SERVER_URL : Global.PROD_METHINKS_SERVER_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
