package io.mtksdk.inappsurvey;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.mtksdk.inappsurvey.converter.Question;
import io.mtksdk.inappsurvey.converter.Section;

/**
 * Created by kgy 2019. 9. 24.
 */

public class SurveyAlertManager extends AppCompatActivity {

    protected static Context thirdUser;
    protected static NetworkManager nm = new NetworkManager();
    protected static ViewControllerManager vcm = new ViewControllerManager();

    public static void showDialog(final Context act, final String res, final String packId, boolean isRequired) {
        thirdUser = act;
        ViewConstant.packId = packId;
        ViewConstant.isRequired = isRequired;

        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(res);
            JSONObject response = (JSONObject) obj;

            if (response.containsKey("result")) {
                JSONObject currResult = (JSONObject) response.get("result");
                if (currResult.containsKey("sections")) {
                    JSONArray sections = (JSONArray) currResult.get("sections");
                    for (int i = 0; i < sections.size(); i++) {
                        JSONObject currTotalSection = (JSONObject) sections.get(i);
                        JSONObject currSecSection = (JSONObject) currTotalSection.get("section");
                        String sectionId = (String) currSecSection.get("objectId");

                        if (i == 0) {
                            ViewConstant.firstSectionId = sectionId;
                        }

                        JSONArray questionArr = (JSONArray) currTotalSection.get("questions");
                        JSONArray sectionLayout = (JSONArray) currSecSection.get("sectionLayout");
                        if (questionArr != null && questionArr.size() != 0 && sectionLayout != null && sectionLayout.size() !=0) {
                            Section newSec = new Section(sectionId, questionArr, sectionLayout, currSecSection);
                            newSec.createQuestionObject();
                            ViewConstant.sectionContainer.put(sectionId, newSec);
                        }

                    }
                    if (ViewConstant.sectionContainer.isEmpty()) {
                        return;
                    }

                    Intent myIntent = new Intent(thirdUser, ViewControllerManager.class);
                    thirdUser.startActivity(myIntent);
                }
            } else {
                Log.i("surveyAvail", "survey not available");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }



//        if (response.has("result") && response.optJSONObject("result").has("sections")) {
//            Log.i("surveyAvail", "survey available");
//            for (int i = 0; i < response.optJSONObject("result").optJSONArray("sections").length(); i++) {
//                String sectionId = response.optJSONObject("result").optJSONArray("sections").optJSONObject(i).optJSONObject("section").optString("objectId");
//
//                if (i == 0) {
//                    ViewConstant.firstSectionId = sectionId;
//                }
//                JSONArray questionArr = response.optJSONObject("result").optJSONArray("sections").optJSONObject(i).optJSONArray("questions");
//                JSONArray sectionLayout = response.optJSONObject("result").optJSONArray("sections").optJSONObject(i).optJSONObject("section").optJSONArray("sectionLayout");
//                if (questionArr != null && questionArr.length() != 0 && sectionLayout != null && sectionLayout.length() != 0) {
//                    Section newSec = new Section(sectionId, questionArr, sectionLayout);
//                    newSec.createQuestionObject();
//                    ViewConstant.sectionContainer.put(sectionId, newSec);
//                }
//            }
//
//            if (ViewConstant.sectionContainer.isEmpty()) {
//                return;
//            }
//
//            Intent myIntent = new Intent(thirdUser, ViewControllerManager.class);
//            thirdUser.startActivity(myIntent);
//        } else {
//            Log.i("surveyAvail", "survey not available");
//        }
    }

//    public static void showDialog(final Context act, final JSONObject response) {
//        thirdUser = act;
//
//        if (response.has("result") && response.optJSONObject("result").has("sections")) {
//            Log.i("surveyAvail", "survey available");
//            for (int i = 0; i < response.optJSONObject("result").optJSONArray("sections").length(); i++) {
//                String sectionId = response.optJSONObject("result").optJSONArray("sections").optJSONObject(i).optJSONObject("section").optString("objectId");
//
//                if (i == 0) {
//                    ViewConstant.firstSectionId = sectionId;
//                }
//                JSONArray questionArr = response.optJSONObject("result").optJSONArray("sections").optJSONObject(i).optJSONArray("questions");
//                JSONArray sectionLayout = response.optJSONObject("result").optJSONArray("sections").optJSONObject(i).optJSONObject("section").optJSONArray("sectionLayout");
//                if (questionArr != null && questionArr.length() != 0 && sectionLayout != null && sectionLayout.length() != 0) {
//                    Section newSec = new Section(sectionId, questionArr, sectionLayout);
//                    newSec.createQuestionObject();
//                    ViewConstant.sectionContainer.put(sectionId, newSec);
//                }
//            }
//
//            if (ViewConstant.sectionContainer.isEmpty()) {
//                return;
//            }
//
//            Intent myIntent = new Intent(thirdUser, ViewControllerManager.class);
//            thirdUser.startActivity(myIntent);
//        } else {
//            Log.i("surveyAvail", "survey not available");
//        }
//    }


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
}
