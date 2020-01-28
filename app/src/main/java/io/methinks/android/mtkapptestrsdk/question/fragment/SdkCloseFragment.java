package io.methinks.android.mtkapptestrsdk.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.methinks.mtkpatchersdk.R;
import io.methinks.mtkpatchersdk.question.custom.widget.MethinksTextView;


public class SdkCloseFragment extends BaseFragment {

    private View view;
    protected JSONObject question;

    public static SdkCloseFragment getInstance(JSONObject question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkCloseFragment closeFragment = new SdkCloseFragment();
        Bundle args = new Bundle();
        String questionStringfy = question.toString();
        args.putString("questionString", questionStringfy);
        args.putSerializable("answerMap", answerMap);
        closeFragment.setArguments(args);
        return closeFragment;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sdk_fragment_close, container, false);
        MethinksTextView tvLabel = view.findViewById(R.id.close_methinks_title);
        tvLabel.setText(getQuestionText());
        return view;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public void skipped() {}

    public String getQuestionText() {
        return question.optString("text");
    }
}
