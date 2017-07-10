# iOS二维码扫描和生成
备注：该项目主要实现二维码扫描和生成的功能，使用的是系统方法，定位于轻量级的二维码扫描和生成。

#### 如何引入项目

1. Podfile

   ```objective-c
   source 'https://github.com/CocoaPods/Specs.git'
   platform :ios, '7.0'
   target 'TargetName' do
   pod 'SHScanQrCode', '~> 0.0.1'
   end
   ```

2. copy文件夹SHScanQrCode到项目中

#### 具体调用实现

1. 二维码生成

   ```objective-c
   /*
   CGSize：传入生成图片的尺寸
   URL：传入生成二维码的连接
   产出图片
   */
   [self.view createImage:CGSizeMake(1080, 1080) withUrl:@"http://m.showjoy.com" successBlock:^(id image) {
      
   }];
   ```

2.二维码扫描

```objective-c
/*
setOverlayPickerViewWithLineImage:withSize传入扫描条图片以及尺寸
createBackBtnWithBackImage:withSize传入返回背景图片以及尺寸
ScanQrCodeSuncessBlock 扫描成功block
ScanQrCodeFailBlock 扫描返回block
ScanQrCodeCancleBlock 点击返回block
*/
ScanQrCodeViewController *qrcodevc = [[ScanQrCodeViewController alloc] init];
[qrcodevc setOverlayPickerViewWithLineImage:[UIImage imageNamed:@"LineSao"] withSize:CGSizeMake(240, 4)];
[qrcodevc createBackBtnWithBackImage:[UIImage imageNamed:@"SaoBack"] withSize:CGSizeMake(40, 40)];
qrcodevc.ScanQrCodeSuncessBlock = ^(ScanQrCodeViewController *aqrvc,NSString *qrString){
    
};
qrcodevc.ScanQrCodeFailBlock = ^(ScanQrCodeViewController *aqrvc){
    
};
qrcodevc.ScanQrCodeCancleBlock = ^(ScanQrCodeViewController *aqrvc){
    [self dismissViewControllerAnimated:YES completion:^{
        
    }];
};
[self presentViewController:qrcodevc animated:YES completion:^{
    
}];
```