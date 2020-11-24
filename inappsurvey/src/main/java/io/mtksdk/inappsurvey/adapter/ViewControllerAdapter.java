package io.mtksdk.inappsurvey.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import io.mtksdk.inappsurvey.ViewConstant;
import io.mtksdk.inappsurvey.converter.Question;
import io.mtksdk.inappsurvey.fragment.BaseFragment;
import io.mtksdk.inappsurvey.fragment.SdkCloseFragment;
import io.mtksdk.inappsurvey.fragment.SdkLikertFragment;
import io.mtksdk.inappsurvey.fragment.SdkMultipleChoiceFragment;
import io.mtksdk.inappsurvey.fragment.SdkOpenEndShortFragment;
import io.mtksdk.inappsurvey.fragment.SdkOpenEndLongFragment;
import io.mtksdk.inappsurvey.fragment.SdkScaleFragment;
import io.mtksdk.inappsurvey.fragment.SdkSmileyFragment;
import io.mtksdk.inappsurvey.fragment.SdkStartFragment;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kgy 2019. 9. 24.
 */

public class ViewControllerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS;
    protected  ArrayList<Question> questions;
    private ArrayList<java.lang.Object> parseQuestions;
    private HashMap<String, ArrayList<Object>> answerMap;
    private HashMap<Integer, BaseFragment> fragments;


    public ViewControllerAdapter(FragmentManager fm, HashMap<String, ArrayList<Object>> answerMap, ArrayList<Question> questions) {
        super(fm);
        this.answerMap = answerMap;
        this.questions = questions;
        this.fragments = new HashMap<>();
    }

    @Override
    public int getCount() {
        NUM_ITEMS = questions.size();
        return NUM_ITEMS;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        Question question = questions.get(position);

        BaseFragment item = null;
        if (fragments.containsKey(position)) {
            item = fragments.get(position);
        } else {
            if (position == 0) {
                ViewConstant.startingType = question.getQuestionType();
            }
            if (question.getQuestionType().equals("intro")) {
                item = SdkStartFragment.getInstance(question, answerMap);
            } else if (question.getQuestionType().equals("multipleChoice")) {
                item = SdkMultipleChoiceFragment.getInstance(question,answerMap);
            } else if (question.getQuestionType().equals("openEnd") && question.getIsShortForm()) {
                item = SdkOpenEndShortFragment.getInstance(question,answerMap);
            } else if (question.getQuestionType().equals("openEnd") && !question.getIsShortForm()) {
                item = SdkOpenEndLongFragment.getInstance(question,answerMap);
            } else if (question.getQuestionType().equals("range")) {
                item = SdkScaleFragment.getInstance(question,answerMap);
            } else if (question.getQuestionType().equals("likert")) {
                item = SdkLikertFragment.getInstance(question, answerMap);
            } else if (question.getQuestionType().equals("smiley")) {
                item = SdkSmileyFragment.getInstance(question, answerMap);
            } else if (question.getQuestionType().equals("outro")){
                item = SdkCloseFragment.getInstance(question, answerMap);
            }
            fragments.put(position, item);
        }
        return item;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page" + position;
    }

    public void setPagerItems(ArrayList<Question> newQuestions) {
        if (fragments != null) {
            fragments.clear();
            questions = newQuestions;
        }
    }

}
