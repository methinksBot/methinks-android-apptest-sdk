package io.mtksdk.inappsurvey.converter;

import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import io.mtksdk.inappsurvey.ViewConstant;

/**
 * Created by kgy 2020. 6. 19.
 */

public class Section {
    private String sectionId;
    private JSONArray questions;
    private ArrayList<HashMap<String, ArrayList<Object>>> answerMap;
    private ArrayList<Question> questionPacks;
    private JSONArray sectionLayout;
    private JSONObject currSecSection;
    private String nextSectionId;

    //Constructor
    public Section(String sectionId, JSONArray questions, JSONArray sectionLayout, JSONObject currSecSection) {
        this.sectionId = sectionId;
        this.questions = questions;
        this.answerMap = new ArrayList<>();
        this.questionPacks = new ArrayList<>();
        this.sectionLayout = sectionLayout;
        this.currSecSection = currSecSection;
        this.nextSectionId = "";
        setNextSectionId();
    }

    public void setNextSectionId() {
        if (currSecSection.containsKey("nextSectionId")) {
            this.nextSectionId = (String) currSecSection.get("nextSectionId");
        }
    }

    public String getNextSectionId() {
        return this.nextSectionId;
    }

    public String getSectionId() {
        return this.sectionId;
    }

    public JSONArray getQuestions() {
        return this.questions;
    }

    public ArrayList<HashMap<String, ArrayList<Object>>> getAnswerMap() {
        return this.answerMap;
    }

    public ArrayList<Question> getQuestionPacks() {
        return this.questionPacks;
    }

    public void createQuestionObject() {
        JSONArray currQuestions = getQuestions();
        final HashMap<String, Integer> currSecLayout = getSectionLayout();
        Log.i("currSecLayout", currSecLayout.toString());
        ArrayList<Question> tempPacks = new ArrayList<>();
        for (int i = 0; i < currQuestions.size(); i++) {
            Question currQuestion = new Question( (JSONObject) currQuestions.get(i), sectionId);
            tempPacks.add(currQuestion);
        }

        tempPacks = getSortedData(tempPacks, currSecLayout);
        questionPacks.addAll(tempPacks);
    }

    public HashMap<String, Integer> getSectionLayout() {
        HashMap<String, Integer> output = new HashMap<>();
        for (int i = 0; i < this.sectionLayout.size(); i++) {
            JSONObject currSectionLayout = (JSONObject) sectionLayout.get(i);
            String currQuestionId = (String) currSectionLayout.get("question");
            output.put(currQuestionId, i + 1);
        }

        return output;
    }

    public ArrayList<Question> getSortedData(ArrayList<Question> list, final HashMap<String, Integer> currLayout) {
        Collections.sort(list, new Comparator<Question>() {
            @Override
            public int compare(Question q1, Question q2) {
                return currLayout.get(q1.getQuestionId()) - currLayout.get(q2.getQuestionId());
            }
        });
        return list;
    }
}
