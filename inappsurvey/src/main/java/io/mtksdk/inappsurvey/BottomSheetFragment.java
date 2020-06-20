package io.mtksdk.inappsurvey;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import io.mtksdk.inappsurvey.adapter.ViewControllerAdapter;
import io.mtksdk.inappsurvey.converter.Question;
import io.mtksdk.inappsurvey.custom.widget.MethinksTextView;
import io.mtksdk.inappsurvey.fragment.BaseFragment;


public class BottomSheetFragment extends DialogFragment {

    protected static ViewControllerAdapter adapter;
    protected static HashMap<String, ArrayList<Object>> answerMap;
    protected static Context context;
    protected org.json.JSONObject currQuestion;
    protected static HashMap<String, String> sequenceLogicCache;
    protected static HashMap<String, Object> cache;
    protected Activity act;
    protected int screenHeightP;
    protected int screenWidthP;
    public int screenHeightL;
    public int screenWidthL;
    protected int currOrientation;
    public ViewPager viewPager;
    public int displayState;
    public static String currSectionId;
    public static ArrayList<Question> currQuestionPack;

    public BottomSheetFragment() {}

    @SuppressLint("ValidFragment")
    public BottomSheetFragment(Activity act, int height, int width, String firstSectionId) {
        this.act = act;
        this.screenHeightP = height;
        this.screenWidthP = width;
        this.screenHeightL = width;
        this.screenWidthL = height;
        this.currSectionId = firstSectionId;
        this.currQuestionPack = ViewConstant.sectionContainer.get(firstSectionId).getQuestionPacks();
        // Required empty public constructor
    }

/*
    public static BottomSheetFragment newInstance() {
        return new BottomSheetFragment();
    }
*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog);
        this.currOrientation = act.getResources().getConfiguration().orientation;
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        ViewGroup.LayoutParams paramsConfig = viewPager.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            currOrientation = newConfig.orientation;

            if (displayState == 0) {
                paramsConfig.height = this.screenHeightL/4;
                paramsConfig.width = this.screenWidthL * 3/5;
                viewPager.requestLayout();
            } else if (displayState == 1) {
                paramsConfig.height = screenHeightL / 3;
                paramsConfig.width = this.screenWidthL * 3/5;
                viewPager.requestLayout();
            } else if (displayState == 2) {
                paramsConfig.height = screenHeightL * 5/9;
                paramsConfig.width = this.screenWidthL * 3/5;
                viewPager.requestLayout();
            } else {
                paramsConfig.height = screenHeightL * 5/9;
                paramsConfig.width = this.screenWidthL * 3/5;
                viewPager.requestLayout();
            }
        } else {
            currOrientation = newConfig.orientation;

            if (displayState == 0) {
                paramsConfig.height = this.screenHeightP / 4;
                paramsConfig.width = this.screenWidthP;
                viewPager.requestLayout();
            } else if (displayState == 1) {
                paramsConfig.height = this.screenHeightP / 4;
                paramsConfig.width = this.screenWidthP;
                viewPager.requestLayout();
            } else if (displayState == 2) {
                paramsConfig.height = screenHeightP /3;
                paramsConfig.width = this.screenWidthP;
                viewPager.requestLayout();
            } else {
                paramsConfig.height = screenHeightP * 8/17;
                paramsConfig.width = this.screenWidthP;
                viewPager.requestLayout();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View bottomSheetView = inflater.inflate(R.layout.sdk_activity_question, null);
        final MethinksTextView submit = bottomSheetView.findViewById(R.id.submit);
        final MethinksTextView closeSurvey = bottomSheetView.findViewById(R.id.finish_button);
        final ImageView logo = bottomSheetView.findViewById(R.id.logo);
        final MethinksTextView logoText = bottomSheetView.findViewById(R.id.logo_text);
        final LinearLayout space = bottomSheetView.findViewById(R.id.space);
        submit.setText("Start");
        displayState = 0;

        closeSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                act.finish();
            }
        });

        WindowManager.LayoutParams winParams = getDialog().getWindow().getAttributes();
        winParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().setAttributes(winParams);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                act.finish();
            }
        });

        this.answerMap = new HashMap<>();                                                           //Map that stores answers
        sequenceLogicCache = new HashMap<>();
        cache = new HashMap<>();

        viewPager = bottomSheetView.findViewById(R.id.view_pager);
        ArrayList<Question> questionPacks = ViewConstant.sectionContainer.get(ViewConstant.firstSectionId).getQuestionPacks();
        adapter = new ViewControllerAdapter(getChildFragmentManager(), answerMap, questionPacks);
        viewPager.setAdapter(adapter);

        int currPosition = viewPager.getCurrentItem();
        final ViewGroup.LayoutParams params = viewPager.getLayoutParams();
        if (currPosition == 0 && currOrientation == Configuration.ORIENTATION_PORTRAIT) {
            params.height = this.screenHeightP / 4;
            viewPager.requestLayout();
        } else if (currPosition == 0 && currOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            params.height = this.screenHeightP / 4;
            params.width = this.screenWidthP * 3/5;
            viewPager.requestLayout();
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.i("onPageSelected", position + "showed and pagechangelistener works");
                BaseFragment currFrag = (BaseFragment) adapter.getItem(viewPager.getCurrentItem());
                String currType = currFrag.getType();
                if (currOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (currType.equals("intro")) {
                        submit.setText("Start");
                    } else if (currType.equals("outro")) {
                        displayState = 1;
                        ViewGroup.LayoutParams paramsQ = viewPager.getLayoutParams();
                        paramsQ.height = screenHeightP / 4;
                        viewPager.requestLayout();
                        submit.setVisibility(View.GONE);
                        logo.setVisibility(View.GONE);
                        logoText.setVisibility(View.GONE);
                        closeSurvey.setVisibility(View.VISIBLE);
                        space.setVisibility(View.VISIBLE);
                    } else if (currType.equals("likert") || currType.equals("smiley")) {
                        displayState = 2;
                        ViewGroup.LayoutParams params1 = viewPager.getLayoutParams();
                        params1.height = screenHeightP / 3;
                        viewPager.requestLayout();
                        submit.setText("Next");
                    } else {
                        Log.i("viewPage", "height to max on question");
                        displayState = 3;
                        ViewGroup.LayoutParams paramsQ = viewPager.getLayoutParams();
                        paramsQ.height = screenHeightP * 8/17;
                        viewPager.requestLayout();
                        submit.setText("Next");
                    }
                } else if (currOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (currType.equals("intro")) {
                        submit.setText("Start");
                    } else if (currType.equals("outro")) {
                        displayState = 1;
                        ViewGroup.LayoutParams paramsQ = viewPager.getLayoutParams();
                        paramsQ.height = screenHeightP / 3;
                        viewPager.requestLayout();
                        submit.setVisibility(View.GONE);
                        logo.setVisibility(View.GONE);
                        logoText.setVisibility(View.GONE);
                        closeSurvey.setVisibility(View.VISIBLE);
                        space.setVisibility(View.VISIBLE);
                    } else if (currType.equals("likert") || currType.equals("smiley")) {
                        displayState = 2;
                        ViewGroup.LayoutParams params1 = viewPager.getLayoutParams();
                        params1.height = screenHeightP * 5/9;
                        viewPager.requestLayout();
                        submit.setText("Next");
                    } else {
                        displayState = 3;
                        Log.i("viewPage", "height to max on question");
                        ViewGroup.LayoutParams paramsQ = viewPager.getLayoutParams();
                        paramsQ.height = screenHeightP * 5/9;
                        viewPager.requestLayout();
                        submit.setText("Next");
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
                Boolean currValidate = currentFragment.validate();
                //getting current question.
                if (currValidate && ViewConstant.needToChangeSection && currPosition == currQuestionPack.size() - 2) {
                    ViewConstant.needToChangeSection = false;
                    currSectionId = ViewConstant.globalCurrSectionId;

                    if (currSectionId.equals("FINISH")){
                        viewPager.setCurrentItem(currQuestionPack.size() - 1);
                    } else {
                        currQuestionPack = ViewConstant.sectionContainer.get(currSectionId).getQuestionPacks();
                        adapter.setPagerItems(currQuestionPack, currPosition);
                        adapter.notifyDataSetChanged();
                        viewPager.setCurrentItem(0);
                    }
                } else if (currValidate) {
                    int nextPosition = currPosition + 1;
                    viewPager.setCurrentItem(nextPosition);
                } else {
                    Toast.makeText(getContext(), "Answer First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return bottomSheetView;
    }

    @Override
    public void onViewCreated(final View contentView, @Nullable Bundle savedInstaceState) {
        super.onViewCreated(contentView, savedInstaceState);
        if (ViewConstant.isRequired) {
            setCancelable(false);
        } else {
            setCancelable(true);
        }
    }

    /*@Override
    public void onViewCreated(final View contentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(contentView, savedInstanceState);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
                FrameLayout bottomSheet = (FrameLayout) dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setPeekHeight(0);
            }
        });
    }*/

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
        ViewConstant.createAnswerForm();
        ViewConstant.hasSurveyed = true;
        Log.i("bottomSheet", "destroyed");
        act.finish();

    }
}

