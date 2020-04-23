require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-color-picker-light"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "9.0" }
  s.swift_version = '5'
  s.requires_arc = true
  s.source       = { :git => "https://github.com/JimmyTai/react-native-color-picker-light.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,swift}"

  s.framework  = 'UIKit'

  s.dependency "React"
end
