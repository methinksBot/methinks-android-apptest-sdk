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
import io.methinks.android.methinks_android_forum_sdk.Global.Type;
import io.methinks.android.methinks_android_forum_sdk.HttpManager;
import io.methinks.android.methinks_android_forum_sdk.Log;
import io.methinks.android.methinks_android_forum_sdk.R;
import io.methinks.android.methinks_android_forum_sdk.adapter.ImagesAdapter;
import io.methinks.android.methinks_android_forum_sdk.adapter.ImagesUrlAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static io.methinks.android.methinks_android_forum_sdk.Global.REQUEST_CODE_GALLERY;
import static io.methinks.android.methinks_android_forum_sdk.Global.getImageId;

public class ForumPostUpdateActivity extends AppCompatActivity {

    private Activity activity;
    private ImageView backBtn;
    private ImageView profileIcon;
    private TextView profileNickName;
    private TextView addImageBtn;
    private TextView titleEditor;
    private EditText descEditor;
    private TextView textCounter;
    private TextView postUpdate;

    public boolean isFirstImage = true;

    // for existed attachments
    public static RecyclerView imagePanel;
    public static RecyclerView.Adapter imageAdapter;
    private RecyclerView.LayoutManager imageLayoutManager;

    public static ArrayList<String> imagesUrl;
    public static ArrayList<String> attachIds;
    private String imageAssets; // result of http call


    // for new image attachments
    public static RecyclerView newImagePanel;
    public static RecyclerView.Adapter newImageAdapter;
    private RecyclerView.LayoutManager newImageLayoutManager;

    public static ArrayList<FileDescriptor> selectedImage;
    public static ArrayList<Uri> selectedImageUri;
    public static JSONArray attachedImages;

    private ParcelFileDescriptor inputPFD;

    // value from post
    private String postId;
    private String sectionId;
    private String writerNameVal;
    private String profileVal;
    private String postTitleVal;
    private String postTextVal;
    private ArrayList<String> attachments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_post_update);
        this.activity = this;
        imageLayoutManager = new LinearLayoutManager(this);
        newImageLayoutManager = new LinearLayoutManager(this);
        selectedImage = new ArrayList<>();
        selectedImageUri = new ArrayList<>();
        attachedImages = new JSONArray();

        backBtn = findViewById(R.id.post_create_back_btn);
        profileIcon = findViewById(R.id.writer_profile_icon);
        profileNickName = findViewById(R.id.writer_nickname);
        addImageBtn = findViewById(R.id.post_add_image);
        imagePanel = findViewById(R.id.exist_attachment_panel);
        newImagePanel = findViewById(R.id.new_attachment_panel);
        titleEditor = findViewById(R.id.title_editor);
        descEditor = findViewById(R.id.desc_editor);
        textCounter = findViewById(R.id.text_counter);
        postUpdate = findViewById(R.id.update_post);

        Log.d(imagePanel.toString());
        imagePanel.setLayoutManager(imageLayoutManager);
        newImagePanel.setLayoutManager(newImageLayoutManager);

        Bundle extras = getIntent().getExtras();
        sectionId = extras.getString("sectionId");

        postId = extras.getString("postId");
        sectionId = extras.getString("sectionId");
        writerNameVal = extras.getString("userName");
        profileVal = extras.getString("profile");
        postTitleVal = extras.getString("postTitle");
        postTextVal = extras.getString("postText");
        attachments = extras.getStringArrayList("attachments");

        profileIcon.setImageResource(getImageId(activity, Global.forumProfile));
        profileNickName.setText(Global.forumNickName);
        titleEditor.setText(postTitleVal);
        descEditor.setText(postTextVal);


        addImageBtn.setOnClickListener(addImageOnClickListener);
        backBtn.setOnClickListener(backbtnClickListener);
        descEditor.addTextChangedListener(descEditorWatcher);
        postUpdate.setOnClickListener(updateOnClickListener);

        getAllPostAttachments();
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

    View.OnClickListener updateOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //arrangePostAttachments();
            callUpdatePost();
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

    public void getAllPostAttachments() {
        new Thread(new Runnable() { @Override public void run() { // TODO Auto-generated method stub
            JSONArray attachmentArray = new JSONArray();
            imagesUrl = new ArrayList<>();
            attachIds = new ArrayList<>();

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
        imageAdapter = new ImagesUrlAdapter(imagesUrl, attachments, activity, postId);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //setAdapter
                imagePanel.setAdapter(imageAdapter);
            }
        });
    }

    public void attachNewImage() {
        if (isFirstImage) {
            newImageAdapter = new ImagesAdapter(selectedImage, activity, "postUpdate");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //setAdapter
                    newImagePanel.setAdapter(newImageAdapter);
                    isFirstImage = false;
                }
            });
        } else {
            newImageAdapter.notifyItemInserted(selectedImage.size()-1);
        }
    }

    View.OnClickListener backbtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    public void arrangePostAttachments() {

    }

    public void callUpdatePost() {
        JSONObject updatePost = new JSONObject();

        try {
            updatePost.put("postId", postId);
            updatePost.put("postTitle", titleEditor.getText());
            updatePost.put("postText", descEditor.getText());
            if (attachedImages.length() > 0)
                updatePost.put("images", attachedImages);
            else
                updatePost.put("images", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.e("UPDATEINFO: " + updatePost);
        Log.d(attachedImages.length() + "");


        new HttpManager().updateForumPost(updatePost, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("Update Post Result: " + res);

                try {
                    JSONObject result = new JSONObject(res);
                    Boolean isNotValidResult = Global.type == Type.Patcher
                            ? !result.has("result") || !result.getString("status").equals("ok")
                            : !result.has("result");
                    if (isNotValidResult) {
                        Log.e("No result!!!");
                        return;
                    }

                    JSONObject post = result.getJSONObject("result");

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
                    intent.putExtra("userName", Global.forumNickName);
                    intent.putExtra("profile", Global.forumProfile);
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
                            Toast.makeText(activity, "New post is updated.",Toast.LENGTH_LONG).show();
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
