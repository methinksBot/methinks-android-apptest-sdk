package io.methinks.android.apptest;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ApplicationTracker {
    private static final String TAG = ApplicationTracker.class.getSimpleName();
    private static ApplicationTracker instance;

    private Application app;
    private boolean calledReadyCallback;

    protected Application.ActivityLifecycleCallbacks callbacks;

    private boolean isBackground;

    private ArrayList<Activity> activityStack;
    private Map<String, String> activityLifes;

    public static synchronized ApplicationTracker getInstance(Application app){
        if (instance == null) {
            instance = new ApplicationTracker(app);
            return instance;
        }

        return instance;
    }

    private ApplicationTracker(Application app){
        this.app = app;
        this.activityStack = new ArrayList<>();
        this.activityLifes = new HashMap<>();
    }

    public void addManually(Activity activity, final ActivityReadyCallback callback){
        Log.d("Adding activity to Activity State Tracker manually because Activity is passed onResume() already. ");

        activityStack.add(activity);
        activityLifes.put(activity.getClass().getSimpleName(), "resume");

        isBackground = false;
        if(!calledReadyCallback){
            callback.readyActivity(activity);
            calledReadyCallback = true;
        }

        if(Global.hoverIntent == null){
            Global.hoverIntent = new Intent(activity, HService.class);
            Log.e("hoverintent created!");
        }

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }


    protected void init(final ActivityReadyCallback callback) {
        Log.d("Initiating Application State Tracker...");

        callbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                Log.d("onActivityCreated() : " + activity.getClass().getSimpleName());
                
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

                activityLifes.put(activity.getClass().getSimpleName(), "create");
                if(Global.hoverIntent == null){
                    Global.hoverIntent = new Intent(activity, HService.class);
                    Log.e("hoverintent created!");
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.d("onActivityStarted() : " + activity.getClass().getSimpleName());
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

                activityLifes.put(activity.getClass().getSimpleName(), "start");
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                Log.d("onActivityResumed() : " + activity.getClass().getSimpleName());
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                if(isBackground){
                    long diff = (new Date().getTime() / 1000) - Global.sForegroundTime;
                    if(diff > 10 * 60)
                        Global.isNew = true;

                    Global.sId = Global.generateRandomString();
                    callback.onAppForeground();
                }
                isBackground = false;
                activityStack.add(activity);
                activityLifes.put(activity.getClass().getSimpleName(), "resume");


                if(!calledReadyCallback){
                    callback.readyActivity(activity);
                    calledReadyCallback = true;
                }
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                Log.d("onActivityPaused() : " + activity.getClass().getSimpleName());
                activityLifes.put(activity.getClass().getSimpleName(), "pause");

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.d("onActivityStopped() : " + activity.getClass().getSimpleName());
                activityLifes.put(activity.getClass().getSimpleName(), "stop");

                boolean isAppBackground = true;
                for(String activityName : activityLifes.keySet()){
                    String life = activityLifes.get(activityName);
                    if(life.equals("resume")){
                        isAppBackground = false;
                        break;
                    }
                }
                if(isAppBackground) {
                    if(!isBackground){
                        isBackground = true;
                        callback.onAppBackground();
                    }
                    isBackground = true;
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {
                Log.d("onActivitySaveInstanceState() : " + activity.getClass().getSimpleName());
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.d("onActivityDestroyed() : " + activity.getClass().getSimpleName());
                activityLifes.remove(activity.getClass().getSimpleName());
                if(activityStack.size() == 1 && activityStack.get(0) == activity){
                    activityStack.clear();
                    app.unregisterActivityLifecycleCallbacks(callbacks);
                }else{
                    activityStack.remove(activity);
                }
            }

        };

        app.registerActivityLifecycleCallbacks(callbacks);
        Log.d("Application State Tracker initiating is done.");
    }

    public Activity getTopActivity(){

        if(activityStack.size() > 0) {
            return activityStack.get(activityStack.size() - 1);
        }

        return null;
    }

    public Activity getFirstActivity(){
        if(activityStack.size() > 0) {
            return activityStack.get(0);
        }

        return null;
    }

    public boolean isBackground(){
        return isBackground;
    }

    public void shutDownApp(){
        for(int i = 0; i < activityStack.size(); i++){
            activityStack.get(i).finish();
        }
    }

    protected interface ActivityReadyCallback{
        void readyActivity(Activity activity);
        void onAppBackground();
        void onAppForeground();
    }
}
