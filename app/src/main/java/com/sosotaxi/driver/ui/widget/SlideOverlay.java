/**
 * @Author 范承祥
 * @CreateTime 2020/7/14
 * @UpdateTime 2020/7/14
 */
package com.sosotaxi.driver.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 滑动覆盖层
 */
public class SlideOverlay extends View {

    private boolean mEnable;

    private Paint mTextPaint;

    private String mText;

    private int mTextColor;

    private float mTextSize;

    private float mRatio;

    private int mMinHeight;

    private int mResId;

    private Paint.FontMetrics mFontMetrics;

    private MotionListener mListener;

    private float mDownX;

    private float mX;

    private float mDistanceX=0;

    public SlideOverlay(Context context){
        this(context,null);
    }

    public SlideOverlay(Context context, AttributeSet attributes){
        super(context,attributes);
    }

    /**
     * 初始化View
     */
    public void initView(){
        // 设置文字
        mTextPaint=new Paint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        // 获取字体测量类
        mFontMetrics=mTextPaint.getFontMetrics();

        // 设置背景
        setBackgroundResource(mResId);

        // 设置触摸事件可用
        mEnable=true;
    }

    /**
     * 重置Overlay
     */
    public void resetOverlay(){
        mDownX=0;
        mDistanceX=0;
        mX=0;
        mEnable=true;
    }

    public void setListener(MotionListener listener){
        mListener=listener;
    }

    public void setEnable(boolean enable){
        mEnable=enable;
    }

    public boolean getEnable(){
        return mEnable;
    }

    public void setTextSize(float textSize){
        mTextSize=textSize;
    }

    public void setTextColor(int textColor){
        mTextColor=textColor;
    }

    public void setBackground(int resId){
        mResId=resId;
    }

    public void setRadio(float ratio){
        mRatio=ratio;
    }

    public void setText(String text){
        mText=text;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 宽度和宽Mode
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        // 高度和高Mode
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                // layout_height为"wrap_content"时显示最小高度
                setMeasuredDimension(MeasureSpec.makeMeasureSpec((int)(widthSize * mRatio), widthMode),
                        MeasureSpec.makeMeasureSpec(mMinHeight, heightMode));
                break;
            default:
                // layout_height为"match_parent"或指定具体高度时显示默认高度
                setMeasuredDimension(MeasureSpec.makeMeasureSpec((int)(widthSize * mRatio), widthMode),
                        MeasureSpec.makeMeasureSpec(heightSize, heightMode));
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 获取文字baseline的Y坐标
        float baselineY = (getMeasuredHeight() - mFontMetrics.top - mFontMetrics.bottom) / 2;
        // 绘制文字
        canvas.drawText(mText == null ? "":mText, getMeasuredWidth() / 2, baselineY, mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnable) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 记录手指按下时SlideIcon的X坐标
                mDownX = event.getRawX();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // 设置手指松开时SlideIcon的X坐标
                mDownX = 0;
                mX = mX + mDistanceX;
                mDistanceX = 0;
                // 触发松开回调并传入当前SlideIcon的X坐标
                if (mListener != null) {
                    mListener.onActionUp((int) mX);
                }
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                // 记录SlideIcon在X轴上的拖动距离
                mDistanceX = event.getRawX() - mDownX;
                // 触发拖动回调并传入当前SlideIcon的拖动距离
                if (mListener != null) {
                    mListener.onActionMove((int) mDistanceX);
                }
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
}
