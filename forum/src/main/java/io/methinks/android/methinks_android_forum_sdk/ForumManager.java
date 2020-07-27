package io.methinks.android.methinks_android_forum_sdk;

import android.content.Context;
import android.content.Intent;

import io.methinks.android.methinks_android_forum_sdk.activity.ForumMainActivity;

public class ForumManager {
    public ForumManager(Context context, String campaignId, String userId, boolean isDebug) {
        Global.campaignId = campaignId;
        Global.userId = userId;
        Global.isDubugMode = isDebug;

        Intent forumMainIntent = new Intent(context, ForumMainActivity.class);
        context.startActivity(forumMainIntent);
    }
}
