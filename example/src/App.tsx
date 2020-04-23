import React, { useRef, useEffect } from 'react';
import { SafeAreaView, StyleSheet, StatusBar } from 'react-native';

import { ColorPicker, ColorPickerRef } from '../../src/index';

const App = () => {
  const colorPicker = useRef<ColorPickerRef>(null);

  useEffect(() => {
    setTimeout(() => {
      colorPicker.current?.setColor('#f0ce78');
    }, 1000);
  }, []);

  return (
    <>
      <StatusBar barStyle="dark-content" />
      <SafeAreaView>
        <ColorPicker
          ref={colorPicker}
          type="white"
          style={styles.colorPicker}
          onColorChange={color => {
            console.log('color:', color);
          }}
        />
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  colorPicker: {
    aspectRatio: 1,
  },
});

export default App;
