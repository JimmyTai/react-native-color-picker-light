//
//  RCTWhitePickerViewManager.swift
//  color_picker
//
//  Created by Jimmy Tai on 2020/4/15.
//

import UIKit

@objc (RCTWhitePickerViewManager)
class RCTWhitePickerViewManager: RCTViewManager {
  
  var pickerView: WhitePickerView?
  
  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  override func view() -> UIView! {
    pickerView = WhitePickerView()
    return pickerView
  }
  
  @objc(showColor:HexColor:) func showColor(dummy: NSNumber, HexColor hex: NSString) {
    print("dummy: \(dummy)")
    print("hex: \(hex)")
    print("what the fuck show color")
    
    DispatchQueue.main.async {
      self.pickerView?.setColor(color: UIColor.hexColor(str: String(hex)))
    }
  }
}
