package io.methinks.android.methinks_android_forum_sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import io.methinks.android.methinks_android_forum_sdk.Global;
import io.methinks.android.methinks_android_forum_sdk.Global.Type;
import io.methinks.android.methinks_android_forum_sdk.HttpManager;
import io.methinks.android.methinks_android_forum_sdk.Log;
import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.adapter.ImagesAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static io.methinks.android.methinks_android_forum_sdk.Global.REQUEST_CODE_GALLERY;
import static io.methinks.android.methinks_android_forum_sdk.Global.getImageId;

public class ForumCommentActivity extends AppCompatActivity {
    private Activity activity;
    private ImageView backBtn;
    private ImageView profileIcon;
    private TextView profileNickName;
    private TextView addImageBtn;
    private EditText descEditor;
    private TextView textCounter;
    private TextView commentSave;
    private ImageView loadingIcon;

    private String sectionId;
    private String postId;
    private int baseCommentCount;
    private String base;
    private String toCommentId;

    public boolean isFirstImage = true;

    public static RecyclerView imagePanel;
    public static RecyclerView.Adapter imageAdapter;
    private RecyclerView.LayoutManager imageLayoutManager;
    public static ArrayList<FileDescriptor> selectedImage;
    public static ArrayList<Uri> selectedImageUri;
    public static JSONArray attachedImages;

    private ParcelFileDescriptor inputPFD;

    private Drawable darkerBackground;
    private Drawable lighterBackground;

    private Animation loadingAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forum_comment_activity);
        this.activity = this;
        imageLayoutManager = new LinearLayoutManager(this);
        selectedImage = new ArrayList<>();
        selectedImageUri = new ArrayList<>();
        attachedImages = new JSONArray();

        backBtn = findViewById(R.id.back_btn);
        profileIcon = findViewById(R.id.writer_profile_icon);
        profileNickName = findViewById(R.id.writer_nickname);
        descEditor = findViewById(R.id.comment_desc_editor);
        textCounter = findViewById(R.id.text_counter);
        addImageBtn = findViewById(R.id.comment_add_image);
        commentSave = findViewById(R.id.save_new_comment);
        imagePanel = findViewById(R.id.image_attaching_panel);
        loadingIcon = findViewById(R.id.loading_icon);

        imagePanel.setLayoutManager(imageLayoutManager);
        profileIcon.setImageResource(getImageId(activity, Global.forumProfile));
        profileNickName.setText(Global.forumNickName);

        Bundle extras = getIntent().getExtras();
        sectionId = extras.getString("sectionId");
        postId = extras.getString("postId");
        toCommentId = extras.getString("toCommentId");
        base = extras.getString("base");
        if (base.equals("post")) {
            baseCommentCount = Integer.parseInt(extras.getString("commentCount"));
        } else if (base.equals("comment")) {
            baseCommentCount = extras.getInt("commentCount");
        }

        loadingAnim = AnimationUtils.loadAnimation(this, R.anim.anim_save_loading);

        darkerBackground = getResources().getDrawable(R.drawable.background_post_darker);
        lighterBackground = getResources().getDrawable(R.drawable.background_post_write);

        addImageBtn.setOnClickListener(addImageOnClickListener);
        backBtn.setOnClickListener(backbtnClickListener);
        descEditor.addTextChangedListener(descEditorWatcher);
        commentSave.setOnTouchListener(saveOnTouchListener);

    }

    View.OnClickListener saveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) { saveNewComment(); }
    };

    View.OnTouchListener saveOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    commentSave.setBackground(darkerBackground);
                    break;
                case MotionEvent.ACTION_UP:
                    commentSave.setBackground(lighterBackground);
                    commentSave.setText("");
                    loadingIcon.startAnimation(loadingAnim);
                    loadingIcon.setVisibility(View.VISIBLE);
                    saveNewComment();
                    break;
            }
            return true;
        }
    };

    public void saveNewComment() {
        if (descEditor.getText().toString().matches("")) {
            Toast.makeText(activity, R.string.forum_no_comment,Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject newComment = new JSONObject();
        try {
            newComment.put("sectionId", sectionId);
            newComment.put("postId", postId);
            newComment.put("commentText", descEditor.getText());

            if (attachedImages.length() > 0) { newComment.put("images", attachedImages); }
            else { newComment.put("images", null); }

            if (base.equals("comment")) {
                newComment.put("toCommentId", toCommentId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpManager().createForumComment(newComment, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("Create Comment Result: " + res);

                try {
                    JSONObject result = new JSONObject(res);
                    Boolean isNotValidResult = Global.type == Type.Patcher
                            ? !result.has("result") || !result.getString("status").equals("ok")
                            : !result.has("result");
                    if (isNotValidResult) {
                        Log.e("No result!!!");
                        return;
                    }

                    //Log.d("Creating New Comment is succeeded! \n" + result);

                    increaseCommentCount();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void increaseCommentCount() {
        new HttpManager().updateCommentCount(postId, baseCommentCount + 1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("update Comment Count: " + res);

                try {
                    JSONObject result = new JSONObject(res);
                    Boolean isNotValidResult = Global.type == Type.Patcher
                            ? !result.has("result") || !result.getString("status").equals("ok")
                            : !result.has("result");
                    if (isNotValidResult) {
                        Log.e("No result!!!");
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "New Comment is created.", Toast.LENGTH_LONG).show();
                        }
                    });
                    activity.finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    TextWatcher descEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            int textLength = descEditor.length();
            String counter = textLength + "/500";
            textCounter.setText(counter);

        }
    };

    View.OnClickListener addImageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        } else {
            switch (requestCode) {
                case REQUEST_CODE_GALLERY:
                    if (data != null) {
                        Uri returnUri = data.getData();

                        /** getting FileDescriptor from Uri*/
                        try {
                            inputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.e("[ForumPostCreateActivity] File not found.");
                            return;
                        }

                        FileDescriptor imageFd = inputPFD.getFileDescriptor();
                        selectedImage.add(imageFd);
                        selectedImageUri.add(returnUri);
                        Log.d("New Image is selected : " + selectedImage);
                        attachNewImage();
                    } else {
                        Log.e("No Image data!!");
                    }
            }
        }
    }

    public void attachNewImage() {
        if (isFirstImage) {
            imageAdapter = new ImagesAdapter(selectedImage, activity, "comment");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //setAdapter
                    imagePanel.setAdapter(imageAdapter);
                    isFirstImage = false;
                }
            });
        } else {
            imageAdapter.notifyItemInserted(selectedImage.size()-1);
        }
    }

    View.OnClickListener backbtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
