package io.methinks.android.apptest.question;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.methinks.android.apptest.Global;
import io.methinks.android.apptest.Log;
import io.methinks.android.apptest.R;
import io.methinks.android.apptest.question.adapter.ViewControllerAdapter;
import io.methinks.android.apptest.question.custom.widget.MethinksTextView;
import io.methinks.android.apptest.question.fragment.BaseFragment;


public class BottomSheetFragment extends DialogFragment {

    protected static ViewControllerAdapter adapter;
    protected static HashMap<String, ArrayList<Object>> answerMap;
    protected int num_pages;
    protected ViewPager mPager;
    protected PagerAdapter pagerAdapter;
    protected static ProgressBar progressBar;
    protected Button prev;
    protected Button skip;
    protected EditText openEndEditText;
    protected static Context context;
    protected JSONObject currQuestion;
    protected static HashMap<String, String> sequenceLogicCache;
    public static HashMap<String, Object> cache;
    public int numQ;
    public int screenHeight;
    public int screenWidth;
    public int currOrientation;

    public static BottomSheetFragment getInstance(int height, int width, boolean isRequired){
        BottomSheetFragment instance = new BottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("height", height);
        args.putInt("width", width);
        args.putBoolean("isRequired", isRequired);
        args.putBoolean("isQuestion", true);
        instance.setArguments(args);

        return instance;
    }

    public static BottomSheetFragment getInstance(int height, int width, boolean isRequired, boolean isBugReport){
        BottomSheetFragment instance = new BottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("height", height);
        args.putInt("width", width);
        args.putBoolean("isRequired", isRequired);
        args.putBoolean("isBugReport", isBugReport);
        args.putBoolean("isQuestion", false);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("height", screenHeight);
        outState.putInt("width", screenWidth);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            setRetainInstance(true);
        }

        if(savedInstanceState != null){
            this.screenHeight = savedInstanceState.getInt("height");
            this.screenWidth  = savedInstanceState.getInt("width");
        }else{
            this.screenHeight = getArguments().getInt("height");
            this.screenWidth = getArguments().getInt("width");
        }

        if(getActivity() != null){
            setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
            this.currOrientation = getActivity().getResources().getConfiguration().orientation;
        }
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View view, int i) {
            if (i == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View view, float v) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View bottomSheetView = inflater.inflate(R.layout.sdk_activity_question, null);
        final MethinksTextView submit = bottomSheetView.findViewById(R.id.submit);
        final MethinksTextView closeSurvey = bottomSheetView.findViewById(R.id.finish_button);
        final ImageView logo = bottomSheetView.findViewById(R.id.logo);
        final MethinksTextView logoText = bottomSheetView.findViewById(R.id.logo_text);
        final LinearLayout space = bottomSheetView.findViewById(R.id.space);
        boolean isQuestion = getArguments().getBoolean("isQuestion", false);
        boolean isBugReport = getArguments().getBoolean("isBugReport", false);
        boolean isRequired = getArguments().getBoolean("isBugReport", false);

        closeSurvey.setText(getString(R.string.sdk_text_close));

        if(isQuestion)
            submit.setText(getString(R.string.sdk_text_next));
        else
            submit.setText(getString(R.string.patcher_done));


        if(getDialog() != null){
            getDialog().setCanceledOnTouchOutside(!getArguments().getBoolean("isRequired"));
            getDialog().setOnKeyListener((dialog, keyCode, event) -> {
                if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK)){
                    if(ViewConstant.isRequired){
                        return true;
                    }else{
                        return false;
                    }
                }
                else
                    return false; // pass on to be processed as normal
            });
        }

        if(Global.logoBitmap != null){
            logoText.setVisibility(View.GONE);
            logo.setImageBitmap(Global.logoBitmap);
        }

        closeSurvey.setOnClickListener(view -> {
            getDialog().dismiss();
            getActivity().finish();
        });

        WindowManager.LayoutParams winParams = getDialog().getWindow().getAttributes();
        winParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().setAttributes(winParams);

        submit.setOnClickListener(view -> {
            getDialog().dismiss();
            getActivity().finish();
        });

        if(isQuestion){
            this.answerMap = new HashMap<>();                                                           //Map that stores answers
            sequenceLogicCache = new HashMap<>();
            cache = new HashMap<>();

            final ViewPager viewPager = bottomSheetView.findViewById(R.id.view_pager);
            ViewConstant.clearQuestions();
            try {
                for(int i = 0; i < ViewConstant.pack.getQuestions().length(); i++){
                    ViewConstant.questions.add(ViewConstant.pack.getQuestions().getJSONObject(i));
                }
                ViewConstant.questionPackId = ViewConstant.pack.getPackId();
                adapter = new ViewControllerAdapter(getChildFragmentManager(), answerMap, ViewConstant.questions);
                viewPager.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            int currPosition = viewPager.getCurrentItem();
            final ViewGroup.LayoutParams params = viewPager.getLayoutParams();
            if (currPosition == 0 && currOrientation == Configuration.ORIENTATION_PORTRAIT) {
                params.height = this.screenHeight / 4;
                viewPager.requestLayout();
            } else if (currPosition == 0 && currOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                params.height = this.screenHeight / 2;
                params.width = this.screenWidth * 3/4;
                viewPager.requestLayout();
            }

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (currOrientation == Configuration.ORIENTATION_PORTRAIT) {
                        if (position == 0) {
                            submit.setText(getString(R.string.sdk_text_next));
                        }
//                        else if (position == ViewConstant.questions.size() - 1) {
//                            ViewGroup.LayoutParams paramsQ = viewPager.getLayoutParams();
//                            paramsQ.height = screenHeight / 4;
//                            viewPager.requestLayout();
//                            submit.setVisibility(View.GONE);
//                            logo.setVisibility(View.GONE);
//                            logoText.setVisibility(View.GONE);
//                            closeSurvey.setVisibility(View.VISIBLE);
//                            space.setVisibility(View.VISIBLE);
//                        }
                        else if (ViewConstant.questions.get(position).optString("type").equals("likert") || ViewConstant.questions.get(position).optString("type").equals("smiley")) {
                            ViewGroup.LayoutParams params1 = viewPager.getLayoutParams();
                            params1.height = screenHeight / 3;
                            viewPager.requestLayout();
                            submit.setText(getString(R.string.patcher_next));
                        } else {
                            ViewGroup.LayoutParams paramsQ = viewPager.getLayoutParams();
                            paramsQ.height = screenHeight * 8/17;
                            viewPager.requestLayout();
                            submit.setText(getString(R.string.patcher_next));
                        }

                        if (position == ViewConstant.questions.size() - 1) {
                            submit.setVisibility(View.GONE);
                            logo.setVisibility(View.GONE);
                            logoText.setVisibility(View.GONE);
                            closeSurvey.setVisibility(View.VISIBLE);
                            space.setVisibility(View.VISIBLE);
                        }
                    } else if (currOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        if (position == 0) {
                            submit.setText(getString(R.string.sdk_text_next));
                        } else if(ViewConstant.questions.get(position).optString("type").equals("range")){
                            ViewGroup.LayoutParams params1 = viewPager.getLayoutParams();
                            params1.height = screenHeight * 1/2;
                            viewPager.requestLayout();
                            submit.setText(getString(R.string.patcher_next));
                        }
//                        else if (position == ViewConstant.questions.size() - 1) {
//                            ViewGroup.LayoutParams paramsQ = viewPager.getLayoutParams();
//                            paramsQ.height = screenHeight / 3;
//                            viewPager.requestLayout();
//                            submit.setVisibility(View.GONE);
//                            logo.setVisibility(View.GONE);
//                            logoText.setVisibility(View.GONE);
//                            closeSurvey.setVisibility(View.VISIBLE);
//                            space.setVisibility(View.VISIBLE);
//                        }
                        else if (ViewConstant.questions.get(position).optString("type").equals("likert") || ViewConstant.questions.get(position).optString("type").equals("smiley")) {
                            ViewGroup.LayoutParams params1 = viewPager.getLayoutParams();
                            params1.height = screenHeight * 5/9;
                            viewPager.requestLayout();
                            submit.setText(getString(R.string.patcher_next));
                        } else {
                            ViewGroup.LayoutParams paramsQ = viewPager.getLayoutParams();
                            paramsQ.height = screenHeight * 6/9;
                            viewPager.requestLayout();
                            submit.setText(getString(R.string.patcher_next));
                        }

                        if (position == ViewConstant.questions.size() - 1) {
                            submit.setVisibility(View.GONE);
                            logo.setVisibility(View.GONE);
                            logoText.setVisibility(View.GONE);
                            closeSurvey.setVisibility(View.VISIBLE);
                            space.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            submit.setOnClickListener(new View.OnClickListener() {                                      //Submitting current question will move to next question
                @Override
                public void onClick(View view) {

                    hideKeyboard();
                    int currPosition = viewPager.getCurrentItem();
                    final BaseFragment currentFragment = (BaseFragment) adapter.getItem(currPosition);
                    //getting current question.
                    currQuestion = ViewConstant.questions.get(currPosition);
                    if (currQuestion.has("sequenceLogic")) {                                       // sequenceLogic
                        if (currentFragment.validate()) {
                            try {
                                JSONObject sequencLogic = currQuestion.optJSONObject("sequenceLogic");
                                String key = null;
                                if (currQuestion.optString("questionType").equals("multipleChoice")) {
                                    String answerChoice = (String) answerMap.get(getSubItemId()).get(0);
                                    for (int i = 0; i< currQuestion.optJSONArray("choices").length(); i++) {
                                        String questionChoice = currQuestion.optJSONArray("choices").getString(i);
                                        if (answerChoice.equals(questionChoice)) {
                                            key = String.valueOf(i +1);
                                        }
                                    }
                                }
                                if (key == null) {
                                    Log.e("Error occurred in Sequence logic.");
                                } else {
                                    int moveIndex = 0;
                                    String moveQuestionId = sequencLogic.getString(key);
                                    for (int i = 0; i < ViewConstant.questions.size(); i++) {
                                        JSONObject q = ViewConstant.questions.get(i);
                                        if (q.optString("objectId").equals(moveQuestionId)) {
                                            moveIndex = i;
                                        }
                                    }
                                    String k = currQuestion.optString("objectId");
                                    String value = ViewConstant.questions.get(moveIndex).optString("objectId");
                                    sequenceLogicCache.put(k, value);
                                    viewPager.setCurrentItem(moveIndex);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), "Answer First", Toast.LENGTH_SHORT).show();
                        }
                    } else if (currentFragment.validate()) {
                        int nextPosition = currPosition + 1;
                        viewPager.setCurrentItem(nextPosition);
                    } else {
                        Toast.makeText(getContext(), "Answer First", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{

        }



        return bottomSheetView;
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public Boolean isSkippable(boolean skippable) {
        return skippable;
    }

    public String getSubItemId() {
        return currQuestion.optString("objectId");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideKeyboard();
        ViewConstant.hasSurveyed = true;
        JSONObject json = new JSONObject(answerMap);
        ViewConstant.answer = json;


        ViewConstant.surveyCompletion(ViewConstant.answer);
        if(getActivity() != null){
            getActivity().finish();
        }
    }


}

