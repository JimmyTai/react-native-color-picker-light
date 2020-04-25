//
//  RCTWhitePickerViewManager.m
//  color_picker
//
//  Created by Jimmy Tai on 2020/4/15.
//

#import <React/RCTViewManager.h>
 
@interface RCT_EXTERN_MODULE(RCTWhitePickerViewManager, RCTViewManager)

RCT_EXTERN_METHOD(showColor:(nonnull NSNumber *)dummy HexColor:(nonnull NSString *)hex)

RCT_EXPORT_VIEW_PROPERTY(onInit, RCTBubblingEventBlock)

RCT_EXPORT_VIEW_PROPERTY(onColorChange, RCTBubblingEventBlock)

RCT_EXPORT_VIEW_PROPERTY(type, NSString)
 
@end
