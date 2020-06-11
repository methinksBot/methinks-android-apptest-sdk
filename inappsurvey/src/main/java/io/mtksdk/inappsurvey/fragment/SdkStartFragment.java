package io.mtksdk.inappsurvey.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.mtksdk.inappsurvey.R;
import io.mtksdk.inappsurvey.ViewConstant;
import io.mtksdk.inappsurvey.converter.Question;

public class SdkStartFragment extends BaseFragment {

    private View view;
    protected Question question;

    public static SdkStartFragment getInstance(Question question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkStartFragment startFragment = new SdkStartFragment();
        Bundle args = new Bundle();
        args.putSerializable("questionClass", question);
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
        question = (Question) getArguments().getSerializable("questionClass");
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
        return question.getText();
    }

    @Override
    public String getType() {
        return question.getQuestionType();
    }

}
