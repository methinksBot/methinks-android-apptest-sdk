package io.methinks.android.apptest.question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.methinks.android.apptest.Global;
import io.methinks.android.apptest.Log;

public class QuestionPack {
    public static final String SESSION_BASED_TYPE = "session";
    public static final String TIME_BASED_TYPE = "time";
    public static final String EVENT_TYPE = "event";

    private JSONArray questions;
    private String packId;
    private boolean isRequired;
    private String type;
    private int session;
    private int time;

    private int activationSession;
    private int activationTime;
    private String title;

    private boolean isFetched;

    public QuestionPack(JSONObject pack, String type) {
        this.packId = pack.optString("questionPackId");
        this.isRequired = pack.optBoolean("required", false);
//        this.isRequired = pack.optBoolean("isRequired", false);
        this.type = type;
        this.session = pack.optInt("session");
        this.time = pack.optInt("time");

        fetch();
    }

    public static String getSessionBasedType() {
        return SESSION_BASED_TYPE;
    }

    public static String getTimeBasedType() {
        return TIME_BASED_TYPE;
    }

    public static String getEventType() {
        return EVENT_TYPE;
    }

    public String getPackId() {
        return packId;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getType() {
        return type;
    }

    public int getActivationSession() {
        return activationSession;
    }

    public int getActivationTime() {
        return activationTime;
    }

    public int getSession() {
        return session;
    }

    public int getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public boolean isFetched() {
        return isFetched;
    }

    public JSONArray getQuestions() {
        return questions;
    }

    private void fetch(){
        ViewConstant.setQuestionPack(this, (response, error) -> {
            try{
                if(response != null){
                    if(response.has("status") && response.getString("status").equals(Global.RESPONSE_OK)){
                        JSONObject result = response.optJSONObject("result");
                        if(result != null && result.has("questions") && result.optJSONArray("questions").length() > 0){
                            questions = result.getJSONArray("questions");
                            isFetched = true;
                        }

                    }
                }else{
                    Log.e("Can't find questions: " + error);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }

        });
    }

    public void fetch(FetchCallback callback){
        ViewConstant.setQuestionPack(this, (response, error) -> {
            try{
                if(response != null){
                    if(response.has("status") && response.getString("status").equals(Global.RESPONSE_OK)){
//                        Log.e("response : " + response);
                        JSONObject result = response.optJSONObject("result");
                        if(result != null && result.has("questions") && result.optJSONArray("questions").length() > 0){
                            questions = result.getJSONArray("questions");

                            isFetched = true;
                            callback.done();
                        }

                    }
                }else{
                    Log.e("Can't find questions: " + error);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        });
    }
    public interface FetchCallback{
        void done();
    }
}
