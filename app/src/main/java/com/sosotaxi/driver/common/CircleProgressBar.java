/**
 * @Author 屠天宇
 * @CreateTime 2020/7/15
 * @UpdateTime 2020/7/15
 */


package com.sosotaxi.driver.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.sosotaxi.driver.R;

public class CircleProgressBar extends View {
    // 画圆环的画笔
    private Paint ringPaint;
    // 画字体的画笔
    private Paint textPaint;
    // 圆环颜色
    private int ringColor;
    // 字体颜色
    private int textColor;
    // 半径
    private float radius;
    // 圆环宽度
    private float strokeWidth;
    // 字的长度
    private float txtWidth;
    // 字的高度
    private float txtHeight;
    // 总进度
    private int totalProgress = 100;
    // 当前进度
    private int currentProgress;

    private int alpha = 5;


    int startAngle = -90;


    int sweepAngle = 0;


    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initVariable();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressbar, 0 , 0);
        radius = typeArray.getDimension(R.styleable.CircleProgressbar_radius, 80);
        strokeWidth = typeArray.getDimension(R.styleable.CircleProgressbar_strokeWidth, 10);
        ringColor = typeArray.getColor(R.styleable.CircleProgressbar_ringColor, 0xFF0000);
        textColor = typeArray.getColor(R.styleable.CircleProgressbar_textColor, 0xFFFFFF);
    }

    private void initVariable() {
        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setDither(true);
        ringPaint.setColor(ringColor);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setStrokeWidth(strokeWidth);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setTextSize(radius/2);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        txtHeight = fm.descent + Math.abs(fm.ascent);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentProgress >= 0) {
            RectF oval = new RectF(getWidth() / 2 - radius, getHeight() / 2 - radius, getWidth() / 2 + radius, getHeight() / 2 + radius);
            ringPaint.setAlpha((int) ( alpha + ((float) currentProgress / totalProgress )*100));
//            canvas.drawArc(oval, 0, 0, false, ringPaint);
//            canvas.drawArc(oval, startAngle, ((float) currentProgress / totalProgress) * 360, false, ringPaint);
            canvas.drawArc(oval, startAngle, sweepAngle, false, ringPaint);
            String txt = currentProgress + "%";
            txtWidth = textPaint.measureText(txt, 0, txt.length());
            canvas.drawText(txt, getWidth() / 2 - txtWidth / 2, getHeight() / 2 + txtHeight / 4, textPaint);
            startAngle += 10;
//            sweepAngle = 100;
        }
    }

    public void setProgress(int progress) {
        currentProgress = progress;
        sweepAngle = 200;
        postInvalidate();
    }
}
