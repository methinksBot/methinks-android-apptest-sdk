package io.methinks.android.methinks_android_forum_sdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.methinks.android.methinks_android_forum_sdk.Global.Type;
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

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public HttpManager() {
        serverURL = Global.serverUrl;
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
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/setForumProfile";
            JSONObject params = new JSONObject();
            try {
                params.put("forumNickName", nickName);
                params.put("forumEmoji", emoji);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());

            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .addHeader("user-code", Global.userId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/setForumProfile";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("participantId", Global.participantId);
                params.put("forumNickName", nickName);
                params.put("forumEmoji", emoji);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void getForumSections(Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/getForumSections";
            RequestBody body = RequestBody.create(null, new byte[0]);
            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/getForumSections";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("campaignId", Global.campaignId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void getForumPosts(String sectionId, Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/getForumPosts";
            JSONObject params = new JSONObject();
            try {
                params.put("sectionId", sectionId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/getForumPosts";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("sectionId", sectionId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void createForumPost(JSONObject post, Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/createForumPost";
            Log.d("sending message: " + post);
            RequestBody body = RequestBody.create(JSON, post.toString());

            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .addHeader("user-code", Global.userId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/createForumPost";
            try {
                post.put("id", Global.userObjectId);
                post.put("campaignId", Global.campaignId);
                post.put("participantId", Global.participantId);
                post.put("attachments", post.getJSONArray("images"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, post.toString());

            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);

    }

    public void getPostComments(String postId, Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/getPostComments";
            RequestBody body = new FormBody.Builder()
                    .add("postId", postId).build();
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/getForumComments";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("postId", postId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public String getPostAttachments(JSONArray objectIds) {
        Request request = null;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/getPostAttachments";
            JSONObject params = new JSONObject();
            try {
                params.put("objectIds", objectIds);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, params.toString());

            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/getImageAssetsWithObjectIds";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("objectIds", objectIds);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

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
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/likeForumPost";
            JSONObject params = new JSONObject();
            try {
                params.put("postId", postId);
                params.put("like", like);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .addHeader("user-code", Global.userId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/likeForumPost";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("postId", postId);
                params.put("isLike", like);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void createForumComment(JSONObject params, Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/createForumComment";

            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .addHeader("user-code", Global.userId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/createForumCommentWithImages";
            try {
                params.put("id", Global.userObjectId);
                params.put("campaignId", Global.campaignId);
                params.put("participantId", Global.participantId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void updateForumPost(JSONObject params, Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/updateForumPost";

            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .addHeader("user-code", Global.userId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/updateForumPost";
            try {
                params.put("id", Global.userObjectId);
                params.put("attachments", params.getJSONArray("images"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void deleteForumPostAttachment(String postId, String toBeDeleted, Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/deleteForumPostAttachment";
            JSONObject params = new JSONObject();
            try {
                params.put("postId", postId);
                params.put("toBeDeleted", toBeDeleted);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .addHeader("user-code", Global.userId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/deleteForumPostAttachment";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("postId", postId);
                params.put("toBeDeleted", toBeDeleted);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void updateCommentCount(String postId, int number, Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/updateCommentCount";

            JSONObject params = new JSONObject();
            try {
                params.put("postId", postId);
                params.put("number", number);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, params.toString());

            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .addHeader("user-code", Global.userId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/updateCommentCountOnPostObject";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("postId", postId);
                params.put("number", number);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void likeForumComment(String commentId, boolean like, Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/likeForumComment";
            JSONObject params = new JSONObject();
            try {
                params.put("commentId", commentId);
                params.put("like", like);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .addHeader("user-code", Global.userId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/likePostComment";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("commentId", commentId);
                params.put("isLike", like);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void deleteForumPost(String postId, Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/deleteForumPost";
            JSONObject params = new JSONObject();
            try {
                params.put("postId", postId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .addHeader("user-code", Global.userId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/deleteForumPost";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("postId", postId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }

    public void deleteForumComment(String commentId, Callback callback) {
        Request request;
        if(Global.type == Type.Patcher) {
            String url = serverURL + "/deleteForumComment";
            JSONObject params = new JSONObject();
            try {
                params.put("commentId", commentId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("project-name", Global.campaignId)
                    .addHeader("user-code", Global.userId)
                    .url(url)
                    .post(body)
                    .build();
        } else {
            String url = serverURL + "/parse/functions/deletePostComment";
            JSONObject params = new JSONObject();
            try {
                params.put("id", Global.userObjectId);
                params.put("commentId", commentId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(JSON, params.toString());
            request = new Request.Builder()
                    .addHeader("X-Parse-Application-Id", Global.applicationId)
                    .addHeader("X-Parse-Client-Key", Global.clientKey)
                    .addHeader("X-Parse-Session-Token", Global.sessionToken)
                    .url(url)
                    .post(body)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(callback);
    }
}
