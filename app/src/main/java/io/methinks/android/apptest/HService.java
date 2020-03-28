package io.methinks.android.apptest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import io.methinks.android.apptest.question.QuestionPack;
import io.methinks.android.apptest.question.SurveyAlertManager;
import io.methinks.android.apptest.question.ViewConstant;

public class HService extends Service {
    private static final String TAG = HService.class.getSimpleName();

    private WindowManager windowManager;
    private final IBinder binder = new LocalBinder();

    private TransparentView bgrnd;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public HService getServiceInstance(){
            return HService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String CHANNEL_ID = "horver_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Hover service", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Hover");
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            Log.d("[NOTIFICATION CHANNEL] :" + notificationManager);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        }else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.methinks_apptest_logo))
                .setSmallIcon(R.drawable.methinks_apptest_logo)
                .setContentTitle(getString(R.string.patcher_text_apptest))
                .setContentText(getString(R.string.patcher_msg_apptest_is_running))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.patcher_msg_methinks_apptest_is_running)))
                .setOngoing(true);

        startForeground(1, builder.build());
        prepareDraw();
    }

    private void initTouchPointer(){
        bgrnd = new TransparentView(this);

        LinearLayout.LayoutParams bgparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        bgrnd.setLayoutParams(bgparams);

        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
//        screenwidth = dm.widthPixels/2;
//        screenheight = dm.heightPixels/2;

        WindowManager.LayoutParams bgwparams;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            bgwparams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,//항상 최 상위. 터치 이벤트 받을 수 있음.
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);                           //투명
        }else{
            bgwparams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,//항상 최 상위. 터치 이벤트 받을 수 있음.
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT);                           //투명
        }
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE); //윈도 매니저
        windowManager.addView(bgrnd, bgwparams);  //최상위 윈도우에 뷰 넣기. permission필요.
    }

    private void prepareDraw(){
        Point point = new Point();
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        Display display;

        if (windowManager != null) {
            display = windowManager.getDefaultDisplay();
            display.getSize(point);
        }

//        initTouchPointer();
        initHover();
        initHoverPopup();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initHover(){
        Global.hover = new Hover(this);
        Global.hover.setListener(() -> {
            initHoverPopup();
            Global.hover.setVisibility(View.GONE);
            Global.hoverPopup.setVisible();
        });

        try {
            WindowManager.LayoutParams params;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                        PixelFormat.TRANSLUCENT
                );
            } else {
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                        PixelFormat.TRANSLUCENT
                );

            }
            params.gravity = Gravity.START | Gravity.TOP;
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            params.x = width - (int) convertDpToPixel(this, 24);
            params.y = height / 2;
            windowManager.addView(Global.hover, params);
            Global.hover.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e("No Overlay Permission");
        }
    }

    private void initHoverPopup(){
        Global.hoverPopup = null;
        Global.hoverPopup = new HoverPopup(this);

        WindowManager.LayoutParams params;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT
            );
        }else{
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    PixelFormat.TRANSLUCENT
            );

        }
        params.gravity = Gravity.START| Gravity.TOP;
        windowManager.addView(Global.hoverPopup.hoverPopup, params);
        Global.hoverPopup.hoverPopup.setVisibility(View.GONE);
        Global.hoverPopup.cancel.setOnClickListener(view -> {
            Global.hover.setVisible();
            Global.hoverPopup.setInvisible();
            Global.hoverPopup.isOpened = false;
        });
        if(Global.isDebugModeFromInspector){
            Global.hoverPopup.cancel.setLongClickable(true);
            Global.hoverPopup.cancel.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Global.hover.setVisible();
                    Global.hoverPopup.setInvisible();
                    Global.hoverPopup.isOpened = false;

                    new AlertDialog.Builder(Global.applicationTracker.getTopActivity())
                            .setMessage("Do you want to reset project id?")
                            .setNegativeButton("No", new     DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                }
                            })
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Global.client.sendMessage(Global.MESSAGE_RESET_SDK, "aa");
                                    dialogInterface.dismiss();
                                }
                            }).show();

                    return true;
                }
            });
        }

        Global.hoverPopup.bugReport.setOnClickListener(view -> {
            Global.isShowingReport = false;
            Global.lastReportTypeIsBug = true;
            if(Global.isUnity){
                requestScreenshot();
            }else{
                String encoded = takeScreenShot();
                Intent intent1 = new Intent(Global.applicationTracker.getTopActivity(), ReportActivity.class);
                //intent1.putExtra("encodedImage", encoded);
                intent1.putExtra("isUnity", false);
                intent1.putExtra("isBugReport", true);
                Global.applicationTracker.getTopActivity().startActivity(intent1);
            }

            Global.hover.setInvisible();
            Global.hoverPopup.setInvisible();
            Global.hoverPopup.isOpened = false;
        });

        Global.hoverPopup.shootevent.setOnClickListener(view -> {

        });

        Global.hoverPopup.bugReport.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Global.hoverPopup.setVisibleEvent();
                // Get Event list from server for debug
                new HttpManager().getEventTrigger((response, error) -> {
                    try{
                        if(response != null && error == null){
                            if(response.has("result") && response.getString("status").equals(Global.RESPONSE_OK)){
                                JSONArray result = response.getJSONArray("result");
                                String[] eventArray = new String[result.length()];
                                for (int i=0; i<result.length(); i++) {
                                    String object = result.getJSONObject(i).getString("eventName");
                                    eventArray[i] = object;
                                }

                                Log.e("현재 이벤트 리스트 :" + eventArray);
                            }
                        }else{
                            Log.e(error);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                });
                return false;
            }
        });

        Global.hoverPopup.suggestion.setOnClickListener(view -> {
            Global.isShowingReport = true;
            Global.lastReportTypeIsBug = false;
            if(Global.isUnity){
                requestScreenshot();
            }else{
                String encoded = takeScreenShot();
                Intent intent1 = new Intent(Global.applicationTracker.getTopActivity(), ReportActivity.class);
//                intent1.putExtra("encodedImage", encoded);
                intent1.putExtra("isUnity", false);
                intent1.putExtra("isBugReport", false);
                Global.applicationTracker.getTopActivity().startActivity(intent1);
            }

            Global.hover.setInvisible();
            Global.hoverPopup.setInvisible();
            Global.hoverPopup.isOpened = false;
        });
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Hover service Stopped", Toast.LENGTH_SHORT).show();
        windowManager.removeViewImmediate(Global.hover);
        windowManager.removeViewImmediate(Global.hoverPopup.hoverPopup);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Toast.makeText(this, "Hover service Starting", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    private void requestScreenshot(){
        Intent intent = new Intent(Global.LOCAL_BROADCAST_RECEIVE_INTENT_FILTER_ACTION);
        intent.putExtra("type", Global.MESSAGE_SCREEN_SHOT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private String takeScreenShot(){
        View view = Global.applicationTracker.getTopActivity().getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        Global.applicationTracker.getTopActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        Global.applicationTracker.getTopActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int width = displaymetrics.widthPixels;
        int height = displaymetrics.heightPixels;

        Bitmap bmp = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  - statusBarHeight);
        view.destroyDrawingCache();


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        LocalStore.getInstance().putLastCaptureImage(encoded);

        return encoded;
    }

    private float convertDpToPixel(Context context, int dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (float) dp * (metrics.densityDpi / 160f);
    }
}
