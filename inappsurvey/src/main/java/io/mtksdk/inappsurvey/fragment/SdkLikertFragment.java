package io.mtksdk.inappsurvey.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;


import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import io.mtksdk.inappsurvey.R;
import io.mtksdk.inappsurvey.ViewConstant;
import io.mtksdk.inappsurvey.converter.Question;
import io.mtksdk.inappsurvey.custom.widget.MethinksTextView;


public class SdkLikertFragment extends BaseFragment {
    private View view;
    protected RadioGroup radioGroup;
    private ArrayList<Integer> selectedPosition;
    private HashMap<Integer, Integer> answerContainer;
    private HashMap<Integer, ImageView> imageMap;
    //    protected JSONObject question;
    private Question question;
    private String questionType;
    private HashMap<String, String> sectionSecMap;
    private ArrayList<String> labelList;
    private Context mContext;



    public static SdkLikertFragment getInstance(Question question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkLikertFragment likertFragment = new SdkLikertFragment();
        Bundle args = new Bundle();
//        String questionStringfy = question.toString();
//        args.putString("questionString", questionStringfy);
        args.putSerializable("questionClass", question);
        args.putSerializable("answerMap", answerMap);
        likertFragment.setArguments(args);
        return likertFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String stringQuestion = getArguments().getString("questionString");
//        try {
//            question = new JSONObject(stringQuestion);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        question = (Question) getArguments().getSerializable("questionClass");
        answerContainer = new HashMap<>();
        imageMap = new HashMap<>();
        sectionSecMap = new HashMap<>();
        handleSectionSeq();
        labelList = getLabelList();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sdk_fragment_likert, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.question_content);
        tvLabel.setText(getQuestionText());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int scale = getScale();
        int width = displayMetrics.widthPixels;
        int newWidth = (width - (int) convertDpToPixel(mContext, 36)) / scale;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(newWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;

        MethinksTextView label01 = view.findViewById(R.id.tv_label_01);
        MethinksTextView label02 = view.findViewById(R.id.tv_label_02);
        MethinksTextView label03 = view.findViewById(R.id.tv_label_03);
        MethinksTextView label04 = view.findViewById(R.id.tv_label_04);
        MethinksTextView label05 = view.findViewById(R.id.tv_label_05);
        MethinksTextView label06 = view.findViewById(R.id.tv_label_06);
        MethinksTextView label07 = view.findViewById(R.id.tv_label_07);
        final ImageView dot01 = view.findViewById(R.id.iv_dot_01);
        final ImageView dot02 = view.findViewById(R.id.iv_dot_02);
        final ImageView dot03 = view.findViewById(R.id.iv_dot_03);
        final ImageView dot04 = view.findViewById(R.id.iv_dot_04);
        final ImageView dot05 = view.findViewById(R.id.iv_dot_05);
        final ImageView dot06 = view.findViewById(R.id.iv_dot_06);
        final ImageView dot07 = view.findViewById(R.id.iv_dot_07);

        imageMap.put(1, dot01);
        imageMap.put(2, dot02);
        imageMap.put(3, dot03);
        imageMap.put(4, dot04);
        imageMap.put(5, dot05);
        imageMap.put(6, dot06);
        imageMap.put(7, dot07);

        label01.setText(labelList.get(0));
        label02.setText(labelList.get(1));
        label03.setText(labelList.get(2));
        label04.setText(labelList.get(3));
        label05.setText(labelList.get(4));
        label01.setTextSize(13);
        label02.setTextSize(13);
        label03.setTextSize(13);
        label04.setTextSize(13);
        label05.setTextSize(13);
        label01.setLayoutParams(params);
        label02.setLayoutParams(params);
        label03.setLayoutParams(params);
        label04.setLayoutParams(params);
        label05.setLayoutParams(params);

        if (scale == 7) {
            label06.setVisibility(View.VISIBLE);
            label07.setVisibility(View.VISIBLE);
            label06.setText(labelList.get(5));
            label07.setText(labelList.get(6));
            label01.setTextSize(12);
            label02.setTextSize(12);
            label03.setTextSize(12);
            label04.setTextSize(12);
            label05.setTextSize(12);
            label06.setTextSize(12);
            label07.setTextSize(12);
            label01.setLayoutParams(params);
            label02.setLayoutParams(params);
            label03.setLayoutParams(params);
            label04.setLayoutParams(params);
            label05.setLayoutParams(params);
            label06.setLayoutParams(params);
            label07.setLayoutParams(params);
            dot06.setVisibility(View.VISIBLE);
            dot07.setVisibility(View.VISIBLE);
            dot06.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answerContainer.containsKey(6)) {
                        dot06.setImageResource(R.drawable.ic_img_single_select_nor);
                        answerContainer.remove(6);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                ImageView prevAns = imageMap.get(key);
                                prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(6, 5);
                        dot06.setImageResource(R.drawable.img_single_select_active_color_01);
                    }
                }
            });

            dot07.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (answerContainer.containsKey(7)) {
                        dot07.setImageResource(R.drawable.ic_img_single_select_nor);
                        answerContainer.remove(7);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                ImageView prevAns = imageMap.get(key);
                                prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(7, 6);
                        dot07.setImageResource(R.drawable.img_single_select_active_color_01);
                    }
                }
            });
        } else {
            label06.setVisibility(View.GONE);
            label07.setVisibility(View.GONE);
            dot06.setVisibility(View.GONE);
            dot07.setVisibility(View.GONE);
        }

        label01.setText(labelList.get(0));
        label02.setText(labelList.get(1));
        label03.setText(labelList.get(2));
        label04.setText(labelList.get(3));
        label05.setText(labelList.get(4));

        dot01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerContainer.containsKey(1)) {
                    dot01.setImageResource(R.drawable.ic_img_single_select_nor);
                    answerContainer.remove(1);
                } else {
                    if (answerContainer.size() != 0) {
                        for (Integer key : answerContainer.keySet()) {
                            ImageView prevAns = imageMap.get(key);
                            prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                            answerContainer.remove(key);
                        }
                    }
                    answerContainer.put(1, 0);
                    dot01.setImageResource(R.drawable.img_single_select_active_color_01);
                }
            }
        });

        dot02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerContainer.containsKey(2)) {
                    dot02.setImageResource(R.drawable.ic_img_single_select_nor);
                    answerContainer.remove(2);
                } else {
                    if (answerContainer.size() != 0) {
                        for (Integer key : answerContainer.keySet()) {
                            ImageView prevAns = imageMap.get(key);
                            prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                            answerContainer.remove(key);
                        }
                    }
                    answerContainer.put(2, 1);
                    dot02.setImageResource(R.drawable.img_single_select_active_color_01);
                }
            }
        });

        dot03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerContainer.containsKey(3)) {
                    dot03.setImageResource(R.drawable.ic_img_single_select_nor);
                    answerContainer.remove(3);
                } else {
                    if (answerContainer.size() != 0) {
                        for (Integer key : answerContainer.keySet()) {
                            ImageView prevAns = imageMap.get(key);
                            prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                            answerContainer.remove(key);
                        }
                    }
                    answerContainer.put(3, 2);
                    dot03.setImageResource(R.drawable.img_single_select_active_color_01);
                }
            }
        });

        dot04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerContainer.containsKey(4)) {
                    dot04.setImageResource(R.drawable.ic_img_single_select_nor);
                    answerContainer.remove(4);
                } else {
                    if (answerContainer.size() != 0) {
                        for (Integer key : answerContainer.keySet()) {
                            ImageView prevAns = imageMap.get(key);
                            prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                            answerContainer.remove(key);
                        }
                    }
                    answerContainer.put(4, 3);
                    dot04.setImageResource(R.drawable.img_single_select_active_color_01);
                }
            }
        });

        dot05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerContainer.containsKey(5)) {
                    dot05.setImageResource(R.drawable.ic_img_single_select_nor);
                    answerContainer.remove(5);
                } else {
                    if (answerContainer.size() != 0) {
                        for (Integer key : answerContainer.keySet()) {
                            ImageView prevAns = imageMap.get(key);
                            prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                            answerContainer.remove(key);
                        }
                    }
                    answerContainer.put(5, 4);
                    dot05.setImageResource(R.drawable.img_single_select_active_color_01);
                }
            }
        });

        return view;
    }

    @Override
    public boolean validate() {
        if (answerContainer.size() == 0) {
            return false;
        } else {
            ArrayList<Object> value = new ArrayList<>();
            for (Integer key : answerContainer.keySet()) {
                Integer answerNum = answerContainer.get(key);
                String labelAnswer = labelList.get(answerNum);
                value.add(labelAnswer);
                break;
            }
            question.setAnswerMap(value);

            String currAnswer = (String) value.get(0);
            if (currAnswer != null) {
                if (sectionSecMap.containsKey(currAnswer)) {
                    String nextSectionId = getSectionId(currAnswer);
                    if (question.getSectionId().equals(nextSectionId)) {
                        ViewConstant.needToChangeSection = false;
                        ViewConstant.globalCurrSectionId = nextSectionId;
                        return true;
                    } else {
                        ViewConstant.needToChangeSection = true;
                        ViewConstant.globalCurrSectionId = nextSectionId;
                        return true;
                    }
                } else {
                    return true;
                }
            }
            Log.i("likert", answerMap.toString());
            return true;
//            answerMap.put(getSubItemId(), value);
        }
    }

    public String getSubItemId() {
        return question.getQuestionId();
    }

    public String getQuestionText() {
        return question.getText();
    }

    @Override
    public void skipped() {}

    @Override
    public String getType() {
        return question.getQuestionType();
    }

    public void handleSectionSeq() {
        JSONObject currSecSeq = question.getSetioncSec();
        if (currSecSeq == null) {
            return;
        }

        for (Object key : currSecSeq.keySet()) {
            String currKey = (String) key;
            JSONArray tempArr = (JSONArray) currSecSeq.get(currKey);
            for (int i = 0; i < tempArr.size(); i++) {
                sectionSecMap.put((String) tempArr.get(i), currKey);
            }
        }
    }

    public String getSectionId(String answer) {
        return sectionSecMap.get(answer);
    }

    public int getScale() {
        long tempL = (long) question.getRule().get("scale");
        return (int) tempL;
    }

    public ArrayList<String> getLabelList() {
        JSONArray labelList = (JSONArray) question.getRule().get("labels");
        ArrayList<String> output = new ArrayList<>();
        for (int i = 0; i < labelList.size(); i++) {
            output.add((String)labelList.get(i));
        }
        return output;
    }

    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

}
