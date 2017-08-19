/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.example.ck.waveanimation;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;


/**
 * Created by user on 16/10/28.
 */

public class WaveView extends View {
    /**
     * y=d-waveAmplifier*sin(waveFrequency*0-wavePhase)
     */
    private static final String TAG = "WaveView";
    private static final int DEFAULT_HEIGHT = 60;
    private Paint wavePaint;
    //振幅
    private float waveAmplifier = 20;
    private float waveAmplifier2 = 20;
    //开始角度
    private float wavePhase = 40;
    //频率
    private float waveFrequency = 1.6f;

    private int waveLineWidth = 1;
    private int viewHeight;
    private int viewWidth;
    private float viewCenterY;
    private WaveHeightListener waveHeightListener;
    private Path path1;
    private Path path2;
    private Path path3;

    private Bitmap bitmap;
    private double tempa;
    private float tempb;
    private AnimatorSet set;

    public WaveView(Context context) {
        this(context, null);
    }


    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setStyle(Paint.Style.FILL);
        wavePaint.setStrokeJoin(Paint.Join.ROUND);
        wavePaint.setStrokeCap(Paint.Cap.ROUND);
        wavePaint.setColor(0xc0ffffff);
        wavePaint.setStrokeWidth(waveLineWidth);


        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xiubi_coin);
        path1 = new Path();
        path2 = new Path();
        path3 = new Path();

    }

    public float getRoundRate(float startradius, float waveFrequency, int currentposition) {
        //避免重复计算，提取公用值
        if (tempa == 0)
            tempa = Math.PI / viewWidth;
        if (tempb == 0) {
            tempb = 2 * (float) Math.PI / 360.0f;
        }
        return (float) (Math.sin(tempa * waveFrequency * (currentposition + 1) + startradius * tempb));
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path1.reset();
        path2.reset();
        path3.reset();
        path1.moveTo(0, viewCenterY - waveAmplifier * getRoundRate(wavePhase, waveFrequency, -1));
        path2.moveTo(0, viewCenterY - 1.3f * waveAmplifier2 * getRoundRate(wavePhase + 90, waveFrequency, -1));
        path3.moveTo(0, viewCenterY + waveAmplifier2 * getRoundRate(wavePhase, waveFrequency, -1));


        for (int i = 0; i < viewWidth - 1; i++) {

            path1.lineTo((float) (i + 1), viewCenterY - waveAmplifier * getRoundRate(wavePhase, waveFrequency, (i + 1)));
            path2.lineTo((float) (i + 1), viewCenterY - 1.3f * waveAmplifier2 * getRoundRate(wavePhase + 90, 0.8f * waveFrequency, (i + 1)));
            path3.lineTo((float) (i + 1), viewCenterY + waveAmplifier2 * getRoundRate(wavePhase, waveFrequency, -1));

        }
        path1.lineTo(viewWidth, viewHeight + 5);
        path2.lineTo(viewWidth, viewHeight);
        path3.lineTo(viewWidth, viewHeight);

        path1.lineTo(0, viewHeight + 5);
        path2.lineTo(0, viewHeight);
        path3.lineTo(0, viewHeight);

        path1.close();
        path2.close();
        path3.close();


        wavePaint.setColor(0xc0ffffff);
        canvas.drawPath(path1, wavePaint);

        wavePaint.setColor(0xB0ffffff);
        canvas.drawPath(path2, wavePaint);

        wavePaint.setColor(0xffffffff);
        canvas.drawBitmap(bitmap, viewWidth / 2 - bitmap.getWidth() / 2, viewCenterY - 1.3f * waveAmplifier *
                getRoundRate(wavePhase + 90, waveFrequency * 0.8f, (viewWidth / 2 + 1)) - bitmap.getHeight() + 20, wavePaint);

        wavePaint.setColor(0x80ffffff);
        canvas.drawPath(path3, wavePaint);



    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
        viewWidth = w;
        viewCenterY = 2 * viewHeight / 3;
        waveAmplifier = (waveAmplifier * 2 > viewHeight) ? (viewHeight / 2) : waveAmplifier;
        waveAnim();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightMeaure;

        if (heightMeasureMode == MeasureSpec.AT_MOST || heightMeasureMode == MeasureSpec.UNSPECIFIED) {
            heightMeaure = dp2px(DEFAULT_HEIGHT);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightMeaure, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (set != null)
            set.cancel();
    }

    public void waveAnim() {
        set = new AnimatorSet();
        //控制平移
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.F, 0F);
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float aFloat = Float.valueOf(animation.getAnimatedValue().toString());
                wavePhase = 360.F * aFloat;
                if (waveHeightListener != null) {
                    waveHeightListener.currentWaveHeightMove(1.3f * waveAmplifier * getRoundRate(wavePhase + 90, waveFrequency * 0.8f, (viewWidth / 2 + 1)));
                }
                invalidate();
            }
        });

       //控制振幅
        ValueAnimator valueAnimator2 = ValueAnimator.ofFloat(1.F, 0F);
        valueAnimator2.setDuration(4000);
        valueAnimator2.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator2.setInterpolator(new LinearInterpolator());
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float aFloat = Float.valueOf(animation.getAnimatedValue().toString());
                waveAmplifier = 10f + 10 * aFloat;
                waveAmplifier2 = 20f + 10 * (1f - aFloat);

            }
        });
        set.playTogether(valueAnimator, valueAnimator2);
        set.start();

    }

    public void stop() {
        if (set != null)
            set.cancel();
    }

    interface WaveHeightListener {
        void currentWaveHeightMove(float currentWavePercent);
    }

    public void setWaveHeightListener(WaveHeightListener waveHeightListener) {
        this.waveHeightListener = waveHeightListener;
    }
}
