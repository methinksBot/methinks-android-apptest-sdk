package io.methinks.android.apptest;

import android.app.Service;
import android.content.Intent;
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

    private synchronized void getPermission() {
        if(Global.screenSharing != null && !Global.isSharedScreen){
            Intent loginIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
            Global.applicationTracker.getTopActivity().startActivity(loginIntent);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        HandlerThread thread = new HandlerThread("login_start", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

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
