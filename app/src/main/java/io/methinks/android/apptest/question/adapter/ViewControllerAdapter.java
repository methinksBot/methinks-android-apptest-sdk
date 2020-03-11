package io.methinks.android.apptest.question.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.methinks.android.apptest.question.fragment.BaseFragment;
import io.methinks.android.apptest.question.fragment.SdkCloseFragment;
import io.methinks.android.apptest.question.fragment.SdkLikertFragment;
import io.methinks.android.apptest.question.fragment.SdkMultipleChoiceFragment;
import io.methinks.android.apptest.question.fragment.SdkOpenEndFragment;
import io.methinks.android.apptest.question.fragment.SdkScaleFragment;
import io.methinks.android.apptest.question.fragment.SdkSmileyFragment;
import io.methinks.android.apptest.question.fragment.SdkStartFragment;

/**
 * Created by kgy 2019. 9. 24.
 */

public class ViewControllerAdapter extends FragmentStatePagerAdapter {
    private static int NUM_ITEMS;
    protected ArrayList<JSONObject> questions;
    private ArrayList<Object> parseQuestions;
    private HashMap<String, ArrayList<Object>> answerMap;
    private HashMap<Integer, BaseFragment> fragments;


    public ViewControllerAdapter(FragmentManager fm, HashMap<String, ArrayList<Object>> answerMap, ArrayList<JSONObject> questions) {
        super(fm);
        this.answerMap = answerMap;
        this.questions = questions;
        this.fragments = new HashMap<>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        NUM_ITEMS = questions.size();
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        /*String[] rangeOne = {"1", "", "10", "10"};
        String[] rangeTwo = {"1", "5", "10", "10"};
        String[] rangeThree = {"1", "4", "7", "7"};*/
        BaseFragment item = null;
        if (fragments.containsKey(position)) {
            item = fragments.get(position);
        } else {
            String type = questions.get(position).has("type") ? questions.get(position).optString("type") : questions.get(position).optString("questionType");

            if (type.equals("intro")) {
                item = SdkStartFragment.getInstance(questions.get(position), answerMap);
            } else if (type.equals("multipleChoice")) {
                item = SdkMultipleChoiceFragment.getInstance(questions.get(position),answerMap);
            } else if (type.equals("openEnd")) {
                item = SdkOpenEndFragment.getInstance(questions.get(position),answerMap);
            } else if (type.equals("range")) {
                item = SdkScaleFragment.getInstance(questions.get(position),answerMap);
            } else if (type.equals("likert")) {
                item = SdkLikertFragment.getInstance(questions.get(position), answerMap);
            } else if (type.equals("smiley")) {
                item = SdkSmileyFragment.getInstance(questions.get(position), answerMap);
            } else if (type.equals("outro")){
                item = SdkCloseFragment.getInstance(questions.get(position), answerMap);
            }else{
            }
            if(item == null){
            }

            fragments.put(position, item);
        }
        return item;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page" + position;
    }

}
