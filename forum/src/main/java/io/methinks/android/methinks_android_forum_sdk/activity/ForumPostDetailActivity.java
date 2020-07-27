package io.methinks.android.methinks_android_forum_sdk.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import io.methinks.android.methinks_android_forum_sdk.Global;
import io.methinks.android.methinks_android_forum_sdk.HttpManager;
import io.methinks.android.methinks_android_forum_sdk.Log;
import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.adapter.CommentAdapter;
import io.methinks.android.methinks_android_forum_sdk.adapter.ImagesUrlAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static io.methinks.android.methinks_android_forum_sdk.Global.forumNickName;
import static io.methinks.android.methinks_android_forum_sdk.Global.getImageId;

public class ForumPostDetailActivity extends AppCompatActivity {
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

        imagePanel.setLayoutManager(imageLayoutManager);
        commentPanel.setLayoutManager(commentLayoutManager);

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
        postDesc.setText(postTextVal);
        likeCount.setText(likeCountVal);
        commentCount.setText(commentCountVal);

        postLike.setOnClickListener(likeClickListener);
        commentIcon.setOnClickListener(commentClickListener);

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
        if (likedUsers != null) {
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

    View.OnClickListener likeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean like = !postLike.isActivated();
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
                                    Log.e(post.getJSONArray("likedUsers").toString());
                                    likeCount.setText(String.valueOf(post.getJSONArray("likedUsers").length()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    postLike.setActivated(!postLike.isActivated());
                }
            });
        }
    };

    View.OnClickListener commentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

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
                    Log.d("Comments: " + res);
                    JSONObject result = new JSONObject(res);
                    if (!result.has("result")) {
                        Log.e("No result!!!");
                        return;
                    }
                    JSONArray commentObjs = result.getJSONArray("result");
                    if (commentObjs.length() != Integer.parseInt(commentCountVal)) {
                        commentCountVal = String.valueOf(commentObjs.length());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                commentCount.setText(commentCountVal);
                            }
                        });
                    }

                    commentAdapter = new CommentAdapter(commentObjs, activity, "post");
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

    public void getAllPostAttachments() {
        new Thread(new Runnable() { @Override public void run() { // TODO Auto-generated method stub
            JSONArray attachmentArray = new JSONArray();
            imagesUrl = new ArrayList<>();

            for (String ele : attachments) { attachmentArray.put(ele); }

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
        }}).start();
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

    public void checkOwnedPost() {
        /** If current user is writer */
        if (Global.forumNickName.equals(writerNameVal)) {
            editMenu.setVisibility(View.VISIBLE);
            editMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editMenuDialog.setVisibility(View.VISIBLE);
                }
            });
            editMenuDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editMenuDialog.setVisibility(View.INVISIBLE);
                }
            });
            editPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                }
            });
            deletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callDeleteForumPost();
                }
            });
        }
    }
}
