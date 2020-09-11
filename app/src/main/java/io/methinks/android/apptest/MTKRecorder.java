package io.methinks.android.apptest;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Date;

public class MTKRecorder {

    private static volatile MTKRecorder instance;

    private String recordingMode = "default";
    private boolean isActivated = false;
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

        Global.currentRecordingEvent = event;
        Global.recordTicket = true;

        Global.client.sendMessage(Global.MESSAGE_EVENT, event);

        Intent overlayIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
        Global.applicationTracker.getTopActivity().startActivity(overlayIntent);

    }

    public void startRecording(String event, int duration) {
        if (Global.recordTicket && Global.recordingMode.equals("default")) {
            Log.e("Another recording is in progress or Mode is defferent.");
            return;
        }

        Global.currentRecordingEvent = event;
        Global.recordTicket = true;

        Global.client.sendMessage(Global.MESSAGE_EVENT, event);

        if (duration != 0) {
            new Thread(new Runnable() {
                public void run() {
                    Log.d("[[start recording timer thread!]]");
                    Global.recordingStartTime = new Date().getTime() / 1000;
                    Log.d("[Recording Timer]: " + Global.recordingStartTime + "/" + event + "/" + Global.currentRecordingEvent);
                    while (Global.recordTimerThreadFlag) {
                        Global.sRecordingTime = new Date().getTime() / 1000;
                        if (Global.sRecordingTime - Global.recordingStartTime >= duration) {
                            endRecording(event);
                            Global.recordTimerThreadFlag = false;
                        }
                    }
                    Global.recordTimerThreadFlag = true;
                }
            }).start();
        }

        Intent overlayIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
        Global.applicationTracker.getTopActivity().startActivity(overlayIntent);


    }

    /** end Screenshare. */
    public void endRecording(String event) {
        if (Global.recordingMode.equals("default") && !Global.currentRecordingEvent.equals(event)) {
            Log.e("There is no proper ending option.");
            return;
        }

        Global.client.sendMessage(Global.MESSAGE_EVENT, event);

        Global.recordTicket = false;
        Global.screenSharing.unpublish();
        Global.screenSharing.end();
        Global.currentRecordingEvent = null;

        Global.applicationTracker.getTopActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(Global.applicationTracker.getTopActivity(), event + " Recording is ended.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Stopping mediaProjection only. */
    public void pauseRecording(String event) {
        if (Global.recordingMode.equals("default") && !Global.currentRecordingEvent.equals(event)) {
            Log.e("There is no proper recording status.");
            return;
        }

        Global.screenSharing.unpublish();
    }

    /** Starting mediaProjection. */
    public void resumeRecording(String event) {
        if (Global.recordingMode.equals("default") && !Global.currentRecordingEvent.equals(event)) {
            Log.e("There is no proper recording status.");
            return;
        }

        Global.screenSharing.republish();
    }


    class RecordTimerThread implements Runnable {
        String event;
        long duration;

        RecordTimerThread(String event, long duration) {
            this.event = event;
            this.duration = duration;
        }

        @Override
        public void run() {
            Log.d("start recording timer thread!");
            Global.recordingStartTime = new Date().getTime() / 1000;
            Log.d("[Recording Timer]: " + Global.recordingStartTime + "/" + event + "/" + Global.currentRecordingEvent);
            while (Global.recordTimerThreadFlag) {
                Global.sRecordingTime = new Date().getTime() / 1000;
                if (Global.sRecordingTime - Global.recordingStartTime >= duration) {
                    endRecording(event);
                    Global.recordTimerThreadFlag = false;
                }
            }
            Global.recordTimerThreadFlag = true;
        }
    }

}
