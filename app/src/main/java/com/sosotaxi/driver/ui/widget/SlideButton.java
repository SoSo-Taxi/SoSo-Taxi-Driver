/**
 * @Author 范承祥
 * @CreateTime 2020/7/14
 * @UpdateTime 2020/7/14
 */
package com.sosotaxi.driver.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.sosotaxi.driver.R;

/**
 * 滑动按钮
 */
public class SlideButton extends ViewGroup {

    private static final String TAG = "SlideButton";

    /** SlideOverlay在父view中的水平偏移量 */
    private static int MARGIN_HORIZONTAL = 0;

    /** SlideOverlay在父view中的水平偏移量 */
    private static int MARGIN_VERTICAL = 0;

     /** SlideOverlay实例 */
    private SlideOverlay mSlideOverlay;

     /** SlideOverlay的X坐标 */
    private int mOverlayX = 0;

     /** SlideOverlay拖动时的X轴偏移量 */
    private int mDistanceX = 0;

     /** 监听 */
    private MotionListener mMotionListener = null;

    /** 背景文字的Paint */
    private Paint mBackgroundTextPaint;

    /** 背景文字的测量类 */
    private Paint.FontMetrics mBackgroundTextFontMetrics;

    /** 拖动过的部分的Paint */
    private Paint mSecondaryPaint;

    /** attr: 最小高度 */
    private int mMinHeight;

    /** attr: 背景图 */
    private int mBackgroundResId;

    /** attr: 背景文字 */
    private String mBackgroundText = "";

    /** attr: 拖动完成后的背景文字 */
    private String mBackgroundTextComplete = "";

    /** attr: 背景文字的颜色 */
    private int mBackgroundTextColor;

    /** attr: 背景文字的大小 */
    private float mBackgroundTextSize;

    /** attr: Overlay背景图 */
    private int mOverlayResId;

    /** attr: Overlay完成时背景图 */
    private int mOverlayCompleteResId;

    /** attr: Overlay上显示的文字 */
    private String mOverlayText = "";

    /** attr: Overlay上文字的颜色 */
    private int mOverlayTextColor;

    /** attr: Overlay上文字的大小 */
    private float mOverlayTextSize;

    /** attr: Overlay的宽度占总长的比例 */
    private float mOverlayRatio;

    /** attr: 滑动到一半松手时是否回到初始状态 */
    private boolean mResetWhenNotFull;

    /** attr: 拖动结束后是否可以再次操作 */
    private boolean mEnableWhenFull;

    /** attr: 拖动过的部分的颜色 */
    private int mSecondaryColor;

    /** 滑动完成监听器 */
    private OnSlideListener mListener = null;

    public SlideButton(Context context) {
        this(context, null);
    }

    public SlideButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlideButton, 0, 0);
        try {
            mResetWhenNotFull = array.getBoolean(R.styleable.SlideButton_reset_not_full, true);
            mEnableWhenFull = array.getBoolean(R.styleable.SlideButton_enable_when_full, false);

            mBackgroundResId = array.getResourceId(R.styleable.SlideButton_background_drawable, R.mipmap.ic_launcher);
            mOverlayResId = array.getResourceId(R.styleable.SlideButton_overlay_drawable, R.mipmap.ic_launcher);
            mOverlayCompleteResId=array.getResourceId(R.styleable.SlideButton_overlay_drawable_complete,R.mipmap.ic_launcher);
            mMinHeight = array.getDimensionPixelSize(R.styleable.SlideButton_min_height, 240);

            mOverlayText = array.getString(R.styleable.SlideButton_overlay_text);
            mOverlayTextColor = array.getColor(R.styleable.SlideButton_overlay_text_color, Color.WHITE);
            mOverlayTextSize = array.getDimensionPixelSize(R.styleable.SlideButton_overlay_text_size, 44);
            mOverlayRatio = array.getFloat(R.styleable.SlideButton_overlay_ratio, 0.2f);

            mBackgroundText = array.getString(R.styleable.SlideButton_background_text);
            mBackgroundTextComplete = array.getString(R.styleable.SlideButton_background_text_complete);
            mBackgroundTextColor = array.getColor(R.styleable.SlideButton_background_text_color, Color.BLACK);
            mBackgroundTextSize = array.getDimensionPixelSize(R.styleable.SlideButton_background_text_size, 44);

            mSecondaryColor = array.getColor(R.styleable.SlideButton_secondary_color, Color.TRANSPARENT);
        } finally {
            array.recycle();
        }
        init();
    }

    private void init() {
        // 设置背景文字Paint
        mBackgroundTextPaint = new Paint();
        mBackgroundTextPaint.setTextAlign(Paint.Align.CENTER);
        mBackgroundTextPaint.setColor(mBackgroundTextColor);
        mBackgroundTextPaint.setTextSize(mBackgroundTextSize);

        // 获取背景文字测量类
        mBackgroundTextFontMetrics = mBackgroundTextPaint.getFontMetrics();

        // 设置拖动过的部分的Paint
        mSecondaryPaint = new Paint();
        mSecondaryPaint.setColor(mSecondaryColor);

        // 设置背景图
        setBackgroundResource(mBackgroundResId);

        // 创建一个SlideOverlay,设置LayoutParams并添加到ViewGroup中
        mSlideOverlay = new SlideOverlay(getContext());
        mSlideOverlay.setBackground(mOverlayResId);
        mSlideOverlay.setTextColor(mOverlayTextColor);
        mSlideOverlay.setTextSize(mOverlayTextSize);
        mSlideOverlay.setRadio(mOverlayRatio);
        mSlideOverlay.setText(mOverlayText);
        mSlideOverlay.initView();

        /**
         * Important:
         * 此处需要设置OverlayView的LayoutParams,这样才能在布局文件中正确通过wrap_content设置布局
         */
        mSlideOverlay.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mSlideOverlay);

        // 设置监听
        mMotionListener = new MotionListener() {
            @Override
            public void onActionMove(int distanceX) {
                // SlideOverlay拖动时根据X轴偏移量重新计算位置并绘制
                if (mSlideOverlay != null) {
                    mDistanceX = distanceX;
                    requestLayout();
                    invalidate();
                }
            }

            @Override
            public void onActionUp(int x) {
                mOverlayX = x;
                mDistanceX = 0;
                if (mOverlayX + mSlideOverlay.getMeasuredWidth() < getMeasuredWidth()) {
                    // SlideOverlay未拖动到底
                    if (mResetWhenNotFull) {
                        // 重置
                        mOverlayX = 0;
                        mSlideOverlay.resetOverlay();
                        requestLayout();
                        invalidate();
                    }
                } else {
                    mSlideOverlay.setBackgroundResource(mOverlayCompleteResId);
                    // SlideOverlay拖动到底
                    if (!mEnableWhenFull) {
                        // 松开后是否可以继续操作
                        mSlideOverlay.setEnable(false);
                    }
                    if (mListener != null) {
                        // 触发回调
                        mListener.onSlideSuccess();
                    }
                }
            }
        };

        mSlideOverlay.setListener(mMotionListener);
    }

    /**
     * 添加滑动完成监听
     */
    public void addSlideListener(OnSlideListener listener) {
        this.mListener = listener;
    }

    /**
     * 重置SlideButton
     */
    public void reset() {
        mOverlayX = 0;
        mDistanceX = 0;
        if (mSlideOverlay != null) {
            mSlideOverlay.resetOverlay();
        }
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 计算子View的尺寸
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 因为只有一个子View,直接取出来
        mSlideOverlay = (SlideOverlay) getChildAt(0);
        // 根据SlideOverlay的高度设置ViewGroup的高度
        setMeasuredDimension(widthMeasureSpec, mSlideOverlay.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mOverlayX + mDistanceX <= 0) {
            // 控制SlideOverlay不能超过左边界限
            mSlideOverlay.layout(MARGIN_HORIZONTAL, MARGIN_VERTICAL,
                    MARGIN_HORIZONTAL + mSlideOverlay.getMeasuredWidth(),
                    mSlideOverlay.getMeasuredHeight() - MARGIN_VERTICAL);
        } else if (mOverlayX + mDistanceX + mSlideOverlay.getMeasuredWidth() >= getMeasuredWidth()) {
            // 控制SlideOverlay不能超过右边界限
            mSlideOverlay.layout(getMeasuredWidth() - mSlideOverlay.getMeasuredWidth() - MARGIN_HORIZONTAL, MARGIN_VERTICAL,
                    getMeasuredWidth() - MARGIN_HORIZONTAL,
                    mSlideOverlay.getMeasuredHeight() - MARGIN_VERTICAL);
        } else {
            // 根据SlideOverlay的X坐标和偏移量计算位置
            mSlideOverlay.layout(mOverlayX + mDistanceX + MARGIN_HORIZONTAL, MARGIN_VERTICAL,
                    mOverlayX + mDistanceX + mSlideOverlay.getMeasuredWidth() + MARGIN_HORIZONTAL,
                    mSlideOverlay.getMeasuredHeight() - MARGIN_VERTICAL);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制已拖动过的区域
        if (mOverlayX + mDistanceX > 0) {
            canvas.drawRect(MARGIN_HORIZONTAL, MARGIN_VERTICAL, mOverlayX + mDistanceX + MARGIN_HORIZONTAL,
                    getMeasuredHeight() - MARGIN_VERTICAL, mSecondaryPaint);
        }

        // 绘制背景文字
        float baselineY = (getMeasuredHeight() - mBackgroundTextFontMetrics.top - mBackgroundTextFontMetrics.bottom) / 2;
        if (mOverlayX + mDistanceX + mSlideOverlay.getMeasuredWidth() >= getMeasuredWidth()) {
            canvas.drawText(mBackgroundTextComplete == null ? "": mBackgroundTextComplete, getMeasuredWidth() / 2, baselineY, mBackgroundTextPaint);
        } else {
            canvas.drawText(mBackgroundText == null ? "": mBackgroundText, getMeasuredWidth() / 2, baselineY, mBackgroundTextPaint);
        }
    }
}
