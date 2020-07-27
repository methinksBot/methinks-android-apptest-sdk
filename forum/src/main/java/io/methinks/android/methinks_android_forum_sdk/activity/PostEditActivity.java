package io.methinks.android.methinks_android_forum_sdk.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.methinks.android.methinks_android_forum_sdk.R;

public class PostEditActivity extends AppCompatActivity {
    private TextView edit;
    private TextView delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_edit_dialog);
        edit.findViewById(R.id.post_edit);
        delete.findViewById(R.id.post_delete);
    }
}
