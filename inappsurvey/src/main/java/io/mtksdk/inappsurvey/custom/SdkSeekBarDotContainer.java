package io.mtksdk.inappsurvey.custom;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import io.mtksdk.inappsurvey.R;

/**
 * Created by kgy 2019. 9. 24.
 */

public class SdkSeekBarDotContainer extends View {
    private static final String TAG = SdkSeekBarDotContainer.class.getSimpleName();
    private Context context;

    private int startX, dotSize, range, currentProgress;
    private float interval;
    private boolean isZeroScale = false;


    public SdkSeekBarDotContainer(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SdkSeekBarDotContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SdkSeekBarDotContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public SdkSeekBarDotContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init(){
        currentProgress = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        for(int i = 0; i < range; i++){
            if(currentProgress == i){
                String text = isZeroScale ? String.valueOf(i) : String.valueOf(i + 1);
                Typeface tf = Typeface.create("sans-serif-medium", Typeface.NORMAL);
                paint.setTypeface(tf);
                paint.setColor(context.getResources().getColor(R.color.cornflower));
                paint.setTextSize((int) convertDpToPixel(context, 20));

                Rect bounds = new Rect();
                paint.getTextBounds(text, 0, text.length(), bounds);
                int height = bounds.height();
                int width = bounds.width();

                if(text.length() > 1){
                    canvas.drawText(String.valueOf(i + 1), (startX - (int) convertDpToPixel(context, 2)) + (i * interval) - (width / 3), height, paint);
                }else{
                    canvas.drawText(String.valueOf(i + 1), (startX - (int) convertDpToPixel(context, 2)) + (i * interval), height, paint);
                }

            }else{
                paint.setColor(context.getResources().getColor(R.color.pale_grey_two));
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                RectF rectF = new RectF(startX + (i * interval), dotSize, startX + (i * interval) + dotSize, dotSize * 2);
                canvas.drawOval(rectF, paint);
            }
        }
    }

    public void setStartX(int startX){
        this.startX = startX;
    }

    public void setInterval(float interval){
        this.interval = interval;
    }

    public void setDotSize(int dotSize){
        this.dotSize = dotSize;
    }

    public void setRange(int range){
        this.range = range;
    }

    public void setCurrentProgress(int progress){
        this.currentProgress = progress;
        invalidate();
    }

    public void draw(){
        invalidate();
    }

    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public void setIsZeroScale(boolean isZeroScale) {
        this.isZeroScale = isZeroScale;
    }

    public float getInterval(int scale) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        width = width - (int) convertDpToPixel(context, 60);

        int dotSize = (int) convertDpToPixel(context, 6);
        int barStart = (int) convertDpToPixel(context, 13);
        int barEnd = width - barStart - dotSize;

        float interval;

        interval = ((float) (barEnd - barStart) / (float)(scale - 1));
        return interval;
    }
}
