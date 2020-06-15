package io.mtksdk.inappsurvey;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by kgy 2019. 9. 24.
 */

public class ViewControllerManager extends AppCompatActivity {
    protected static HashMap<String, ArrayList<Object>> answerMap;
    protected static Context context;
    public static HashMap<String, Object> cache;
    protected BottomSheetFragment bottomSheetFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        bottomSheetFragment = new BottomSheetFragment(ViewControllerManager.this, height, width, ViewConstant.firstSectionId);
        bottomSheetFragment.setCancelable(false);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

}
