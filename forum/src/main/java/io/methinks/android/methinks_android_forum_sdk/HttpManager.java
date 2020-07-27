package io.methinks.android.methinks_android_forum_sdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpManager {

    private static final String TAG = HttpManager.class.getSimpleName();
    private String serverURL;

    public OkHttpClient okHttpClient;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public HttpManager() {
        serverURL = Global.isDubugMode ? Global.DEV_PATCHER_SERVER_URL : Global.PROD_PATCHER_SERVER_URL;
        okHttpClient = new OkHttpClient();
    }

    public void getMyParticipant(Callback callback) {
        String url = serverURL + "/getMyParticipant";
        RequestBody body = RequestBody.create(null, new byte[0]);

        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void setForumProfile(String nickName, String emoji, Callback callback) {
        String url = serverURL + "/setForumProfile";
//        RequestBody body = new FormBody.Builder()
//                .add("forumNickName", nickName)
//                .add("forumEmoji", emoji).build();
        JSONObject params = new JSONObject();
        //Log.d(nickName + "/" + emoji + "/" + Global.campaignId + "/" + Global.userId);
        try {
            params.put("forumNickName", nickName);
            params.put("forumEmoji", emoji);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, params.toString());

        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public void getForumSections(Callback callback) {
        String url = serverURL + "/getForumSections";
        RequestBody body = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void getForumPosts(String sectionId, Callback callback) {
        String url = serverURL + "/getForumPosts";
        JSONObject params = new JSONObject();
        try {
            params.put("sectionId", sectionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, params.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void createForumPost(JSONObject post, Callback callback) {
        String url = serverURL + "/createForumPost";
        Log.d("sending message: " + post);
        RequestBody body = RequestBody.create(JSON, post.toString());

        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(callback);

    }

    public void getPostComments(String postId, Callback callback) {
        String url = serverURL + "/getPostComments";
        RequestBody body = new FormBody.Builder()
                .add("postId", postId).build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public String getPostAttachments(JSONArray objectIds) {
        String url = serverURL + "/getPostAttachments";
        JSONObject params = new JSONObject();
        try {
            params.put("objectIds", objectIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, params.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        String message = "";
        try {
            Response response = okHttpClient.newCall(request).execute();
            message = response.body().string();
            return message;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public void likeForumPost(String postId, boolean like, Callback callback) {
        String url = serverURL + "/likeForumPost";
        JSONObject params = new JSONObject();
        try {
            params.put("postId", postId);
            params.put("like", like);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, params.toString());
        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void createForumComment(JSONObject params, Callback callback) {
        String url = serverURL + "/createForumComment";

        RequestBody body = RequestBody.create(JSON, params.toString());
        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void updateForumPost(JSONObject params, Callback callback) {
        String url = serverURL + "/updateForumPost";

        RequestBody body = RequestBody.create(JSON, params.toString());
        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void deleteForumPostAttachment(String postId, String toBeDeleted, Callback callback) {
        String url = serverURL + "/deleteForumPostAttachment";
        JSONObject params = new JSONObject();
        try {
            params.put("postId", postId);
            params.put("toBeDeleted", toBeDeleted);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, params.toString());
        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void updateCommentCount(String postId, int number, Callback callback) {
        String url = serverURL + "/updateCommentCount";

        JSONObject params = new JSONObject();
        try {
            params.put("postId", postId);
            params.put("number", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, params.toString());

        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void likeForumComment(String commentId, boolean like, Callback callback) {
        String url = serverURL + "/likeForumComment";
        JSONObject params = new JSONObject();
        try {
            params.put("commentId", commentId);
            params.put("like", like);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, params.toString());
        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void deleteForumPost(String postId, Callback callback) {
        String url = serverURL + "/deleteForumPost";
        JSONObject params = new JSONObject();
        try {
            params.put("postId", postId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, params.toString());
        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void deleteForumComment(String commentId, Callback callback) {
        String url = serverURL + "/deleteForumComment";
        JSONObject params = new JSONObject();
        try {
            params.put("commentId", commentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(JSON, params.toString());
        Request request = new Request.Builder()
                .addHeader("project-name", Global.campaignId)
                .addHeader("user-code", Global.userId)
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }
}
