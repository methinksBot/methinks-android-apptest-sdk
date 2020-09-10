package io.methinks.android.apptest;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MTKRecorder {

    private static volatile MTKRecorder instance;

    private String recordingMode = "default";
    private boolean isActivated = false;
    private String currentRecordingEvent;
    private boolean recordTrigger = false;

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

    /** create Screenshare with new session. */
    public void startRecording(String event) {
        if (Global.recordTicket && Global.recordingMode.equals("default")) {
            Log.e("Another recording is in progress or Mode is defferent.");
            return;
        }

        currentRecordingEvent = event;
        Global.recordTicket = true;

        Global.client.sendMessage(Global.MESSAGE_EVENT, event);

        Intent overlayIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
        Global.applicationTracker.getTopActivity().startActivity(overlayIntent);

    }

    /** end Screenshare. */
    public void endRecording(String event) {
        if (Global.recordingMode.equals("default") && !currentRecordingEvent.equals(event)) {
            Log.e("There is no proper ending option.");
            return;
        }

        Global.client.sendMessage(Global.MESSAGE_EVENT, event);

        Global.recordTicket = false;
        Global.screenSharing.end();
        Toast.makeText(Global.applicationTracker.getTopActivity(), event + " recording is ended.", Toast.LENGTH_LONG).show();
    }

    /** Stopping mediaProjection only. */
    public void pauseRecording(String event) {
        if (Global.recordingMode.equals("default") && !currentRecordingEvent.equals(event)) {
            Log.e("There is no proper recording status.");
            return;
        }

        Global.screenSharing.unpublish();
    }

    /** Starting mediaProjection. */
    public void resumeRecording(String event) {
        if (Global.recordingMode.equals("default") && !currentRecordingEvent.equals(event)) {
            Log.e("There is no proper recording status.");
            return;
        }

        Global.screenSharing.republish();
    }




}
