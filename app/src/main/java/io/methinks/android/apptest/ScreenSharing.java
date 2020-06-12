package io.methinks.android.apptest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

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
    private String iceServerList = "";
    private ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();

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

                        boolean isUserInRoom = result.getBoolean("isUserInRoom");

                        if (isUserInRoom) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Global.applicationTracker.getTopActivity(), R.style.MyDialogTheme);
                            builder.setTitle(R.string.patcher_user_already_logined);
                            builder.setCancelable(false);
                            // positive 버튼 설정
                            builder.setPositiveButton(R.string.patcher_next, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Global.applicationTracker.getTopActivity().stopService(Global.hoverIntent);
                                    Global.applicationTracker.getTopActivity().finishAffinity();
                                    System.exit(0);
                                }
                            });
                            Dialog installdialog = builder.create();
                            installdialog.setCanceledOnTouchOutside(false);
                            AlertDialog alertDialog = (AlertDialog) installdialog;
                            alertDialog.show();
                        }

                        /** IceServer Info fetching proc */
                        new HttpManager().getIceServerUrl((res, err) -> {
                            try {
                                String iceServerUrl = "";
                                if(res != null && err == null && res.getString("status").equals(Global.RESPONSE_OK)) {
                                    if(res.has("ice_server_url")) {
                                        iceServerUrl = res.getString("ice_server_url");
                                        Log.e("ICESerVer: " + iceServerUrl);
                                        Log.d(res.toString());

                                        new HttpManager().getIceServerList(iceServerUrl, (respon, erro) -> {
                                           try {
                                               if (respon != null && erro == null && respon.getString("status").equals(Global.RESPONSE_OK)) {
                                                   if (respon.has("iceServers")) {
                                                       iceServerList = respon.getJSONArray("iceServers").toString();
                                                       try {
                                                           JSONArray iceServersAry = new JSONArray(iceServerList);
                                                           for (int i = 0; i < iceServersAry.length(); ++i) {
                                                               JSONObject server = iceServersAry.getJSONObject(i);
                                                               JSONArray turnUrls = server.getJSONArray("urls");
                                                               String username = server.has("username") ? server.getString("username") : "";
                                                               String credential = server.has("credential") ? server.getString("credential") : "";
                                                               for (int j = 0; j < turnUrls.length(); j++) {
                                                                   String turnUrl = turnUrls.getString(j);
                                                                   iceServers.add(PeerConnection.IceServer.builder(turnUrl).setUsername(username).setPassword(credential).createIceServer());
                                                               }
                                                           }

                                                           /** mtkrtc Initializing */
                                                           if (Global.isScreenStreamAllowed) {
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
                                                                       .baseFeature("apptest_sdk")
                                                                       .iceServers(iceServers)
                                                                       .build();
                                                               mtkVideoChatClient.connect();
                                                           }


                                                       } catch (JSONException e) {
                                                           e.printStackTrace();
                                                       }
                                                   } else {
                                                       Log.e("There is no IceServers Info!!!");
                                                   }
                                               }
                                           } catch (Exception e) {

                                           }
                                        });
                                    }
                                }

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });

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
