package io.methinks.android.apptest;

import android.content.Context;

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

    private void makeRecording(String event) {
        if (isActivated) {
            Log.e("Another recording is in progress");
            return;
        }
    }

    private void startRecording(String event) {
        if (isActivated) {
            Log.e("Another recording is in progress");
            return;
        }


    }

    private void endRecording(String event) {

    }

    private void pauseRecording(String event) {

    }

    private void resumeRecording(String event) {

    }
}
