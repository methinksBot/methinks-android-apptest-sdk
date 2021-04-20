package io.methinks.android.apptest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import io.methinks.sdk.sectionsurvey.builder.MtkSectionSurvey;
import io.methinks.sdk.sectionsurvey.model.Question;
import io.methinks.sdk.sectionsurvey.model.Section;
import io.methinks.sdk.sectionsurvey.model.SectionAsset;

import static io.methinks.sdk.sectionsurvey.ui.MtkSectionSurveyActivity.EXTRA_ANSWER;
import static io.methinks.sdk.sectionsurvey.ui.MtkSectionSurveyActivity.EXTRA_ANSWER_MAP;
import static io.methinks.sdk.sectionsurvey.ui.MtkSectionSurveyActivity.EXTRA_FILE_PATH_LIST;
import static io.methinks.sdk.sectionsurvey.ui.MtkSectionSurveyActivity.EXTRA_NEXT_SECTION_ID;
import static io.methinks.sdk.sectionsurvey.ui.MtkSectionSurveyActivity.REQ_CODE_SECTION_SURVEY;

public class SurveyActivity extends AppCompatActivity {

    private Section section = null;
    private ArrayList<Question> questionList = null;
    private List<SectionAsset> sectionAssets = null;
    private HashMap<String, ArrayList<Object>> answerMap = null;

    private String packId = null;
    private String sectionId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        Objects.requireNonNull(getSupportActionBar()).hide();

        initialize();

        JSONObject result = null;
        //if (savedInstanceState != null) {
            // Restore value of members from saved state
            try {
                result = new JSONObject(getIntent().getStringExtra(Global.SURVEY_SECTIONS));
                packId = getIntent().getStringExtra(Global.SURVEY_INIT_PACK);
                Log.e("ReceivedData: " + result);
                sectionsParser(result);

            } catch (JSONException e) {
                e.printStackTrace();
            }
//        } else {
//            Log.e("No Data for this Activity !!!");
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("[SectionSurvey] requestCode: " + requestCode + ", resultCode : " + resultCode);

        if(requestCode == REQ_CODE_SECTION_SURVEY) {
            if(resultCode == RESULT_OK) {
                // screener, survey, inapp
                data.getStringExtra(EXTRA_NEXT_SECTION_ID);
                answerMap = (HashMap<String, ArrayList<Object>>) data.getSerializableExtra(EXTRA_ANSWER_MAP);
                Log.e("[AnswerMap]: " + answerMap);

                new HttpManager().inAppAnswer(packId, sectionId, new JSONObject(answerMap), new HttpManager.Callback() {
                    @Override
                    public void done(JSONObject response, String error) {
                        try {
                            String result = response.getString("status");
                            if (result.equals("ok")) {
                                finish();
                            } else {
                                Log.e("Could not send answer properly!!!!");
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            finish();
                        }

                    }
                });

            } else {
                // content
            }
        }

    }

    private void initialize() {
        section = null;
        questionList = new ArrayList<Question>();
        sectionAssets = new ArrayList<SectionAsset>();
    }

    private void startSectionSurvey() {
        MtkSectionSurvey.with(this)
                .setPackType("survey")
                .setSection(section)
                .setQuestionList(questionList)
                .start();
    }

    private void sectionsParser(JSONObject result) {
        try {
            // apptest always get one section
            JSONObject sectionObj = (JSONObject)result.getJSONArray("sections").get(0);
            JSONObject curSection = sectionObj.getJSONObject("section");

            sectionId = curSection.getString("objectId");

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
                    for (int j=0; j<choicesArray.length(); j++) {
                        array.add(choicesArray.getJSONObject(j).getString("text"));
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
            startSectionSurvey();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
