# WaveAnimation
水波动画
### 自定义波浪动画

今天讲讲以前项目的一个动画，先看看效果

![2017-08-18-20-38-38.gif](http://upload-images.jianshu.io/upload_images/6456519-2386673405955b40.gif?imageMogr2/auto-orient/strip)


---![2017-08-19-12-02-52.gif](http://upload-images.jianshu.io/upload_images/6456519-f48b62df0147c5da.gif?imageMogr2/auto-orient/strip)

> 原理：正弦曲线y=a*sin(b*α+c)+m;

> a:控制振幅  b:控制波长 c:控制轴偏移  m:控制y轴偏移 α：角度

> 思路：画出波的path，通过动画控制振幅a达到水波的起伏效果<br>控制c的值达到移动效果
---

#### 使用

```
 <com.example.ck.waveanimation.WaveView
        android:id="@+id/waveline"
        android:background="#999"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
      />
      //监听硬币偏移量
    WaveView waveView= (WaveView) findViewById(R.id.waveline);
    waveView.setWaveHeightListener(new WaveView.WaveHeightListener() {
        @Override
        public void currentWaveHeightMove(float currentWavePercent) {
                Log.i("WaveView", "currentWaveHeightScal: "+currentWavePercent);
        }
    });
```



#### 第一步：View的测量

```
在非EXACTLY模式下，默认一个高度
   if (heightMeasureMode == MeasureSpec.AT_MOST || heightMeasureMode ==     MeasureSpec.UNSPECIFIED) {
       heightMeaure = dp2px(DEFAULT_HEIGHT);
       heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightMeaure, MeasureSpec.EXACTLY);
        }
```


```
在onSizeChanged获取一些初始值
 protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
        viewWidth = w;
        viewCenterY = 2 * viewHeight / 3;
        waveAmplifier = (waveAmplifier * 2 > viewHeight) ? (viewHeight / 2) : waveAmplifier;
        waveAnim();
    }
```
#### View的绘制

```
各个波的path路径绘制出来
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
```


```
draw path以及硬币
wavePaint.setColor(0xc0ffffff);
        canvas.drawPath(path1, wavePaint);

        wavePaint.setColor(0xB0ffffff);
        canvas.drawPath(path2, wavePaint);

        wavePaint.setColor(0xffffffff);
        canvas.drawBitmap(bitmap, viewWidth / 2 - bitmap.getWidth() / 2, viewCenterY - 1.3f * waveAmplifier *
                getRoundRate(wavePhase + 90, waveFrequency * 0.8f, (viewWidth / 2 + 1)) - bitmap.getHeight() + 20, wavePaint);

        wavePaint.setColor(0x80ffffff);
        canvas.drawPath(path3, wavePaint);
```


```
动画的控制
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
```
