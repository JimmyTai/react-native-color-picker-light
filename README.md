# react-native-color-picker-light

> This color picker component for React Native is designed for IoT light controlling function. Therefore we provide two mode. One is for white light mode, another is for color light mode. 

[![NPM](https://img.shields.io/npm/v/react-native-color-picker-light.svg)](https://www.npmjs.com/package/react-native-color-picker-light) [![JavaScript Style Guide](https://img.shields.io/badge/code_style-standard-brightgreen.svg)](https://standardjs.com)

## Tutorial

## Demo


- **iOS Style**

<img src="https://github.com/JimmyTai/react-native-color-picker-light/blob/master/images/ios-white.png?raw=true" alt="drawing" width="250"/> <img src="https://github.com/JimmyTai/react-native-color-picker-light/blob/master/images/ios-color.png?raw=true" alt="drawing" width="250"/>

- **Android Style**

<img src="https://github.com/JimmyTai/react-native-color-picker-light/blob/master/images/android-white.png?raw=true" alt="drawing" width="250"/> <img src="https://github.com/JimmyTai/react-native-color-picker-light/blob/master/images/android-color.png?raw=true" alt="drawing" width="250"/>

## Install

```bash
npm install react-native-color-picker-light

or

yarn add react-native-color-picker-light
```
and install cocoapods
```bash
cd ios
pod install
```
### Very Important !!!

Because this library is written by Swift, you have to create **Bridge-Header**.

- First, you should open the project in XCode. Then click new file.

<img src="https://github.com/JimmyTai/react-native-color-picker-light/blob/master/images/xcode_setup_01.png?raw=true" alt="drawing" width="70%"/>


- Select Swift File, then click next.

<img src="https://github.com/JimmyTai/react-native-color-picker-light/blob/master/images/xcode_setup_02.png?raw=true" alt="drawing" width="70%"/>

- Name the file as Dummy.swift and click create.

<img src="https://github.com/JimmyTai/react-native-color-picker-light/blob/master/images/xcode_setup_03.png?raw=true" alt="drawing" width="70%"/>

- Finally, Xcode will ask you do you want to create bridge header. Please select Create Bridging Header.

<img src="https://github.com/JimmyTai/react-native-color-picker-light/blob/master/images/xcode_setup_04.png?raw=true" alt="drawing" width="70%"/>

## Usage
```ts
const picker = useRef();

<ColorPicker
  ref={picker}
  type="color"
  style={{ width: 200, height: 200 }}
  onColorChange={color  => {
	console.log('color:', color);
  }}
/>

picker.current.setColor('#f0ce78');

```

## Props

### `type`

Used to choose which type of picker you want.

| Type       | Required | default |
| ---------- | -------- | ------- |
| color \| white | No   | color   |

---
### `onColorChange`

Callback that is called when the user select a color.

| Type       | Required | default |
| ---------- | -------- | ------- |
| function   | No       | null    |

---

## License

MIT License

Copyright (c) 2020  [JimmyTai](https://github.com/JimmyTai).

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the “Software”), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
