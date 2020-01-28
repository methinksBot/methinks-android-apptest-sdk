package io.methinks.android.apptest.question.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.methinks.android.apptest.R;
import io.methinks.android.apptest.question.adapter.SdkMultipleChoiceAdapter;

/**
 * Created by kgy 2019. 9. 24.
 */

public class SdkMultipleChoiceFragment extends BaseFragment {

    private String title;
    private int page;
    private Boolean multipleSelection;
    private HashMap<String, ArrayList<Object>> answerMap;
    protected JSONObject question;
    private SdkMultipleChoiceAdapter multipleChoiceAdapter;


    public static SdkMultipleChoiceFragment getInstance(JSONObject question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkMultipleChoiceFragment multipleChoiceFragment = new SdkMultipleChoiceFragment();
        Bundle args = new Bundle();
        String questionStringfy = question.toString();
        args.putString("questionString", questionStringfy);
        args.putSerializable("answerMap", answerMap);
        multipleChoiceFragment.setArguments(args);
        return multipleChoiceFragment;
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
        answerMap = (HashMap<String, ArrayList<Object>>) getArguments().getSerializable("answerMap");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sdk_fragment_multiple_choice, container, false);
        /*TextView tvLabel = (TextView) view.findViewById(R.id.question_type);
        tvLabel.setText(page + " -- " + title);*/

        ArrayList<String> choiceList = getChoiceList();
        SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();

        multipleChoiceAdapter = new SdkMultipleChoiceAdapter(getActivity(),getQuestionText(),getSubItemId(), choiceList, isMultiSelection(), isShuffleOrder(), answerMap);

        sectionAdapter.addSection(multipleChoiceAdapter);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.multiple_choice_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);

        return view;
    }

    @Override
    public boolean validate() {
        if (multipleChoiceAdapter != null) {
            return multipleChoiceAdapter.validate();
        } else {
            return false;
        }
    }

    public ArrayList<String> getChoiceList() {
        ArrayList<String> choices = new ArrayList<>();
        JSONArray choiceFromQ = question.optJSONArray("choices");
        for (int i = 0; i < choiceFromQ.length(); i++) {
            choices.add(choiceFromQ.optString(i));
        }
        return choices;
    }

    @Override
    public void skipped() {
        /*answerMap.remove(getQuestionText());*/
    }

    public String getSubItemId() {
        return question.optString("objectId");
    }

    public String getQuestionText() {
        return question.optString("text");
    }

    public Boolean isMultiSelection() {
        return question.optBoolean("allowMultipleSelection");
    }

    public Boolean isShuffleOrder() {
        return question.has("shuffleOrder") && question.optBoolean("shuffleOrder");
    }

}

