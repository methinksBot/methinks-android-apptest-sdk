package io.methinks.android.apptest.question.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.methinks.android.apptest.R;


public class SdkLikertFragment extends BaseFragment {
    private View view;
    protected RadioGroup radioGroup;
    private ArrayList<Integer> selectedPosition;
    private HashMap<Integer, Integer> answerContainer;
    private HashMap<Integer, ImageView> imageMap;
    protected JSONObject question;



    public static SdkLikertFragment getInstance(JSONObject question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkLikertFragment likertFragment = new SdkLikertFragment();
        Bundle args = new Bundle();
        String questionStringfy = question.toString();
        args.putString("questionString", questionStringfy);
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
        String stringQuestion = getArguments().getString("questionString");
        try {
            question = new JSONObject(stringQuestion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        answerContainer = new HashMap<>();
        imageMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sdk_fragment_likert, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.question_content);
        tvLabel.setText(getQuestionText());
        final ImageView rb1 =  view.findViewById(R.id.strong_agree);
        final ImageView rb2 =  view.findViewById(R.id.agree);
        final ImageView rb3 =  view.findViewById(R.id.neutral);
        final ImageView rb4 =  view.findViewById(R.id.disagree);
        final ImageView rb5 =  view.findViewById(R.id.strong_disagree);
        imageMap.put(1, rb1);
        imageMap.put(2, rb2);
        imageMap.put(3, rb3);
        imageMap.put(4, rb4);
        imageMap.put(5, rb5);

        rb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerContainer.containsKey(1)) {
                    rb1.setImageResource(R.drawable.ic_img_single_select_nor);
                    answerContainer.remove(1);
                } else {
                    if (answerContainer.size() != 0) {
                        for (Integer key : answerContainer.keySet()) {
                            ImageView prevAns = imageMap.get(key);
                            prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                            answerContainer.remove(key);
                        }
                    }
                    answerContainer.put(1, 5);
                    rb1.setImageResource(R.drawable.img_single_select_active_color_01);
                }
            }
        });

        rb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerContainer.containsKey(2)) {
                    rb2.setImageResource(R.drawable.ic_img_single_select_nor);
                    answerContainer.remove(2);
                } else {
                    if (answerContainer.size() != 0) {
                        for (Integer key : answerContainer.keySet()) {
                            ImageView prevAns = imageMap.get(key);
                            prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                            answerContainer.remove(key);
                        }
                    }
                    answerContainer.put(2, 4);
                    rb2.setImageResource(R.drawable.img_single_select_active_color_01);
                }
            }
        });

        rb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerContainer.containsKey(3)) {
                    rb3.setImageResource(R.drawable.ic_img_single_select_nor);
                    answerContainer.remove(3);
                } else {
                    if (answerContainer.size() != 0) {
                        for (Integer key : answerContainer.keySet()) {
                            ImageView prevAns = imageMap.get(key);
                            prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                            answerContainer.remove(key);
                        }
                    }
                    answerContainer.put(3, 3);
                    rb3.setImageResource(R.drawable.img_single_select_active_color_01);
                }
            }
        });

        rb4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerContainer.containsKey(4)) {
                    rb4.setImageResource(R.drawable.ic_img_single_select_nor);
                    answerContainer.remove(4);
                } else {
                    if (answerContainer.size() != 0) {
                        for (Integer key : answerContainer.keySet()) {
                            ImageView prevAns = imageMap.get(key);
                            prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                            answerContainer.remove(key);
                        }
                    }
                    answerContainer.put(4, 2);
                    rb4.setImageResource(R.drawable.img_single_select_active_color_01);
                }
            }
        });

        rb5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerContainer.containsKey(5)) {
                    rb5.setImageResource(R.drawable.ic_img_single_select_nor);
                    answerContainer.remove(5);
                } else {
                    if (answerContainer.size() != 0) {
                        for (Integer key : answerContainer.keySet()) {
                            ImageView prevAns = imageMap.get(key);
                            prevAns.setImageResource(R.drawable.ic_img_single_select_nor);
                            answerContainer.remove(key);
                        }
                    }
                    answerContainer.put(5, 1);
                    rb5.setImageResource(R.drawable.img_single_select_active_color_01);
                }
            }
        });


        /*radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                final ImageView rb1 =  view.findViewById(R.id.strong_agree);
                final ImageView rb2 =  view.findViewById(R.id.agree);
                final ImageView rb3 =  view.findViewById(R.id.neutral);
                final ImageView rb4 =  view.findViewById(R.id.disagree);
                final ImageView rb5 =  view.findViewById(R.id.strong_disagree);

                if (checkedId == R.id.strong_agree) {

                } else if (checkedId == R.id.agree) {
                    if (answerContainer.containsKey(checkedId)) {
                        rb2.setImageResource(R.drawable.ic_img_single_select_nor);
                        answerContainer.remove(checkedId);
                    } else {
                        answerContainer.put(checkedId, 4);
                        rb2.setImageResource(R.drawable.img_single_select_active_color_01);
                    }
                } else if (checkedId == R.id.neutral) {
                    if (answerContainer.containsKey(checkedId)) {
                        rb3.setImageResource(R.drawable.ic_img_single_select_nor);
                        answerContainer.remove(checkedId);
                    } else {
                        answerContainer.put(checkedId, 3);
                        rb3.setImageResource(R.drawable.img_single_select_active_color_01);
                    }
                } else if (checkedId == R.id.disagree) {
                    if (answerContainer.containsKey(checkedId)) {
                        rb4.setImageResource(R.drawable.ic_img_single_select_nor);
                        answerContainer.remove(checkedId);
                    } else {
                        answerContainer.put(checkedId, 2);
                        rb4.setImageResource(R.drawable.img_single_select_active_color_01);
                    }
                } else if (checkedId == R.id.strong_disagree) {
                    if (answerContainer.containsKey(checkedId)) {
                        rb5.setImageResource(R.drawable.ic_img_single_select_nor);
                        answerContainer.remove(checkedId);
                    } else {
                        answerContainer.put(checkedId, 1);
                        rb5.setImageResource(R.drawable.img_single_select_active_color_01);
                    }
                }
            }
        });*/

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
            answerMap.put(getSubItemId(), value);
            return true;
        }
    }

    public String getSubItemId() {
        return question.optString("objectId");
    }

    public String getQuestionText() {
        return question.optString("text");
    }

    @Override
    public void skipped() {}
}
