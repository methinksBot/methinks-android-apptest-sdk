package io.methinks.android.apptest;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import io.methinks.android.apptest.question.custom.widget.MethinksEditText;
import io.methinks.android.apptest.question.custom.widget.MethinksTextView;

import static android.view.View.VISIBLE;

public class HoverLogin {

    private Context context;
    protected LinearLayout bgrnd, contents;
    protected ScrollView scrollView;
    protected ImageView logoImage;
    protected MethinksTextView mainText, description;
    protected MethinksEditText testUserCode;
    public boolean isOpened;

    public HoverLogin(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        bgrnd = new LinearLayout(context);
        LinearLayout.LayoutParams bgparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        bgrnd.setLayoutParams(bgparams);
        bgrnd.setBackgroundColor(0xFFF);


    }

}
