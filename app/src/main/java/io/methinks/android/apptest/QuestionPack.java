package io.methinks.android.apptest;

import io.methinks.android.apptest.Global;
import io.methinks.android.apptest.HttpManager;
import io.methinks.android.apptest.Log;
import io.methinks.android.rtc.MTKDataStore;
import io.methinks.sdk.sectionsurvey.builder.MtkSectionSurvey;
import io.methinks.sdk.sectionsurvey.model.Question;
import io.methinks.sdk.sectionsurvey.model.Section;
import io.methinks.sdk.sectionsurvey.model.SectionAsset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QuestionPack {
    public static final String SESSION_BASED_TYPE = "session";
    public static final String TIME_BASED_TYPE = "time";
    public static final String EVENT_TYPE = "event";

    private JSONArray questions;
    private String packId;
    private boolean isRequired;
    private String type;
    private int session;
    private int time;

    private int activationSession;
    private int activationTime;
    private String title;

    private boolean isFetched;

    private Section section = null;
    private ArrayList<Question> questionList = null;
    private List<SectionAsset> sectionAssets = null;

    public QuestionPack(JSONObject pack, String type) {
        JSONObject surveyPack = pack.optJSONObject("surveyPack");
        packId = surveyPack.optString("objectId");
        this.type = type;
        time = surveyPack.optInt("createdAt");
        isRequired = surveyPack.optBoolean("isRequired");

        initialize();
    }


    private void initialize() {
        section = null;
        questionList = new ArrayList<Question>();
        sectionAssets = new ArrayList<SectionAsset>();
    }

    public void fetch(FetchCallback callback) {
        new HttpManager().inAppSections(packId, (response, error) -> {
            try {
                if (response != null) {
                    if (response.has("status") && response.getString("status").equals(Global.RESPONSE_OK)) {
                        JSONObject result = response.optJSONObject("result");
                        if (result != null && result.has("sections") && result.optJSONArray("sections").length() > 0) {
                            isFetched = true;

                            Global.hover.setInvisible();
                            Global.hoverPopup.setInvisible();
                            Global.hoverPopup.isOpened = false;

                            Log.e("sendMessage isRequired : " + Global.eventQuestionPack.isRequired);
                            Log.e("[inAppSection]" + result);

                            sectionsParser(result);

                            callback.done();
                        }
                    }
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        });
    }

    private void sectionsParser(JSONObject result) {
        try {
        // apptest always get one section
        JSONObject sectionObj = (JSONObject)result.getJSONArray("sections").get(0);
        JSONObject curSection = sectionObj.getJSONObject("section");


            /** Section setup */
            this.section = new Section(
                    curSection.getString("objectId"),
                    curSection.getString("title"),
                    curSection.has("desc") ? curSection.getString("desc") : null,
                    curSection.getJSONArray("sectionLayout"),
                    curSection.has("nextSectionId")? curSection.getString("nextSectionId") : null,
                    curSection.has("attachedVideoUrl")? curSection.getString("attachedVideoUrl") : null,
                    curSection.has("attachedImageUrl")? curSection.getString("attachedImageUrl") : null
            );

            /** Questions setup */
            JSONArray questionsArray = sectionObj.getJSONArray("questions");

            for (int i=0; i<questionsArray.length(); i++) {
                JSONObject question = (JSONObject)questionsArray.get(i);

                JSONArray choicesArray = question.has("choices") ? question.getJSONArray("choices") : null;
                Log.e("choicesArray: " + choicesArray);
                ArrayList choices = null;
                if (choicesArray != null) {
                    ArrayList array = new ArrayList<String>();
                    for (int j=0; i<choicesArray.length(); j++) {
                        array.add(choicesArray.getString(j));
                    }
                    choices = array;
                }


                JSONArray abTestingAssetsArray = question.has("abTestingAssets") ? question.getJSONArray("abTestingAssets") : null;
                Log.e("abTestingAssets: " + abTestingAssetsArray);
                ArrayList<String> abTestingAssets = null;

                if (abTestingAssetsArray != null) {
                    ArrayList array = new ArrayList<String>();
                    for (int j=0; j<abTestingAssetsArray.length(); j++) {
                        array.add(abTestingAssetsArray.getString(j));
                    }
                    abTestingAssets = array;
                }

                JSONObject rule = new JSONObject("{}");
                Iterator<String> keys = question.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (key.endsWith("Rule")) {
                        rule = question.getJSONObject(key);
                    }
                }

                this.questionList.add(i, new Question(
                        question.getString("objectId"),
                        question.getString("questionType"),
                        question.getString("text"),
                        question.has("rowText")? question.getString("rowText") : null,
                        question.getBoolean("required"),
                        rule,
                        choices,
                        abTestingAssets,
                        question.has("sectionSequence")? question.getJSONObject("sectionSequence") : null,
                        question.has("attachedVideoUrl")? question.getString("attachedVideoUrl") : null,
                        question.has("attachedImageUrl")? question.getString("attachedImageUrl") : null
                ));
            }

            /** Apptest is not using question features that using SectionAsset */

            /** Build Section Survey with elements above */
            buildSectionSurvey();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void buildSectionSurvey() {
        Log.e("[Current Top Activity]: " + Global.applicationTracker.getTopActivity() + "\n" + Global.applicationTracker.getFirstActivity());
        MtkSectionSurvey.with(Global.applicationTracker.getTopActivity())
                .setPackType("survey")
                .setSection(this.section)
                .setQuestionList(questionList)
                .start();
    }

    public static String getSessionBasedType() {
        return SESSION_BASED_TYPE;
    }

    public static String getTimeBasedType() {
        return TIME_BASED_TYPE;
    }

    public static String getEventType() {
        return EVENT_TYPE;
    }

    public String getPackId() {
        return packId;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getType() {
        return type;
    }

    public int getActivationSession() {
        return activationSession;
    }

    public int getActivationTime() {
        return activationTime;
    }

    public int getSession() {
        return session;
    }

    public int getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public boolean isFetched() {
        return isFetched;
    }

    public JSONArray getQuestions() {
        return questions;
    }

    public interface FetchCallback{
        void done();
    }
}
