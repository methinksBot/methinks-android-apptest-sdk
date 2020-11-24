package io.mtksdk.inappsurvey;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.mtksdk.inappsurvey.converter.Question;
import io.mtksdk.inappsurvey.converter.Section;

/**
 * Created by kgy 2019. 9. 24.
 */
public class ViewConstant {
    public static String campId;
    public static ArrayList<JSONObject> questions = new ArrayList<>();
    public static ArrayList<Object> openEndAnswers = new ArrayList<>();
    public static JSONObject answer = new JSONObject();
    protected static NetworkManager nm = new NetworkManager();
    public static String apiKey = "";
    public static String deviceId = "";
    public static String packId = "";
    public static Boolean hasSurveyed = false;
    public static Boolean isRequired = true;
    public static LinkedHashMap<String, Section> sectionContainer = new LinkedHashMap<>();
    public static String firstSectionId = "";
    public static Boolean needToChangeSection = false;
    public static String globalCurrSectionId = "";
    public static HashMap<String, Integer> sectionIdx = new HashMap<>();
    public static String startingType = "";
    /*public static void surveyCompletion(String aKey, String devId, String packId, JSONObject answer) {
        try {
            nm.inAppSurveyCompletion(aKey, devId, packId, answer, new NetworkManager.CallbackInterface() {
                @Override
                public void onDownloadSuccess(boolean success, JSONObject response) {
                    Log.i("surveyCompletion", response.toString());
                    ViewConstant.answer = new JSONObject();
                }

                @Override
                public void onDownloadFail(boolean fail, Throwable e) {
                    Log.i("surveyCompletion", e.toString());
                    ViewConstant.answer = new JSONObject();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }*/

    public static void createAnswerForm() { //Create and send answers
        HashMap<String,HashMap<String, ArrayList<Object>>> finalAnswerMap = new HashMap<>();
        for (String sectionId : sectionContainer.keySet()) {
            ArrayList<Question> currQuesPack = sectionContainer.get(sectionId).getQuestionPacks();
//            ArrayList<HashMap<String, ArrayList<Object>>> tempAnswer = new ArrayList<>();
            HashMap<String, ArrayList<Object>> tempAnswer = new HashMap<>();
            for (int i = 0; i < currQuesPack.size(); i++) {

                if (!currQuesPack.get(i).getQuestionType().equals("intro") && !currQuesPack.get(i).getQuestionType().equals("outro")) {
                    String currQuesId = currQuesPack.get(i).getQuestionId();
                    ArrayList<Object> currPackAnswer = currQuesPack.get(i).getAnswerMap().get(currQuesId);
                    if (currPackAnswer != null && currPackAnswer.size() != 0) {
                        tempAnswer.put(currQuesId, currPackAnswer);
                    }
                }
            }
            if (tempAnswer != null && tempAnswer.size() != 0) {
                finalAnswerMap.put(sectionId, tempAnswer);
            }
        }
        JSONObject json = new JSONObject(finalAnswerMap);
        Log.i("finalAnswer", json.toString());
        setInAppAnswer(json);
//        surveyCompletion(ViewConstant.apiKey, ViewConstant.deviceId, ViewConstant.packId, json);
    }

    public static String getSurvIdView(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        if (prefs.contains(key)) {
            String survId = prefs.getString(key, null);
            return survId;
        } else {
            return null;
        }
    }

    public static String getAuthKeyView(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains(key)) {
            return prefs.getString(key, null);
        } else {
            return null;
        }
    }

    public static void setInAppAnswer(JSONObject json) {
        answer = json;
    }

    public static JSONObject getInAppAnswer() {
        return answer;
    }
}
