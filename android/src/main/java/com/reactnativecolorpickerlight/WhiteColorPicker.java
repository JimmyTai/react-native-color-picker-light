package com.reactnativecolorpickerlight;

import android.content.Context;
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
public class WhiteColorPicker extends RelativeLayout implements View.OnTouchListener {

  private static final String TAG = WhiteColorPicker.class.getSimpleName();

  public interface OnWhiteColorPickerListener {

    void onInitialized();
    void onColorSelected(int color);
  }

  private OnWhiteColorPickerListener listener;

  public void setWhitePickerListener(OnWhiteColorPickerListener listener) {
    this.listener = listener;
  }

  private int indicatorRadius, indicatorThickness, indicatorShadowRadius;
  private float indicatorActivateScale;
  private int indicatorShadowColor;

  public WhiteColorPicker(Context context) {
    super(context);
    init();
  }

  public WhiteColorPicker(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public WhiteColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width = getMeasuredWidth(), height = getMeasuredHeight();
    Log.d(TAG, "onMeasure -> height: " + height + ", width: " + width);
    if (width <= 0 || height <= 0)
      return;
    iv_color.setImageBitmap(createGradientColor(width, height));

    setIndicatorColor();
  }

  private boolean isLayoutPrepared = false;

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    if (changed) {
      if (!isLayoutPrepared) {
        if (listener != null) {
          listener.onInitialized();
        }
      }
      isLayoutPrepared = true;
      Log.d(TAG, String.format(Locale.getDefault(), "left: %d, top: %d, right: %d, bottom: %d", l, t, r, b));
    }
  }

  private ImageView iv_color;
  private ColorIndicatorView view_color_indicator;

  private int indicatorX = 0, indicatorY = 0;

  private void init() {
    indicatorRadius = dp2px(ColorIndicatorView.DEFAULT_RADIUS);
    indicatorActivateScale = ensureRange(ColorIndicatorView.DEFAULT_ACTIVATE_SCALE, 1.f, 2.f);
    indicatorThickness = dp2px(ColorIndicatorView.DEFAULT_THICKNESS);
    indicatorShadowRadius = dp2px(ColorIndicatorView.DEFAULT_SHADOW_RADIUS);
    indicatorShadowColor = ColorIndicatorView.DEFAULT_SHADOW_COLOR;

    createViews();
  }

  private void init(AttributeSet attrs, int defStyle) {
    indicatorRadius = dp2px(ColorIndicatorView.DEFAULT_RADIUS);
    indicatorActivateScale = ColorIndicatorView.DEFAULT_ACTIVATE_SCALE;
    indicatorThickness = dp2px(ColorIndicatorView.DEFAULT_THICKNESS);
    indicatorShadowRadius = dp2px(ColorIndicatorView.DEFAULT_SHADOW_RADIUS);
    indicatorShadowColor = ColorIndicatorView.DEFAULT_SHADOW_COLOR;

    createViews();
  }

  private void createViews() {
    iv_color = new ImageView(getContext());
    RelativeLayout.LayoutParams params_iv_color = new LayoutParams(
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
    int hue = (int) hsv[0];
    hue = ensureRange(hue, 44, 196);
    float x = 0.0f, y = 0.0f;
    if (hue <= 60) {
      x = Math.abs(Color.blue(color) - 150f) / 99f * ((float) getWidth() / 2f);
      y = (float) getHeight() - ((float) getHeight() / (float) getWidth() * x);
    } else {
      x = getWidth() / 2f + Math.abs(Color.red(color) - 249) / 56f * (getWidth() / 2f);
      y = (float) getHeight() - ((float) getHeight() / (float) getWidth() * x);
    }
    indicatorX = (int) ensureRange(x, 0, getMeasuredWidth() - 1);
    indicatorY = (int) ensureRange(y, 0, getMeasuredHeight() - 1);
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
      view_color_indicator.setColor(pixel);
      float[] hsv = new float[3];
      Color.colorToHSV(pixel, hsv);
      int hue = (int) hsv[0];
      int saturation = ((int) (hsv[1] * 100));
      int brightness = ((int) (hsv[2] * 100));
      invalidate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Bitmap createGradientColor(int width, int height) {
    Log.d(TAG, String.format(Locale.getDefault(), "width: %d, height: %d", width, height));
    LinearGradient gradient = new LinearGradient(width, 0, 0, height,
      new int[]{
        Color.parseColor("#C1EAF9"),
        Color.parseColor("#F9F9F3"),
        Color.parseColor("#EED796")
      },
      new float[]{
        0.0f, 0.5f, 1.0f
      },
      android.graphics.Shader.TileMode.CLAMP);
    Paint p = new Paint();
    p.setDither(true);
    p.setShader(gradient);

    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(bitmap);
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
