package io.methinks.android.apptest;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Date;

import static android.content.Context.WINDOW_SERVICE;

public class Hover extends AppCompatImageView implements View.OnClickListener {
    private static final String TAG = Hover.class.getSimpleName();

    private static final int MAX_CLICK_DISTANCE = 15;   // 15dp

    private Context context;
    private OnClickHover listener;

    public boolean isOpenedPopup = false;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    public float lastX;
    public float lastY;
    private int maxX, maxY;
    private long pressStartTime;
    public int height;
    private int totalWidth;
    private long lastActionTime = 0;

    public int iconWidth, iconHeight;

    private BitmapDrawable mDrawable;


    private WindowManager windowManager;
    protected WindowManager.LayoutParams windowParams;



    protected ViewGroup.LayoutParams lp;


    public Hover(Context context) {
        super(context);
        init(context);
    }

    public Hover(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Hover(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context){
        this.context = context;
        this.windowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);


        setBackground(context.getDrawable(R.drawable.hover_icon_background));
        setImageResource(R.drawable.ic_button_patcher);

        setOnClickListener(this);
        setLastActionTime();

        int shadowValue = (int)convertDpToPixel(context, 10);
        setElevation(shadowValue);


        Point point = new Point();
        Display display;
        if (windowManager != null) {
            display = windowManager.getDefaultDisplay();
            display.getSize(point);
            maxX = point.x;
            maxY = point.y;
        }

        int size = (int) convertDpToPixel(context);
        lp = new ViewGroup.LayoutParams(size, size);

        this.iconWidth = size;
        this.iconHeight = size;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(size, size);
        setLayoutParams(params);

        int topBottomPadding = (int)convertPixelsToDp(context, 19);
        int leftRightPadding = (int)convertPixelsToDp(context, 18);
        setPadding(leftRightPadding, topBottomPadding, leftRightPadding, topBottomPadding);
        setScaleType(ImageView.ScaleType.CENTER);

        setOnTouchListener((view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(!isOpenedPopup){
                        Hover.this.setLastActionTime();
                        initialX = windowParams.x;
                        initialY = windowParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        pressStartTime = System.currentTimeMillis();
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    if(isClickEvent(initialX, initialY, (int)lastX, (int)lastY)){
                        if(listener != null){
                            listener.onClicked();
                        }
                    } else {
                        // Hover icon movement according to width value
//                        if(lastX >= (maxX / 2)) {
//                            windowParams.x = maxX;
//                            lastX = maxX;
//                        } else {
//                            windowParams.x = 0;
//                            lastX = 0;
//                        }
//                        windowManager.updateViewLayout(Hover.this, windowParams);
                    }
                    return false;
                case MotionEvent.ACTION_MOVE:
                    if(!isOpenedPopup){
                        int newX = initialX + (int) (event.getRawX() - initialTouchX);
                        int newY = initialY + (int) (event.getRawY() - initialTouchY);

                        if(newX >= 0 && newX < maxX){
                            windowParams.x = newX;
                            windowManager.updateViewLayout(Hover.this, windowParams);
                            lastX = newX;
                        }
                        if((newY >= 0 && newY <= maxY)){
                            windowParams.y = newY;
                            windowManager.updateViewLayout(Hover.this, windowParams);
                            lastY = newY;
                        }
                    }

                    return false;
            }
            return false;
        });

        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int paddingValue = (int)convertDpToPixel(context, 5);
        setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        windowParams = (WindowManager.LayoutParams) getLayoutParams();
    }

    protected void setLastActionTime(){
        lastActionTime = new Date().getTime();
    }


    private boolean isClickEvent(float x1, float y1, float x2, float y2){
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);

        return convertPixelsToDp(context, distanceInPx) < MAX_CLICK_DISTANCE;
    }

    private float convertDpToPixel(Context context, int dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (float) dp * (metrics.densityDpi / 160f);
    }

    private float convertDpToPixel(Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (float) 56 * (metrics.densityDpi / 160f);
    }

    private static float convertPixelsToDp(Context context, float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    public void setListener(OnClickHover listener){
        this.listener = listener;
    }

    public void setVisible(){
        setVisibility(VISIBLE);
    }

    public void setInvisible(){
        setVisibility(GONE);
    }

    @Override
    public void onClick(View view) {
        if(this.listener != null){
            listener.onClicked();
        }
    }

    protected interface OnClickHover{
        void onClicked();
    }
}
