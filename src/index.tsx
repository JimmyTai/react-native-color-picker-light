import React, { useImperativeHandle, useRef } from 'react';
import {
  requireNativeComponent,
  UIManager,
  findNodeHandle,
  StyleProp,
  ViewStyle,
} from 'react-native';

export interface ColorPickerRef {
  setColor(color: string): void;
}

interface Props {
  style?: StyleProp<ViewStyle>;
  type?: 'color' | 'white';
  onColorChange?: (color: string) => void;
}

const RCTColorPickerView = requireNativeComponent('RCTColorPickerView');
const RCTWhitePickerView = requireNativeComponent('RCTWhitePickerView');

const ColorPickerView: React.RefForwardingComponent<ColorPickerRef, Props> = (
  { style, type = 'color', onColorChange },
  ref
) => {
  const view = useRef<any>(null);

  useImperativeHandle(ref, () => ({
    setColor: (color: string) => {
      console.log('set color');
      UIManager.dispatchViewManagerCommand(
        findNodeHandle(view.current),
        UIManager.getViewManagerConfig(
          type === 'color' ? 'RCTColorPickerView' : 'RCTWhitePickerView'
        ).Commands.showColor,
        [color]
      );
    },
  }));

  if (type === 'color') {
    return (
      <RCTColorPickerView
        ref={view}
        style={style}
        onColorChange={(event: any) => {
          if (!onColorChange) {
            return;
          }
          onColorChange(event.nativeEvent.color);
        }}
      />
    );
  } else {
    return (
      <RCTWhitePickerView
        ref={view}
        style={style}
        onColorChange={(event: any) => {
          if (!onColorChange) {
            return;
          }
          onColorChange(event.nativeEvent.color);
        }}
      />
    );
  }
};

export const ColorPicker = React.forwardRef(ColorPickerView);
