//
//  ColorIndicatorView.swift
//  color_picker
//
//  Created by Jimmy Tai on 2020/4/13.
//

import UIKit

@IBDesignable
open class ColorIndicatorView: UIView {
    
    @IBInspectable
    open var color: UIColor = .black {
        didSet {
            if oldValue != color {
                self.setNeedsDisplay()
            }
            
        }
    }
    
    override public init(frame: CGRect) {
        super.init(frame: frame)
        
        self.isOpaque = false
        self.isUserInteractionEnabled = false
    }
    
    required public init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
    }
    
    override open func draw(_ rect: CGRect) {
        guard let context = UIGraphicsGetCurrentContext() else {
            return
        }
        let center = CGPoint(x: self.bounds.midX, y: self.bounds.midY)
        let radius = self.bounds.midX
        
        // Fill it:
        context.addArc(center: center, radius: radius - 5.0, startAngle: 0.0, endAngle: 2.0 * .pi, clockwise: true)
        UIColor.white.setFill()
        context.setShadow(offset: CGSize(width: 0, height: 0), blur: 5.0, color: UIColor.hexColor(str: "#e0e0e0").cgColor)
        context.fillPath()
        
        // Fill it:
        context.addArc(center: center, radius: radius - 9.0, startAngle: 0.0, endAngle: 2.0 * .pi, clockwise: true)
        self.color.setFill()
        context.fillPath()
    }
}
