package io.methinks.android.apptest;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Objects;

public class AnnouncementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Log.d("Announcement Activity is started");
        if(Global.loginResult == null || !Global.loginResult.has("statusResult")){
            finish();
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        JSONObject statusResult = Global.loginResult.optJSONObject("statusResult");

        Toolbar toolbar = (Toolbar)findViewById(R.id.announcement_toolbar);
        TextView title = (TextView)findViewById(R.id.announcement_project_title);
        TextView progress = (TextView)findViewById(R.id.announcement_project_progress);
        TextView duration = (TextView)findViewById(R.id.announcement_project_duration);


        TextView announceTitle = (TextView)findViewById(R.id.announcement_announce_title);
        TextView surveyTitle = (TextView)findViewById(R.id.announcement_survey_title);
        TextView announceCount = (TextView)findViewById(R.id.announcement_announce_count);
        TextView surveyCount = (TextView)findViewById(R.id.announcement_survey_count);

        toolbar.setTitle(Global.loginResult.optString("campaignName"));
        String titleStr = String.format(getString(R.string.patcher_android_welcome_hi), Global.loginResult.optString("screenName"));
        title.setText(titleStr);
        String progressStr = getString(R.string.patcher_your_progress) + " <font color='#6586ff'>" + statusResult.optString("status") + "</font>";
        progress.setText(Html.fromHtml(progressStr));


        if(statusResult.optInt("announcementCount") > 0){
            announceCount.setVisibility(View.VISIBLE);
            announceCount.setText(String.valueOf(statusResult.optInt("announcementCount")));
            announceTitle.setText(statusResult.optString("announcement"));
            findViewById(R.id.announcement_announce_container).setOnClickListener(view -> {
                String url;
                if(Global.isDebugMode){
                    url = "mtkdebug://announcement?project_id=" + Global.sProjectId;
                }else{
                    url = "methinks://announcement?project_id=" + Global.sProjectId;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                    Toast.makeText(AnnouncementActivity.this, getString(R.string.patcher_msg_need_thinker_app), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if(statusResult.optInt("surveyCount") > 0){
            surveyCount.setVisibility(View.VISIBLE);
            surveyCount.setText(String.valueOf(statusResult.optInt("surveyCount")));
            surveyTitle.setText(statusResult.optString("survey"));
            findViewById(R.id.announcement_survey_container).setOnClickListener(view -> {
                String url;
                if(Global.isDebugMode){
                    url = "mtkdebug://survey?project_id=" + Global.sProjectId;
                }else{
                    url = "methinks://survey?project_id=" + Global.sProjectId;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                try{
                    startActivity(intent);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(AnnouncementActivity.this, getString(R.string.patcher_msg_need_thinker_app), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            });
        }



        View.OnClickListener closeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };
        findViewById(R.id.announcement_empty_space).setOnClickListener(closeListener);
        toolbar.setNavigationOnClickListener(closeListener);
    }

    @Override
    public void finish() {
        Global.isShowingAnnouncement = false;

        if(Global.applicationTracker == null){
            throw new NullPointerException("ApplicationTracker can't be null to show PermissionActivity");
        }
        if(Global.applicationTracker.getTopActivity() == null){
            throw new NullPointerException("ApplicationTracker's top activity can't be null to show PermissionActivity");
        }

        Intent loginIntent = new Intent(Global.applicationTracker.getTopActivity(), PermissionActivity.class);
        Global.applicationTracker.getTopActivity().startActivity(loginIntent);
        Log.d("Announcement Activity is finished");
        super.finish();
    }
}
