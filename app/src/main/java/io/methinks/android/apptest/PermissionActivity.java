package io.methinks.android.apptest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import io.methinks.android.rtc.MTKScreenSharingPermUtil;


public class PermissionActivity extends AppCompatActivity {
    private static final String TAG = PermissionActivity.class.getSimpleName();

    private MediaProjectionManager mediaProjectionManager;
    private boolean isGrantedOverlayPermission;
    private boolean isGrantedCapturePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        Objects.requireNonNull(getSupportActionBar()).hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        if(Global.isScreenStreamAllowed) {
            mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
            Global.mediaProjectionManager = mediaProjectionManager;
        }

        checkOverlayPermission();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("requestCode: " + requestCode + ", resultCode : " + resultCode);

        if(requestCode == Global.REQUEST_SCREEN_SHARING && resultCode == RESULT_OK){
            isGrantedCapturePermission = true;
            Global.screenCaptureIntent = data;
            Global.screenCaptureResultCode = resultCode;
            Global.isSharedScreen = true;
            ScreenSharing screenSharing = new ScreenSharing(PermissionActivity.this.getApplication());

            screenSharing.start();

            Global.screenSharing = screenSharing;
            checkExternalStoragePermission();

        }else if(requestCode == Global.REQUEST_OVERLAY_PERMISSION){
            checkOverlayPermission();
        }else if(requestCode == Global.REQUEST_SHOW_TOUCHES) {
            checkShowTouches();
        }else if(requestCode == Global.REQUEST_SHOW_DEV_GUIDE) {
            checkShowTouches();
        }else if(requestCode == Global.EXTENTION_INSTALL_DONE) {
            checkShowTouches();
        }else if(requestCode == Global.REQUEST_EXTENSION_SHOW_TOUCHES) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    checkShowTouches();
                }
            }, 500);
        }else if(requestCode == Global.REQUEST_SCREEN_SHARING){
            setResult(RESULT_CANCELED);
            checkOverlayPermission();
        }else if(requestCode == Global.REQUEST_EXTERNAL_STORAGE_PERMISSION){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    setResult(RESULT_OK);
//                    finish();
                    checkShowTouches();
                }else{
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.patcher_msg_permission_setting_menu))
                            .setPositiveButton(R.string.patcher_settings, (dialogInterface, i) -> {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", getApplicationContext().getPackageName(), null));
                                startActivityForResult(intent, Global.REQUEST_EXTERNAL_STORAGE_PERMISSION);
                                dialogInterface.cancel();
                            }).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Global.REQUEST_EXTERNAL_STORAGE_PERMISSION){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                }else{
                    if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Global.REQUEST_EXTERNAL_STORAGE_PERMISSION);
                    }else{
                        new AlertDialog.Builder(this)
                                .setMessage(getString(R.string.patcher_msg_permission_setting_menu))
                                .setPositiveButton(R.string.patcher_settings, (dialogInterface, i) -> {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.fromParts("package", getApplicationContext().getPackageName(), null));
                                    startActivityForResult(intent, Global.REQUEST_EXTERNAL_STORAGE_PERMISSION);
                                    dialogInterface.cancel();
                                }).show();
                    }
                }
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    setResult(RESULT_OK);
                    finish();
                }else{
                    finish();
                }
            }
        }/* else if(requestCode == Global.REQUEST_FOREGROUND_SERVICE) {
            Log.d("[RequestResult] : Foreground_service succeed : " + ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE));

            Global.hoverIntent = new Intent(this, HService.class);
            this.startService(Global.hoverIntent);
            Log.e("### hoverintent created! ###");
        }*/
    }

    @Override
    public void onBackPressed() {
    }


    private void checkExternalStoragePermission(){
        Log.d("Request WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE permissions");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Global.REQUEST_EXTERNAL_STORAGE_PERMISSION);
            }else{
                setResult(RESULT_OK);
                finish();
            }
        }else{
            setResult(RESULT_OK);
            finish();
        }
    }

    private boolean isGrantedOverlay(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (canDrawOverlays()) { return true; }
                else { return false; }
            } else {
                if (Settings.canDrawOverlays(this)) { return true; }
                else { return false; }
            }
        }else{ return true; }
    }

    private void checkOverlayPermission(){
        if(isGrantedOverlay()){
            isGrantedOverlayPermission = true;
            Log.d("OverlayPermission 결과: "+ isGrantedOverlayPermission);
            if(!Global.recordingMode.equals("none")){
                checkShowTouches();
            }else{
                /** 스크린레코딩 없을 시 터치포인터 enable X. 곧바로 screenshare.start() start내부에 또 isScreenStreamAllowed분기 존재. */
                // 이 경우 mtkrtc는 레코딩없이 api콜만 실행.
                ScreenSharing screenSharing = new ScreenSharing(PermissionActivity.this.getApplication());
                screenSharing.start();
                Global.screenSharing = screenSharing;
                checkExternalStoragePermission();
            }
        }else{
            isGrantedOverlayPermission = false;
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
            ((Activity) this).startActivityForResult(intent, Global.REQUEST_OVERLAY_PERMISSION);
            Log.d("Request Overlay permission to user.");
            Log.d("OverlayPermission 결과 (else): "+ isGrantedOverlayPermission);
        }
    }

    /**
     * Workaround for Android O
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean canDrawOverlays(){
        try {
            WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
            if (windowManager == null) {
                return false;
            }

            final View viewToAdd = new View(this);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT
            );
            viewToAdd.setLayoutParams(params);
            windowManager.addView(viewToAdd, params);
            windowManager.removeView(viewToAdd);

            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void checkShowTouches(){
        boolean enableShowTouches = false;
        try {
            enableShowTouches = Settings.System.getInt(getContentResolver(), "show_touches") != 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        //Log.e("ShowTouches: " + enableShowTouches);
        if(!enableShowTouches){
            new ShowTouchSetupDialogFragment(this).show(getSupportFragmentManager(), "show_touches_dialog");

            //Toast.makeText(this, getString(R.string.patcher_req_show_touch), Toast.LENGTH_LONG).show();
            Log.d("Request SHOW TOUCHES permission to user.");
        }else{
            if(!isGrantedCapturePermission){
                /** media projection and screen sharing is started when it's allowed. */
                if (Global.recordingMode.equals("full") || (Global.recordingMode.equals("default") && Global.recordTicket))
                    MTKScreenSharingPermUtil.checkPermissionCapture(this, Global.REQUEST_SCREEN_SHARING, mediaProjectionManager);
                /*Log.d("Request SCREEN CAPTURE permission to user.");
                isGrantedCapturePermission = true;
                Global.isSharedScreen = true;
                //Global.screenCaptureIntent = data;
                //Global.screenCaptureResultCode = resultCode;
                ScreenSharing screenSharing = new ScreenSharing(PermissionActivity.this.getApplication());

                screenSharing.start();

                Global.screenSharing = screenSharing;
                checkExternalStoragePermission();*/
            }
        }
    }

    @Override
    public void finish() {
        Global.completedPermission = true;
        Global.isShowingAnnouncement = true;

        Log.e(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ? "READ true" : "READ false");
        Log.e(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ? "WRITE true" : "WRITE false");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && isGrantedOverlayPermission) {
            Global.startService(this);
        }
        Log.d("Permission Activity is finished");
        super.finish();
    }
}
