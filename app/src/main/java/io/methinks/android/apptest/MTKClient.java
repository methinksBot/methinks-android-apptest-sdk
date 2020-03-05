package io.methinks.android.apptest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;

import io.methinks.android.apptest.question.QuestionPack;
import io.methinks.android.apptest.question.SurveyAlertManager;
import io.methinks.android.apptest.question.ViewConstant;


public class MTKClient implements ApplicationTracker.ActivityReadyCallback{
    private static final String TAG = MTKClient.class.getSimpleName();

    private static volatile MTKClient instance;
    private Application app;
    private Activity activity;
    private MTKRTCMainActivity unityActivity;
    private Thread timerThread;

    // about screen shot detecting
    private ScreenshotContentObserver screenShotContentObserver;
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final String[] PROJECTION = new String[]{
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns._ID
    };
    private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;
    private  static final String FILE_POSTFIX = "FROM_ASS";
    private ContentResolver contentResolver;
    private String lastPath;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            if(type.equals(Global.MESSAGE_SCREEN_SHOT)){
                if(Global.isUnity){
                    Message msg = new Message();
                    msg.what = Global.MESSAGE_WHAT_SCREEN_SHOT;
                    unityActivity.handler.sendMessage(msg);
                }
            }
        }
    };

    public MTKClient(Context context) {
        Log.d("Creating MTKClient instance...");
        if(context instanceof Application){
            this.app = (Application)context;
            Log.w("Based on Application!!!");
        }else{
            if(context instanceof Activity){
                this.app = (Application)(context.getApplicationContext());
                this.activity = (Activity)context;
                Log.w("Based on Activity!!!");
            }
            Global.isUnity = false;
            if(context instanceof MTKRTCMainActivity){
                Global.isUnity = true;
                unityActivity = (MTKRTCMainActivity)context;
            }
        }
        Global.app = app;
//        setContentObserver();
        HandlerThread handlerThread = new HandlerThread("content_observer");
        handlerThread.start();
        final Handler handler = new Handler(handlerThread.getLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        screenShotContentObserver = new ScreenshotContentObserver(handler, app.getContentResolver());
        screenShotContentObserver.register();

        //Log.d("CHECK CURRENT ACTIVITY: " + activity);

        LocalStore.getInstance().init(context);

        Global.applicationTracker = ApplicationTracker.getInstance(app);
        Global.applicationTracker.init(this);

        /*if(activity != null){
            Global.hoverIntent = new Intent(activity, HService.class);
            activity.startService(Global.hoverIntent);
            Log.e("Main hoverintent created! ");
        }*/

        LocalBroadcastManager.getInstance(app).registerReceiver(broadcastReceiver, new IntentFilter(Global.LOCAL_BROADCAST_RECEIVE_INTENT_FILTER_ACTION));



        Log.d("Completed creating MTKClient instance.");
    }


    /**
     *  get context from here, and make application context from host app
     */
    public static synchronized MTKClient getInstance(Context context) {
        if (instance == null) {
            instance = new MTKClient(context);
            Global.client = instance;
            return instance;
        }

        return instance;
    }



    private void login(){
        try {
            PackageInfo packageInfo = activity.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(activity.getApplicationContext().getPackageName(), 0 );
            String version = packageInfo.versionName;
            Log.e("########### version : " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Log.d("Try to login now...");
        if(Global.sTestUserCode == null){   // 로그인 필요. 로그인 화면 보여줌
            /*Intent loginIntent = new Intent(Global.applicationTracker.getTopActivity(), LoginActivity.class);
            Global.applicationTracker.getTopActivity().startActivity(loginIntent);*/
            if(Global.screenSharing != null && !Global.isSharedScreen){
                Intent loginIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
                Global.applicationTracker.getTopActivity().startActivity(loginIntent);
            }
            Intent loginIntent = new Intent(Global.applicationTracker.getTopActivity(), LoginService.class);
            Global.applicationTracker.getTopActivity().startService(loginIntent);
        }else{  // 자동 로그인 처리
            JSONObject deviceInfo = DeviceInfo.getDeviceInfo(app);
            JSONObject lastSessionLog = LocalStore.getInstance().getSessionLog();
            JSONObject lastAnswer = LocalStore.getInstance().getAnswer();

            new HttpManager().login(deviceInfo, lastSessionLog, lastAnswer, new HttpManager.Callback() {
                @Override
                public void done(JSONObject response, String error) {
                    try{
                        if(response != null && response.has("status") && response.getString("status").equals(Global.RESPONSE_OK)){
                            Log.d("AppTest user is completed login. : " + response);
                            LocalStore.getInstance().putTestUserCode(Global.sTestUserCode);


                            JSONObject result = response.getJSONObject("result");
                            Global.isScreenStreamAllowed = result.has("isScreenStreamAllowed") && result.getBoolean("isScreenStreamAllowed");
                            Global.sLoginTime = new Date().getTime() / 1000;
                            Global.sessionStartTime = Global.sLoginTime;
                            Global.sForegroundTime = Global.sLoginTime;
                            Global.loginResult = response.getJSONObject("result");
                            Global.sCampaignParticipantId = result.getString("participantId");
                            Global.sUserId = result.getString("userId");
                            Global.sScreenName = result.getString("screenName");
                            Global.sId = Global.generateRandomString();
                            Global.isLogined = true;
                            Global.isNew = true;

                            Intent announcementIntent = new Intent(Global.applicationTracker.getTopActivity(), AnnouncementActivity.class);
                            Global.applicationTracker.getTopActivity().startActivity(announcementIntent);
                        }else{
                            Log.e("AppTest user can't login to AppTest server now.");
                            LocalStore.getInstance().reset();
                            Global.sTestUserCode = null;
                            login();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }

        startTimer();
    }

    /**
     * set QuestionPacks to main QuestionPack queue.
     */
    private void setQuestionPacks(){
        try{
            JSONArray sessionBasedPacks = Global.loginResult.has("sessionBasedSurveyPacks") ? Global.loginResult.getJSONArray("sessionBasedSurveyPacks") : null;
            JSONArray timeBasedPacks = Global.loginResult.has("timeBasedSurveyPacks") ? Global.loginResult.getJSONArray("timeBasedSurveyPacks") : null;

            if(sessionBasedPacks != null){
                for(int i = 0; i < sessionBasedPacks.length(); i++){
                    JSONObject pack = sessionBasedPacks.getJSONObject(i);
                    Global.packQueue.add(new QuestionPack(pack, QuestionPack.SESSION_BASED_TYPE));
                }
            }
            if(timeBasedPacks != null){
                for(int i = 0; i < timeBasedPacks.length(); i++){
                    JSONObject pack = timeBasedPacks.getJSONObject(i);
                    Global.packQueue.add(new QuestionPack(pack, QuestionPack.TIME_BASED_TYPE));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * start main timer thread.
     */
    private void startTimer(){
        timerThread = new Thread(new TimerThread());
        timerThread.setDaemon(true);
        Global.timerThreadFlag = true;
        timerThread.start();
    }

    public void initializeNativeApp(String presetModule, String presetProject){
        LocalStore.getInstance().init(app);

        try{
            JSONObject presetProjectJSON = new JSONObject(presetProject);
            if(presetProjectJSON.has("id"))
                Global.sProjectId = presetProjectJSON.getString("id");
            if(presetProjectJSON.has("debug_mode"))
                Global.isDebugMode = presetProjectJSON.getBoolean("debug_mode");

            Global.isUnity = false;
            Log.i("SDK initiating is done.");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /*************************************************************************
     * Called from Host App.
     * @param presetProject project info
    /***************************************************************************/
    public void initialize(String presetProject){
        Log.i("Initiating SDK...");
        LocalStore.getInstance().init(app);

        try{
            JSONObject presetProjectJSON = new JSONObject(presetProject);

            if(presetProjectJSON.has("id"))
                Global.sProjectId = presetProjectJSON.getString("id");

            presetProjectJSON.put("debug_mode", true);
            if(presetProjectJSON.has("debug_mode")) {
                Global.isDebugMode = presetProjectJSON.getBoolean("debug_mode") ? true : false;
                Global.isDebugModeFromInspector = presetProjectJSON.getBoolean("debug_mode");
            }

            if(unityActivity != null && Global.isUnity){
                Global.applicationTracker.addManually(unityActivity, this);
            }

            Log.d("SDK initiating is done.");
        }catch (JSONException e){
            e.printStackTrace();

        }
    }

    /***************************************************************************
     * Called from Host App.
     * @param presetModule ""
     * @param presetProject project info
     ***************************************************************************/
    public void initialize(String presetModule, String presetProject){
        Log.i("Initiating SDK...");
        LocalStore.getInstance().init(app);

        try{
            JSONObject presetProjectJSON = new JSONObject(presetProject);

            if(presetProjectJSON.has("id"))
                Global.sProjectId = presetProjectJSON.getString("id");

            if(presetProjectJSON.has("debug_mode")) {
                Global.isDebugMode = presetProjectJSON.getBoolean("debug_mode") ? true : false;
                Global.isDebugModeFromInspector = presetProjectJSON.getBoolean("debug_mode");
            }

            if(unityActivity != null && Global.isUnity){
                Global.applicationTracker.addManually(unityActivity, this);
//                Lifecycle.State lifecycle = unityActivity.getLifecycle().getCurrentState();
//                if(lifecycle != Lifecycle.State.CREATED && lifecycle != Lifecycle.State.STARTED){
//
//                }
            }

            Log.d("SDK initiating is done.");
        }catch (JSONException e){
            e.printStackTrace();

        }
    }

    /***************************************************************************
     * Called from Host App.
     * @param key type
     * @param value value
     **************************************************************************/
    public void sendMessage(String key, String value){
        Log.e("key : " + key + ", value : " + value);
        if(key.equals(Global.MESSAGE_SCREEN_SHOT)){ // detect screen shot
            Intent intent1 = new Intent(Global.applicationTracker.getTopActivity(), ReportActivity.class);
            intent1.putExtra("fileFullPath", value);    // value is fileFullPath
            intent1.putExtra("isBugReport", Global.lastReportTypeIsBug);
            Global.applicationTracker.getTopActivity().startActivity(intent1);
        }else if(key.equals(Global.MESSAGE_EVENT)){ // from event trigger
            Log.callEvent(key, value);
            new HttpManager().event(value, (response, error) -> {
                try{
                    if(response != null && error == null){
                        if(response.has("result") && response.getString("status").equals(Global.RESPONSE_OK)){
                            JSONObject pack = response.getJSONObject("result");
                            Global.eventQuestionPack = new QuestionPack(pack, QuestionPack.EVENT_TYPE);
                            if(!Global.isShowingReport && !Global.isShowingQuestion){
                                Global.isShowingQuestion = true;
                                Global.eventQuestionPack.fetch(() -> {
                                    ViewConstant.pack = Global.eventQuestionPack;
                                    Global.hover.setInvisible();
                                    Global.hoverPopup.setInvisible();
                                    Global.hoverPopup.isOpened = false;

                                    Log.e("sendMessage isRequired : " + Global.eventQuestionPack.isRequired());
                                    new Handler(Looper.getMainLooper()).post(() -> SurveyAlertManager.showDialog(Global.applicationTracker.getTopActivity(), Global.eventQuestionPack.isRequired()));
                                });
                            }
                        }
                    }else{
                        Log.e(error);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            });
        }else if(key.equals(Global.MESSAGE_RESET_SDK)){

            if(Global.screenSharing != null){
                Global.screenSharing.finish();
            }
            if(Global.hoverPopup != null && Global.hoverPopup.isOpened){
                Global.hoverPopup.setInvisible();

            }
            if(Global.hover != null){
                Global.hover.setInvisible();
            }

            if(timerThread != null){
                Global.timerThreadFlag = false;
                timerThread.interrupt();
            }
            Global.applicationTracker.getTopActivity().stopService(Global.hoverIntent);
            LocalBroadcastManager.getInstance(app).unregisterReceiver(broadcastReceiver);

            LocalStore.getInstance().reset();
            Global.applicationTracker.shutDownApp();

        }else if(key.equals("test_temp_activity")){
            Intent intent = new Intent(activity, TempActivity.class);
            activity.startActivity(intent);
        }
    }

    @Override
    public void readyActivity(Activity activity) {
        Log.d("First Activity is ready now:" + activity.getClass().getSimpleName());

        LocalStore.getInstance().init(app);

        /**
         *  Setting Env (dev / prod) from client side
         * */
        if (Global.isDebugModeFromInspector) {
            LinearLayout mainContainer = new LinearLayout(activity);
            ViewGroup.LayoutParams mainParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mainContainer.setOrientation(LinearLayout.VERTICAL);
            mainContainer.setLayoutParams(mainParams);
            LinearLayout toggleContainer = new LinearLayout(activity);
            toggleContainer.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams toggleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            toggleParams.setMargins(50, 50, 50, 50);
            toggleContainer.setLayoutParams(toggleParams);

            EditText projectIdEditText = new EditText(activity);
            if(!TextUtils.isEmpty(LocalStore.getInstance().getDevProjectId())){
                projectIdEditText.setText(LocalStore.getInstance().getDevProjectId());
            }else if(!TextUtils.isEmpty(Global.sProjectId)){
                projectIdEditText.setText(Global.sProjectId);
            }else{
                projectIdEditText.setHint("Project id.");
            }
            LinearLayout.LayoutParams projectEditTextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            projectEditTextParams.setMargins(50, 50, 50, 50);
            projectIdEditText.setLayoutParams(projectEditTextParams);
            Button debugToggleButton = new Button(activity);
            Button releaseToggleButton = new Button(activity);
            LinearLayout.LayoutParams toggleButtonParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            toggleButtonParams.weight = 0.5f;
            debugToggleButton.setLayoutParams(toggleButtonParams);
            releaseToggleButton.setLayoutParams(toggleButtonParams);
            debugToggleButton.setTextColor(Color.WHITE);
            releaseToggleButton.setTextColor(Color.WHITE);
            debugToggleButton.setBackgroundColor(activity.getResources().getColor(R.color.cornflower));
            releaseToggleButton.setBackgroundColor(Color.LTGRAY);
            debugToggleButton.setText("Dev");
            releaseToggleButton.setText("Prod");
            debugToggleButton.setOnClickListener(view -> {
                debugToggleButton.setBackgroundColor(activity.getResources().getColor(R.color.cornflower));
                releaseToggleButton.setBackgroundColor(Color.LTGRAY);
                Global.isDebugMode = true;
            });
            releaseToggleButton.setOnClickListener(view -> {
                debugToggleButton.setBackgroundColor(Color.LTGRAY);
                releaseToggleButton.setBackgroundColor(activity.getResources().getColor(R.color.cornflower));
                Global.isDebugMode = false;
            });

            toggleContainer.addView(debugToggleButton);
            toggleContainer.addView(releaseToggleButton);
            mainContainer.addView(projectIdEditText);
            mainContainer.addView(toggleContainer);

            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setView(mainContainer)
                    .setPositiveButton("Ok", (dialogInterface, i) -> {}).create();

            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Log.d("Show Project Id and UserCode input AlertDialog For debug.");
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
                if (!TextUtils.isEmpty(projectIdEditText.getText().toString()) && !TextUtils.isEmpty(projectIdEditText.getText().toString().trim())) {
                    Global.sProjectId = projectIdEditText.getText().toString().trim();
                    LocalStore.getInstance().putDevProjectId(Global.sProjectId);
                    dialog.dismiss();
                    getClientLogo();
                    Log.d("Dismiss Project Id and UserCode input AlertDialog For debug.");
                } else {
                    Toast.makeText(activity, "Please, provide project id", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            getClientLogo();
        }
        getClientLogo();
    }

    private void getClientLogo(){
        new HttpManager().getClientLogo((response, errorMsg) -> {
            Log.d("Received client logo.");

            try{
                if(response != null && errorMsg == null){
                    if(response.has("result") && response.getString("status").equals(Global.RESPONSE_OK)){
                        String savedLogoURL = LocalStore.getInstance().getLogoURL();
                        String logoURL = response.getJSONObject("result").getString("url");
                        if(savedLogoURL != null && savedLogoURL.equals(logoURL)){ // 이전에 캐싱한 로고 이미지와 같을 때
                            String encodedImage = LocalStore.getInstance().getLogoImage();
                            if(TextUtils.isEmpty(encodedImage)){
                                new HttpManager().getImage(savedLogoURL, new HttpManager.ImageCallback() {
                                    @Override
                                    public void done() {
                                        login();
                                    }
                                });
                            }else{
                                byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Global.logoBitmap = decodedByte;
                                login();
                            }
                        }else{  // 이전에 캐싱한 로고 이미지와 다를 때
                            LocalStore.getInstance().putLogoURL(logoURL);
                            LocalStore.getInstance().putLogoImage(null);
                            new HttpManager().getImage(logoURL, new HttpManager.ImageCallback() {
                                @Override
                                public void done() {
                                    login();
                                }
                            });
                        }
                        Global.sLogoURL = logoURL;
                    }else{  // show methinks logo
                        LocalStore.getInstance().putLogoURL(null);
                        LocalStore.getInstance().putLogoImage(null);
                        login();
                    }
                }else if(errorMsg != null){
                    Log.e(errorMsg);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onAppForeground() {
        Log.d("App state: Foreground");

        if(screenShotContentObserver != null){
            screenShotContentObserver.register();
        }

        if(Global.screenSharing != null && !Global.isSharedScreen){
            Intent loginIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
            Global.applicationTracker.getTopActivity().startActivity(loginIntent);
        }

        /** Permission 클래스를 통해 */
        if(activity != null){
            Global.hoverIntent = new Intent(activity, HService.class);
            activity.startService(Global.hoverIntent);
            Log.e("Main hoverintent created! ");
        }

        if(LocalStore.getInstance().getSessionLog() != null) {
            Log.w("left session log send to server : " + LocalStore.getInstance().getSessionLog());
            new HttpManager().log(LocalStore.getInstance().getSessionLog(), null);
        }

        Global.sessionStartTime = new Date().getTime() / 1000;
        startTimer();

        if(!Global.isLogined){
            getClientLogo();
        }

        if(Global.completedPermission && Global.applicationTracker != null && Global.applicationTracker.getTopActivity() != null){
            Global.startService(Global.applicationTracker.getTopActivity());
        }

        LocalBroadcastManager.getInstance(app).registerReceiver(broadcastReceiver, new IntentFilter(Global.LOCAL_BROADCAST_RECEIVE_INTENT_FILTER_ACTION));
    }

    @Override
    public void onAppBackground() {
        Log.d("App state: Background");
        if(screenShotContentObserver != null){
            screenShotContentObserver.unregister();
        }
        if(Global.isLogined){
            JSONObject sessionJSON = LocalStore.getInstance().getSessionLog();

            new HttpManager().log(sessionJSON, new HttpManager.Callback() {
                @Override
                public void done(JSONObject response, String error) {
                    if(response != null && response.has("status") && response.optString("status").equals(Global.RESPONSE_OK)){
                        LocalStore.getInstance().putSessionLog(null);
                        Global.isNew = false;
                    }
                }
            });
        }

        if(Global.isSharedScreen && Global.screenSharing != null){
                Global.screenSharing.finish();
                Global.isSharedScreen = false;
        }

        if(Global.hoverPopup != null && Global.hoverPopup.isOpened){
            Global.hoverPopup.setInvisible();

        }
        if(Global.hover != null){
            Global.hover.setInvisible();
        }

        if(timerThread != null){
            Global.timerThreadFlag = false;
            timerThread.interrupt();
        }




        Global.applicationTracker.getTopActivity().stopService(Global.hoverIntent);
        LocalBroadcastManager.getInstance(app).unregisterReceiver(broadcastReceiver);
    }

    /**
     * Main Timer Thread
     * 1. Set question packs to main queue.
     * 2. Save session log to SharedPreference(Local storage).
     * 3. Check QuestionPack to show.
     */
    class TimerThread implements Runnable {
        @Override
        public void run() {
            Log.d("Start TimerThread.");
            while(Global.timerThreadFlag){
                if(Global.isLogined && !Global.applicationTracker.isBackground()){
                    try {
                        // 1. Set question packs
                        if(Global.packQueue == null){
                            Global.packQueue = new LinkedList<>();
                            setQuestionPacks();
                        }

                        Global.sForegroundTime = new Date().getTime() / 1000;
                        // 2. Save sessionJSON object to local store.
                        JSONObject sessionJSON = createSessionJSON();
                        if(sessionJSON != null)
                            LocalStore.getInstance().putSessionLog(sessionJSON);


                        // 3. Check QuestionPack to show.
                        if(Global.completedPermission && !Global.isShowingReport && Global.packQueue.size() > 0 && !Global.isShowingQuestion && !Global.isShowingAnnouncement){
                            Global.isShowingQuestion = true;
                            QuestionPack pack = Global.packQueue.peek();
                            if(Global.eventQuestionPack != null){
                                Global.eventQuestionPack.fetch(() -> {
                                    ViewConstant.pack = Global.eventQuestionPack;

                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Global.hover.setInvisible();
                                        Global.hoverPopup.setInvisible();
                                        Global.hoverPopup.isOpened = false;

                                        SurveyAlertManager.showDialog(Global.applicationTracker.getTopActivity(), Global.eventQuestionPack.isRequired());
                                    });
                                });
                            }else if(pack.getType().equals(QuestionPack.SESSION_BASED_TYPE)){ // session based type
                                if(pack.isFetched() && Global.loginResult.getInt("sessionCount") >= pack.getSession()){
                                    ViewConstant.pack = pack;

                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Global.hover.setInvisible();
                                        Global.hoverPopup.setInvisible();
                                        Global.hoverPopup.isOpened = false;

                                        SurveyAlertManager.showDialog(Global.applicationTracker.getTopActivity(), pack.isRequired());
                                    });
                                }

                            }else if(pack.getType().equals(QuestionPack.TIME_BASED_TYPE)){  // time based type
                                if(Global.loginResult.has("totalSessionTime")){
                                    if(pack.isFetched() && Global.loginResult.getLong("totalSessionTime") > pack.getTime()){
                                        ViewConstant.pack = pack;
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            Global.hover.setInvisible();
                                            Global.hoverPopup.setInvisible();
                                            Global.hoverPopup.isOpened = false;

                                            SurveyAlertManager.showDialog(Global.applicationTracker.getTopActivity(), pack.isRequired());
                                        });
                                    }
                                }
                            }
                        }

                        /*// 4. check hover button.
                        if(Global.hoverIntent == null){
                            if(activity != null){
                                Global.hoverIntent = new Intent(activity, HService.class);
                                if(Global.applicationTracker != null && !Global.applicationTracker.isBackground()){
                                    Global.startService(activity);
                                }
                            }
                        }

                        // 5. check ApplicationTracker object.
                        if(Global.applicationTracker == null){
                            if(app != null){
                                Global.applicationTracker = ApplicationTracker.getInstance(app);
                                Global.applicationTracker.init(MTKClient.this);
                                Global.applicationTracker.addManually(unityActivity, MTKClient.this);
                            }
                        }*/

                        Thread.sleep(10000);
                    }catch (InterruptedException e){

                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {

                    }
                }
            }
        }

        /**
         * {
         *     "session": {
         *         "log": [<start time in second, epoch>, <end time in second, epoch>],
         *         "isNew": <bool>,
         *         "sid": "<random session id provided by client>" // optional
         *     }
         * }
         * @return
         */
        private JSONObject createSessionJSON(){
            try{

                JSONObject sessionJSON = new JSONObject();
                JSONArray logJSONArray = new JSONArray();
                logJSONArray.put(Global.sessionStartTime);
                logJSONArray.put(Global.sForegroundTime);
                sessionJSON.put("log", logJSONArray);
                sessionJSON.put("isNew", Global.isNew);
                if(TextUtils.isEmpty(Global.sId)){
                    LocalStore.getInstance().getLastSId();
                }else{
                    sessionJSON.put("sid", Global.sId);
                }
                Log.w("updated session : " + sessionJSON);

                return sessionJSON;
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }
    }
//
//    private void setContentObserver(){
//        HandlerThread handlerThread = new HandlerThread("content_observer");
//        handlerThread.start();
//        final Handler handler = new Handler(handlerThread.getLooper()) {
//
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//            }
//        };
//
//
//        contentResolver = app.getContentResolver();
//        contentResolver.registerContentObserver(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                true,
//                new ContentObserver(handler) {
//                    @Override
//                    public boolean deliverSelfNotifications() {
//                        Log.w("deliverSelfNotifications");
//                        return super.deliverSelfNotifications();
//                    }
//
//                    @Override
//                    public void onChange(boolean selfChange) {
//                        super.onChange(selfChange);
//                    }
//
//                    @Override
//                    public void onChange(boolean selfChange, Uri uri) {
//                        super.onChange(selfChange, uri);
//                        if (uri.toString().startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
//                            try {
//                                Data result = getLatestData(uri);
//                                if(result != null && !(lastPath != null && lastPath.equals(result.path))){
//                                    long currentTime = System.currentTimeMillis() / 1000;
//                                    if (matchPath(result.path) && matchTime(currentTime, result.dateAdded)) {
//                                        lastPath = result.path;
//                                        if(Global.applicationTracker != null && Global.applicationTracker.getTopActivity() != null){
//                                            new AlertDialog.Builder(Global.applicationTracker.getTopActivity())
//                                                    .setMessage(app.getString(R.string.patcher_msg_illegal_screenshot))
//                                                    .setPositiveButton(app.getString(R.string.patcher_confirm), (dialogInterface, i) -> dialogInterface.dismiss()).show();
//                                        }
//                                    }
//                                }
//                            } catch (Exception e) {
//                            }
//                        }
//                    }
//                }
//        );
//    }
//
//    private boolean matchPath(String path) {
//        return (path.toLowerCase().contains("screenshots/") && !path.contains(FILE_POSTFIX));
//    }
//
//    private boolean matchTime(long currentTime, long dateAdded) {
//        return Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS;
//    }
//
//
//    private Data getLatestData(Uri uri) throws Exception {
//        Data data = null;
//        Cursor cursor = null;
//        try {
//            cursor = contentResolver.query(uri, PROJECTION, null, null, SORT_ORDER);
//            if (cursor != null && cursor.moveToFirst()) {
//                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
//                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
//                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
//
//                if (fileName.contains(FILE_POSTFIX)) {
//                    if (cursor.moveToNext()) {
//                        id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
//                        fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
//                        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                        dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
//                    } else {
//                        return null;
//                    }
//                }
//
//                data = new Data();
//                data.id = id;
//                data.fileName = fileName;
//                data.path = path;
//                data.dateAdded = dateAdded;
//                Log.e("[Recent File] Name : " + fileName);
//            }
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//        return data;
//    }
//
//    class Data {
//        long id;
//        String fileName;
//        String path;
//        long dateAdded;
//    }

}
