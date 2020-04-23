//
//  ColorPickerView.swift
//  color_picker
//
//  Created by Jimmy Tai on 2020/4/13.
//

import UIKit

protocol WhitePickerViewDelegate {
    func colorSelected(whiteSelectedColor color: UIColor)
}

@IBDesignable
@objc open class WhitePickerView: UIControl {
  
  @objc var onColorChange: RCTBubblingEventBlock?
  
  private let contentInsetX: CGFloat = 0
  private let contentInsetY: CGFloat = 0
  
  private let indicatorSizeInactive: CGFloat = 45
  private let indicatorSizeActive: CGFloat = 55
  
  var initialized: Bool = false
  var delegate: WhitePickerViewDelegate?
  var tempColor: UIColor?
  
  private lazy var colorSquareView: ColorSquareView = {
    return ColorSquareView()
  }()
  
  open lazy var indicator: ColorIndicatorView = {
    
    let size = CGSize(width: self.indicatorSizeInactive, height: self.indicatorSizeInactive)
    let indicatorRect = CGRect(origin: .zero, size: size)
    
    return ColorIndicatorView(frame: indicatorRect)
  }()
  
  public required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
    self.clipsToBounds = true
  }
  
  public override init(frame: CGRect) {
    super.init(frame: frame)
    self.clipsToBounds = true
  }
  
  open override func layoutSubviews() {
    if initialized { return }
    self.isUserInteractionEnabled = true
    if colorSquareView.superview == nil {
      print("ColorSquareView superview is nil")
      let image = UIImage.gradientImageWithBounds(
        bounds: CGRect(x: 0, y: 0, width: self.bounds.width, height: self.bounds.height),
        colors: [
          UIColor.hexColor(str: "#C1EAF9").cgColor, UIColor.hexColor(str:  "#F3F3F3").cgColor,
          UIColor.hexColor(str: "#EEC348").cgColor
        ],
        startPoint: CGPoint(x: 1, y: 0),
        endPoint: CGPoint(x: 0, y: 1))
      colorSquareView.image = image
      colorSquareView.translatesAutoresizingMaskIntoConstraints = false
      self.addSubview(colorSquareView)
      
      colorSquareView.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: contentInsetX).isActive = true
      colorSquareView.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -contentInsetX).isActive = true
      colorSquareView.topAnchor.constraint(equalTo: self.topAnchor, constant: contentInsetY).isActive = true
      colorSquareView.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: -contentInsetY).isActive = true
    }
    
    if indicator.superview == nil {
      print("Indicator superview is nil")
      self.addSubview(indicator)
    }
    
    if let color = tempColor {
      setColor(color: color)
    } else {
      indicator.center = self.center
    }
    setIndicatorColor()
    initialized = true
  }
  
  public func setColor(color: UIColor) {
    var r: CGFloat = 0.0, g: CGFloat = 0.0, b: CGFloat = 0.0, a: CGFloat = 0.0
    color.getRed(&r, green: &g, blue: &b, alpha: &a)
    setColor(red: Int(r * 255), green: Int(g * 255), blue: Int(b * 255))
  }
  
  public func setColor(red: Int, green: Int, blue: Int) {
    let rgb = RGB(r: CGFloat(red) / 255.0, g: CGFloat(green) / 255, b: CGFloat(blue) / 255)
    tempColor = UIColor(red: rgb.r, green: rgb.g, blue: rgb.b, alpha: 1.0)
    print("setColor: \((tempColor != nil) ? "not nil" : "nil")")
    let hsv = rgb.toHSV(preserveHS: true)
    var hue = CGFloat(hsv.h) * 360
    if hue > 196 {
      hue = 196
    } else if hue < 44 {
      hue = 44
    }
    var x: CGFloat = 0.0, y: CGFloat = 0.0;
    if hue <= 60 {
      x = abs(CGFloat(blue) - 150) / 99 * (self.bounds.width / 2)
      y = self.bounds.height - (self.bounds.height / self.bounds.width * x)
    } else {
      x = self.bounds.width / 2 + abs(CGFloat(red) - 249) / 56 * (self.bounds.width / 2)
      y = self.bounds.height - (self.bounds.height / self.bounds.width * x)
    }
    indicator.center = CGPoint(x: x, y: y)
    setIndicatorColor()
  }
  
  public func getColor() -> UIColor {
    return indicator.color
  }
  
  private func setIndicatorColor() {
    let color = colorSquareView.getPixelColorAtPoint(point: indicator.center)
    indicator.color = color
  }
  
  // MARK: - Tracking
  
  private func trackIndicator(with touch: UITouch) {
    var position = touch.location(in: self)
    if position.x > self.bounds.width - 2 {
      position.x = self.bounds.width - 2
    } else if position.x < 2 {
      position.x = 2
    }
    if position.y > self.bounds.height - 2 {
      position.y = self.bounds.height - 2
    } else if position.y < 2 {
      position.y = 2
    }
    
    indicator.center = position
    setIndicatorColor()
  }
  
  open override func beginTracking(_ touch: UITouch, with event: UIEvent?) -> Bool {
    self.trackIndicator(with: touch)
    growIndicator()
    return true
  }
  
  open override func continueTracking(_ touch: UITouch, with event: UIEvent?) -> Bool {
    self.trackIndicator(with: touch)
    return true
  }
  
  open override func endTracking(_ touch: UITouch?, with event: UIEvent?) {
    super.endTracking(touch, with: event)
    shrinkIndicator()
    
    if let callback = self.onColorChange {
      let params: [String : Any] = ["color": indicator.color.toHexString()]
      callback(params)
    }
    
    if let d = delegate {
      d.colorSelected(whiteSelectedColor: indicator.color)
    }
  }
  
  open override func cancelTracking(with event: UIEvent?) {
    super.cancelTracking(with: event)
    shrinkIndicator()
    
    if let callback = self.onColorChange {
      let params: [String : Any] = ["color": indicator.color.toHexString()]
      callback(params)
    }
    
    if let d = delegate {
      d.colorSelected(whiteSelectedColor: indicator.color)
    }
  }
  
  private func changeIndicatorSize(to size: CGFloat) {
    let center = self.indicator.center
    
    let size = CGSize(width: size, height: size)
    let indicatorRect = CGRect(origin: .zero, size: size)
    
    self.indicator.frame = indicatorRect
    self.indicator.center = center
  }
  
  private func growIndicator() {
    UIView.animate(withDuration: 0.15, delay: 0.0, options: [.curveEaseIn], animations: {
      self.changeIndicatorSize(to: self.indicatorSizeActive)
    }) { (finished) in
    }
  }
  
  private func shrinkIndicator() {
    UIView.animate(withDuration: 0.15, delay: 0.0, options: [.curveEaseOut], animations: {
      self.changeIndicatorSize(to: self.indicatorSizeInactive)
      self.indicator.setNeedsDisplay()
      
    }) { (finished) in
      self.indicator.setNeedsDisplay()
    }
  }
}

extension UIColor {
  
  static func hexColor(str: String) -> UIColor {
    let hex = str.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
    var int = UInt32()
    Scanner(string: hex).scanHexInt32(&int)
    let a, r, g, b: UInt32
    switch hex.count {
    case 3: // RGB (12-bit)
      (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
    case 6: // RGB (24-bit)
      (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
    case 8: // ARGB (32-bit)
      (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
    default:
      (a, r, g, b) = (255, 0, 0, 0)
    }
    return UIColor(red: CGFloat(r) / 255, green: CGFloat(g) / 255, blue: CGFloat(b) / 255, alpha: CGFloat(a) / 255)
  }
  
  func toHexString() -> String {
      var r:CGFloat = 0
      var g:CGFloat = 0
      var b:CGFloat = 0
      var a:CGFloat = 0
      
      getRed(&r, green: &g, blue: &b, alpha: &a)
      
      let rgb:Int = (Int)(r*255)<<16 | (Int)(g*255)<<8 | (Int)(b*255)<<0
      
      return String(format:"#%06x", rgb)
  }
}
