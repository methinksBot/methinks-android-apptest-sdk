package io.methinks.android.methinks_android_forum_sdk.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Util {

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(Context context, float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();

        return px / (metrics.densityDpi / 160f);
    }

    public static void shuffleArrayList(ArrayList arrayList){
        long seed = System.nanoTime();
        Collections.shuffle(arrayList, new Random(seed));
    }

    public static Bitmap resizeBitmapWithDP(Context context, Bitmap origin, int widthDP, int heightDP){
        return Bitmap.createScaledBitmap(origin, (int) convertDpToPixel(context, widthDP), (int) convertDpToPixel(context, heightDP),false);
    }

    public static <T> List<T> convertJSONArrayToList(JSONArray jsonArray) {
        ArrayList<T> list = new ArrayList<>();
        try {
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++){
                    list.add((T) jsonArray.get(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static String convertArrayListToString(ArrayList<Object> values) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            result.put(values.get(i));
        }

        return result.toString();
    }

    public static String timeFormatted(double duration){

        int rounded = (int)Math.ceil(duration);
        String result = "";
        int sec = rounded % 60;
        int min = (rounded / 60) % 60;
        int hour = rounded / 3600;

        if(hour == 0){
            if(min == 0){
                result = String.format("(%d sec)", sec);
            }else{
                result = String.format("(%02d:%02d)", min, sec);
            }
        }else{
            result = String.format("(%02d:%02d:%02d)", hour, min, sec);
        }
        return result;
    }

}
