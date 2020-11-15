package io.methinks.android.methinks_android_forum_sdk;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.methinks.android.methinks_android_forum_sdk.activity.ForumMainActivity;

public class ForumManager {

    // patcher
    public ForumManager(Context context, String campaignId, String userId, boolean isDebug) {
        Global.type = Global.Type.Patcher;
        Global.campaignId = campaignId;
        Global.userId = userId;
        Global.isDebugMode = isDebug;
        Global.serverUrl = isDebug
                ? Global.DEV_PATCHER_SERVER_URL
                : Global.PROD_PATCHER_SERVER_URL;

        Intent forumMainIntent = new Intent(context, ForumMainActivity.class);
        forumMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(forumMainIntent);
    }

    // methinks, nexon
    public ForumManager(@NonNull Context context,
                        @NonNull Global.Type type,
                        @NonNull String applicationId,
                        @NonNull String clientKey,
                        @NonNull String serverUrl,
                        @NonNull String sessionToken,
                        @NonNull String campaignId,
                        @NonNull String userObjectId,
                        @NonNull String participantId,
                        @Nullable String forumNickName,
                        @Nullable String forumProfile) {
        Global.init();
        Global.type = type;
        Global.applicationId = applicationId;
        Global.clientKey = clientKey;
        Global.serverUrl = serverUrl;
        Global.sessionToken = sessionToken;
        Global.campaignId = campaignId;
        Global.userObjectId = userObjectId;
        Global.participantId = participantId;
        Global.forumNickName = forumNickName;
        Global.forumProfile = forumProfile;

        if(!TextUtils.isEmpty(Global.forumNickName) && !TextUtils.isEmpty(Global.forumProfile)) {
            Global.isUserAllocated = true;
        }

        Intent forumMainIntent = new Intent(context, ForumMainActivity.class);
        forumMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(forumMainIntent);
    }

}
