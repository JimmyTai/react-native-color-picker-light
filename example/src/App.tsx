import React, { useRef, useEffect, useState } from 'react';
import { SafeAreaView, StyleSheet, StatusBar, View, Text } from 'react-native';

import { ColorPicker, ColorPickerRef } from '../../src/index';
import Slider from '@react-native-community/slider';
import { IcBrightness } from './ic_brightness';

const App = () => {
  const [brightness, setBrightness] = useState<number>(100);
  const colorPicker = useRef<ColorPickerRef>(null);

  useEffect(() => {
    // setTimeout(() => {
    //   colorPicker.current?.setColor('#f0ce78');
    // }, 1000);
  }, []);

  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <View style={styles.appbar}>
          <Text style={styles.appbarTitle}>ColorPicker</Text>
        </View>
        <ColorPicker
          ref={colorPicker}
          type="color"
          style={styles.colorPicker}
          onInit={() => {
            console.log('color picker initialized');
            colorPicker.current?.setColor('#f0ce78');
          }}
          onColorChange={color => {
            console.log('color:', color);
          }}
        />
        <View style={styles.brightnessRoot}>
          <IcBrightness color="#505050" style={styles.brightnessIcon} />
          <Slider
            style={styles.brightnessSlider}
            step={1}
            value={100}
            minimumValue={20}
            maximumValue={100}
            onValueChange={val => setBrightness(val)}
          />
          <Text style={styles.brightnessValue}>{Math.floor(brightness)}</Text>
        </View>
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  appbar: {
    height: 60,
    backgroundColor: '#fff',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'flex-end',
    paddingHorizontal: 15,
  },
  appbarTitle: {
    fontSize: 30,
    color: '#000',
    fontWeight: 'bold',
  },
  colorPicker: {
    aspectRatio: 1,
  },
  brightnessRoot: {
    height: 60,
    marginTop: 20,
    paddingHorizontal: 15,
    backgroundColor: '#fff',
    flexDirection: 'row',
    alignItems: 'center',
  },
  brightnessIcon: {
    height: '50%',
    aspectRatio: 1,
  },
  brightnessSlider: {
    flex: 1,
    marginHorizontal: 15,
  },
  brightnessValue: {
    width: 40,
    fontSize: 20,
    color: '#606060',
    textAlign: 'center',
  },
});

export default App;
