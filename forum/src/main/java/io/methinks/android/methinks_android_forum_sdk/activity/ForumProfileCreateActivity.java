package io.methinks.android.methinks_android_forum_sdk.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import io.methinks.android.methinks_android_forum_sdk.Global;
import io.methinks.android.methinks_android_forum_sdk.HttpManager;
import io.methinks.android.methinks_android_forum_sdk.Log;
import io.methinks.android.methinks_android_forum_sdk.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ForumProfileCreateActivity extends AppCompatActivity {
    private Activity activity;
    Field[] drawablesFields = io.methinks.android.methinks_android_forum_sdk.R.drawable.class.getFields();
    private GridLayout profileGrid;
    private Drawable profileBackground;
    private Drawable profileBackgroundSelected;
    private Field selectedIcon;
    private String selectedIconFileName;
    private EditText newNickNameEdit;
    private String nickName;
    private TextView saveButton;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_profile_create);
        this.activity = this;
        profileGrid = findViewById(R.id.profile_grid);
        newNickNameEdit = findViewById(R.id.new_nickname);
        saveButton = findViewById(R.id.save_new_profile);
        backBtn = findViewById(R.id.back_btn);

        profileBackground = getResources().getDrawable(R.drawable.background_post_profile);
        profileBackgroundSelected = getResources().getDrawable(R.drawable.background_post_profile_selected);

        saveButton.setOnClickListener(saveOnClickListener);
        backBtn.setOnClickListener(backbtnClickListener);

        filteringDrawable();
    }

    public void filteringDrawable() {
        for (Field field : drawablesFields) {
            try {
                if (field.getName().startsWith("icons8")) {
                    //Log.d(field.getName());
                    ImageView newIcon = new ImageView(this);
                    newIcon.setImageResource(field.getInt(null));
                    newIcon.setTag(field);

                    GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                            GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                            GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));

                    param.setMargins(35, 35, 35, 35);
                    newIcon.setLayoutParams(param);
                    newIcon.setBackground(profileBackground);
                    newIcon.setOnClickListener(iconOnClickListener);

                    profileGrid.addView(newIcon);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    View.OnClickListener iconOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            unselectAllIcons();
            view.setBackground(profileBackgroundSelected);
            selectedIcon = (Field) view.getTag();
            Log.d("Object Looks like: " + selectedIcon);
        }
    };

    public void unselectAllIcons() {
        for(int i=0; i < ((ViewGroup) profileGrid).getChildCount(); i++) {
            View curIcon = ((ViewGroup) profileGrid).getChildAt(i);
            curIcon.setBackground(profileBackground);
        }
    }

    // save event
    View.OnClickListener saveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            nickName = newNickNameEdit.getText().toString();

            if (nickName.length() < 2) {
                Toast.makeText(activity, R.string.forum_no_nickname,Toast.LENGTH_LONG).show();
            } else {
                if (selectedIcon == null) {
                    Toast.makeText(activity, R.string.forum_no_profile_icon,Toast.LENGTH_LONG).show();
                } else {
                    try {
                        selectedIconFileName = getResources().getResourceEntryName(selectedIcon.getInt(null));
                        Log.d("Trying to call Create: " + selectedIconFileName);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    callSetForumProfile();
                }
            }
        }
    };

    public void callSetForumProfile() {
        new HttpManager().setForumProfile(nickName, selectedIconFileName, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace(); }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Log.d("create Result: " + res);
                try {
                    JSONObject result = new JSONObject(res);
                    if (!result.has("result")) {
                        Log.e("No result!!!");
                        return;
                    }
                    Global.forumNickName = nickName;
                    Global.forumProfile = selectedIconFileName;
                    Global.isUserAllocated = true;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, R.string.forum_profile_created,Toast.LENGTH_LONG).show();
                        }
                    });

                    finish();
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
