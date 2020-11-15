package io.methinks.android.methinks_android_forum_sdk;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;

import java.text.ParseException;
import java.util.Date;

public class Global {

    public static final String ENV_DEV = "dev";
    public static final String ENV_PROD = "prod";

    protected static final String HTTP_POST = "POST";
    protected static final String HTTP_GET = "GET";

    protected static final String DEV_PATCHER_SERVER_URL = "https://apptest-dev.methinks.io";
    protected static final String PROD_PATCHER_SERVER_URL = "https://apptest.methinks.io";

    public static final int REQUEST_CODE_GALLERY = 0;

    public static String campaignId;
    public static String userId;
    public static String sessionToken = null;
    public static String forumNickName = null;
    public static String forumProfile = null;
    public static String userObjectId = null;
    public static String participantId = null;
    public static boolean isUserAllocated = false;

    // for methinks, nexon project
    public static String serverUrl = null;
    public static String applicationId = null;
    public static String clientKey = null;

    public static boolean isDebugMode = true;
    public static Type type;

    public enum Type {
        Methinks,
        Nexon,
        Patcher
    }

    public static void init() {
        sessionToken = null;
        forumNickName = null;
        forumProfile = null;
        userObjectId = null;
        participantId = null;
        serverUrl = null;
        applicationId = null;
        clientKey = null;
        isUserAllocated = false;
        isDebugMode = true;
    }

    static public int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    /**
     *   passed time calculator for 'createdAt' and 'post_is_new'
     *   'post_is_new' is shown up when post's createdAt is lower than 1 day
     * */
    public static String passedTimeCalc(String createdAt) {
        SimpleDateFormat sdf = null;
        Log.d(createdAt);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        } else { return "ago"; }

        try {
            Date createTime =  sdf.parse(createdAt);

            String current = sdf.format(new Date());
            Date currentTime = sdf.parse(current);

            long timeDiffer = currentTime.getTime() - createTime.getTime();
            Log.d("createdAt: " + createTime.getTime() + "\n" + "current: " + currentTime.getTime());
            Log.d("Time differ: " + timeDiffer);

            if (timeDiffer < 60000) {
                return (timeDiffer / 1000) + " sec ago";
            } else if (timeDiffer < 3600000) {
                return (timeDiffer / 60000) + " mins ago";
            } else if (timeDiffer < 86400000) {
                return (timeDiffer / 3600000) + " hours ago";
            } else if (timeDiffer < 2592000000L) {
                return (timeDiffer / 86400000) + " days ago";
            } else if (timeDiffer < 31104000000L) {
                return (timeDiffer / 2592000000L) + " months ago";
            } else {
                return (timeDiffer / 31104000000L) + " years ago";
            }

        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
