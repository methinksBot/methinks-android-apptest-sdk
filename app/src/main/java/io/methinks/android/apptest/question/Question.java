package io.methinks.android.apptest.question;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Question {
    private String questionId;
    private String questionType;
    private int sequence;
    private String eventName;
    private int activationTime;
    private ArrayList<String> replaceConditions;
    private String text;
    private String appTestQuestionPackId;
    private ArrayList<String> choices;
    private boolean allowMultipleSelection;
    private String participantId;
    private int askCount;
    private String activationMethod;
    private String eventId;
    private String type;
    private ArrayList<String> range;
    private boolean isMultipleChoice;

    public Question(JSONObject questionJSON) {
        this.questionId = questionJSON.optString("objectId");
        this.questionType = questionJSON.optString("questionType");
        this.sequence = questionJSON.optInt("sequence", -1);
        this.eventName = questionJSON.optString("eventName");
        this.activationTime = questionJSON.optInt("activationTime", -1);
        this.text = questionJSON.optString("text");
        this.appTestQuestionPackId = questionJSON.optJSONObject("appTestQuestionPack").optString("objectId");
        this.allowMultipleSelection = questionJSON.optBoolean("allowMultipleSelection");
        this.participantId = questionJSON.optString("participantId");
        this.askCount = questionJSON.optInt("askCount", -1);
        this.activationMethod = questionJSON.optString("activationMethod");
        this.eventId = questionJSON.optString("eventId");
        this.type = questionJSON.optString("type");
        this.isMultipleChoice = questionJSON.optBoolean("allowMuisMultipleChoiceltipleSelection");


        try{
            if(questionJSON.has("replaceConditions")){
                this.replaceConditions = new ArrayList<>();
                for(int i = 0; i < questionJSON.getJSONArray("replaceConditions").length(); i++){
                    replaceConditions.add(questionJSON.getJSONArray("replaceConditions").getString(i));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        try{
            if(questionJSON.has("choices")){
                this.choices = new ArrayList<>();
                for(int i = 0; i < questionJSON.getJSONArray("choices").length(); i++){
                    choices.add(questionJSON.getJSONArray("choices").getString(i));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        try{
            if(questionJSON.has("range")){
                this.range = new ArrayList<>();
                for(int i = 0; i < questionJSON.getJSONArray("range").length(); i++){
                    range.add(questionJSON.getJSONArray("range").getString(i));
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public int getSequence() {
        return sequence;
    }

    public String getEventName() {
        return eventName;
    }

    public int getActivationTime() {
        return activationTime;
    }

    public ArrayList<String> getReplaceConditions() {
        return replaceConditions;
    }

    public String getText() {
        return text;
    }

    public String getAppTestQuestionPackId() {
        return appTestQuestionPackId;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public boolean isAllowMultipleSelection() {
        return allowMultipleSelection;
    }

    public String getParticipantId() {
        return participantId;
    }

    public int getAskCount() {
        return askCount;
    }

    public String getActivationMethod() {
        return activationMethod;
    }

    public String getEventId() {
        return eventId;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getRange() {
        return range;
    }

    public boolean isMultipleChoice() {
        return isMultipleChoice;
    }
}


/*
*
{
    "questionType":"openEnd",
    "sequence":1,
    "text":"This is sample questions for open end type. Please answer. ",
    "campaign":{
       "__type":"Pointer",
       "className":"Campaign",
       "objectId":"koJF1wHo2P"
    },
    "type":"openEnd",
    "createdAt":"2019-12-19T16:23:35.300Z",
    "updatedAt":"2019-12-20T06:34:33.940Z",
    "appTestQuestionPack":{
       "__type":"Pointer",
       "className":"AppTestQuestionPack",
       "objectId":"uvqKXe74WT"
    },
    "creator":{
       "__type":"Pointer",
       "className":"_User",
       "objectId":"kqtoixA5wL"
    },
    "objectId":"P6NjyL6qxm"
}
* */