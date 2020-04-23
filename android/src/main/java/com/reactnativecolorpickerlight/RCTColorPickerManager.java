package com.reactnativecolorpickerlight;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by JimmyTai on 2020/4/23.
 */
public class RCTColorPickerManager extends SimpleViewManager {
  private static final String TAG = "RCTColorPickerManager";
  private static final String REACT_CLASS = "RCTColorPickerView";

  private static final int COMMAND_SHOW_COLOR = 1;

  private ReactApplicationContext mCallerContext;

  public RCTColorPickerManager(ReactApplicationContext mCallerContext) {
    this.mCallerContext = mCallerContext;
  }

  @NonNull
  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @NonNull
  @Override
  protected View createViewInstance(@NonNull ThemedReactContext reactContext) {
    ColorPicker picker = new ColorPicker(reactContext);
    picker.setOnColorSelectedListener(color -> {
      WritableMap event = Arguments.createMap();
      event.putString("color", String.format("#%06x", (0xFFFFFF & color)));
      reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
        picker.getId(),
        "colorChange",
        event);
    });
    return picker;
  }

  @Nullable
  @Override
  public Map<String, Object> getExportedCustomBubblingEventTypeConstants() {
    MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
    builder.put("colorChange", MapBuilder.of("phasedRegistrationNames", MapBuilder.of("bubbled", "onColorChange")));
    return builder.build();
  }

  @Nullable
  @Override
  public Map<String, Integer> getCommandsMap() {
    return MapBuilder.of(
      "showColor", COMMAND_SHOW_COLOR
    );
  }

  @Override
  public void receiveCommand(@NonNull View root, int commandId, @Nullable ReadableArray args) {
    Log.d(TAG, String.format("int command id: %d", commandId));
    try {
      if (root instanceof ColorPicker && args != null) {
        switch (commandId) {
          case COMMAND_SHOW_COLOR:
            if (args.isNull(0)) break;
            String hex = args.getString(0);
            ((ColorPicker) root).setColor(Color.parseColor(hex));
            break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void receiveCommand(@NonNull View root, String commandId, @Nullable ReadableArray args) {
    Log.d(TAG, String.format("string command id: %s", commandId));
    try {
      if (root instanceof ColorPicker && args != null) {
        switch (commandId) {
          case "showColor":
            if (args.isNull(0)) break;
            String hex = args.getString(0);
            ((ColorPicker) root).setColor(Color.parseColor(hex));
            break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
