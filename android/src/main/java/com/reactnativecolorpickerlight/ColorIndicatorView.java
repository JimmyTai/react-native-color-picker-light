package com.reactnativecolorpickerlight;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by JimmyTai on 2018/8/23.
 */
public class ColorIndicatorView extends View {

    private static final String TAG = ColorIndicatorView.class.getSimpleName();

    private int color = Color.BLACK;

    static final float DEFAULT_RADIUS = 20f, DEFAULT_ACTIVATE_SCALE = 1.3f, DEFAULT_THICKNESS = 4f, DEFAULT_SHADOW_RADIUS = 8f;
    static final int DEFAULT_SHADOW_COLOR = Color.parseColor("#e0e0e0");

    private int radius, thickness, shadowRadius;
    private float activateScale;
    private int shadowColor;

    public ColorIndicatorView(Context context, int radius, float activateScale,
                              int thickness, int shadowRadius, int shadowColor) {
        super(context);
        this.radius = radius;
        this.nowRadius = radius;
        this.activateScale = activateScale;
        this.thickness = thickness;
        this.shadowRadius = shadowRadius;
        this.shadowColor = shadowColor;
        init();
    }

    public ColorIndicatorView(Context context) {
        super(context);
        this.radius = dp2px(DEFAULT_RADIUS);
        this.nowRadius = radius;
        this.activateScale = DEFAULT_ACTIVATE_SCALE;
        this.thickness = dp2px(DEFAULT_THICKNESS);
        this.shadowRadius = dp2px(DEFAULT_SHADOW_RADIUS);
        this.shadowColor = DEFAULT_SHADOW_COLOR;
        init();
    }

    protected int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return ((int) (dp * scale + 0.5f));
    }

    private Paint outerPaint, innerPaint;

    private void init() {
        outerPaint = new Paint();
        outerPaint.setAntiAlias(true);
        outerPaint.setColor(Color.WHITE);
        outerPaint.setStyle(Paint.Style.FILL);
        outerPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);

        innerPaint = new Paint();
        innerPaint.setAntiAlias(true);
        innerPaint.setColor(color);
        innerPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private int nowRadius;

    public void setActivate(boolean activate) {
        if (activate) {
            ValueAnimator animator = ValueAnimator.ofInt(radius, ((int) (activateScale * (float) radius)));
            animator.setDuration(150);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    nowRadius = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.start();
        } else {
            ValueAnimator animator = ValueAnimator.ofInt(((int) (activateScale * (float) radius)), radius);
            animator.setDuration(150);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    nowRadius = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.start();
        }
    }

    public int getColor() {
        return color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, outerPaint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, nowRadius, outerPaint);

        int innerRadius = nowRadius - thickness;
        innerPaint.setColor(color);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, innerRadius, innerPaint);
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }
}
