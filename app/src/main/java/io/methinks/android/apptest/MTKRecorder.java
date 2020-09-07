package io.methinks.android.apptest;

import android.content.Context;
import android.content.Intent;

public class MTKRecorder {

    private static volatile MTKRecorder instance;

    private boolean forceStopRecording = false;
    private boolean isActivated = false;

    public MTKRecorder(Context context) {

    }

    public static synchronized MTKRecorder getInstance(Context context) {
        if (instance == null) {
            instance = new MTKRecorder(context);
            Global.recorder = instance;
            return instance;
        }
        return instance;
    }

    /**
     *   partially recording.
     * */

    private void makeRecording(String event) {
        if (isActivated && Global.recordingMode.equals("default")) {
            Log.e("Another recording is in progress or Mode is defferent.");
            return;
        }

        Intent overlayIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
        Global.applicationTracker.getTopActivity().startActivity(overlayIntent);

        // TODO: 2020/09/07 set timer thread to count 10 min
    }

    private void startRecording(String event) {
        if (isActivated && Global.recordingMode.equals("default")) {
            Log.e("Another recording is in progress or Mode is defferent.");
            return;
        }

        Intent overlayIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
        Global.applicationTracker.getTopActivity().startActivity(overlayIntent);

    }

    private void endRecording(String event) {
        if (Global.recordingMode.equals("default")) {
            Log.e("Another recording is in progress");
            return;
        }
    }

    private void pauseRecording(String event) {

    }

    private void resumeRecording(String event) {

    }




}
