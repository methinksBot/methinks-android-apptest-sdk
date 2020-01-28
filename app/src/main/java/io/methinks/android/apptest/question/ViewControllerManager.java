package io.methinks.android.apptest.question;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;

import io.methinks.android.apptest.Global;
import io.methinks.android.apptest.Log;
import io.methinks.android.apptest.question.adapter.ViewControllerAdapter;


/**
 * Created by kgy 2019. 9. 24.
 */

public class ViewControllerManager extends FragmentActivity {


    protected static HashMap<String, ArrayList<Object>> answerMap;
    protected int num_pages;
    protected ViewPager mPager;
    protected PagerAdapter pagerAdapter;
    protected static ViewControllerAdapter adapter;
    protected static ProgressBar progressBar;
    protected Button prev;
    protected Button skip;
    protected EditText openEndEditText;
    protected static Context context;
    protected org.json.JSONObject currQuestion;
    protected static HashMap<String, String> sequenceLogicCache;
    public static HashMap<String, Object> cache;
    protected BottomSheetFragment bottomSheetFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setRequestedOrientation(getIntent().getIntExtra("orientation", ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
        }catch (IllegalStateException e){
            Log.w("API 26 doesn't support setRequestedOrientation function.");
        }


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        boolean isQuestion = getIntent().getBooleanExtra("isQuestion", true);
        boolean isRequired = getIntent().getBooleanExtra("isRequired", false);
        if(isQuestion){
            bottomSheetFragment = BottomSheetFragment.getInstance(height, width, isRequired);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        }else{
            boolean isBugReport = getIntent().getBooleanExtra("isBugReport", false);
            bottomSheetFragment = BottomSheetFragment.getInstance(height, width, isRequired, isBugReport);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if(!getIntent().getBooleanExtra("isRequired", false)){
            super.onBackPressed();
        }

    }

    @Override
    protected void onDestroy() {
        Global.isShowingQuestion = false;
        if(ViewConstant.pack.getType().equals(QuestionPack.EVENT_TYPE)){
            Global.eventQuestionPack = null;
        }else{
            Global.packQueue.remove(Global.packQueue.peek());
        }

        if(Global.hover != null)
            Global.hover.setVisible();

        if(Global.hover != null) {
            Global.hoverPopup.setInvisible();
            Global.hoverPopup.isOpened = false;
        }

        super.onDestroy();
    }
}
