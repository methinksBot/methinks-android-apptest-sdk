package io.methinks.android.apptest;

public class Log {
    private static final String TAG = "[MTKPatcher]";

    public static void i(String string) {
        android.util.Log.i(TAG, string);
    }
    public static void d(String string) {
        android.util.Log.d(TAG, string);
    }
    public static void v(String string) {
        android.util.Log.v(TAG, string);
    }
    public static void w(String string) {
        String logMsg = " " +
                "\n" +
                "################################################################" + "\n" +
                string + "\n" +
                "################################################################";
        android.util.Log.w(TAG, logMsg);
    }
    public static void e(String string) {
        String logMsg = " " +
                "\n" +
                "################################################################" + "\n" +
                string + "\n" +
                "################################################################";
        android.util.Log.e(TAG, logMsg);
    }

    public static void callEvent(String key, String value) {
        android.util.Log.d(TAG, "Caught event - Event name: " + key + ", Event value: " + value);
    }
}
