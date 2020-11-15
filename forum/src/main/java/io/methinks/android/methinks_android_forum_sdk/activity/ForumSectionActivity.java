package io.methinks.android.methinks_android_forum_sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
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
import io.methinks.android.methinks_android_forum_sdk.adapter.PostAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForumSectionActivity extends AppCompatActivity {
    private RecyclerView postRecyclerView;
    private RecyclerView.Adapter postAdapter;
    private RecyclerView.LayoutManager postLayoutManager;
    private Activity activity;
    private String sectionId;
    private ImageView backBtn;
    private ImageView postCreateBtn;

    private Drawable darkerBackground;
    private Drawable lighterBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_section_activity);
        this.activity = this;

        backBtn = findViewById(R.id.back_btn);
        postRecyclerView = (RecyclerView) findViewById(R.id.post_recycler_view);
        postCreateBtn = findViewById(R.id.post_create);
        postLayoutManager = new LinearLayoutManager(this);

        postRecyclerView.setLayoutManager(postLayoutManager);
        backBtn.setOnClickListener(backbtnClickListener);
        postCreateBtn.setOnTouchListener(postCreateOnTouchListener);

        darkerBackground = getResources().getDrawable(R.drawable.background_post_darker);
        lighterBackground = getResources().getDrawable(R.drawable.background_post_write);


        Bundle extras = getIntent().getExtras();
        sectionId = extras.getString("sectionId");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeContents();
    }

    private void initializeContents() {
        callGetForumPost();
    }

    private void callGetForumPost() {
        new HttpManager().getForumPosts(sectionId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                try {
                    //Log.d("POST" + res);
                    JSONObject result = new JSONObject(res);
                    if (!result.has("result")) {
                        Log.e("No result!!!");
                        return;
                    }

//                    Log.d(result.toString());
                    JSONArray posts = result.getJSONArray("result");
                    Log.e(posts.toString());
                    for (int i=0; i<posts.length(); i++) {
                        JSONObject section = posts.getJSONObject(i);
                        Log.d(section.toString());
                    }
                    postAdapter = new PostAdapter(posts, activity);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            postRecyclerView.setAdapter(postAdapter);
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

    View.OnClickListener postCreateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Global.isUserAllocated = false;
            if (Global.isUserAllocated) {
                // go to postCreate Activity
                Log.d("User is updated");
                Intent intent = new Intent(activity, ForumPostCreateActivity.class);
                intent.putExtra("sectionId", sectionId);
                activity.startActivity(intent);
            } else {
                // go to profileCreate Activity
                Log.d("No user set profile");
                Intent intent = new Intent(activity, ForumProfileCreateActivity.class);
                activity.startActivity(intent);
            }
        }
    };

    View.OnTouchListener postCreateOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    postCreateBtn.setBackground(darkerBackground);
                    break;
                case MotionEvent.ACTION_UP:
                    postCreateBtn.setBackground(lighterBackground);
                    //Global.isUserAllocated = false;
                    if (Global.isUserAllocated) {
                        // go to postCreate Activity
                        Log.d("User is updated");
                        Intent intent = new Intent(activity, ForumPostCreateActivity.class);
                        intent.putExtra("sectionId", sectionId);
                        activity.startActivity(intent);
                    } else {
                        // go to profileCreate Activity
                        Log.d("No user set profile");
                        Intent intent = new Intent(activity, ForumProfileCreateActivity.class);
                        activity.startActivity(intent);
                    }
                    break;
            }
            return true;
        }
    };
}
