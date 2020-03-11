package io.methinks.android.apptest.question;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Surface;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import io.methinks.android.apptest.Log;


public class SurveyAlertManager extends AppCompatActivity {

    public static Context thirdUser;
    public static ViewControllerManager vcm = new ViewControllerManager();
    public static boolean sisRequired;

    public static void showDialog(final Context context, boolean isRequired) {
        Log.i("Show In App Survey questions");
        sisRequired = isRequired;
        thirdUser = context;
        int orientation = -2;
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        switch (windowManager.getDefaultDisplay().getRotation()){
            case Surface.ROTATION_0:{
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                //Portrait 0
                break;
            }case Surface.ROTATION_90:{
                orientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
                //Landscape 90
                break;
            }case Surface.ROTATION_180:{
                //Portrait 180
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            }case Surface.ROTATION_270:{
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                //Landscape 270
                break;
            }
        }

        Intent myIntent = new Intent(thirdUser, ViewControllerManager.class);
        myIntent.putExtra("orientation", orientation);
        myIntent.putExtra("isQuestion", true);
        myIntent.putExtra("isRequired", isRequired);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        thirdUser.startActivity(myIntent);
    }

    public static void showReportDialog(Context context, boolean isBugReport){
        thirdUser = context;
        int orientation = -2;
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        switch (windowManager.getDefaultDisplay().getRotation()){
            case Surface.ROTATION_0:{
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                //Portrait 0
                break;
            }case Surface.ROTATION_90:{
                orientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE;
                //Landscape 90
                break;
            }case Surface.ROTATION_180:{
                //Portrait 180
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            }case Surface.ROTATION_270:{
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                //Landscape 270
                break;
            }
        }

        Intent myIntent = new Intent(thirdUser, ViewControllerManager.class);
        myIntent.putExtra("orientation", orientation);
        myIntent.putExtra("isQuestion", false);
        myIntent.putExtra("isBugReport", isBugReport);
        myIntent.putExtra("isRequired", true);
//        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        thirdUser.startActivity(myIntent);
    }

    public static JSONObject createJSObj(String key, String value) {
        JSONObject temp = new JSONObject();
        try {
            temp.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }

    @Override
    public void onBackPressed() {
        if(!sisRequired){
            super.onBackPressed();
        }

    }
}
