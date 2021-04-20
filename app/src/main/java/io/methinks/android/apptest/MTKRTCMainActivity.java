package io.methinks.android.apptest;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

/**
 * This Activity is only for AppTest.
 * This cannot be used any other room type.
 */
public class MTKRTCMainActivity extends UnityPlayerActivity /* implements LifecycleOwner */{
    private static final String TAG = MTKRTCMainActivity.class.getSimpleName();

    /***************************************************************************
     * Called from Host App.
     * @param presetModule
     * @param presetProject
     ***************************************************************************/
    public void initialize(String presetModule, String presetProject){
        MTKClient.getInstance(this).initialize(presetModule, presetProject);
        Log.i("Unity SDK initiating is done.");
    }

    /***************************************************************************
     * Called from Host App.
     * @param key
     * @param value
     **************************************************************************/
    public void sendMessage(String key, String value){
        Log.d("MTKRTCMainActivity sendMessage(). key: " + key + ", value: " + value);
        switch(key){
            case Global.MESSAGE_RESPONSE:

                String[] splits = value.split("\\$");
                String type = splits[0];

                switch(type){
                    case Global.MESSAGE_SCREEN_SHOT:
                        String fileFullPathName = splits[1];
                        MTKClient.getInstance(this).sendMessage(Global.MESSAGE_SCREEN_SHOT, fileFullPathName);
                        break;
                    default:
                        MTKClient.getInstance(this).sendMessage(key, value);
                        break;
                }
            case Global.MESSAGE_EVENT:
                MTKClient.getInstance(this).sendMessage(key, value);
                break;
            case "test_temp_activity":
                MTKClient.getInstance(this).sendMessage(key, value);
                break;
            default:
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Global.MESSAGE_WHAT_SCREEN_SHOT:
                    UnityPlayer.UnitySendMessage("methinks_sdk_manager","DispatchNativeMessage", "request$screenshot");
                    break;
                default:
                    break;
            }
        }
    };

}
