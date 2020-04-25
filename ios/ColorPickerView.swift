//
//  ColorPickerView.swift
//  color_picker
//
//  Created by Jimmy Tai on 2020/4/15.
//

import UIKit

protocol ColorPickerViewDelegate {
    func onInitialized()
    func colorSelected(selectedColor color: UIColor)
}

class ColorPickerView: UIControl {
    
    @objc var onInit: RCTBubblingEventBlock?
    @objc var onColorChange: RCTBubblingEventBlock?
    
    private let contentInsetX: CGFloat = 0
    private let contentInsetY: CGFloat = 0
    
    private let indicatorSizeInactive: CGFloat = 45
    private let indicatorSizeActive: CGFloat = 55
    
    var initialized: Bool = false
    var delegate: ColorPickerViewDelegate?
    var tempColor: UIColor?
    
    private lazy var colorSquareView: ColorSquareView = {
        return ColorSquareView()
    }()
    
    private lazy var aplphaSquareView: ColorSquareView = {
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
    
    // MARK - Drawing
    
    open override func layoutSubviews() {
        if initialized { return }
        self.isUserInteractionEnabled = true
        if colorSquareView.superview == nil {
            let image = UIImage.gradientImageWithBounds(
                bounds: CGRect(x: 0, y: 0, width: self.bounds.width, height: self.bounds.height),
                colors: [
                    UIColor.hexColor(str: "#E6312E").cgColor,
                    UIColor.hexColor(str: "#E6842E").cgColor, UIColor.hexColor(str:  "#E6D72E").cgColor,
                    UIColor.hexColor(str:  "#98E62E").cgColor, UIColor.hexColor(str:  "#2EE62F").cgColor,
                    UIColor.hexColor(str:  "#2EE67C").cgColor, UIColor.hexColor(str:  "#2ED5E6").cgColor,
                    UIColor.hexColor(str:  "#2E79E6").cgColor, UIColor.hexColor(str:  "#302EE6").cgColor,
                    UIColor.hexColor(str: "#7D2EE6").cgColor, UIColor.hexColor(str:  "#E62EE3").cgColor,
                    UIColor.hexColor(str:  "#E62EB5").cgColor,
                    UIColor.hexColor(str:  "#E6312E").cgColor
                ],
                startPoint: CGPoint(x: 0, y: 0.5),
                endPoint: CGPoint(x: 1, y: 0.5))
            colorSquareView.image = image
            colorSquareView.translatesAutoresizingMaskIntoConstraints = false
            self.addSubview(colorSquareView)
            
            colorSquareView.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: contentInsetX).isActive = true
            colorSquareView.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -contentInsetX).isActive = true
            colorSquareView.topAnchor.constraint(equalTo: self.topAnchor, constant: contentInsetY).isActive = true
            colorSquareView.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: -contentInsetY).isActive = true
        }
        
        if aplphaSquareView.superview == nil {
            let alphaImage = UIImage.gradientImageWithBounds(
                bounds: CGRect(x: 0, y: 0, width: self.bounds.width, height: self.bounds.height),
                colors: [UIColor.hexColor(str: "#20FFFFFF").cgColor, UIColor.hexColor(str:  "#C0FFFFFF").cgColor],
                startPoint: CGPoint(x: 0.5, y: 0),
                endPoint: CGPoint(x: 0.5, y: 1))
            aplphaSquareView.image = alphaImage
            aplphaSquareView.translatesAutoresizingMaskIntoConstraints = false
            self.addSubview(aplphaSquareView)
            
            aplphaSquareView.leadingAnchor.constraint(equalTo: self.leadingAnchor, constant: contentInsetX).isActive = true
            aplphaSquareView.trailingAnchor.constraint(equalTo: self.trailingAnchor, constant: -contentInsetX).isActive = true
            aplphaSquareView.topAnchor.constraint(equalTo: self.topAnchor, constant: contentInsetY).isActive = true
            aplphaSquareView.bottomAnchor.constraint(equalTo: self.bottomAnchor, constant: -contentInsetY).isActive = true
        }
        
        if indicator.superview == nil {
            self.addSubview(indicator)
        }
        
        if let color = tempColor {
            setColor(color: color)
        } else {
            indicator.center = self.center
        }
        
        setIndicatorColor()
        if initialized == false {
            // send event back to js
            if let callback = self.onInit {
                let params: [String : Any] = [:]
                callback(params)
            }
            if let d = delegate {
                d.onInitialized()
            }
        }
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
        let hsv = rgb.toHSV(preserveHS: true)
        
        let x = hsv.h * self.bounds.width
        var saturation = hsv.s
        if saturation > 1 {
            saturation = 1
        } else if saturation < 0.2 {
            saturation = 0.2
        }
        let y = self.bounds.height - (saturation - 0.2) / 0.8 * self.bounds.height
        
        indicator.center = CGPoint(x: x, y: y)
        setIndicatorColor()
    }
    
    public func getColor() -> UIColor {
        return indicator.color
    }
    
    private func setIndicatorColor() {
        let color = colorSquareView.getPixelColorAtPoint(point: CGPoint(x: indicator.center.x, y: 5))
        var hue: CGFloat = 0
        var saturation: CGFloat = 0
        var brightness: CGFloat = 0
        var alpha: CGFloat = 0
        color.getHue(&hue, saturation: &saturation, brightness: &brightness, alpha: &alpha)
        let sat = ((self.bounds.height - indicator.center.y) / self.bounds.height * 0.8 + 0.2)
        let newColor = UIColor(hue: hue, saturation: sat, brightness: brightness, alpha: alpha)
        indicator.color = newColor
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
        
        // send event back to js
        if let callback = self.onColorChange {
            let params: [String : Any] = ["color": indicator.color.toHexString()]
            callback(params)
        }
        
        if let d = delegate {
            d.colorSelected(selectedColor: indicator.color)
        }
    }
    
    open override func cancelTracking(with event: UIEvent?) {
        super.cancelTracking(with: event)
        shrinkIndicator()
        
        // send event back to js
        if let callback = self.onColorChange {
            let params: [String : Any] = ["color": indicator.color.toHexString()]
            callback(params)
        }
        
        if let d = delegate {
            d.colorSelected(selectedColor: indicator.color)
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
