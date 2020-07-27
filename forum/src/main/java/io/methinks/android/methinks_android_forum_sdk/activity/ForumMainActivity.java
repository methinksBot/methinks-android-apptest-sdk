package io.methinks.android.methinks_android_forum_sdk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.methinks.android.methinks_android_forum_sdk.Global;
import io.methinks.android.methinks_android_forum_sdk.HttpManager;
import io.methinks.android.methinks_android_forum_sdk.Log;
import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.adapter.SectionAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForumMainActivity extends AppCompatActivity {
    private RecyclerView sectionRecyclerView;
    private RecyclerView.Adapter sectionAdapter;
    private RecyclerView.LayoutManager sectionLayoutManager;
    private Activity activity;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_main_activity);
        this.activity = this;

        backBtn = findViewById(R.id.back_btn);
        sectionRecyclerView = (RecyclerView) findViewById(R.id.section_recycler_view);
        //sectionRecyclerView.setHasFixedSize(true);

        sectionLayoutManager = new LinearLayoutManager(this);
        sectionRecyclerView.setLayoutManager(sectionLayoutManager);
        backBtn.setOnClickListener(backbtnClickListener);

        getParticipantInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        callGetForumSections();
    }

    public void getParticipantInfo() {
        new HttpManager().getMyParticipant(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)  { e.printStackTrace(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                try {
                    Log.d(res);
                    JSONObject result = new JSONObject(res);
                    Log.d("RESULT" + result.toString());
                    if (!result.getString("status").equals("ok")) {
                        Log.e("fetching failed");
                        return;
                    }

                    // no participant == admin
                    if (!result.has("result")) {
                        Global.forumNickName = getString(R.string.patcher_text_admin);
                        Global.forumProfile = "logoprofile";
                        Global.isUserAllocated = true;
                    } else {
                        JSONObject participant = result.getJSONObject("result");
                        Log.d("Participant Info: " + participant.toString());
                        JSONObject user = participant.getJSONObject("user");
                        Global.userObjectId = user.getString("objectId");
                        Global.participantId = participant.getString("objectId");

                        if (participant.has("forumNickName") || participant.has("forumEmoji")) {
                            Global.forumNickName = participant.getString("forumNickName");
                            Global.forumProfile = participant.getString("forumEmoji");
                            Global.isUserAllocated = true;
                        }
                    }

                    Log.d("Result: " + Global.forumNickName + Global.forumProfile);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void callGetForumSections() {
        new HttpManager().getForumSections(new Callback() {
            @Override
            public void onFailure(Call call, IOException e)  { e.printStackTrace(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                    try {
                        JSONObject result = new JSONObject(res);
                        if (!result.has("result")) {
                            Log.e("No result!!!");
                            return;
                        }

                    JSONArray sections = result.getJSONArray("result");
                    for (int i=0; i<sections.length(); i++) {
                        JSONObject section = sections.getJSONObject(i);
                        Log.d(section.toString());
                    }
                    sectionAdapter = new SectionAdapter(sections, activity);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sectionRecyclerView.setAdapter(sectionAdapter);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    View.OnClickListener backbtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
