package io.methinks.android.apptest.question.custom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import io.methinks.android.apptest.R;


/**
 * Created by kgy 2019. 9. 24.
 */
public class MethinksEditText extends AppCompatEditText {
    private static final String TAG = MethinksEditText.class.getSimpleName();

    private Context context;

    public MethinksEditText(Context context) {
        super(context);
    }

    public MethinksEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MethinksEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs){
        this.context = context;
        String lang = Locale.getDefault().getISO3Language();
        if(lang.equals("kor") || lang.equals("eng") || lang.equals("jpn")){
            String fontPath = "fonts/NotoSansKR-Regular-Hestia.otf";
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView);
            String fontWeight = a.getString(R.styleable.CustomView_fw);
            if(TextUtils.isEmpty(fontWeight)){
                fontWeight = context.getString(R.string.font_weight_regular);
            }
            if(lang.equals("eng") || lang.equals("kor")){
                if(fontWeight.equals(context.getString(R.string.font_weight_regular))){
                    fontPath = "fonts/NotoSansKR-Regular-Hestia.otf";
                }else if(fontWeight.equals(context.getString(R.string.font_weight_medium))){
                    fontPath = "fonts/NotoSansKR-Medium-Hestia.otf";
                }else if(fontWeight.equals(context.getString(R.string.font_weight_bold))){
                    fontPath = "fonts/NotoSansKR-Bold-Hestia.otf";
                }
            }else if(lang.equals("jpn")){
                if(fontWeight.equals(context.getString(R.string.font_weight_regular))){
                    fontPath = "fonts/NotoSansJP-Regular-min.ttf";
                }else if(fontWeight.equals(context.getString(R.string.font_weight_medium))){
                    fontPath = "fonts/NotoSansJP-Medium-min.ttf";
                }else if(fontWeight.equals(context.getString(R.string.font_weight_bold))){
                    fontPath = "fonts/NotoSansJP-Bold-min.ttf";
                }
            }
            Typeface tf = Typeface.createFromAsset(context.getResources().getAssets(), fontPath);
            setTypeface(tf);
        }
    }

    public void disableEmoji(){
        List<InputFilter> filterList = new ArrayList<>(Arrays.asList(new InputFilter[]{}));
        filterList.add(getUnwantedCharacterFilter());
        InputFilter specifiedFilters[] = filterList.toArray(new InputFilter[]{});
        setFilters(specifiedFilters);
    }

    private InputFilter getUnwantedCharacterFilter() {
        InputFilter emojiFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int index = start; index < end; index++) {

                    int type = Character.getType(source.charAt(index));

                    if (type == Character.SURROGATE) {
                        return "";
                    }
                }
                return null;
            }
        };

        return emojiFilter;
    }

}
