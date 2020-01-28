package io.methinks.android.apptest;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class LocalStore {
    private Context context;

    public static LocalStore getInstance(){
        return LocalStore.LazyHolder.INSTANCE;
    }

    public void init(Context context){
        this.context = context;

        Global.sLogoURL = getLogoURL();
        Global.sTestUserCode = getTestUserCode();

    }

    private static class LazyHolder{
        private static final LocalStore INSTANCE = new LocalStore();
    }

    public String getDevProjectId(){
        return getString(Global.LOCAL_STORE_DEV_PROJECT_ID);
    }

    public void putDevProjectId(String devProjectId){
        putString(Global.LOCAL_STORE_DEV_PROJECT_ID, devProjectId);
    }

    public String getLastCaptureImage(){
        return getString(Global.LOCAL_STORE_LAST_CAPTURED_IMAGE_KEY);
    }

    public void putLastCaptureImage(String encoded){
        putString(Global.LOCAL_STORE_LAST_CAPTURED_IMAGE_KEY, encoded);
    }

    public String getLogoImage(){
        return getString(Global.LOCAL_STORE_LOGO_IMAGE_KEY);
    }

    public void putLogoImage(String logoURL){
        putString(Global.LOCAL_STORE_LOGO_IMAGE_KEY, logoURL);
    }

    public JSONObject getAnswer(){
        String sessionLogStr = getString(Global.LOCAL_STORE_ANSWER_KEY);
        if(sessionLogStr != null){
            try {
                JSONObject sessionLogJSON = new JSONObject(sessionLogStr);

                return sessionLogJSON;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void putAnswer(JSONObject answer){
        putString(Global.LOCAL_STORE_ANSWER_KEY, answer.toString());
    }

    public JSONObject getSessionLog(){
        String sessionLogStr = getString(Global.LOCAL_STORE_SESSION_LOG_KEY);
        if(sessionLogStr != null){
            try {
                JSONObject sessionLogJSON = new JSONObject(sessionLogStr);

                return sessionLogJSON;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void putSessionLog(JSONObject sessionLog){
        if(sessionLog == null){
            putString(Global.LOCAL_STORE_SESSION_LOG_KEY, null);
        }else{
            putString(Global.LOCAL_STORE_SESSION_LOG_KEY, sessionLog.toString());
        }

    }

    public String getLogoURL(){
        return getString(Global.LOCAL_STORE_LOGO_URL_KEY);
    }

    public void putLogoURL(String logoURL){
        putString(Global.LOCAL_STORE_LOGO_URL_KEY, logoURL);

        Global.sLogoURL = logoURL;
    }

    public String getTestUserCode(){
        return getString(Global.LOCAL_STORE_TEST_USER_CODE_KEY);
    }

    public void putTestUserCode(String testUserCode){
        putString(Global.LOCAL_STORE_TEST_USER_CODE_KEY, testUserCode);

        Global.sTestUserCode = testUserCode;
    }

    public String getLastSId(){
        JSONObject lastSessionLog = getSessionLog();
        return lastSessionLog.optString("sid");
    }

    public void reset(){
        SharedPreferences pref = context.getSharedPreferences(Global.LOCAL_STORE_TYPE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    private void putString(String k, String v){
        SharedPreferences pref = context.getSharedPreferences(Global.LOCAL_STORE_TYPE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if(v == null){
            editor.remove(k);
        }else{
            editor.putString(k, v);
        }
        editor.commit();

    }

    private String getString(String k){
        SharedPreferences pref = context.getSharedPreferences(Global.LOCAL_STORE_TYPE, Context.MODE_PRIVATE);
        return pref.getString(k, null);
    }
}
