package io.mtksdk.inappsurvey.converter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by kgy 2020. 6. 19.
 */

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
        if (question.containsKey("objectId")) { //required
            this.questionId = (String) question.get("objectId");
        }
        if (question.containsKey("text")) { //required
            this.text = (String) question.get("text");
        }
        if (question.containsKey("questionType")) { //required
            this.questionType = (String) question.get("questionType");
        }
        this.finalChoices = new ArrayList<>();
        this.answerMap = new HashMap<>();
        this.isShortForm = false;

        //range rule
        if (questionType.equals("smiley") || questionType.equals("likert") || questionType.equals("range")) {
            this.ruleName = "rangeRule";
            if (question.containsKey("sectionSequence")) {
                this.sectionSec = (JSONObject) question.get("sectionSequence");
            }
        } else {
            this.ruleName = questionType + "Rule";
        }

        if (question.containsKey(ruleName)) {
            this.rule = (JSONObject) question.get(ruleName); //required
        }

        if (question.containsKey("required")) {
            this.required = (Boolean) question.get("required");
        }

        //multipleChoice
        if (questionType.equals("multipleChoice")) {
            if (question.containsKey("sectionSequence")) {
                this.sectionSec = (JSONObject) question.get("sectionSequence");
            }
            if (question.containsKey("choices")) { //required for multiple choice
                this.choices = (JSONArray) question.get("choices");
                for (int i = 0; i < choices.size(); i++) {
                    finalChoices.add((String) choices.get(i));
                }
                if (this.rule.containsKey("shuffleOrder") && (Boolean) this.rule.get("shuffleOrder")) {
                    Collections.shuffle(finalChoices);
                }
            }
        }

        //openEnd
        if (questionType.equals("openEnd") && (Boolean) rule.get("isShortForm")) {
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
