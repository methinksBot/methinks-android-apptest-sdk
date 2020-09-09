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

    /**
     *   partially recording.
     * */

    public void makeRecording(String event) {
        if (Global.recordTicket && Global.recordingMode.equals("default")) {
            Log.e("Another recording is in progress or Mode is defferent.");
            return;
        }

        currentRecordingEvent = event;
        Global.recordTicket = true;
        Intent overlayIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
        Global.applicationTracker.getTopActivity().startActivity(overlayIntent);

        // TODO: 2020/09/07 set timer thread to count 10 min
    }

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

    public void endRecording(String event) {
        if (Global.recordingMode.equals("default") && !currentRecordingEvent.equals(event)) {
            Log.e("There is no proper ending option.");
            return;
        }

        Global.client.sendMessage(Global.MESSAGE_EVENT, event);

        Global.recordTicket = false;
        Global.screenSharing.finish();
        Toast.makeText(Global.applicationTracker.getTopActivity(), event + " recording is ended.", Toast.LENGTH_LONG).show();
    }

    public void pauseRecording(String event) {
        if (Global.recordingMode.equals("default") && !currentRecordingEvent.equals(event)) {
            Log.e("There is no proper recording status.");
            return;
        }

        Global.screenSharing.unpublish();
    }

    public void resumeRecording(String event) {
        if (Global.recordingMode.equals("default") && !currentRecordingEvent.equals(event)) {
            Log.e("There is no proper recording status.");
            return;
        }

        Global.screenSharing.republish();
    }




}
