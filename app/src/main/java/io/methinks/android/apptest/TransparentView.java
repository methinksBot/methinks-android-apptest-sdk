package io.methinks.android.apptest;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.Queue;

/**
 * For 'show touches' on WebView
 * created by kkb. 2018-06-07
 * You have to add to maximum size your Activity's xml
 *
 * ex)
 * <RelativeLayout>
 *     <LinearLayout
 *          android:layout_width="match_parent"
 *          android:layout_height="match_parent">
 *         .. main layout
 *     </LinearLayout>
 *     <io.methinks.android.custom.TransparentView
 *          android:id="@+id/transparent_container"
 *          android:clickable="false"
 *          android:background="@android:color/transparent"
 *          android:layout_width="match_parent"
 *          android:layout_height="match_parent" />
 *  </RelativeLayout>
 *
 *  And then you have to implement dispatchTouchEvent() method in  your activity.
 *
 *  ex)
 *  @Override
 *      public boolean dispatchTouchEvent(MotionEvent motionEvent) {
 *       int x = (int)motionEvent.getX();
 *       int y = (int)motionEvent.getY();
 *
 *       switch (motionEvent.getAction()) {
 *           case MotionEvent.ACTION_DOWN:
 *               TransparentViewInstance.setPosition(x, y);
 *           case MotionEvent.ACTION_MOVE:
 *               TransparentViewInstance.setPosition(x, y);
 *           case MotionEvent.ACTION_UP:
 *                TransparentViewInstance.removeAllCircles();
 *           default:
 *               break;
 *       }
 *       return super.dispatchTouchEvent(motionEvent);
 *  }
 */
public class TransparentView extends RelativeLayout {
    private static final String TAG = TransparentView.class.getSimpleName();

    private Context context;
    private Paint paint;
    private static final int DEFAULT_RADIUS = 100;
    private Handler handler;

    private Queue<ImageView> queue;


    public TransparentView(Context context) {
        super(context);
        this.context = context;
        handler = new Handler(context.getMainLooper());
        init();
    }

    public TransparentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        handler = new Handler(context.getMainLooper());
        init();
    }

    public TransparentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        handler = new Handler(context.getMainLooper());
        init();
    }

    public TransparentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        handler = new Handler(context.getMainLooper());
        init();
    }

    private void init(){
        queue = new LinkedList<>();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setARGB(255, 255, 0, 0);
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int x = (int)motionEvent.getX();
        int y = (int)motionEvent.getY();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPosition(x, y);
            case MotionEvent.ACTION_MOVE:
                setPosition(x, y);
            case MotionEvent.ACTION_UP:
                removeAllCircles();
            case MotionEvent.ACTION_OUTSIDE:
                setPosition(x, y);
                removeAllCircles();
            default:
                break;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    public void setPosition(int x, int y){
        setCircle(x, y);
    }


    private void setCircle(int x, int y){
        removeAllCircles();

        ImageView circle = new ImageView(context);
        // 터치포인트를 표시할 객체
        circle.setImageResource(R.drawable.touch_pointer);
        LinearLayout.LayoutParams tpparams = new LinearLayout.LayoutParams(60, 60);
        circle.setLayoutParams(tpparams);
        circle.setVisibility(View.VISIBLE);
        circle.setBackgroundColor(Color.TRANSPARENT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(DEFAULT_RADIUS, DEFAULT_RADIUS);
        int startX = x - (DEFAULT_RADIUS / 2);
        int startY = y - (DEFAULT_RADIUS / 2);
        circle.setX(startX);
        circle.setY(startY);
        circle.setLayoutParams(params);

        queue.offer(circle);
        addView(circle);
    }

    /**
     * Remove all circles
     */
    public void removeAllCircles(){
        for(int i = 0; i < queue.size(); i++){
            final ImageView child = queue.poll();
            if(child == null){
                break;
            }else{
                AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                AnimationSet anim = new AnimationSet(true);
                anim.setDuration(50);
                anim.addAnimation(alphaAnimation);

                child.startAnimation(anim);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        child.setVisibility(GONE);
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                removeView(child);
                            }
                        };

                        Handler handler = new Handler();
                        handler.postDelayed(runnable, 100);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        }
    }
}
