package io.methinks.android.apptest;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.mtksdk.inappsurvey.SurveyAlertManager;


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
        JSONObject surveyPack = pack.optJSONObject("surveyPack");
        packId = surveyPack.optString("objectId");
        this.type = type;
        time = surveyPack.optInt("createdAt");
        isRequired = surveyPack.optBoolean("isRequired");
    }

    public void fetch(FetchCallback callback) {
        new HttpManager().inAppSections(packId, (response, error) -> {
            try {
                if (response != null) {
                    if (response.has("status") && response.getString("status").equals(Global.RESPONSE_OK)) {
                        JSONObject result = response.optJSONObject("result");
                        if (result != null && result.has("sections") && result.optJSONArray("sections").length() > 0) {
                            isFetched = true;

                            Global.hover.setInvisible();
                            Global.hoverPopup.setInvisible();
                            Global.hoverPopup.isOpened = false;

                            Log.e("sendMessage isRequired : " + Global.eventQuestionPack.isRequired);
                            Log.e("[inAppSection]" + result);

                            //sectionsParser(result);
                            //startSurveyActivity(result);

                            new Handler(Looper.getMainLooper()).post(() -> SurveyAlertManager.showDialog(Global.applicationTracker.getTopActivity(), response.toString(),
                                    packId, isRequired, Global.sProjectId, Global.sTestUserCode, Global.isDebugMode, Global.isDebugMode ? Global.DEV_PATCHER_SERVER_URL : Global.PROD_PATCHER_SERVER_URL));
                            callback.done();

                        }
                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        });
    }

    /*public void startSurveyActivity(JSONObject result) {
        Log.e("[Current Top Activity]: " + Global.applicationTracker.getTopActivity() + "\n" + Global.applicationTracker.getFirstActivity());

        Intent sendIntent = new Intent(Global.applicationTracker.getTopActivity(), SurveyActivity.class);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Global.SURVEY_SECTIONS, result.toString());
        sendIntent.putExtra(Global.SURVEY_INIT_PACK, packId);
        // Start the activity
        Global.applicationTracker.getTopActivity().startActivity(sendIntent);
    }*/

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

    public interface FetchCallback{
        void done();
    }
}
