package io.mtksdk.inappsurvey.converter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Question implements Serializable {
    private String sectionId;
    private String questionId;
    private String text;
    private String questionType;
    private Boolean required;
    private JSONObject rule;
    private JSONArray choices;
    private ArrayList<String> finalChoices = new ArrayList<>();
    private String ruleName;
    private JSONObject sectionSec;
    private Boolean isShortForm;
    private HashMap<String, ArrayList<Object>> answerMap;

    public Question(JSONObject question, String sectionId) {
        this.sectionId = sectionId;
        this.questionId = question.optString("objectId");
        this.text = question.optString("text");
        this.questionType = question.optString("questionType");
        this.finalChoices = new ArrayList<>();
        this.answerMap = new HashMap<>();
        this.isShortForm = false;

        //range rule
        if (questionType.equals("smiley") || questionType.equals("likert") || questionType.equals("range")) {
            this.ruleName = "rangeRule";
            if (question.has("sectionSequence")) {
                this.sectionSec = question.optJSONObject("sectionSequence");
            }
        } else {
            this.ruleName = questionType + "Rule";
        }

        this.rule = question.optJSONObject(ruleName);

        this.required = question.optBoolean("required");

        //multipleChoice
        if (questionType.equals("multipleChoice")) {
            if (question.has("sectionSequence")) {
                this.sectionSec = question.optJSONObject("sectionSequence");
            }
            this.choices = question.optJSONArray("choices");
            for (int i = 0; i < choices.length(); i++) {
                finalChoices.add(choices.optString(i));
            }
            if (this.rule.has("shuffleOrder") && this.rule.optBoolean("shuffleOrder")) {
                Collections.shuffle(finalChoices);
            }
        }

        //openEnd
        if (questionType.equals("openEnd") && rule.optBoolean("isShortForm")) {
            this.isShortForm = true;
        }

    }

    public Question(String intro) {
        this.questionType = intro;
        if (intro.equals("intro")) {
            this.text = "Welcome to methinks!";
        } else {
            this.text = "Thank you";
        }
    }





    //Getter


    public String getSectionId() {
        return sectionId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getText() {
        return text;
    }

    public String getQuestionType() {
        return questionType;
    }

    public Boolean getRequired() {
        return required;
    }

    public JSONObject getRule() {
        return rule;
    }

    public JSONArray getChoices() {
        return choices;
    }

    public String getRuleName() {
        return ruleName;
    }

    public ArrayList<String> getFinalChoices() {
        return finalChoices;
    }

    public JSONObject getSetioncSec() {
        return sectionSec;
    }

    public Boolean getIsShortForm() {
        return isShortForm;
    }

    public void setAnswerMap(ArrayList<Object> answers) {
        String objId = getQuestionId();
        this.answerMap.put(objId, answers);
    }

    public HashMap<String, ArrayList<Object>> getAnswerMap() {
        return this.answerMap;
    }
}
