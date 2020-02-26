package io.methinks.android.apptest;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class ScreenshotContentObserver extends ContentObserver {

    private final String TAG = this.getClass().getSimpleName();
    private static final String[] PROJECTION = new String[]{
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.ImageColumns._ID
    };
    private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";

    public static final String FILE_POSTFIX = "FROM_ASS";
    private static final String WATERMARK = "Scott";
    private ContentResolver mContentResolver;
    private String lastPath;

    public ScreenshotContentObserver(Handler handler, ContentResolver contentResolver) {
        super(handler);
        mContentResolver = contentResolver;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    synchronized public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    synchronized public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.e("[Start] onChange : " + selfChange + " / uri : " + uri.toString());

        if (uri.toString().startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
            try {
                process(uri);
            } catch (Exception e) {
            }
        } else {
        }
    }

    public void register() {
        mContentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, this);
    }

    public void unregister() {
        mContentResolver.unregisterContentObserver(this);
    }

    private boolean process(Uri uri) throws Exception {
        Data result = getLatestData(uri);
        if (result == null) {
            return false;
        }
        if (lastPath != null && lastPath.equals(result.path)) {
            return false;
        }
        long currentTime = System.currentTimeMillis() / 1000;
        if (matchPath(result.path) && matchTime(currentTime, result.dateAdded)) {
            lastPath = result.path;
            Log.d("[Result] Took a screenshot : " + result.fileName + " | dateAdded : " + result.dateAdded + " / " + currentTime);
            if(Global.applicationTracker != null && Global.applicationTracker.getTopActivity() != null){
                Uri screenUri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + result.id);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContentResolver, screenUri);
                Bitmap copyBitmap = bitmap.copy(bitmap.getConfig(), true);
                bitmap.recycle();



                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                copyBitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String encodedScreenshot = Base64.encodeToString(byteArray, Base64.DEFAULT);

                new HttpManager().capture("Inappropriate hardware capture.", "suggestion", encodedScreenshot, new HttpManager.Callback() {
                    @Override
                    public void done(JSONObject response, String error) {
                        if(error == null){
                            Log.d("Inappropriate Hardware Screenshot was saved");
                            return;
                        }else{
                            Log.e("CAPTURE RES ERROR: " + error + "\n" + response );
                        }
                    }
                });


                new AlertDialog.Builder(Global.applicationTracker.getTopActivity())
                        .setMessage(Global.app.getString(R.string.patcher_msg_illegal_screenshot))
                        .setPositiveButton(Global.app.getString(R.string.patcher_confirm), (dialogInterface, i) -> dialogInterface.dismiss()).show();
            }

            return true;
        } else {
            Log.e("[Result] No ScreenShot : " + result.fileName);
        }
        return false;
    }

    private Data getLatestData(Uri uri) throws Exception {
        Data data = null;
        Cursor cursor = null;
        try {
            cursor = mContentResolver.query(uri, PROJECTION, null, null, SORT_ORDER);
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                if (fileName.contains(FILE_POSTFIX)) {
                    if (cursor.moveToNext()) {
                        id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                        fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                    } else {
                        return null;
                    }
                }

                data = new Data();
                data.id = id;
                data.fileName = fileName;
                data.path = path;
                data.dateAdded = dateAdded;
                Log.w("[Recent File] Name : " + fileName);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return data;
    }

    private boolean matchPath(String path) {
        return (path.toLowerCase().contains("screenshots/") && !path.contains(FILE_POSTFIX));
    }

    private boolean matchTime(long currentTime, long dateAdded) {
        return Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS;
    }

    class Data {
        long id;
        String fileName;
        String path;
        long dateAdded;
    }
}