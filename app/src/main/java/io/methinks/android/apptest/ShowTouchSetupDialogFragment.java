package io.methinks.android.apptest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;


public class ShowTouchSetupDialogFragment extends DialogFragment {

    private Context context;
    PackageManager pm;
    DownloadManager downloadManager;
    private long downloadID;
    private String url;
    private Activity activity;

    @SuppressLint("ValidFragment")
    public ShowTouchSetupDialogFragment(Context context) {
        this();
        this.context = context;
    }

    public ShowTouchSetupDialogFragment() {
        this.url = Global.isDebugMode ? Global.DEV_METHINKS_SERVER_URL : Global.PROD_METHINKS_SERVER_URL;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);
        LayoutInflater inflater = activity.getLayoutInflater();


        builder.setTitle(R.string.patcher_req_show_touch)
                .setView(inflater.inflate(R.layout.sdk_show_touches_dialog, null))
                .setPositiveButton(R.string.patcher_show_touch_option_first, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /** Check developer mode enabled   1 = dev on , 0 = dev off   */
                        boolean devCheck = Settings.Secure.getInt(context.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0;

                        if (devCheck) {
                            /** Enable show touches on user's own */
                            activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS), Global.REQUEST_SHOW_TOUCHES);

                        } else {
                            // to return current activity..

                            Snackbar.make(activity.findViewById(R.id.devModeCoordinatorLayout), R.string.patcher_set_devmode, Snackbar.LENGTH_INDEFINITE)
                                    .setActionTextColor(getResources().getColor(R.color.cornflower))
                                    .setAction(R.string.patcher_set_devmode_guide, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            redirectToGuide();
                                        }
                                    })
                                    .show();

                        }

                    }
                })
                .setNeutralButton(R.string.patcher_show_touch_option_second, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /** Enable show touches by methinks_show_touches Extension app */

                        /** check methinks_touch_support is existing on user's device */
                        pm = context.getPackageManager();
                        boolean inInstalled = isPackageInstalled("io.methinks.android.methinks_touchsupports", pm);
                        Log.w("서포트 앱 설치여부 " + inInstalled);

                        if (!inInstalled) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);

                            builder.setTitle(getString(R.string.patcher_download_extension_dialog_title)).setMessage(getString(R.string.patcher_download_extension_dialog_desc));
                            builder.setCancelable(false);
                            // positive 버튼 설정
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    downloadSupportApp();
                                }
                            });

                            Dialog installdialog = builder.create();
                            installdialog.setCanceledOnTouchOutside(false);
                            AlertDialog alertDialog = (AlertDialog) installdialog;
                            alertDialog.show();

                        } else {
                            Intent intent = activity.getPackageManager().getLaunchIntentForPackage("io.methinks.android.methinks_touchsupports");
                            activity.startActivityForResult(intent, Global.REQUEST_EXTENSION_SHOW_TOUCHES);
                            Toast.makeText(context, "Touch Pointer Enabled", Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setCancelable(false);

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void redirectToGuide() {
        Uri uri = Uri.parse(url + "/project/instruction/" + Global.sProjectId);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            activity.startActivityForResult(intent, Global.REQUEST_SHOW_DEV_GUIDE);
        }
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void downloadSupportApp() {

        File file = new File(context.getExternalFilesDir(null), "methinks_touchsupports.apk");
        if (file.exists() == true) {
            file.delete();
        }

        String url = this.url + "/download/android/touchsupport";

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle("Downlading methinks_touchsupports.apk")
                .addRequestHeader("Authorization", "m3ThiNqs!")
                .setMimeType("application/vnd.android.package-archive")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(file));

        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);

        final BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.w("Installing Extension success : " + id + "/" + downloadID + BuildConfig.APPLICATION_ID);

                if (id == downloadID) {
                    /*if (targetSDKVersion >= Build.VERSION_CODES.N) {
                    *//** HostApp have androidx as support libraries **//*
                    Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(activity.getExternalFilesDir(null), "methinks_touchsupports.apk"));

                    Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
                    openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    openFileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    openFileIntent.setData(contentUri);
                    //Toast.makeText(context, getString(R.string.patcher_install_extension_instruction), Toast.LENGTH_LONG);
                    activity.startActivityForResult(openFileIntent, Global.EXTENTION_INSTALL_DONE);
                    context.unregisterReceiver(this);

                    } else {*/
                     /** HostApp have support libraries lower then androidx  **/
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MyDialogTheme);

                    builder.setTitle(getString(R.string.patcher_install_extension_by_user_title)).setMessage(getString(R.string.patcher_install_extension_by_user_desc));
                    builder.setCancelable(false);
                    // positive 버튼 설정
                    builder.setPositiveButton(getString(R.string.patcher_text_next), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    });

                    Dialog installdialog = builder.create();
                    installdialog.setCanceledOnTouchOutside(false);
                    AlertDialog alertDialog = (AlertDialog) installdialog;
                    alertDialog.show();
                    //}

                }
            }
        };

        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }
}
