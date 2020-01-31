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
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

import io.methinks.android.rtc.MTKScreenSharingPermUtil;


public class PermissionActivity extends AppCompatActivity {
    private static final String TAG = PermissionActivity.class.getSimpleName();

    private MediaProjectionManager mediaProjectionManager;
    private boolean isGrantedOverlayPermission;
    private boolean isGrantedCapturePermission;
    private boolean showDeveloperOption;

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
            Global.isSharedScreen = true;
            Global.screenCaptureIntent = data;
            Global.screenCaptureResultCode = resultCode;
            ScreenSharing screenSharing = new ScreenSharing(PermissionActivity.this.getApplication());
            if (Build.VERSION.SDK_INT < 29) {
                screenSharing.start();
            }
            Global.screenSharing = screenSharing;
            checkExternalStoragePermission();
        }else if(requestCode == Global.REQUEST_OVERLAY_PERMISSION){
            checkOverlayPermission();
        }else if(requestCode == Global.REQUEST_SHOW_TOUCHES){
            checkShowTouches();
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
        }
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
                if (canDrawOverlays()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (Settings.canDrawOverlays(this)) {
                    return true;
                } else {
                    return false;
                }
            }
        }else{
            return true;
        }
    }

    private void checkOverlayPermission(){
        if(isGrantedOverlay()){
            isGrantedOverlayPermission = true;
            if(Global.isScreenStreamAllowed){
                checkShowTouches();
            }else{
                checkExternalStoragePermission();
            }
        }else{
            isGrantedOverlayPermission = false;
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
            ((Activity) this).startActivityForResult(intent, Global.REQUEST_OVERLAY_PERMISSION);
            Log.d("Request Overlay permission to user.");
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
        boolean enableShowTouches = Settings.System.getInt(getContentResolver(), "show_touches", 1) != 0;
        if(!enableShowTouches){
            startActivityForResult(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS), Global.REQUEST_SHOW_TOUCHES);
            Log.d("Request SHOW TOUCHES permission to user.");
        }else{
            if(!isGrantedCapturePermission){
                MTKScreenSharingPermUtil.checkPermissionCapture(this, Global.REQUEST_SCREEN_SHARING, mediaProjectionManager);
                Log.d("Request SCREEN CAPTURE permission to user.");
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
