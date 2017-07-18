//
//  UIView+Recognize.m
//  ScanQrCode
//
//  Created by guo on 16/7/18.
//  Copyright © 2016年 YunRuo. All rights reserved.
//

#import "UIView+Recognize.h"

@implementation UIView (Recognize)

/**
 *  调用生成二维码图片
 *
 *  @param successBlock block里返回的是image
 */
- (void)createImage:(CGSize)imageSize withUrl:(NSString *)mstrUrl successBlock:(void (^)(id image))successBlock{
    if (mstrUrl.length > 0)
    {
        UIImage *image = [self createImageForString:mstrUrl imageSize:imageSize];
        if (successBlock)
        {
            successBlock(image);
        }
    }
}
- (UIImage *)createImageForString:(NSString *)string imageSize:(CGSize)size
{
    if (![string length])
    {
        return nil;
    }
    CIFilter *filter = [CIFilter filterWithName:@"CIQRCodeGenerator"];
    [filter setDefaults];
    NSData *data = [string dataUsingEncoding:NSUTF8StringEncoding];
    [filter setValue:data forKey:@"inputMessage"];
    CIImage *outputImage = [filter outputImage];
    UIImage * mimage = [self createNonInterpolatedUIImageFormCIImage:outputImage withSize:size];
    return mimage;
}

/**
 * 根据CIImage生成指定大小的UIImage
 *
 * @param image CIImage
 * @param size 图片宽度
 */
- (UIImage *)createNonInterpolatedUIImageFormCIImage:(CIImage *)image withSize:(CGSize) size
{
    CGRect extent = CGRectIntegral(image.extent);
    CGFloat scale = MIN(size.width/CGRectGetWidth(extent), size.height/CGRectGetHeight(extent));
    // 1.创建bitmap;
    size_t width = CGRectGetWidth(extent) * scale;
    size_t height = CGRectGetHeight(extent) * scale;
    CGColorSpaceRef cs = CGColorSpaceCreateDeviceGray();
    CGContextRef bitmapRef = CGBitmapContextCreate(nil, width, height, 8, 0, cs, (CGBitmapInfo)kCGImageAlphaNone);
    CIContext *context = [CIContext contextWithOptions:nil];
    CGImageRef bitmapImage = [context createCGImage:image fromRect:extent];
    CGContextSetInterpolationQuality(bitmapRef, kCGInterpolationNone);
    CGContextScaleCTM(bitmapRef, scale, scale);
    CGContextDrawImage(bitmapRef, extent, bitmapImage);
    // 2.保存bitmap到图片
    CGImageRef scaledImage = CGBitmapContextCreateImage(bitmapRef);
    CGContextRelease(bitmapRef);
    CGImageRelease(bitmapImage);
    return [UIImage imageWithCGImage:scaledImage];
}


@end
