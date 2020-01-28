package io.methinks.android.apptest.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.methinks.android.apptest.R;

public class SdkStartFragment extends BaseFragment {

    private View view;
    protected JSONObject question;

    public static SdkStartFragment getInstance(JSONObject question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkStartFragment startFragment = new SdkStartFragment();
        Bundle args = new Bundle();
        String questionStringfy = question.toString();
        args.putString("questionString", questionStringfy);
        args.putSerializable("answerMap", answerMap);
        startFragment.setArguments(args);
        return startFragment;
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
        view = inflater.inflate(R.layout.sdk_fragment_start, container, false);
        TextView body = view.findViewById(R.id.popup_methinks_message);
        body.setText(getQuestionText());
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
