package io.mtksdk.inappsurvey.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import io.mtksdk.inappsurvey.R;
import io.mtksdk.inappsurvey.ViewConstant;
import io.mtksdk.inappsurvey.converter.Question;
import io.mtksdk.inappsurvey.custom.SdkSeekBar;
import io.mtksdk.inappsurvey.custom.SdkSeekBarDotContainer;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kgy 2019. 9. 24.
 */
public class SdkScaleFragment extends BaseFragment {

    private String title;
    private int page;
    private TextView questionContent;
    private ImageView attachedImage;
    private SdkSeekBar sdkSeekBar;
    private TextView low;
    private TextView mid;
    private TextView high;
    private SdkSeekBarDotContainer sdkSeekBarDotContainerotContainer;
    private LinearLayout numContainer;
    private String[] range;
    //    protected JSONObject question;
    private Question question;
    private HashMap<Integer, String> sectionSecMap;
    private Integer currAnswer;



    private boolean hasActing;

    public static SdkScaleFragment getInstance(Question question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkScaleFragment scaleFragment = new SdkScaleFragment();
        Bundle args = new Bundle();
//        String questionStringfy = question.toString();
//        args.putString("questionString", questionStringfy);
        args.putSerializable("questionClass", question);
        args.putSerializable("answerMap", answerMap);
        scaleFragment.setArguments(args);
        return scaleFragment;
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
        sectionSecMap = new HashMap<>();
        currAnswer = 0;
        handleSectionSeq();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sdk_fragment_scale, container, false);
        questionContent = view.findViewById(R.id.question_content);
        questionContent.setText(getQuestionText());
        sdkSeekBar =  view.findViewById(R.id.sdk_seek_bar);
        low = view.findViewById(R.id.low);
        mid = view.findViewById(R.id.mid);
        high = view.findViewById(R.id.high);
        sdkSeekBarDotContainerotContainer = view.findViewById(R.id.dot_container);
        numContainer = view.findViewById(R.id.num_container);

        range = getRange();

        final int maxRange = getScale();
        final float intervalCalc = sdkSeekBarDotContainerotContainer.getInterval(getScale());

        sdkSeekBar.setRange(maxRange);
        sdkSeekBar.setMax(maxRange - 1);

        sdkSeekBar.setListener(new SdkSeekBar.SeekBarListener() {
            @Override
            public void didLoadSeekBar(int barStart, int barEnd, float interval) {
                sdkSeekBarDotContainerotContainer.setStartX(barStart);
                sdkSeekBarDotContainerotContainer.setInterval(intervalCalc);
                sdkSeekBarDotContainerotContainer.setRange(maxRange);
                sdkSeekBarDotContainerotContainer.setDotSize(6);
                sdkSeekBarDotContainerotContainer.setIsZeroScale(getIsZeroScale());
                sdkSeekBarDotContainerotContainer.draw();
                if (answerMap != null && answerMap.containsKey(getSubItemId())) {                            //Saved answer shows
                    ArrayList<Object> savedPoint = answerMap.get(getSubItemId());
                    sdkSeekBar.setProgress((int) savedPoint.get(0) - 1);
                } else {                                                                            //No saved answer exist, starting new
                    double value = (double)maxRange / (double)2;
                    setAnswer((int)Math.round(value));                                              // init this question's answer*/
                    sdkSeekBar.setProgress(0);                             // init this question's answer
                }
            }
        });

        sdkSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (hasActing) {
                    //need to work on changeStateActionButton
                }
                sdkSeekBarDotContainerotContainer.setCurrentProgress(i);
                i += 1;
                setAnswer(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        if (TextUtils.isEmpty(range[1])) {                //no mid
            low.setText(range[0]);
            mid.setText("");
            high.setText(range[2]);
        } else {
            low.setText(range[0]);
            mid.setVisibility(View.VISIBLE);
            mid.setText(range[1]);
            high.setText(range[2]);
        }

        return view;

    }

    @Override
    public boolean validate() {
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
        Log.i("range", answerMap.toString());
        return true;
    }

    private void setAnswer(int progress){
        currAnswer = progress;
        ArrayList<Object> value = new ArrayList<>();
        value.add(progress);
//        answerMap.put(getSubItemId(), value);
        question.setAnswerMap(value);
    }

    @Override
    public void skipped(){
        answerMap.remove(getSubItemId());
    }

    public String getQuestionText() {
        return question.getText();
    }

    public String getSubItemId() {
        return question.getQuestionId();
    }

    public String[] getRange() {
        JSONArray labels = (JSONArray) question.getRule().get("labels");
        String[] scaleRange = new String[labels.size()];
        for (int i = 0; i < scaleRange.length; i++) {
            scaleRange[i] = (String) labels.get(i);
        }
        return scaleRange;
    }

    public int getScale() {
        long tempL = (long) question.getRule().get("scale");
        return (int) tempL;
    }

    @Override
    public String getType() {
        return question.getQuestionType();
    }

    public Boolean getIsZeroScale() {
        return (Boolean) question.getRule().get("isZeroScale");
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
                long tempLong = (long) tempArr.get(i);
                int convertedInt = (int) tempLong;
                sectionSecMap.put(convertedInt, currKey);
            }
        }
    }

    public String getSectionId(Integer answer) {
        return sectionSecMap.get(answer);
    }

}

