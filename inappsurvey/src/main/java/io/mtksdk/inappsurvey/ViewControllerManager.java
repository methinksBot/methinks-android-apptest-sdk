package io.mtksdk.inappsurvey;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import io.mtksdk.inappsurvey.adapter.ViewControllerAdapter;
import io.mtksdk.inappsurvey.fragment.BaseFragment;

import org.json.JSONException;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static io.mtksdk.inappsurvey.ViewConstant.answer;
import static io.mtksdk.inappsurvey.ViewConstant.apiKey;
import static io.mtksdk.inappsurvey.ViewConstant.packId;


/**
 * Created by kgy 2019. 9. 24.
 */

public class ViewControllerManager extends FragmentActivity {
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
