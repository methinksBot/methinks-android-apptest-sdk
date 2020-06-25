package io.mtksdk.inappsurvey.custom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;

import java.util.Locale;

import io.mtksdk.inappsurvey.R;

/**
 * Created by kgy 2019. 9. 24.
 */

public class MethinksTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final String TAG = MethinksTextView.class.getSimpleName();
    private Context context;
    private AttributeSet attrs;

    public MethinksTextView(Context context) {
        super(context);
        init(context, null);
    }

    public MethinksTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MethinksTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs){
        this.context = context;
        this.attrs = attrs;
        String lang = Locale.getDefault().getISO3Language();
        if(lang.equals("kor") || lang.equals("eng") || lang.equals("jpn")){
            String fontPath = "fonts/NotoSansKR-Regular-Hestia.otf";
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView);
            String fontWeight = a.getString(R.styleable.CustomView_fw);
            if(TextUtils.isEmpty(fontWeight)){
                fontWeight = context.getString(R.string.font_weight_regular);
            }
            if(lang.equals("kor") || lang.equals("eng")){
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

    private void init(Context context, @Nullable AttributeSet attrs, String fontWeight){
        this.context = context;
        this.attrs = attrs;
        String lang = Locale.getDefault().getISO3Language();
        if(lang.equals("kor") || lang.equals("eng") || lang.equals("jpn")){
            String fontPath = "fonts/NotoSansKR-Regular-Hestia.otf";
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView);
            if(TextUtils.isEmpty(fontWeight)){
                fontWeight = context.getString(R.string.font_weight_regular);
            }
            if(lang.equals("kor") || lang.equals("eng")){
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

    public void setFontWeight(String fontWeight){
        String lang = Locale.getDefault().getISO3Language();
        if(lang.equals("kor") || lang.equals("eng") || lang.equals("jpn")){
            String fontPath = "fonts/NotoSansKR-Regular-Hestia.otf";
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView);
            if(TextUtils.isEmpty(fontWeight)){
                fontWeight = context.getString(R.string.font_weight_regular);
            }
            if(lang.equals("kor") || lang.equals("eng")){
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


}

