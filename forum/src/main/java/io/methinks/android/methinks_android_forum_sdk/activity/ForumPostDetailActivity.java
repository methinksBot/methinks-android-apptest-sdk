package io.methinks.android.methinks_android_forum_sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.methinks.android.methinks_android_forum_sdk.Global;
import io.methinks.android.methinks_android_forum_sdk.HttpManager;
import io.methinks.android.methinks_android_forum_sdk.Log;
import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.adapter.CommentAdapter;
import io.methinks.android.methinks_android_forum_sdk.adapter.ImagesUrlAdapter;
import io.methinks.android.methinks_android_forum_sdk.util.HtmlUtil;
import io.methinks.android.methinks_android_forum_sdk.util.OnLinkClickListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static io.methinks.android.methinks_android_forum_sdk.Global.forumNickName;
import static io.methinks.android.methinks_android_forum_sdk.Global.getImageId;

public class ForumPostDetailActivity extends AppCompatActivity implements OnLinkClickListener {
    private Activity activity;

    private ImageView writerProfile;
    private TextView writerName;
    private TextView createdAt;
    private ImageView editMenu;
    private TextView postTitle;
    private TextView postDesc;
    private ImageView postLike;
    private TextView likeCount;
    public static ImageView commentIcon;
    private TextView commentCount;
    private TextView editPost;
    private TextView deletePost;
    private ImageView backBtn;

    private RelativeLayout editMenuDialog;

    // post value
    private String postId;
    private String sectionId;
    private String writerNameVal;
    private String profileVal;
    private String postTitleVal;
    private String postTextVal;
    private String likeCountVal;
    private String commentCountVal;
    private String createdAtVal;
    private ArrayList<String> attachments;
    private String likedUsers;

    private String imageAssets; // result of http call
    public ArrayList<String> imagesUrl;


    public static RecyclerView imagePanel;
    public static RecyclerView.Adapter imageAdapter;
    private RecyclerView.LayoutManager imageLayoutManager;

    public static RecyclerView commentPanel;
    public static RecyclerView.Adapter commentAdapter;
    private RecyclerView.LayoutManager commentLayoutManager;

    private Drawable darkerBackground;
    private Drawable lighterBackground;

    private Animation feedbackAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
        setContentView(R.layout.forum_post_detail);

        imageLayoutManager = new LinearLayoutManager(this);
        commentLayoutManager = new LinearLayoutManager(this);

        writerProfile = findViewById(R.id.writer_profile);
        writerName = findViewById(R.id.writer_name);
        createdAt = findViewById(R.id.post_created_time);
        editMenu = findViewById(R.id.edit_menu);
        postTitle = findViewById(R.id.post_title);
        postDesc = findViewById(R.id.post_desc);
        postLike = findViewById(R.id.post_like);
        likeCount = findViewById(R.id.like_count);
        commentIcon = findViewById(R.id.comment_icon);
        commentCount = findViewById(R.id.comment_count);
        editMenuDialog = findViewById(R.id.edit_menu_dialog);
        editPost = findViewById(R.id.post_edit);
        deletePost = findViewById(R.id.post_delete);
        imagePanel = findViewById(R.id.images_panel);
        commentPanel = findViewById(R.id.comments_panel);
        backBtn = findViewById(R.id.back_btn);

        imagePanel.setLayoutManager(imageLayoutManager);
        commentPanel.setLayoutManager(commentLayoutManager);

        darkerBackground = getResources().getDrawable(R.drawable.background_edit_darker);
        lighterBackground = getResources().getDrawable(R.drawable.background_edit_dialog);

        feedbackAnim = AnimationUtils.loadAnimation(this, R.anim.anim_beat_effect);

        Bundle extras = getIntent().getExtras();

        postId = extras.getString("postId");
        sectionId = extras.getString("sectionId");
        writerNameVal = extras.getString("userName");
        profileVal = extras.getString("profile");
        postTitleVal = extras.getString("postTitle");
        postTextVal = extras.getString("postText");
        likeCountVal = extras.getString("likeCount");
        commentCountVal = extras.getString("commentCount");
        createdAtVal = extras.getString("createdAt");
        attachments = extras.getStringArrayList("attachments");
        likedUsers = extras.getString("likedUsers");

        writerProfile.setImageResource(getImageId(activity, profileVal));
        writerName.setText(writerNameVal);
        createdAt.setText(createdAtVal);
        postTitle.setText(postTitleVal);
        likeCount.setText(likeCountVal);
        commentCount.setText(commentCountVal);

//        postDesc.setText(HtmlUtil.fromHtml(postTextVal, this));
        postDesc.setText(postTextVal);
        postDesc.setMovementMethod(LinkMovementMethod.getInstance());
        postDesc.setClickable(true);

        postLike.setOnTouchListener(likeOnTouchListener);
        commentIcon.setOnClickListener(commentClickListener);
        backBtn.setOnClickListener(backbtnClickListener);

        checkOwnedPost();
        likeDetector();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeContents();
    }

    public void initializeContents() {
        feedbackCounter();
        getAllPostAttachments();
        getPostComment();
    }
    public void feedbackCounter() {
        //Log.e("Counter " + likeCountVal + "/" + commentCountVal);
        if (Integer.parseInt(likeCountVal) > 0) { likeCount.setVisibility(View.VISIBLE); }
        if (Integer.parseInt(commentCountVal) > 0) { commentCount.setVisibility(View.VISIBLE); }
    }

    public void likeDetector() {
        if (likedUsers != null && Integer.parseInt(likeCountVal) > 0) {
            Log.e("likedUsers: " + likedUsers + "/" + Global.userObjectId);
            ArrayList<String> likedUserArray = new ArrayList<>();
            String likedUsersSource = likedUsers.replaceAll("[\\[\\](){}]", "");
            for (String user : likedUsersSource.split(",")) {
                likedUserArray.add(user.replaceAll("\"", ""));
            }
            Log.d(likedUserArray.toString());
            Log.d(likedUserArray.contains(Global.userObjectId) + "/" + postLike.isActivated());
            if (likedUserArray.contains(Global.userObjectId) && !postLike.isActivated()) {
                postLike.setActivated(true);
            }
        }
    }

    OnLinkClickListener descOnLinkClickListener = new OnLinkClickListener() {
        @Override
        public void onLinkClick(@NotNull String url) {
            Toast.makeText(activity, "executing web view...", Toast.LENGTH_LONG).show();
        }
    };

    View.OnTouchListener likeOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    boolean like = !postLike.isActivated();
                    Log.e("Current Like: " + like);

                    postLike.setActivated(like);
                    postLike.startAnimation(feedbackAnim);
                    new HttpManager().likeForumPost(postId, like, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) { }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();
                            try {
                                JSONObject result = new JSONObject(res);
                                JSONObject post = result.getJSONObject("result");
                                Log.d(post.toString());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            int postLikeCount =
                                                    post.has("likedUsers")
                                                            && post.getJSONArray("likedUsers").length() > 0
                                                            ? post.getJSONArray("likedUsers").length()
                                                            : 0;
                                            Log.e(String.valueOf(postLikeCount));
                                            likeCount.setVisibility(View.VISIBLE);
                                            likeCount.setText(String.valueOf(postLikeCount));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
            }
            return true;
        }
    };

    View.OnClickListener commentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //view.setActivated(true);
            view.startAnimation(feedbackAnim);
            Intent intent = new Intent(activity, ForumCommentActivity.class);
            intent.putExtra("postId", postId);
            intent.putExtra("sectionId", sectionId);
            intent.putExtra("commentCount", commentCountVal);
            intent.putExtra("base", "post");
            activity.startActivity(intent);
        }
    };

    public void getPostComment() {
        new HttpManager().getPostComments(postId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                try {
                    Log.e("Comments: " + res);
                    JSONObject result = new JSONObject(res);
                    if (!result.has("result")) {
                        Log.e("No result!!!");
                        return;
                    }
                    JSONArray commentObjs = result.getJSONArray("result");
                    commentObjs = commentAligner(commentObjs);

                    Log.e("comments Length: " + commentObjs.length());
                    commentCountVal = String.valueOf(commentObjs.length());

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commentCount.setVisibility(View.VISIBLE);
                            commentCount.setText(commentCountVal);
                        }
                    });

                    commentAdapter = new CommentAdapter(
                            commentObjs,
                            activity,
                            "post");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            commentPanel.setAdapter(commentAdapter);
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public JSONArray commentAligner(JSONArray comments) {

        ArrayList<JSONObject> commentArray = new ArrayList<>();

        try {
            // conversing JSONArray to ArrayList.
            for (int i=0; i<comments.length(); i++) {
                commentArray.add(comments.getJSONObject(i));
            }

            List commentList = commentArray;

            Log.d("Comments: " + commentList);

            for (int i=0; i<commentList.size(); i++) {
                JSONObject curObj = (JSONObject) commentList.get(i);
                if (curObj.has("toComment")) {

                    // split
                    List subList = new ArrayList<>(commentList.subList(0, i));
                    List temp = findCommentSequence(subList, curObj);
                    List last = new ArrayList<>(commentList.subList(i+1, commentList.size()));
                    //Log.d("cur: " + commentList);
                    //Log.d("temp: " + temp);
                    //Log.d("last: " + last);

                    commentList = new ArrayList<>();
                    commentList.addAll(temp);
                    commentList.addAll(last);

                    //Log.d("Rearranged list: " + commentList.size() + "/" + commentList.size());
                    //Log.d("After Comment: " + commentList);
                }
            }

            JSONArray sortedList = new JSONArray();
            for (int i=0; i<commentList.size(); i++) {
                sortedList.put(commentList.get(i));
            }
            Log.e("Final List: " + sortedList);
            return sortedList;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List findCommentSequence(List splitObj, JSONObject cur) {

        try {
            boolean isFind = false;
            int compDepth = 0;

            for (int i=0; i<splitObj.size(); i++) {
                JSONObject curObj = (JSONObject) splitObj.get(i);
                JSONObject toComment = cur.getJSONObject("toComment");

                if (curObj.getString("objectId").equals(toComment.getString("objectId"))) {

                    isFind = true;
                    compDepth = curObj.getInt("depth");
                    int commentCount = curObj.has("commentCount") ? curObj.getInt("commentCount") + 1 : 1;
                    curObj.put("commentCount", commentCount);
                    if (i == splitObj.size()-1) {
                        splitObj.add(i+1, cur);
                        Log.e("Index " + (i+1) + " is selected Index!");
                        return splitObj;
                    }

                } else if (isFind) {

                    if (curObj.getInt("depth") <= compDepth) {
                        splitObj.add(i, cur);
                        Log.e("Index " + i + " is selected Index!");
                        return splitObj;
                    } else if (i == splitObj.size()-1) {
                        splitObj.add(i+1, cur);
                        Log.e("Index " + (i+1) + " is selected Index!");
                        return splitObj;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void getAllPostAttachments() {
        if (attachments.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() { // TODO Auto-generated method stub
                    JSONArray attachmentArray = new JSONArray();
                    imagesUrl = new ArrayList<>();

                    for (String ele : attachments) {
                        attachmentArray.put(ele);
                    }

                    imageAssets = new HttpManager().getPostAttachments(attachmentArray);
                    //Log.d(imageAssets);
                    try {
                        JSONObject imageObj = new JSONObject(imageAssets);
                        if (imageObj.has("result")) {
                            JSONArray assetsArray = imageObj.getJSONArray("result");

                            // getting base64 string from url to keep images
                            for (int i = 0; i < assetsArray.length(); i++) {
                                JSONObject objectAsset = assetsArray.getJSONObject(i).getJSONObject("image");
                                imagesUrl.add(objectAsset.getString("url"));
                            }
                            setAttachments();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Log.d("no attachments to bring");
        }
    }

    public void setAttachments() {
        Log.d(imagesUrl.toString());
        imageAdapter = new ImagesUrlAdapter(imagesUrl, activity);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //setAdapter
                imagePanel.setAdapter(imageAdapter);
            }
        });
    }

    public void callDeleteForumPost() {
        new HttpManager().deleteForumPost(postId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {e.printStackTrace();}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("delete post Result: " + res);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Post is deleted.",Toast.LENGTH_LONG).show();
                    }
                });
                finish();
            }
        });
    }

    View.OnTouchListener editOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    view.setBackground(darkerBackground);
                    break;
                case MotionEvent.ACTION_UP:
                    view.setBackground(lighterBackground);
                    if (view == editPost) {
                        Intent intent = new Intent(activity, ForumPostUpdateActivity.class);
                        intent.putExtra("postId", postId);
                        intent.putExtra("sectionId", sectionId);
                        intent.putExtra("userName", forumNickName);
                        intent.putExtra("profile", profileVal);
                        intent.putExtra("postTitle", postTitleVal);
                        intent.putExtra("postText", postTextVal);
                        intent.putExtra("attachments", attachments);
                        activity.startActivity(intent);
                        finish();
                    } else if (view == deletePost) {
                        callDeleteForumPost();
                    }
                    break;
            }
            return true;
        }
    };

    public void checkOwnedPost() {
        /** If current user is writer */
        if (Global.forumNickName.equals(writerNameVal)) {
            editMenu.setVisibility(View.VISIBLE);
            editMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //editMenuDialog.setVisibility(View.VISIBLE);
                    editMenuDialog.setAlpha(0f);
                    editMenuDialog.setVisibility(View.VISIBLE);

                    editMenuDialog.animate()
                            .alpha(1f)
                            .setDuration(400)
                            .setListener(null);
                }
            });
            editMenuDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editMenuDialog.setVisibility(View.INVISIBLE);
                }
            });
            editPost.setOnTouchListener(editOnTouchListener);
            deletePost.setOnTouchListener(editOnTouchListener);
        }
    }

    View.OnClickListener backbtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    @Override
    public void onLinkClick(@NotNull String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        this.startActivity(browserIntent);
    }
}
