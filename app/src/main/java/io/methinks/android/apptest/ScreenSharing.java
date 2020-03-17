package io.methinks.android.apptest;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;

import java.util.ArrayList;

import io.methinks.android.rtc.MTKConst;
import io.methinks.android.rtc.MTKError;
import io.methinks.android.rtc.MTKPerson;
import io.methinks.android.rtc.MTKPublisher;
import io.methinks.android.rtc.MTKSubscriber;
import io.methinks.android.rtc.MTKVideoChatClient;
import io.methinks.android.rtc.MTKVideoChatSession;

import static io.methinks.android.rtc.MTKError.ErrorCode.SessionStateFailed;

public class ScreenSharing implements MTKVideoChatClient.MTKRTCClientListener {
    private static final String TAG = ScreenSharing.class.getSimpleName();

    private Application app;
    private EglBase eglBase;
    private MTKVideoChatClient mtkVideoChatClient;
    private MTKPublisher mainPublisher;

    public ScreenSharing(Application app) {
        this.app = app;
        this.eglBase = EglBase.create();
    }

    public void start(){
        new HttpManager().getJanusRoomInfo((response, error) -> {
            try{
                if(response != null && error == null && response.getString("status").equals(Global.RESPONSE_OK)){
                    if(response.has("result")){
                        System.out.println("getJanusRoomInfo : " + response);
                        JSONObject result = response.getJSONObject("result");
                        Log.e(result.toString());

                        Global.blockEmulator = result.getBoolean("blockEmulator");
                        /** Block emulator**/
                        Global.blockEmulator = true;
                        Global.isPlayedByEmulator = true;
                        Log.e("[EMULATOR BLOCKING TEST] :" + Global.isPlayedByEmulator + " / " + Global.blockEmulator);
                        if (Global.blockEmulator && Global.isPlayedByEmulator) {
                            Log.e("[EMULATOR BLOCKING TEST2] :" + Global.isPlayedByEmulator + " / " + Global.blockEmulator);

                            AlertDialog.Builder builder = new AlertDialog.Builder(app.getApplicationContext(), R.style.MyDialogTheme);

                            builder.setTitle(R.string.patcher_block_emulator_title).setMessage(R.string.patcher_block_emulator_desc);
                            builder.setCancelable(false);
                            // positive 버튼 설정
                            builder.setPositiveButton(R.string.patcher_next, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                    System.exit(0);
                                }
                            });

                            Dialog installdialog = builder.create();
                            installdialog.setCanceledOnTouchOutside(false);
                            AlertDialog alertDialog = (AlertDialog) installdialog;
                            alertDialog.show();

                        }

                        String targetServer = Global.isDebugMode ? "dev" : "prod";
                        mtkVideoChatClient = new MTKVideoChatClient.Builder()
                                .context(app)
                                .bucket(result.getString("bucketName"))    // us-oregon or kr-seoul from Campaign's bucketName
                                .secret(response.has("secret") ? response.getString("secret") : "kqtoixA5wL1576548431884")
                                .userId(Global.sUserId)
                                .userName(Global.sScreenName)
                                .projectId(Global.sProjectId)
                                .roomType(MTKConst.ROOM_TYPE_APP_TEST)
                                .roomToken(Global.sCampaignParticipantId)
                                .targetServer(targetServer)
                                .eglBase(eglBase)
                                .socketURL(result.getString("socketUrl"))
                                .roomId(result.getInt("id"))
                                .roomPin(result.getString("pin"))
                                .apiToken(result.has("apiToken") ? response.getJSONObject("result").getString("apiToken") : "1576520141,janus,janus.plugin.videoroom:eAlYmNuzxdzT0QiF18DeVU3z254=")
                                .sId(Global.sId)
                                .listener(ScreenSharing.this)
                                .build();
                        mtkVideoChatClient.connect();
                    }else{

                    }

                }else{
                    Log.e("Can't use Screen sharing.");
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onChangedClientState(MTKVideoChatClient client, MTKVideoChatClient.MTKVideoChatClientState state) {
        if(state == MTKVideoChatClient.MTKVideoChatClientState.connected && mainPublisher == null){
            if(Global.screenCaptureIntent != null){
                mainPublisher = new MTKPublisher(MTKPerson.StreamVideoType.screen, Global.screenCaptureIntent);
                mainPublisher.setPublishAudio(false);
                mainPublisher.setPublishVideo(true);

                client.publish(mainPublisher);
            }else{
                Log.e("Can't start methinks AppTest Screen Sharing.");
            }
        }
    }

    @Override
    public void onStartedPublishing(MTKVideoChatClient mtkVideoChatClient, MTKPublisher mtkPublisher) {

    }

    @Override
    public void onAddedStream(MTKVideoChatClient mtkVideoChatClient, MediaStream mediaStream, MTKSubscriber mtkSubscriber, MTKVideoChatSession mtkVideoChatSession) {

    }

    @Override
    public void onRemovedStreamOfSubscriber(MTKVideoChatClient mtkVideoChatClient, MTKSubscriber mtkSubscriber) {

    }

    @Override
    public void onReceivedBroadcastSignal(MTKVideoChatClient mtkVideoChatClient, JSONObject jsonObject) {

    }

    @Override
    public void onCreatedLocalExternalSampleCapturer(MTKVideoChatClient mtkVideoChatClient, JSONObject jsonObject) {

    }

    @Override
    public void getStats(MTKVideoChatClient mtkVideoChatClient, ArrayList<String> arrayList) {

    }

    @Override
    public void onResultTranscription(String s) {

    }

    @Override
    public void onError(MTKVideoChatClient mtkVideoChatClient, MTKError e) {
        if(e.getErrorCode() == SessionStateFailed && e.getMessage().contains("Ice connection state is fail")){
            AlertDialog dialog = new AlertDialog.Builder(Global.applicationTracker.getTopActivity())
                    .setPositiveButton("Ok", (dialogInterface, i) -> {}).create();

            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(Global.applicationTracker.getTopActivity().getString(R.string.patcher_msg_use_wifi_network));
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> Global.applicationTracker.shutDownApp());
        }
    }

    public void finish(){
        if(mtkVideoChatClient != null) {
            mtkVideoChatClient.disconnect();
        }
    }
}
