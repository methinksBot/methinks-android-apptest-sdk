package io.methinks.android.apptest;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ReportActivity extends AppCompatActivity {

    private TextView questionTitle;
    private LinearLayout textAnswerContainer;
    private LinearLayout placeholder;
    private ImageButton addAnswer;
    private ScrollView scrollView;
    private ImageView attachedImage;
    private EditText reportContent;
    private String encodedImageBase64;
    private Bitmap attachedBitmap;
    private boolean isBugReport;

    @SuppressLint({"InflateParams", "StaticFieldLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        isBugReport = getIntent().getBooleanExtra("isBugReport", false);


        TextView tvLabel = (TextView)findViewById(R.id.question_content);
        reportContent = (EditText)findViewById(R.id.report_content);
        textAnswerContainer = (LinearLayout)findViewById(R.id.text_answer_container);
        attachedImage = (ImageView)findViewById(R.id.attached_image);
        placeholder = (LinearLayout) getLayoutInflater().from(this).inflate(R.layout.sdk_open_end_answer_placeholder, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) convertDpToPixel(this, 19);
        placeholder.setLayoutParams(params);
        addAnswer = placeholder.findViewById(R.id.add_answer);
        scrollView = (ScrollView)findViewById(R.id.scroll_view);

        if(isBugReport)
            tvLabel.setText(getString(R.string.patcher_msg_bug_report_desc));
        else
            tvLabel.setText(getString(R.string.patcher_msg_suggestion_desc));


        if(getIntent().hasExtra("encodedImage")){
            this.encodedImageBase64 = getIntent().getStringExtra("encodedImage");
            byte[] decodedString = Base64.decode(getIntent().getStringExtra("encodedImage"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            attachedImage.setImageBitmap(decodedByte);

            this.attachedBitmap = decodedByte;
        }else if(getIntent().hasExtra("isUnity") && getIntent().getBooleanExtra("isUnity", false)){
            this.encodedImageBase64 = takeScreenShot();
            byte[] decodedString = Base64.decode(this.encodedImageBase64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            attachedImage.setImageBitmap(decodedByte);

            this.attachedBitmap = decodedByte;
        }else if(getIntent().hasExtra("fileFullPath")){
            String fileFullPath = getIntent().getStringExtra("fileFullPath");
            if(!TextUtils.isEmpty(fileFullPath)){
                File imgFile = new File(fileFullPath);
                if(imgFile.exists()){
                    Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    attachedImage.setImageBitmap(bmp);
                    this.attachedBitmap = bmp;

                    new AsyncTask<Void, Void, Void>(){
                        @Override
                        protected Void doInBackground(Void... voids) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ReportActivity.this.attachedBitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            ReportActivity.this.encodedImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }

        findViewById(R.id.report_cancel).setOnClickListener(view -> finish());

        findViewById(R.id.report_submit).setOnClickListener(view -> {
            submit();
            finish();
        });

        reportContent.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                submit();
                finish();
            }
            return false; // pass on to other listeners.
        });
    }

    private void submit(){
        if(getIntent().hasExtra("fileFullPath")){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ReportActivity.this.attachedBitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            ReportActivity.this.encodedImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        }
        findViewById(R.id.report_submit).setEnabled(false);
        new HttpManager().capture(reportContent.getText().toString(), (isBugReport ? "bug" : "suggestion"), encodedImageBase64, new HttpManager.Callback() {
            @Override
            public void done(JSONObject response, String error) {
                if(error == null){
                    finish();
                    return;
                }else{
                    findViewById(R.id.report_submit).setEnabled(true);
                }
            }
        });
    }

    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    @Override
    protected void onDestroy() {
        Global.isShowingReport = false;
        Global.hover.setVisible();
        super.onDestroy();
    }

    private String takeScreenShot(){
        View view = Global.applicationTracker.getTopActivity().getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        Global.applicationTracker.getTopActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        Global.applicationTracker.getTopActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int width = displaymetrics.widthPixels;
        int height = displaymetrics.heightPixels;

        Bitmap bmp = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  - statusBarHeight);
        view.destroyDrawingCache();


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        LocalStore.getInstance().putLastCaptureImage(encoded);

        return encoded;
    }
}
