//
//  ScanQrCodeViewController.m
//  ScanQrCode
//
//  Created by guo on 16/6/16.
//  Copyright © 2016年 YunRuo. All rights reserved.
//
#import "ScanQrCodeViewController.h"
#import <AVFoundation/AVFoundation.h>
//设备宽/高/坐标

#define kDeviceWidth [UIScreen mainScreen].bounds.size.width
#define KDeviceHeight [UIScreen mainScreen].bounds.size.height
#define KDeviceFrame [UIScreen mainScreen].bounds
#define BEI6 (kDeviceWidth/375)
#define kReaderViewWidth (kDeviceWidth-35*BEI6*2)
#define kReaderViewHeight (kDeviceWidth-35*BEI6*2)
#define kLineMinY (KDeviceHeight-(kDeviceWidth-35*BEI6*2))/2
#define kLineMaxY kLineMinY+kReaderViewHeight
#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]

@interface ScanQrCodeViewController () <AVCaptureMetadataOutputObjectsDelegate>

@property (nonatomic, strong) AVCaptureSession                 *qrSession;//回话
@property (nonatomic, strong) AVCaptureVideoPreviewLayer       *qrVideoPreviewLayer;//读取
@property (nonatomic, strong) UIImageView                      *line;//交互线
@property (nonatomic, strong) NSTimer                          *lineTimer;//交互线控制

@end

@implementation ScanQrCodeViewController




- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
  
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.view.backgroundColor = [UIColor whiteColor];
    [self initUI];
    [self startSYQRCodeReading];
}

- (void)dealloc
{
    if (_qrSession) {
        [_qrSession stopRunning];
        _qrSession = nil;
    }
    
    if (_qrVideoPreviewLayer) {
        _qrVideoPreviewLayer = nil;
    }
    
    if (_line) {
        _line = nil;
    }
    
    if (_lineTimer)
    {
        [_lineTimer invalidate];
        _lineTimer = nil;
    }
}
#pragma mark - Delegate
#pragma mark 输出代理方法
//此方法是在识别到QRCode，并且完成转换
//如果QRCode的内容越大，转换需要的时间就越长
- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputMetadataObjects:(NSArray *)metadataObjects fromConnection:(AVCaptureConnection *)connection
{
    //扫描结果
    if (metadataObjects.count > 0)
    {
        [self stopSYQRCodeReading];
        
        AVMetadataMachineReadableCodeObject *obj = metadataObjects[0];
        
        if (obj.stringValue && ![obj.stringValue isEqualToString:@""] && obj.stringValue.length > 0)
        {
            
            if ([obj.stringValue containsString:@"http"])
            {
                
                [self performSelectorOnMainThread:@selector(MainClick:) withObject:obj.stringValue waitUntilDone:YES];
                
            }
            else
            {
                if (self.ScanQrCodeFailBlock) {
                    self.ScanQrCodeFailBlock(self);
                }
            }
        }
        else
        {
            if (self.ScanQrCodeFailBlock) {
                self.ScanQrCodeFailBlock(self);
            }
        }
    }
    else
    {
        if (self.ScanQrCodeFailBlock) {
            self.ScanQrCodeFailBlock(self);
        }
    }
}

#pragma mark - Action
#pragma mark 交互事件
- (void)startSYQRCodeReading
{
    _lineTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 / 20 target:self selector:@selector(animationLine) userInfo:nil repeats:YES];
    
    [self.qrSession startRunning];
    
}

- (void)stopSYQRCodeReading
{
    if (_lineTimer)
    {
        [_lineTimer invalidate];
        _lineTimer = nil;
    }
    
    [self.qrSession stopRunning];
    
}

//取消扫描
- (void)cancleSYQRCodeReading
{
    [self stopSYQRCodeReading];
    
    if (self.ScanQrCodeCancleBlock)
    {
        self.ScanQrCodeCancleBlock(self);
    }
    
}
-(void)MainClick:(NSString *)str{
    
    if (self.ScanQrCodeSuncessBlock) {
        self.ScanQrCodeSuncessBlock(self,str);
    }
    
}

#pragma mark - Init

- (void)createBackBtnWithBackImage:(UIImage *)backImage withSize:(CGSize)size
{
    UIButton *btn = [UIButton buttonWithType:UIButtonTypeCustom];
    [btn setFrame:CGRectMake(10, 23.5, size.width, size.height)];
    [btn setImage:backImage forState:UIControlStateNormal];
    [btn addTarget:self action:@selector(cancleSYQRCodeReading) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:btn];
}

- (void)initUI
{
    
    self.qrSession = nil;
    NSError *error;
    
    AVCaptureDevice *captureDevice = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    
    AVCaptureDeviceInput *input = [AVCaptureDeviceInput deviceInputWithDevice:captureDevice error:&error];
    if (!input) {
        NSLog(@"%@", [error localizedDescription]);
        return;
    }
    
    self.qrSession = [[AVCaptureSession alloc] init];
    [self.qrSession addInput:input];
    
    // 读取质量，质量越高，可读取小尺寸的二维码
    if ([self.qrSession canSetSessionPreset:AVCaptureSessionPreset1920x1080])
    {
        [self.qrSession setSessionPreset:AVCaptureSessionPreset1920x1080];
    }
    else if ([self.qrSession canSetSessionPreset:AVCaptureSessionPreset1280x720])
    {
        [self.qrSession setSessionPreset:AVCaptureSessionPreset1280x720];
    }
    else
    {
        [self.qrSession setSessionPreset:AVCaptureSessionPresetPhoto];
    }
    
    
    AVCaptureMetadataOutput *captureMetadataOutput = [[AVCaptureMetadataOutput alloc] init];
    [self.qrSession addOutput:captureMetadataOutput];
    
    dispatch_queue_t dispatchQueue;
    dispatchQueue = dispatch_queue_create("myQueue", NULL);
    [captureMetadataOutput setMetadataObjectsDelegate:self queue:dispatchQueue];
    [captureMetadataOutput setMetadataObjectTypes:[NSArray arrayWithObject:AVMetadataObjectTypeQRCode]];
    
    self.qrVideoPreviewLayer = [[AVCaptureVideoPreviewLayer alloc] initWithSession:self.qrSession];
    [self.qrVideoPreviewLayer setVideoGravity:AVLayerVideoGravityResizeAspectFill];
    [self.qrVideoPreviewLayer setFrame:self.view.layer.bounds];
    [self.view.layer addSublayer:self.qrVideoPreviewLayer];
    
    [self.qrSession startRunning];
    
}

- (void)setOverlayPickerViewWithLineImage:(UIImage *)lineImage withSize:(CGSize)size
{
    //画中间的基准线
    _line = [[UIImageView alloc] initWithFrame:CGRectMake((kDeviceWidth - size.width) / 2.0, kLineMinY, size.width, size.height)];
    [_line setImage:lineImage];
    [self.view addSubview:_line];
    
    //最上部view
    UIView* upView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kLineMinY)];//80
    upView.alpha = 0.5;
    upView.backgroundColor = [UIColor blackColor];
    [self.view addSubview:upView];
    
    //左侧的view
    UIView *leftView = [[UIView alloc] initWithFrame:CGRectMake(0, kLineMinY, (kDeviceWidth - kReaderViewWidth) / 2.0, kReaderViewHeight)];
    leftView.alpha = 0.5;
    leftView.backgroundColor = [UIColor blackColor];
    [self.view addSubview:leftView];
    
    //右侧的view
    UIView *rightView = [[UIView alloc] initWithFrame:CGRectMake(kDeviceWidth - CGRectGetMaxX(leftView.frame), kLineMinY, CGRectGetMaxX(leftView.frame), kReaderViewHeight)];
    rightView.alpha = 0.5;
    rightView.backgroundColor = [UIColor blackColor];
    [self.view addSubview:rightView];
    
    //底部view
    UIView *downView = [[UIView alloc] initWithFrame:CGRectMake(0, kLineMaxY, kDeviceWidth, kLineMinY)];
    downView.alpha = 0.5;
    downView.backgroundColor = [UIColor blackColor];
    [self.view addSubview:downView];
    
    UIView *scanCropView = [[UIView alloc] initWithFrame:CGRectMake(CGRectGetMaxX(leftView.frame),kLineMinY,self.view.frame.size.width - 2 * CGRectGetMaxX(leftView.frame), kReaderViewHeight)];
    scanCropView.layer.borderColor = UIColorFromRGB(0xffffff).CGColor;
    scanCropView.layer.borderWidth = 0.5;
    [self.view addSubview:scanCropView];
    
    //说明label
    UILabel *labIntroudction = [[UILabel alloc] init];
    labIntroudction.backgroundColor = [UIColor clearColor];
    labIntroudction.frame = CGRectMake(0, CGRectGetMinY(scanCropView.frame) - 30, kDeviceWidth, 12.5);
    labIntroudction.textAlignment = NSTextAlignmentCenter;
    labIntroudction.font = [UIFont boldSystemFontOfSize:12.0];
    labIntroudction.textColor = UIColorFromRGB(0xFFFFFF);
    labIntroudction.text = @"将二维码放入框内，即可自动扫描";
    [self.view addSubview:labIntroudction];
}

#pragma mark - Util
#pragma mark 上下滚动交互线

- (void)animationLine
{
    __block CGRect frame = _line.frame;
    
    static BOOL flag = YES;
    
    if (flag)
    {
        frame.origin.y = kLineMinY;
        flag = NO;
        
        [UIView animateWithDuration:1.0 / 20 animations:^{
            
            frame.origin.y += 5;
            _line.frame = frame;
            
        } completion:nil];
    }
    else
    {
        if (_line.frame.origin.y >= kLineMinY)
        {
            if (_line.frame.origin.y >= kLineMaxY - 12)
            {
                frame.origin.y = kLineMinY;
                _line.frame = frame;
                
                flag = YES;
            }
            else
            {
                [UIView animateWithDuration:1.0 / 20 animations:^{
                    
                    frame.origin.y += 5;
                    _line.frame = frame;
                    
                } completion:nil];
            }
        }
        else
        {
            flag = !flag;
        }
    }
    
    //NSLog(@"_line.frame.origin.y==%f",_line.frame.origin.y);
}



@end
