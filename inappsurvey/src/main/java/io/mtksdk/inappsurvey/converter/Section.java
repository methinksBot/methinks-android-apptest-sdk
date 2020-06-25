package io.mtksdk.inappsurvey.converter;

import android.util.Log;

import org.json.JSONArray;

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

    //Constructor
    public Section(String sectionId, JSONArray questions, JSONArray sectionLayout) {
        this.sectionId = sectionId;
        this.questions = questions;
        this.answerMap = new ArrayList<>();
        this.questionPacks = new ArrayList<>();
        this.sectionLayout = sectionLayout;
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
        if (getSectionId().equals(ViewConstant.firstSectionId)) {
            Question intro = new Question("intro");
            questionPacks.add(intro);
        }
        JSONArray currQuestions = getQuestions();
        final HashMap<String, Integer> currSecLayout = getSectionLayout();
        Log.i("currSecLayout", currSecLayout.toString());
        ArrayList<Question> tempPacks = new ArrayList<>();
        for (int i = 0; i < currQuestions.length(); i++) {
            Question currQuestion = new Question(currQuestions.optJSONObject(i), sectionId);
            tempPacks.add(currQuestion);
        }

        tempPacks = getSortedData(tempPacks, currSecLayout);
        questionPacks.addAll(tempPacks);

        Question outro = new Question("outro");
        questionPacks.add(outro);
    }

    public HashMap<String, Integer> getSectionLayout() {
        HashMap<String, Integer> output = new HashMap<>();
        for (int i = 0; i < this.sectionLayout.length(); i++) {
            String currQuestionId = sectionLayout.optJSONObject(i).optString("question");
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
