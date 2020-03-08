package io.methinks.android.apptest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

public class ErrorDialogFragment extends DialogFragment {

    private String errorString;

    @SuppressLint("ValidFragment")
    public ErrorDialogFragment(String errorCode) {
        this();
        switch (errorCode) {
            case "removedFromCampaign":
                this.errorString = "You are no longer a part of this project.";
                break;
            case "cantSeeProject":
                this.errorString = "The project has ended.";
                break;
            case "invalidUserCode":
                this.errorString = "Invalid code.";
                break;
            default:
                break;
        }
    }

    public ErrorDialogFragment() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        builder.setMessage(errorString)
                .setPositiveButton(R.string.common_text_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
