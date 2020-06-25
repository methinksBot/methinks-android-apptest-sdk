package io.mtksdk.inappsurvey.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;


/**
 * Created by kgy 2019. 9. 24.
 */

public class SdkSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {
    private static final String TAG = "KSeekbar";
    private Context context;

    private int dotSize = 0;
    private int barStart = 0;
    private int barEnd = 0;
    private int range = 0;
    private float interval = 0;

    private SeekBarListener listener;

    public SdkSeekBar(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SdkSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SdkSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init(){
        dotSize = (int)  convertDpToPixel(context, 6);
        barStart = (int) convertDpToPixel(context, 13);
    }

    public void setListener(SeekBarListener listener){
        this.listener = listener;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        w = w - (int) convertDpToPixel(context, 60);
        barEnd = w - barStart - dotSize;

        if(range == 0){
            new NullPointerException("set seekbar's range before change size");
        }else{
            interval = ((float)(barEnd - barStart) / (float)(range - 1));
        }
        listener.didLoadSeekBar(barStart, barEnd, interval);
    }

    public void setRange(int range){
        this.range = range;
    }


    public interface SeekBarListener{
        void didLoadSeekBar(int barStart, int barEnd, float interval);
    }

    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}
