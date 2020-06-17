package io.methinks.android.apptest;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class HttpManager {
    private static final String TAG = HttpManager.class.getSimpleName();

    private String serverURL;


    public HttpManager() {
        serverURL = Global.isDebugMode ? Global.DEV_PATCHER_SERVER_URL : Global.PROD_PATCHER_SERVER_URL;
    }


    /**
     * @param "You don't need it"
     * @response
     * {
     *     "status": "ok",
     *     "result": {
     *         "id": 1386,
     *         "pin": "2044965516078e3f64c1899b86e5f97c219baa41cdd904fd50e7162278ae77c0",
     *         "videoType": "screen",
     *         "restUrl": "https://rtc.methinks.io/janus",
     *         "socketUrl": "wss://rtc.methinks.io/janusws/",
     *         "janusArchiveId": "lAdEYDEzlE",
     *         "projectId": "koJF1wHo2P",
     *         "bucketName": "us-oregon"
     *     }
     * }
     * @param callback
     */
    public void getJanusRoomInfo(Callback callback){
        String url = serverURL + "/getJanusRoomInfo";
        String[]strings = new String[]{url, Global.HTTP_POST, null};

        new HttpAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strings);
    }

    /**
     * To get logo dynamically.
     * @param "You don't need it"
     * @response
     * "{
     *     status:""ok"",
     *     result: {                     // if image file exists
     *         url: ""<image url>""
     *     }
     * }"
     */
    public void getClientLogo(Callback callback){
        String url = serverURL + "/getClientLogo";
        String[]strings = new String[]{url, Global.HTTP_POST, null};

        Log.d("Current URL: " + Arrays.toString(strings));
        Log.d("RESULTS: " + url + Global.HTTP_POST);

        new HttpAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strings);
    }

    /**
     * @params
     * "{
     *     ""deviceInfo"": {},
     *     ""prevSession: {
     *         ""log"": [<start time in second, epoch>, <end time in second, epoch>],
     *         ""isNew"": <bool>,
     *         ""sid"": ""<random session id provided by client>"" // optional
     *     },
     *     ""prevAnswers: {
     *        ""questionPackId"": ""<AppTestQuestionPack objectId>"",
     *        ""answers"": {
     *           ""questionId"": [""<answer>""],
     *            ...
     *        }
     *     }
     * } "
     * @response
     * "{
     *     ""deviceInfo"": {},
     *     ""prevSession: {
     *         ""log"": [XX,XX],
     *         ""isNew"": BOOL,
     *         ""sid"": XXX // optional
     *     },
     *     ""prevAnswers: {
     *         ""questionPackId"": ""<AppTestQuestionPack objectId>"",
     *         ""answers"": {
     *            ""questionId"": [""<answer>""],
     *            ...
     *         }
     *     },
     *     ""minimumTestBuildNumber"": {""ios"":1, ""android"":0}
     * } "
     */
    public void login(JSONObject deviceInfo, JSONObject prevSession, JSONObject prevAnswers, Callback callback){
        if(deviceInfo == null)
            throw new NullPointerException("deviceInfo is required.");

        try{
            String url = serverURL + "/login";
//            String url = "http://10.0.1.19:3000" + "/login";
            JSONObject params = new JSONObject();
            params.put("deviceInfo", deviceInfo);
            params.put("isPlayedByEmulator", Global.isPlayedByEmulator);

            if(prevSession != null)
                params.put("prevSession", prevSession);
            if(prevAnswers != null)
                params.put("prevAnswers", prevAnswers);

            String[]strings = new String[]{url, Global.HTTP_POST, params.toString()};

//            System.out.println("check login() : " + prevSession);
            new HttpAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strings);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void log(JSONObject session, Callback callback){
        if(session == null) {
            Log.e("######session is null");
            return;
        }

        System.out.println("check log() : " + session);
        Log.w("session log: " + session);


        if(session.has("log")){
            try {
                JSONArray logJSONArray = session.getJSONArray("log");
                if(logJSONArray.length() == 2){
                    if(logJSONArray.getLong(0) >= logJSONArray.getLong(1)){
                        return;
                    }
                }else{
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }else {
            return;
        }

        try{
            String url = serverURL + "/log";
            JSONObject params = new JSONObject();
            params.put("session", session);
            params.put("isPlayedByEmulator", Global.isPlayedByEmulator);
            String[]strings = new String[]{url, Global.HTTP_POST, params.toString()};

            new HttpAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strings);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getEventTrigger(Callback callback) {
        try{
            String url = serverURL + "/getEventTriggers";
            JSONObject params = new JSONObject();
            params.put("project-name", Global.sProjectId);
            String[] strings = new String[]{url, Global.HTTP_POST, params.toString()};

            new HttpAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strings);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void event(String eventName, Callback callback){
        if(eventName == null)
            throw new NullPointerException("eventName is required.");

        try{
            String url = serverURL + "/event";
            JSONObject params = new JSONObject();
            params.put("eventName", eventName);
            params.put("sid", Global.sId);
            String[]strings = new String[]{url, Global.HTTP_POST, params.toString()};

            new HttpAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strings);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getQuestions(String questionPackId, Callback callback){
        if(questionPackId == null)
            throw new NullPointerException("eventName is required.");

        try{
            String url = serverURL + "/getQuestions";
            JSONObject params = new JSONObject();
            params.put("questionPackId", questionPackId);
            String[]strings = new String[]{url, Global.HTTP_POST, params.toString()};

            new HttpAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strings);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void inAppSections(String questionPackId, Callback callback) {
        if(questionPackId == null)
            throw new NullPointerException("eventName is required.");

        try{
            String url = serverURL + "/inAppSections";
            JSONObject params = new JSONObject();
            params.put("packId", questionPackId);
            String[]strings = new String[]{url, Global.HTTP_POST, params.toString()};

            new HttpAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strings);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void answer(String questionPackId, JSONObject answer, Callback callback){
        if(questionPackId == null)
            throw new NullPointerException("questionPackId is required.");
        else if(answer == null)
            throw new NullPointerException("answer is required.");

        try{
            String url = serverURL + "/answer";
//            String url = "http://10.0.1.19:3000" + "/answer";
            JSONObject params = new JSONObject();
            params.put("questionPackId", questionPackId);
            params.put("answers", answer);
            String[]strings = new String[]{url, Global.HTTP_POST, params.toString()};

            new HttpAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strings);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void inAppAnswer(String packId, JSONObject answer, Callback callback) {
        if (packId == null)
            throw new NullPointerException("packId is required.");
        else if (answer == null)
            throw new NullPointerException("answer is required");


    }

    public void capture(String reason, String type, String imageData, Callback callback){
        if(reason == null)
            throw new NullPointerException("reason is required.");
        else if(type == null)
            throw new NullPointerException("type is required.");
        else if(imageData == null)
            throw new NullPointerException("imageData is required.");

        try{
            String url = serverURL + "/capture";
            JSONObject params = new JSONObject();
            params.put("reason", reason);
            params.put("type", type);
            params.put("sid", Global.sId);
            params.put("imageData", imageData);
            String[]strings = new String[]{url, Global.HTTP_POST, params.toString()};

            new HttpAsyncTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, strings);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void getImage(String imageUrl, ImageCallback callback){
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... urls) {
                String remoteURL = urls[0];
                Bitmap bmp = null;
                try {
                    InputStream in = new java.net.URL(remoteURL).openStream();
                    bmp = BitmapFactory.decodeStream(in);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream .toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    LocalStore.getInstance().putLogoImage(encoded);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bmp;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap != null){
                    Global.logoBitmap = bitmap;
                    if(callback != null){
                        callback.done();
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{imageUrl});
    }


    static class HttpAsyncTask extends AsyncTask<String, Void, JSONObject> {
        private Callback callback;

        public HttpAsyncTask(Callback callback) {
            this.callback = callback;
        }

        /**
         *
         * @param strings
         * strings[0] = url
         * strings[1] = HTTP Method
         * strings[2] = params(JSONObject)
         * @return
         */
        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                if(strings == null || strings.length < 2){
                    throw new NullPointerException("Arguments are required.");
                }else if(TextUtils.isEmpty(strings[0])){
                    throw new NullPointerException("Target server address is required.");
                }else if(TextUtils.isEmpty(strings[1])){
                    throw new NullPointerException("HTTP Method is required.");
                }

                JSONObject params = !TextUtils.isEmpty(strings[2]) ? new JSONObject(strings[2]) : new JSONObject();
                Log.d("HTTP request params: " + params);

                URL obj = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestProperty("project-name", Global.sProjectId);
                conn.setRequestProperty("user-code", !TextUtils.isEmpty(Global.sTestUserCode) ? Global.sTestUserCode : "");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod(strings[1]);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");

                byte []outputBytes = params.toString().getBytes("UTF-8");
                OutputStream os = conn.getOutputStream();
                os.write(outputBytes);
                os.flush();
                os.close();

                String response;
                int code = conn.getResponseCode();
                if(code == 200){
                    InputStream is = conn.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] byteBuffer = new byte[1024];
                    byte[] byteData;
                    int length;
                    while((length = is.read(byteBuffer, 0, byteBuffer.length)) != -1){
                        baos.write(byteBuffer, 0, length);
                    }
                    byteData = baos.toByteArray();
                    response = new String(byteData);
                    JSONObject responseJSON = new JSONObject(response);
                    Log.d("HTTP response: " + response);

                    return responseJSON;
                }else{
                    Log.e("HTTP response error code : " + code);
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                System.out.println(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);

            if(callback != null) {
                try {
                    if (result == null) {
                        callback.done(null, "Failed HTTP request");
                    } else {
                        if (result.has("error")) {
                            String errorMsg = result.getString("error");
                            if (TextUtils.isEmpty(errorMsg)) {
                                callback.done(result, null);
                            } else {
                                callback.done(result, errorMsg);
                            }
                        } else {
                            callback.done(result, null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface Callback{
        void done(JSONObject response, String error);
    }

    public interface ImageCallback{
        void done();
    }
}
