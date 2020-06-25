package io.mtksdk.inappsurvey.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.mtksdk.inappsurvey.R;
import io.mtksdk.inappsurvey.ViewConstant;
import io.mtksdk.inappsurvey.adapter.SdkMultipleChoiceAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.mtksdk.inappsurvey.converter.Question;

/**
 * Created by kgy 2019. 9. 24.
 */

public class SdkMultipleChoiceFragment extends BaseFragment {

    private String title;
    private int page;
    private Boolean multipleSelection;
    private HashMap<String, ArrayList<Object>> answerMap;
    private SdkMultipleChoiceAdapter multipleChoiceAdapter;
    private Question question;
    private HashMap<String, String> sectionSecMap;
    private HashSet<String> sectionSecSet;


    public static SdkMultipleChoiceFragment getInstance(Question question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkMultipleChoiceFragment multipleChoiceFragment = new SdkMultipleChoiceFragment();
        Bundle args = new Bundle();
        args.putSerializable("questionClass", question);
        args.putSerializable("answerMap", answerMap);
        multipleChoiceFragment.setArguments(args);
        return multipleChoiceFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sectionSecMap = new HashMap<>();
        sectionSecSet = new HashSet<>();
        question = (Question) getArguments().getSerializable("questionClass");
        answerMap = (HashMap<String, ArrayList<Object>>) getArguments().getSerializable("answerMap");
        handleSectionSeq();    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sdk_fragment_multiple_choice, container, false);
        /*TextView tvLabel = (TextView) view.findViewById(R.id.question_type);
        tvLabel.setText(page + " -- " + title);*/

        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();

        multipleChoiceAdapter = new SdkMultipleChoiceAdapter(getActivity(), question);

        sectionAdapter.addSection(multipleChoiceAdapter);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.multiple_choice_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);

        return view;
    }

    @Override
    public boolean validate() {
        Boolean isValidate = false;
        if (multipleChoiceAdapter != null) {
            isValidate = multipleChoiceAdapter.validate();
            String currAnswer = (String) question.getAnswerMap().get(question.getQuestionId()).get(0);
            if (currAnswer != null || currAnswer.length() != 0) {
                if (sectionSecSet.contains(currAnswer)) {
                    String nextSectionId = getSectionId(currAnswer);
                    Log.i("sectionChange", nextSectionId);
                    if (question.getSectionId().equals(nextSectionId)) {
                        ViewConstant.needToChangeSection = false;
                        ViewConstant.globalCurrSectionId = nextSectionId;
                        isValidate =  true;
                    } else {
                        ViewConstant.needToChangeSection = true;
                        ViewConstant.globalCurrSectionId = nextSectionId;
                        isValidate = true;
                    }
                }
            }
        }
        return isValidate;
    }

    @Override
    public void skipped() {
        /*answerMap.remove(getQuestionText());*/
    }

    @Override
    public String getType() {
        return question.getQuestionType();
    }

    public String getSectionId(String answer) {
        return sectionSecMap.get(answer);
    }

    public void handleSectionSeq() {
        JSONObject currSecSeq = question.getSetioncSec();
        if (currSecSeq == null) {
            return;
        }
        Iterator keys = currSecSeq.keys();
        while(keys.hasNext()) {
            String currKey = (String)keys.next();
            JSONArray tempArr = currSecSeq.optJSONArray(currKey);
            for (int i = 0; i < tempArr.length(); i++) {
                sectionSecSet.add(tempArr.optString(i));
                sectionSecMap.put(tempArr.optString(i), currKey);
            }
        }
    }


}

