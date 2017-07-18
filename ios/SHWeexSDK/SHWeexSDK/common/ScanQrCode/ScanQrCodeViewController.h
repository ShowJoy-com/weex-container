//
//  ScanQrCodeViewController.h
//  ScanQrCode
//
//  Created by guo on 16/6/16.
//  Copyright © 2016年 YunRuo. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ScanQrCodeViewController : UIViewController

@property (nonatomic, copy) void (^ScanQrCodeCancleBlock) (ScanQrCodeViewController *);//扫描取消
@property (nonatomic, copy) void (^ScanQrCodeSuncessBlock) (ScanQrCodeViewController *,NSString *);//扫描结果
@property (nonatomic, copy) void (^ScanQrCodeFailBlock) (ScanQrCodeViewController *);//扫描失败
- (void)setOverlayPickerViewWithLineImage:(UIImage *)lineImage withSize:(CGSize)size;
- (void)createBackBtnWithBackImage:(UIImage *)backImage withSize:(CGSize)size;
@end
