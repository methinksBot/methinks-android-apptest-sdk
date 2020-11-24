package io.mtksdk.inappsurvey.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class SdkSmileyFragment extends BaseFragment {
    private View view;
    protected RadioGroup radioGroup;
    private ArrayList<Integer> selectedPosition;
    private HashMap<Integer, Integer> answerContainer;
    private HashMap<Integer, TextView> imageMap;
    private Question question;
    private HashMap<Integer, String> sectionSecMap;




    public static SdkSmileyFragment getInstance(Question question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkSmileyFragment smileyFragment = new SdkSmileyFragment();
        Bundle args = new Bundle();
        args.putSerializable("questionClass", question);
        args.putSerializable("answerMap", answerMap);
        smileyFragment.setArguments(args);
        return smileyFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question = (Question) getArguments().getSerializable("questionClass");
        answerContainer = new HashMap<>();
        imageMap = new HashMap<>();
        sectionSecMap = new HashMap<>();
        handleSectionSeq();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sdk_fragment_smiley, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.question_content);
        tvLabel.setText(getQuestionText());
        LinearLayout original = view.findViewById(R.id.original);

        if (question.getRule().containsKey("scale") && (Long) question.getRule().get("scale") == 3) {
            original.setVisibility(View.GONE);
            LinearLayout three = view.findViewById(R.id.scale_three);
            three.setVisibility(View.VISIBLE);
            imageMap.clear();
            final TextView sOne = view.findViewById(R.id.st_one);
            final TextView sTwo = view.findViewById(R.id.st_two);
            final TextView sThree = view.findViewById(R.id.st_three);
            sOne.setText(R.string.frwoning);
            sTwo.setText(R.string.neutral);
            sThree.setText(R.string.smile);
            imageMap.put(1, sOne);
            imageMap.put(2, sTwo);
            imageMap.put(3, sThree);
            sOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(1)) {
                        sOne.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(1);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(1, 1);
                        sOne.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            sTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(2)) {
                        sTwo.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(2);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(2, 2);
                        sTwo.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            sThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(3)) {
                        sThree.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(3);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(3, 3);
                        sThree.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });
        } else {
            imageMap.clear();
            final TextView rb1 =  view.findViewById(R.id.s_strong_agree);
            final TextView rb2 =  view.findViewById(R.id.s_agree);
            final TextView rb3 =  view.findViewById(R.id.s_neutral);
            final TextView rb4 =  view.findViewById(R.id.s_disagree);
            final TextView rb5 =  view.findViewById(R.id.s_strong_disagree);
            rb1.setText(R.string.smile);
            rb2.setText(R.string.slightly_smile);
            rb3.setText(R.string.neutral);
            rb4.setText(R.string.slightly_frowning);
            rb5.setText(R.string.frwoning);
            imageMap.put(1, rb1);
            imageMap.put(2, rb2);
            imageMap.put(3, rb3);
            imageMap.put(4, rb4);
            imageMap.put(5, rb5);

            rb1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(1)) {
                        rb1.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(1);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(1, 5);
                        rb1.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            rb2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(2)) {
                        rb2.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(2);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(2, 4);
                        rb2.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            rb3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(3)) {
                        rb3.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(3);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(3, 3);
                        rb3.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            rb4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(4)) {
                        rb4.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(4);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(4, 2);
                        rb4.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            rb5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(5)) {
                        rb5.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(5);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(5, 1);
                        rb5.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });
        }
        return view;
    }

    @Override
    public boolean validate() {
        if (answerContainer.size() == 0) {
            return false;
        } else {
            ArrayList<Object> value = new ArrayList<>();
            for (Integer key : answerContainer.keySet()) {
                value.add(answerContainer.get(key));
                break;
            }
            question.setAnswerMap(value);

            Integer currAnswer = (Integer) value.get(0);
            if (currAnswer != null) {
                if (sectionSecMap.containsKey(currAnswer)) {
                    String nextSectionId = getSectionId(currAnswer);
                    if (question.getSectionId().equals(nextSectionId)) {
                        return true;
                    } else {
                        ViewConstant.needToChangeSection = true;
                        ViewConstant.globalCurrSectionId = nextSectionId;
                        return true;
                    }
                }
            }
            Log.i("smiley", answerMap.toString());
            return true;
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
                long tempL = (long) tempArr.get(i);
                sectionSecMap.put((int) tempL, currKey);
            }
        }
    }

    public String getSectionId(Integer answer) {
        return sectionSecMap.get(answer);
    }
}

