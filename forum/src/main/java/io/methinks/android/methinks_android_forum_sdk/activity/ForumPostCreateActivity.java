package io.methinks.android.methinks_android_forum_sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
import io.methinks.android.methinks_android_forum_sdk.HttpManager;
import io.methinks.android.methinks_android_forum_sdk.Log;
import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.adapter.ImagesAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static io.methinks.android.methinks_android_forum_sdk.Global.REQUEST_CODE_GALLERY;
import static io.methinks.android.methinks_android_forum_sdk.Global.forumNickName;
import static io.methinks.android.methinks_android_forum_sdk.Global.getImageId;

public class ForumPostCreateActivity extends AppCompatActivity {
    private Activity activity;
    private ImageView backBtn;
    private ImageView profileIcon;
    private TextView profileNickName;
    private TextView addImageBtn;
    private TextView titleEditor;
    private EditText descEditor;
    private TextView textCounter;
    private TextView postSave;

    public boolean isFirstImage = true;

    public static RecyclerView imagePanel;
    public static RecyclerView.Adapter imageAdapter;
    private RecyclerView.LayoutManager imageLayoutManager;
    public static ArrayList<FileDescriptor> selectedImage;
    public static ArrayList<Uri> selectedImageUri;
    public static JSONArray attachedImages;

    private ParcelFileDescriptor inputPFD;

    private String sectionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_post_create);
        this.activity = this;
        imageLayoutManager = new LinearLayoutManager(this);
        selectedImage = new ArrayList<>();
        selectedImageUri = new ArrayList<>();
        attachedImages = new JSONArray();

        backBtn = findViewById(R.id.post_create_back_btn);
        profileIcon = findViewById(R.id.writer_profile_icon);
        profileNickName = findViewById(R.id.writer_nickname);
        addImageBtn = findViewById(R.id.post_add_image);
        imagePanel = findViewById(R.id.image_attaching_panel);
        titleEditor = findViewById(R.id.title_editor);
        descEditor = findViewById(R.id.desc_editor);
        textCounter = findViewById(R.id.text_counter);
        postSave = findViewById(R.id.save_new_post);

        imagePanel.setLayoutManager(imageLayoutManager);
        profileIcon.setImageResource(getImageId(activity, Global.forumProfile));
        profileNickName.setText(Global.forumNickName);

        addImageBtn.setOnClickListener(addImageOnClickListener);
        backBtn.setOnClickListener(backbtnClickListener);
        descEditor.addTextChangedListener(descEditorWatcher);
        postSave.setOnClickListener(saveOnClickListener);

        Bundle extras = getIntent().getExtras();
        sectionId = extras.getString("sectionId");
    }

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

    TextWatcher descEditorWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void afterTextChanged(Editable editable) {
            int textLength = descEditor.length();
            String counter = String.valueOf(textLength) + "/500";
            textCounter.setText(counter);

        }
    };

    View.OnClickListener saveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveNewPost();
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

                        /** getting FileDescriptor from Uri */
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
            imageAdapter = new ImagesAdapter(selectedImage, activity, "post");
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

    public void saveNewPost() {
        JSONObject newPost = new JSONObject();
        try {
            newPost.put("sectionId", sectionId);
            newPost.put("postTitle", titleEditor.getText());
            newPost.put("postText", descEditor.getText());
            if (attachedImages.length() > 0)
                newPost.put("images", attachedImages);
            else
                newPost.put("images", null);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HttpManager().createForumPost(newPost, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("Create Post Result: " + res);

                try {
                    JSONObject result = new JSONObject(res);
                    if (!result.has("result") || !result.getString("status").equals("ok")) {
                        Log.e("No result!!!");
                        return;
                    }

                    JSONObject post = result.getJSONObject("result");
                    JSONObject participant = post.getJSONObject("participant");


                    ArrayList<String> attachments = new ArrayList<>();
                    if (post.has("attachments")) {
                        String attachmentsSource = post.getString("attachments").replaceAll("[\\[\\](){}]", "");
                        for (String attachment : attachmentsSource.split(",")) {
                            attachments.add(attachment.replaceAll("\"", ""));
                        }
                    }

                    String likeCount = post.has("likeCount") ?
                            post.getString("likeCount") : "0";
                    String commentCount = post.has("commentCount") ?
                            post.getString("commentCount") : "0";
                    String likedUsers = post.has("likedUsers") ?
                            post.getString("likedUsers") : null;

                    Intent intent = new Intent(activity, ForumPostDetailActivity.class);
                    intent.putExtra("sectionId", sectionId);
                    intent.putExtra("postId", post.getString("objectId"));
                    intent.putExtra("userName", participant.getString("forumNickName"));
                    intent.putExtra("profile", participant.getString("forumEmoji"));
                    intent.putExtra("postTitle", post.getString("postTitle"));
                    intent.putExtra("postText", post.getString("postText"));
                    intent.putExtra("likeCount", likeCount);
                    intent.putExtra("commentCount", commentCount);
                    intent.putExtra("createdAt", Global.passedTimeCalc(post.getString("createdAt")));
                    intent.putExtra("attachments", attachments);
                    intent.putExtra("likedUsers", likedUsers);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, "New post is created.",Toast.LENGTH_LONG).show();
                        }
                    });

                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
