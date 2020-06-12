package io.methinks.android.apptest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;

import org.json.JSONObject;

import java.util.List;
import java.util.Queue;

import io.methinks.android.apptest.question.QuestionPack;


public class Global {

    public static final int REQUEST_SCREEN_SHARING = 1111;
    public static final int REQUEST_SCREEN_SHARING_PERM = 2222;
    public static final int REQUEST_OVERLAY_PERMISSION = 3333;
    public static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 4444;
    public static final int REQUEST_SHOW_TOUCHES = 5555;
    public static final int REQUEST_FOREGROUND_SERVICE = 6666;
    public static final int REQUEST_SHOW_DEV_GUIDE = 7777;
    public static final int EXTENTION_INSTALL_DONE = 8888;
    public static final int REQUEST_EXTENSION_SHOW_TOUCHES = 9999;

    public static final int MESSAGE_WHAT_SCREEN_SHOT = 625;
    public static final int MESSAGE_WHAT_TEST_TEMP_ACTIVITY = 626;

    protected static final String LOCAL_BROADCAST_RECEIVE_INTENT_FILTER_ACTION = "mtk-hservice-action";

    protected static final String LOCAL_STORE_TYPE = "methinks";
    protected static final String LOCAL_STORE_DEV_PROJECT_ID = "dev_project_id";
    protected static final String LOCAL_STORE_LAST_CAPTURED_IMAGE_KEY = "last_captured_image";
    protected static final String LOCAL_STORE_LOGO_IMAGE_KEY = "logo_image";
    protected static final String LOCAL_STORE_LOGO_URL_KEY = "logo_url";
    protected static final String LOCAL_STORE_PROJECT_ID_KEY = "project_id";
    protected static final String LOCAL_STORE_TEST_USER_CODE_KEY = "test_user_code";
    protected static final String LOCAL_STORE_SESSION_LOG_KEY = "session_log";
    protected static final String LOCAL_STORE_ANSWER_KEY = "answer";
    protected static final String LOCAL_STORE_EXTENSION_NAME = "io.methinks.android.methinks_touchsupports";

    protected static final String JIN_PATCHER_SERVER_URL = "http://10.0.1.19:3000";
    protected static final String DEV_PATCHER_SERVER_URL = "https://apptest-dev.methinks.io";
    protected static final String PROD_PATCHER_SERVER_URL = "https://apptest.methinks.io";
    protected static final String DEV_METHINKS_SERVER_URL = "https://dev.methinks.io";
    protected static final String PROD_METHINKS_SERVER_URL = "https://methinks.io";

    protected static final String ENV_DEV = "dev";
    protected static final String ENV_PROD = "prod";

    protected static final String MESSAGE_RESPONSE = "response";
    protected static final String MESSAGE_SCREEN_SHOT = "screenshot";
    protected static final String MESSAGE_EVENT = "event";
    protected static final String MESSAGE_RESET_SDK = "sdk_reset";

    protected static final String HTTP_POST = "POST";
    protected static final String HTTP_GET = "GET";
    public static final String RESPONSE_OK = "ok";
    protected static final String RESPONSE_ERROR = "error";

    protected static final String DEFAULT_BACKGROUND_COLOR_HEX = "#FAFAFA";

    public static Application app;
    protected static MTKClient client;

    protected static ApplicationTracker applicationTracker;

    protected static String sId;
    protected static boolean timerThreadFlag;
    protected static boolean isUnity;
    protected static boolean isNew;
    protected static boolean isLogined;
    protected static boolean isSharedScreen;
    protected static boolean isScreenStreamAllowed;
    protected static boolean isShowingAnnouncement;
    protected static boolean isPlayedByEmulator;
    protected static boolean isInternalTester;
    protected static boolean hideHoverButton;
    public static boolean isShowingQuestion;
    public static boolean isShowingReport;
    protected static long sLoginTime;
    protected static long sessionStartTime;
    protected static long sForegroundTime;
    protected static String sProjectId;
    protected static String sTestUserCode;
    protected static String sLogoURL;
    protected static boolean isDebugModeFromInspector = false;
    protected static boolean isDebugMode = false;
    protected static String sCampaignParticipantId;
    protected static String sUserId;
    protected static String sScreenName;
    protected static int sSuveyCount;
    protected static int sAnnouncementCount;
    protected static boolean lastReportTypeIsBug;
    protected static boolean completedPermission;
    protected static boolean blockEmulator = false;

    // For hover
    protected static HService hService;
    protected static Intent hoverIntent;
    public static Hover hover;
    public static HoverPopup hoverPopup;
    public static HoverLogin hoverLogin;
    public static String[] eventArray;

    protected static ScreenSharing screenSharing;
    protected static MediaProjectionManager mediaProjectionManager;
    protected static MediaProjection mediaProjection;
    public static Queue<QuestionPack> packQueue;
    public static QuestionPack eventQuestionPack;
    protected static JSONObject loginResult;
    protected static Thread sTimerThread;
    protected static int screenCaptureResultCode;
    protected static Intent screenCaptureIntent;
    public static Bitmap logoBitmap;


    protected static void clear(){
    }

    protected static String generateRandomString(){
        int len = 12;
        String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder transactionId = new StringBuilder();

        for (int i = 0; i < len; i++) {
            int position = (int) Math.floor(Math.random() * charSet.length());
            transactionId.append(charSet.substring(position, position + 1));
        }

        return transactionId.toString();

    }

    protected static void startService(Activity activity){
        Log.e("########################## startService()");
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if(manager != null){
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                Log.e(HService.class.getName() + " service names : " + service.service.getClassName());
                if (HService.class.getName().equals(service.service.getClassName())) {
                    // hover service is running already.
                    return;
                }
            }
        }else{
            Log.w("Can't deal with ActivityManger");
            return;
        }

        if(Global.hoverIntent == null){
            if(Global.applicationTracker != null){
                Global.hoverIntent = new Intent(Global.applicationTracker.getFirstActivity(), HService.class);
                Log.e("hoverintent created!");
            }
        }

        if(Global.hoverIntent == null){
            throw new NullPointerException("ApplicationTracker object can't be null to create HoverService");
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                activity.startForegroundService(Global.hoverIntent);
            else
                activity.startService(Global.hoverIntent);
        }else{
            activity.startService(Global.hoverIntent);
        }
        Log.d("Started Hover Service");
    }


}
