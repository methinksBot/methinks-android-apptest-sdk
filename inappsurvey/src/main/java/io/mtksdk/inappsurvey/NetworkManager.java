package io.mtksdk.inappsurvey;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kgy 2019. 9. 24.
 */

public class NetworkManager extends Activity {

    final String METHINKS_SDK_URL_SURVEYQUESTION = "functions/getSurveyQuestions";
    final String METHINKS_SDK_URL_COMPLETE = "functions/surveyCompletion";
    final String METHINKS_SDK_URL_GETINAPPSURVEY = "functions/getInAppSurveySections";
    final String METHINKS_SDK_URL_INAPPCOMPLETE = "functions/inAppSurveyCompletion";





    public interface CallbackInterface {
        public void onDownloadSuccess(boolean success, JSONObject response);
        public void onDownloadFail(boolean fail, Throwable e);
    }

//    public void getSurveyQuestion(String aKey, String surveyId, final NetworkManager.CallbackInterface callback) throws UnsupportedEncodingException {
//        try {
//            AsyncHttpClient clientQuestion = new AsyncHttpClient();
//
//            clientQuestion.addHeader("X-Parse-Application-Id", "mySDKAppId");
//            clientQuestion.addHeader("X-Parse-REST-API-Key", "myRESTAPIKey");
//
//            JSONObject params = new JSONObject();
//            params.put("aKey", aKey);
//            params.put("surveyId", surveyId);
//
//            StringEntity entity = new StringEntity(params.toString());
//
//
//            clientQuestion.post(this, BuildConfig.SERVER_URL + METHINKS_SDK_URL_SURVEYQUESTION, entity, "application/json", new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    callback.onDownloadSuccess(true, response);
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
//                    callback.onDownloadFail(true, e);
//
//                    Log.e("getSurveyQuestion", "Fail: " + e.toString());
//                    Log.d("getSurveyQuestion", "StatusCode :" + statusCode);
//                }
//            });
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void suveryCompletion(String aKey, String devId, String surveyId, JSONObject answer, final CallbackInterface callback) throws UnsupportedEncodingException {
//        try {
//            AsyncHttpClient clientSurvComp = new AsyncHttpClient();
//
//            clientSurvComp.addHeader("X-Parse-Application-Id", "mySDKAppId");
//            clientSurvComp.addHeader("X-Parse-REST-API-Key", "myRESTAPIKey");
//
//            JSONObject params = new JSONObject();
//            params.put("aKey", aKey);
//            params.put("devId", devId);
//            params.put("os", "android");
//            params.put("surveyId", surveyId);
//            if (answer != null && answer.length() != 0) {
//                params.put("answers", answer);
//            }
//
//            StringEntity entity = new StringEntity(params.toString());
//
//            clientSurvComp.post(this, BuildConfig.SERVER_URL + METHINKS_SDK_URL_COMPLETE, entity, "application/json", new JsonHttpResponseHandler(){
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    callback.onDownloadSuccess(true, response);
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
//                    callback.onDownloadFail(true, e);
//
//                    Log.e("suveryCompletion", "Fail: " + e.toString());
//                    Log.d("suveryCompletion", "StatusCode :" + statusCode);
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public void inAppSurveyCompletion(String aKey, String devId, String packId, JSONObject answer, final CallbackInterface callback) throws UnsupportedEncodingException {
        try {
            AsyncHttpClient clientInAppComplete = new AsyncHttpClient();

            clientInAppComplete.addHeader("X-Parse-Application-Id", "mySDKAppId");
            clientInAppComplete.addHeader("X-Parse-REST-API-Key", "myRESTAPIKey");

            JSONObject params = new JSONObject();
            params.put("aKey", aKey);
            params.put("devId", devId);
            params.put("packId", packId);
            params.put("os", "android");
            params.put("answers", answer);

            StringEntity entity = new StringEntity(params.toString());

            clientInAppComplete.post(this, "https://sdk-dev.methinks.io/parse/" + METHINKS_SDK_URL_INAPPCOMPLETE, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    callback.onDownloadFail(true, e);

                    Log.e("inAppSurveyCompletion", "Fail: " + e.toString());
                    Log.d("inAppSurveyCompletion", "StatusCode :" + statusCode);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public void inAppAnswer(String questionPackId, JSONObject answers) throws UnsupportedEncodingException {
//        if (questionPackId == null)
//            throw new NullPointerException("packId is required.");
//        else if (answers == null)
//            throw new NullPointerException("answer is required");
//
//        try {
//            AsyncHttpClient clientInAppComplete = new AsyncHttpClient();
//
//            JSONObject params = new JSONObject();
//            params.put("questionPackId", questionPackId);
//            params.put("answers", answers);
//            StringEntity entity = new StringEntity(params.toString());
//
//            final String DEV_PATCHER_SERVER_URL = "https://apptest-dev.methinks.io";
//            final String PROD_PATCHER_SERVER_URL = "https://apptest.methinks.io";
//
//            clientInAppComplete.post(this,  DEV_PATCHER_SERVER_URL + "/inAppAnswer", entity, "application/json", new JsonHttpResponseHandler() {
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    //callback.onDownloadSuccess(true, response);
//                    Log.e("#### Answer Result ####", response.toString());
//                }
//
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
//                    //callback.onDownloadFail(true, e);
//
//                    Log.e("inAppSurveyCompletion", "Fail: " + e.toString());
//                    Log.d("inAppSurveyCompletion", "StatusCode :" + statusCode);
//                }
//            });
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public void inAppAnswer(String packId, JSONObject answers) {
        Request request;

        OkHttpClient okHttpClient;

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final String DEV_PATCHER_SERVER_URL = "https://apptest-dev.methinks.io";
        final String PROD_PATCHER_SERVER_URL = "https://apptest.methinks.io";

        String serverUrl = ViewConstant.isDebug ? DEV_PATCHER_SERVER_URL : PROD_PATCHER_SERVER_URL;
        okHttpClient = new OkHttpClient();

        String url = serverUrl + "/inAppAnswer";
        JSONObject params = new JSONObject();
        try {
            params.put("packId", packId);
            params.put("answers", answers);

            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("project-name", ViewConstant.projectId)
                    .addHeader("user-code", ViewConstant.userCode)
                    .url(url)
                    .post(body)
                    .build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("#### Answer Result ####", response.toString());


                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void getInAppSurveySections(String aKey, String devId, String packId, final CallbackInterface callback) throws UnsupportedEncodingException {
        try {
            AsyncHttpClient clientInAppSurvSec = new AsyncHttpClient();

            clientInAppSurvSec.addHeader("X-Parse-Application-Id", "mySDKAppId");
            clientInAppSurvSec.addHeader("X-Parse-REST-API-Key", "myRESTAPIKey");

            JSONObject params = new JSONObject();
            params.put("aKey", aKey);
            params.put("devId", devId);
            params.put("packId", packId);
            params.put("os", "android");

            StringEntity entity = new StringEntity(params.toString());

            clientInAppSurvSec.post(this, "https://sdk-dev.methinks.io/parse/" + METHINKS_SDK_URL_GETINAPPSURVEY, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    callback.onDownloadFail(true, e);

                    Log.e("getInAppSurveySections", "Fail: " + e.toString());
                    Log.d("getInAppSurveySections", "StatusCode :" + statusCode);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
