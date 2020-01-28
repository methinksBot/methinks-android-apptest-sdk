package io.methinks.android.apptest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Surface;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceInfo {
    private static final String TAG = DeviceInfo.class.getSimpleName();

    private static boolean isEarphoneOn;
    private static AudioManager audioManager;
    private static TelephonyManager telephonyManager;

    /**
     * {
     *       "OS Version": "13.2.3",
     *       "Carrier": "T-Mobile",
     *       "Device": "iPhone10,1",
     *       "Connectivity": "WiFi Connection",
     *       "Volume Level(db)": "34%",
     *       "Battery Status": "plugged in, at 100%",
     *       "Screen Brightness": "54%",
     *       "Memory Usage": "21%",
     *       "Free Space": "56282 MB / 244130 MB",
     *       "Screen Orientation": "Unknown",
     *       "Sound Output Device": "Output on a Bluetooth A2DP device"
     *     }
     * @return
     */
    public static JSONObject getDeviceInfo(Context context){

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        }
        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                if(intentFilter.matchAction(Intent.ACTION_SCREEN_ON)){
                }else if(intentFilter.matchAction(Intent.ACTION_SCREEN_OFF)){
                }else if(intentFilter.matchAction(Intent.ACTION_HEADSET_PLUG)){
                    isEarphoneOn = (intent.getIntExtra("state", 0) > 0) ? true : false;
//                    if (isEarphoneOn)
//
//                    else
                }
            }
        }, intentFilter);


        try{
            JSONObject deviceInfo = new JSONObject();
            deviceInfo.put("OS Version", Build.VERSION.RELEASE); // done
            deviceInfo.put("Carrier", TextUtils.isEmpty(telephonyManager.getNetworkOperatorName()) ? "Unknown" : telephonyManager.getNetworkOperatorName()); // done
            deviceInfo.put("Device", Build.MODEL); // done
            deviceInfo.put("Connectivity", getNetworkClass(context));   // done
            deviceInfo.put("Volume Level(db)", getVolumeLevel() + "%"); // done
            deviceInfo.put("Battery Status", getBatteryStatus(context)); // done
            deviceInfo.put("Screen Brightness", getScreenBrightness(context) + "%");    // so weird.
            deviceInfo.put("Memory Usage", getMemoryUsage());   // done
            deviceInfo.put("Free Space", getFreeSpace());   // done
            deviceInfo.put("Screen Orientation", getScreenOrientation(context));    // done
            deviceInfo.put("Sound Output Device", getSoundOutputDevice(context));   // done

            return deviceInfo;
        }catch (JSONException e){
            e.printStackTrace();
        }

        return null;
    }

    private static String getMemoryUsage(){
        Runtime info = Runtime.getRuntime();
        long freeSize = info.freeMemory();
        long totalSize = info.totalMemory();
        long usedSize = totalSize - freeSize;

        return (int)((float)usedSize / (float)totalSize * (float)100) + "%";
    }

    private static String getFreeSpace() {
        StatFs stat1= new StatFs(Environment.getDataDirectory().getPath());
        long blockSize1 = stat1.getBlockSizeLong();
        long availableBlocks = stat1.getAvailableBlocksLong();

        StatFs stat2= new StatFs(Environment.getDataDirectory().getPath());
        long blockSize2 = stat2.getBlockSizeLong();
        long totalBlocks = stat2.getBlockCountLong();

        return ((blockSize1 * availableBlocks) / (1024 * 1024)) + " MB / " + ((blockSize2 * totalBlocks) / (1024 * 1024))+" MB ";
    }

    private static String getNetworkClass(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;

        if(networkInfo == null || !networkInfo.isConnected())
            return "-"; //not connected
        if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            return "Wifi";
        if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
        {
            int networkType = networkInfo.getSubtype();
            switch (networkType)
            {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                    return "EDGE";//"2G";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                case TelephonyManager.NETWORK_TYPE_TD_SCDMA:  //api<25 : replace by 17
                    return "3G";
                case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                case TelephonyManager.NETWORK_TYPE_IWLAN:  //api<25 : replace by 18
                case 19:  //LTE_CA
                    return "LTE";//"4G";
                default:
                    return "?";
            }
        }
        return "?";
    }

    private static int getVolumeLevel(){
        int audioStreamType = AudioManager.STREAM_MUSIC;//AudioManager.STREAM_VOICE_CALL
        int currentStreamVolume = audioManager.getStreamVolume(audioStreamType);
        int maxStreamVolume = audioManager.getStreamMaxVolume(audioStreamType);
        int volumeLevel = (currentStreamVolume * 100) / maxStreamVolume;

        return volumeLevel;
    }

    private static String getBatteryStatus(Context context){
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        String pluggedStatus = "plugged out";
        if(plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS)
            pluggedStatus = "plugged in";

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        int batteryPercent = (int)(batteryPct * 100);

        return pluggedStatus + ", "+batteryPercent+"%";
    }

    private static int getScreenBrightness(Context context){
        int screenBrightness = -1;
        try{
            screenBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        }catch(Exception e){
            e.printStackTrace();
        }

        return (screenBrightness * 100) / 255;
    }

    private static String getScreenOrientation(Context context){
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        String screenOrientation = "";
        switch (windowManager.getDefaultDisplay().getRotation()){
            case Surface.ROTATION_0:{
                //Portrait 0
                screenOrientation = "Device oriented vertically, home button on the bottom";
                break;
            }case Surface.ROTATION_90:{
                //Landscape 90
                screenOrientation = "Device oriented horizontally, home button on the right";
                break;
            }case Surface.ROTATION_180:{
                //Portrait 180
                screenOrientation = "Device oriented vertically, home button on the top";
                break;
            }case Surface.ROTATION_270:{
                //Landscape 270
                screenOrientation = "Device oriented horizontally, home button on the left";
                break;
            }
        }

        return screenOrientation;
    }

    private static String getSoundOutputDevice(Context context){
        String soundOutputDevice = "Built-in speaker on an Android device";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            if(isEarphoneOn) {
                soundOutputDevice = "Headphones";
            } else {
                if (audioManager.isBluetoothA2dpOn()) {
                    soundOutputDevice = "BluetoothA2DP";
                }

                if (audioManager.isSpeakerphoneOn()) {
                    soundOutputDevice = "Speakerphone";
                }

                if (audioManager.isBluetoothScoOn()) {
                    soundOutputDevice = "BluetoothSCO";
                }
            }
        }else{
            AudioDeviceInfo[] audioDeviceInfos = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS);
            for (int countIndex = 0; countIndex < audioDeviceInfos.length; countIndex++){
                AudioDeviceInfo audioDeviceInfo = audioDeviceInfos[countIndex];
                switch(audioDeviceInfo.getType()){
                    case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:{
                        soundOutputDevice = "Headphones";
                        break;
                    }case AudioDeviceInfo.TYPE_BLUETOOTH_A2DP:{
                        soundOutputDevice = "BluetoothA2DP";
                        break;
                    }case AudioDeviceInfo.TYPE_BLUETOOTH_SCO:{
                        soundOutputDevice = "BluetoothSCO";
                        break;
                    }case AudioDeviceInfo.TYPE_BUILTIN_EARPIECE:{
                        soundOutputDevice = "Built in Earpiece";
                        break;
                    }case AudioDeviceInfo.TYPE_BUILTIN_SPEAKER:{
                        soundOutputDevice = "Built-in speaker on an Android device";
                        break;
                    }

                }
            }
        }

        return soundOutputDevice;
    }



}
