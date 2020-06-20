package io.mtksdk.inappsurvey;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.mtksdk.inappsurvey.converter.Question;
import io.mtksdk.inappsurvey.converter.Section;

public class SurveyAlertManager extends AppCompatActivity {


    protected static Context thirdUser;
    protected static NetworkManager nm = new NetworkManager();
    protected static ViewControllerManager vcm = new ViewControllerManager();

    public static void showDialog(final Context act, final JSONObject response, final String packId) {
        thirdUser = act;
        ViewConstant.packId = packId;

        for (int i = 0; i < response.optJSONObject("result").optJSONArray("sections").length(); i++) {
            String sectionId = response.optJSONObject("result").optJSONArray("sections").optJSONObject(i).optJSONObject("section").optString("objectId");

            if (i == 0) {
                ViewConstant.firstSectionId = sectionId;
            }
            JSONArray questionArr = response.optJSONObject("result").optJSONArray("sections").optJSONObject(i).optJSONArray("questions");
            JSONArray sectionLayout = response.optJSONObject("result").optJSONArray("sections").optJSONObject(i).optJSONObject("section").optJSONArray("sectionLayout");
            if (questionArr != null && sectionLayout != null) {
                Section newSec = new Section(sectionId, questionArr, sectionLayout);
                newSec.createQuestionObject();
                ViewConstant.sectionContainer.put(sectionId, newSec);
            }
        }

        /*Question testQ = ViewConstant.sectionContainer.get(ViewConstant.firstSectionId).getQuestionPacks().get(2);
        Log.i("first Question", testQ.getText() + " " + testQ.getQuestionType());*/

        Intent myIntent = new Intent(thirdUser, ViewControllerManager.class);
        thirdUser.startActivity(myIntent);
    }

    public static void showDialog(final Context act, final JSONObject response) {
        thirdUser = act;

        for (int i = 0; i < response.optJSONObject("result").optJSONArray("data").length(); i++) {
            String sectionId = response.optJSONObject("result").optJSONArray("data").optJSONObject(i).optJSONObject("section").optString("objectId");

            if (i == 0) {
                ViewConstant.firstSectionId = sectionId;
            }
            JSONArray questionArr = response.optJSONObject("result").optJSONArray("data").optJSONObject(i).optJSONArray("questions");
            JSONArray sectionLayout = response.optJSONObject("result").optJSONArray("data").optJSONObject(i).optJSONObject("section").optJSONArray("sectionLayout");
            if (questionArr != null && sectionLayout != null) {
                Section newSec = new Section(sectionId, questionArr, sectionLayout);
                newSec.createQuestionObject();
                ViewConstant.sectionContainer.put(sectionId, newSec);
            }
        }

        Question testQ = ViewConstant.sectionContainer.get(ViewConstant.firstSectionId).getQuestionPacks().get(2);
        Log.i("first Question", testQ.getText() + " " + testQ.getQuestionType());

        Intent myIntent = new Intent(thirdUser, ViewControllerManager.class);
        thirdUser.startActivity(myIntent);
    }


    /*public static void showDialog(final Context act, final String aKey, final String packId, final String devId) {
        thirdUser = act;

        ViewConstant.apiKey = aKey;
        ViewConstant.deviceId = devId;
        ViewConstant.packId = packId;

        if (ViewConstant.questions != null && !ViewConstant.questions.isEmpty()) {
            ViewConstant.questions.clear();
        }
        try {
            nm.getInAppSurveySections(ViewConstant.apiKey, ViewConstant.deviceId, ViewConstant.packId, new NetworkManager.CallbackInterface() {
                @Override
                public void onDownloadSuccess(boolean success, JSONObject response) {
                    Log.i("surveyQ", response.toString());

                    for (int i = 0; i < response.optJSONObject("result").optJSONArray("data").length(); i++) {
                        String sectionId = response.optJSONObject("result").optJSONArray("data").optJSONObject(i).optJSONObject("section").optString("objectId");

                        if (i == 0) {
                            ViewConstant.firstSectionId = sectionId;
                        }
                        JSONArray questionArr = response.optJSONObject("result").optJSONArray("data").optJSONObject(i).optJSONArray("questions");
                        JSONArray sectionLayout = response.optJSONObject("result").optJSONArray("data").optJSONObject(i).optJSONObject("section").optJSONArray("sectionLayout");
                        if (questionArr != null && sectionLayout != null) {
                            Section newSec = new Section(sectionId, questionArr, sectionLayout);
                            newSec.createQuestionObject();
                            ViewConstant.sectionContainer.put(sectionId, newSec);
                        }
                    }

                    Question testQ = ViewConstant.sectionContainer.get(ViewConstant.firstSectionId).getQuestionPacks().get(2);
                    Log.i("first Question", testQ.getText() + " " + testQ.getQuestionType());

                    Intent myIntent = new Intent(thirdUser, ViewControllerManager.class);
                    thirdUser.startActivity(myIntent);
                }

                @Override
                public void onDownloadFail(boolean fail, Throwable e) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static JSONObject createJSObj(String key, String value) {
        JSONObject temp = new JSONObject();
        try {
            temp.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp;
    }
}
