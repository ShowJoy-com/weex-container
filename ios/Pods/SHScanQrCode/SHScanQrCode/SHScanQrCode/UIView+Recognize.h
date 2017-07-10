//
//  UIView+Recognize.h
//  ScanQrCode
//
//  Created by guo on 16/7/18.
//  Copyright © 2016年 YunRuo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIView (Recognize)
/**
 *  调用生成二维码图片
 *
 *  @param successBlock block里返回的是image
 */
- (void)createImage:(CGSize)imageSize withUrl:(NSString *)mstrUrl successBlock:(void (^)(id image))successBlock;

@end
