package io.methinks.android.apptest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginService extends Service {
    private static final String TAG = LoginService.class.getSimpleName();

    private WindowManager windowManager;
    private View bgrnd;

    private Looper serviceLooper;
    private ServiceHandler serviceHandler;


    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            getPermission();
            /** Synchronized task for drawing Login Popup */
            drawLoginPopup();
            stopSelf(msg.arg1);
        }
    }

    private void getPermission() {
        if(Global.screenSharing != null && !Global.isSharedScreen){
            Intent loginIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
            Global.applicationTracker.getTopActivity().startActivity(loginIntent);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        /*HandlerThread thread = new HandlerThread("login_start", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);*/

//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder builder;
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            String CHANNEL_ID = "login_service_channel";
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Login service", NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("Login");
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//            Log.d("[NOTIFICATION CHANNEL] :" + notificationManager);
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(channel);
//            }
//            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//        }else {
//            builder = new NotificationCompat.Builder(this);
//        }
//
//        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.methinks_apptest_logo))
//                .setSmallIcon(R.drawable.methinks_apptest_logo)
//                .setContentTitle(getString(R.string.patcher_text_apptest))
//                .setContentText(getString(R.string.patcher_msg_apptest_is_running))
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.patcher_msg_methinks_apptest_is_running)))
//                .setOngoing(true);
//
//        startForeground(1, builder.build());
//
        getPermission();
        drawLoginPopup();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

//        Message msg = serviceHandler.obtainMessage();
//        msg.arg1 = startId;
//        serviceHandler.sendMessage(msg);
//        if(Global.screenSharing != null && !Global.isSharedScreen){
//            Intent loginIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
//            Global.applicationTracker.getTopActivity().startActivity(loginIntent);
//        }
//
//        drawLoginPopup();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Login succeed", Toast.LENGTH_SHORT).show();
    }

    private void drawLoginPopup() {
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);

        Global.hoverLogin = null;
        Global.hoverLogin = new HoverLogin(this);

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

        windowManager.addView(Global.hoverLogin.bgrnd, params);
        Global.hoverLogin.bgrnd.setVisibility(View.VISIBLE);
    }
}
