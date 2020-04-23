package com.reactnativecolorpickerlight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Locale;

/**
 * Created by JimmyTai on 2018/8/23.
 */
public class ColorPicker extends RelativeLayout implements View.OnTouchListener {

    private static final String TAG = ColorPicker.class.getSimpleName();

    public interface OnColorSelectedListener {

        void onColorSelected(int color);
    }

    private OnColorSelectedListener listener;

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    private int indicatorRadius, indicatorThickness, indicatorShadowRadius;
    private float indicatorActivateScale;
    private int indicatorShadowColor;

    public ColorPicker(Context context) {
        super(context);
        init();
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth(), height = getMeasuredHeight();
        Log.d(TAG, "onMeasure -> height: " + height + ", width: " + width);
        if (width <= 0 || height <= 0) return;
        iv_color.setImageBitmap(createGradientColor(width, height));

        setIndicatorColor();
    }

    private boolean isLayoutPrepared = false;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            isLayoutPrepared = true;
            Log.d(TAG, String.format(Locale.getDefault(), "left: %d, top: %d, right: %d, bottom: %d", l, t, r, b));
        }
    }

    private ImageView iv_color;
    private ColorIndicatorView view_color_indicator;

    private int indicatorX = 0, indicatorY = 0;

    private void init() {
        indicatorRadius = dp2px(ColorIndicatorView.DEFAULT_RADIUS);
        indicatorActivateScale = ColorIndicatorView.DEFAULT_ACTIVATE_SCALE;
        indicatorThickness = dp2px(ColorIndicatorView.DEFAULT_THICKNESS);
        indicatorShadowRadius = dp2px(ColorIndicatorView.DEFAULT_SHADOW_RADIUS);
        indicatorShadowColor = ColorIndicatorView.DEFAULT_SHADOW_COLOR;

        createViews();
    }

    private void init(AttributeSet attrs, int defStyle) {
        indicatorRadius = dp2px(ColorIndicatorView.DEFAULT_RADIUS);
        indicatorActivateScale = ensureRange(ColorIndicatorView.DEFAULT_ACTIVATE_SCALE, 1.f, 2.f);
        indicatorThickness = dp2px(ColorIndicatorView.DEFAULT_THICKNESS);
        indicatorShadowRadius = dp2px(ColorIndicatorView.DEFAULT_SHADOW_RADIUS);
        indicatorShadowColor = ColorIndicatorView.DEFAULT_SHADOW_COLOR;

        createViews();
    }

    private void createViews() {
        iv_color = new ImageView(getContext());
        LayoutParams params_iv_color = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv_color.setLayoutParams(params_iv_color);
        addView(iv_color);
        view_color_indicator = new ColorIndicatorView(getContext(),
                indicatorRadius, indicatorActivateScale, indicatorThickness, indicatorShadowRadius, indicatorShadowColor);
        int indicatorSize = (int) (indicatorActivateScale * indicatorRadius + indicatorShadowRadius) * 2;
        RelativeLayout.LayoutParams params_view_color_indicator = new LayoutParams(indicatorSize, indicatorSize);
        params_view_color_indicator.setMargins(0, 0, 0, 0);
        view_color_indicator.setLayoutParams(params_view_color_indicator);
        addView(view_color_indicator);
        setOnTouchListener(this);
    }

    protected int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return ((int) (dp * scale + 0.5f));
    }

    public int getColor() {
        return view_color_indicator.getColor();
    }

    public void setColor(int color) {
        if (!isLayoutPrepared) {
            getViewTreeObserver().addOnGlobalLayoutListener(new MyGlobalLayoutListener(color));
            return;
        }
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        int x = ((int) (hsv[0] / 360f * getWidth()));
        int y = ((int) ((float) getHeight() - (ensureRange(hsv[1], 0.2f, 1f) - 0.2f) / 0.8f * (float) getHeight()));
        indicatorX = ensureRange(x, 0, getWidth() - 1);
        indicatorY = ensureRange(y, 0, getHeight() - 1);
        setIndicatorColor();
    }

    private class MyGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private int color;

        MyGlobalLayoutListener(int color) {
            this.color = color;
        }

        @Override
        public void onGlobalLayout() {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
            setColor(color);
        }
    }

    // MARK - Drawing

    private void setIndicatorColor() {
        try {
            view_color_indicator.setX(indicatorX - (view_color_indicator.getWidth() / 2));
            view_color_indicator.setY(indicatorY - (view_color_indicator.getHeight() / 2));
            Bitmap bitmap = ((BitmapDrawable) iv_color.getDrawable()).getBitmap();
            int pixel = bitmap.getPixel(indicatorX, indicatorY);
            float[] hsv = new float[3];
            Color.colorToHSV(pixel, hsv);
            float hue = hsv[0];
            float saturation = hsv[1];
            float brightness = hsv[2];
            float newSaturation = ((float) (getHeight() - indicatorY) / (float) getHeight() * 0.8f + 0.2f);
            if (getHeight() == 0) {
                view_color_indicator.setColor(pixel);
            } else {
                int newColor = Color.HSVToColor(new float[]{hue, newSaturation, brightness});
                view_color_indicator.setColor(newColor);
            }
            invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap createGradientColor(int width, int height) {
        Log.d(TAG, String.format(Locale.getDefault(), "width: %d, height: %d", width, height));
        LinearGradient gradient = new LinearGradient(0, 0, width, 0,
                new int[]{
                        Color.parseColor("#E6312E"),
                        Color.parseColor("#E6842E"), Color.parseColor("#E6D72E"),
                        Color.parseColor("#98E62E"), Color.parseColor("#2EE62F"),
                        Color.parseColor("#2EE67C"), Color.parseColor("#2ED5E6"),
                        Color.parseColor("#2E79E6"), Color.parseColor("#302EE6"),
                        Color.parseColor("#7D2EE6"), Color.parseColor("#E62EE3"),
                        Color.parseColor("#E62EB5"),
                        Color.parseColor("#E6312E")
                },
                new float[]{
                        0.0f, 1f / 12f, 2f / 12f, 3f / 12f, 4f / 12f, 5f / 12f, 6f / 12f, 7f / 12f,
                        8f / 12f, 9f / 12f, 10f / 12f, 11f / 12f, 12f / 12f
                },
                android.graphics.Shader.TileMode.CLAMP);
        Paint p = new Paint();
        p.setDither(true);
        p.setShader(gradient);

        LinearGradient alphaGradient = new LinearGradient(width / 2, 0, width / 2, height,
                new int[]{
                        Color.parseColor("#20FFFFFF"), Color.parseColor("#C0FFFFFF")
                },
                new float[]{
                        0.0f, 1.0f
                },
                android.graphics.Shader.TileMode.CLAMP);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawRect(0, 0, width, height, p);
        p.setShader(alphaGradient);
        c.drawRect(0, 0, width, height, p);

        return bitmap;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            Log.d(TAG, "indicator -> action down");
            view_color_indicator.setActivate(true);
            int[] coordinate = new int[2];
            getLocationOnScreen(coordinate);
            int x = ensureRange((int) event.getRawX() - coordinate[0], 0, getWidth() - 1);
            int y = ensureRange((int) event.getRawY() - coordinate[1], 0, getHeight() - 1);
            indicatorX = x;
            indicatorY = y;
            setIndicatorColor();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] coordinate = new int[2];
            getLocationOnScreen(coordinate);
            int x = ensureRange((int) event.getRawX() - coordinate[0], 0, getWidth() - 1);
            int y = ensureRange((int) event.getRawY() - coordinate[1], 0, getHeight() - 1);
//            Log.d(TAG, "indicator -> action move, x: " + x + ", y: " + y);
            indicatorX = x;
            indicatorY = y;
            setIndicatorColor();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
//            Log.d(TAG, "indicator -> action up");
            view_color_indicator.setActivate(false);
            if (listener != null) {
                listener.onColorSelected(getColor());
            }
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
//            Log.d(TAG, "indicator -> action cancel");
            view_color_indicator.setActivate(false);
            if (listener != null) {
                listener.onColorSelected(getColor());
            }
        }
        return true;
    }

    private int ensureRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    private float ensureRange(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
}
