package io.methinks.android.mtkapptestrsdk.question.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.methinks.mtkpatchersdk.R;
import io.methinks.mtkpatchersdk.question.custom.SdkSeekBar;
import io.methinks.mtkpatchersdk.question.custom.SdkSeekBarDotContainer;

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
    protected JSONObject question;


    private boolean hasActing;

    public static SdkScaleFragment getInstance(JSONObject question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkScaleFragment scaleFragment = new SdkScaleFragment();
        Bundle args = new Bundle();
        String questionStringfy = question.toString();
        args.putString("questionString", questionStringfy);
        args.putSerializable("answerMap", answerMap);
        scaleFragment.setArguments(args);
        return scaleFragment;
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

        int tempMaxRange = Integer.parseInt(range[range.length -1]);
        if(question.has("isZeroScale") && question.optBoolean("isZeroScale")){
            tempMaxRange++;
        }

        final int maxRange = tempMaxRange;
        sdkSeekBar.setRange(maxRange);
        sdkSeekBar.setMax(maxRange - 1);

        sdkSeekBar.setListener(new SdkSeekBar.SeekBarListener() {
            @Override   // isZeroScale
            public void didLoadSeekBar(int barStart, int barEnd, float interval) {

                sdkSeekBarDotContainerotContainer.setStartX(barStart);
                sdkSeekBarDotContainerotContainer.setInterval(interval);
                sdkSeekBarDotContainerotContainer.setRange(maxRange);
                sdkSeekBarDotContainerotContainer.setDotSize(6);
                if(question.has("isZeroScale")) {
                    sdkSeekBarDotContainerotContainer.setIsZeroScale(question.optBoolean("isZeroScale"));
                }

                if(question.has("isZeroScale") && question.optBoolean("isZeroScale")) { // zero scale: true
                    sdkSeekBar.setProgress(0);
                    sdkSeekBarDotContainerotContainer.setCurrentProgress(0);
                    setAnswer(0);
                } else {
                    sdkSeekBar.setProgress(0);    // zero scale: false
                    sdkSeekBarDotContainerotContainer.setCurrentProgress(0);
                    setAnswer(1);
                }
                sdkSeekBarDotContainerotContainer.draw();
            }
        });

        sdkSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (hasActing) {
                    //need to work on changeStateActionButton
                    hasActing = true;
                }
                sdkSeekBarDotContainerotContainer.setCurrentProgress(progress);
                if(!question.has("isZeroScale") || !question.optBoolean("isZeroScale")) { // zero scale: false
                    progress += 1;
                }
                setAnswer(progress);

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

        if(question != null){
            // init this question's answer
            if(question.has("isZeroScale") && question.optBoolean("isZeroScale")) { // zero scale: true
                sdkSeekBar.setProgress((int) Math.round(-1));
                sdkSeekBarDotContainerotContainer.setCurrentProgress((int) Math.round(-1));
            } else {
                sdkSeekBar.setProgress((int) Math.round(-1) - 1);    // zero scale: false
                sdkSeekBarDotContainerotContainer.setCurrentProgress((int) Math.round(-1) - 1);
            }
        }

        return view;

    }

    @Override
    public boolean validate() {
        return true;
    }

    private void setAnswer(int progress){
        ArrayList<Object> value = new ArrayList<>();
        value.add(progress);
        answerMap.put(getSubItemId(), value);
    }

    @Override
    public void skipped(){
        answerMap.remove(getSubItemId());
    }

    public String getQuestionText() {
        return question.optString("text");
    }

    public String getSubItemId() {
        return question.optString("objectId");
    }

    public String[] getRange() {
        String scaleRange[] = new String[4];
        for (int i = 0; i < 4; i++) {
            scaleRange[i] = question.optJSONArray("range").optString(i);
        }

        return scaleRange;
    }
}

