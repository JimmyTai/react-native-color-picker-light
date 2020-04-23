//
//  ColorSquareView.swift
//  color_picker
//
//  Created by Jimmy Tai on 2020/4/13.
//

import UIKit

@IBDesignable
open class ColorSquareView: UIImageView {
  
  func getPixelColorAtPoint(point:CGPoint) -> UIColor{
      
      let pixel = UnsafeMutablePointer<CUnsignedChar>.allocate(capacity: 4)
      let colorSpace = CGColorSpaceCreateDeviceRGB()
      let bitmapInfo = CGBitmapInfo(rawValue: CGImageAlphaInfo.premultipliedLast.rawValue)
      let context = CGContext(data: pixel, width: 1, height: 1, bitsPerComponent: 8, bytesPerRow: 4, space: colorSpace, bitmapInfo: bitmapInfo.rawValue)
      var color: UIColor? = nil
      
      if let context = context {
          context.translateBy(x: -point.x, y: -point.y)
          self.layer.render(in: context)
          
          color = UIColor(red: CGFloat(pixel[0])/255.0,
                          green: CGFloat(pixel[1])/255.0,
                          blue: CGFloat(pixel[2])/255.0,
                          alpha: CGFloat(pixel[3])/255.0)
          
          pixel.deallocate()
      }
      return color ?? UIColor.white
  }
}

extension UIImage {
    static func gradientImageWithBounds(bounds: CGRect, colors: [CGColor],
                                        startPoint: CGPoint, endPoint: CGPoint) -> UIImage {
        let gradientLayer = CAGradientLayer()
        gradientLayer.frame = bounds
        gradientLayer.colors = colors
        gradientLayer.startPoint = startPoint
        gradientLayer.endPoint = endPoint
        
        UIGraphicsBeginImageContext(gradientLayer.bounds.size)
        gradientLayer.render(in: UIGraphicsGetCurrentContext()!)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image!
    }
}
