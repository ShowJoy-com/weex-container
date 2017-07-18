//
//  SHSpecialRootViwe.m
//  CloseliSDK_testbad
//
//  Created by guo on 2017/7/14.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import "SHSpecialRootViwe.h"

@interface SHSpecialRootViwe ()

@property(nonatomic,strong)UIView      *mviewBackRoot;

@end

@implementation SHSpecialRootViwe


-(id)initSpecialView:(UIView *)view{
    self = [super init];
    if (self) {
        self.frame = CGRectMake(0, 0, [[UIScreen mainScreen] bounds].size.width, [[UIScreen mainScreen] bounds].size.height);
        [self addSubview:view];
        UIButton * mbtnClose = [UIButton buttonWithType:UIButtonTypeSystem];
        mbtnClose.frame=CGRectMake(20, 20, 50 , 50);
        [mbtnClose setTitle:@"关闭" forState:UIControlStateNormal];
        [mbtnClose addTarget:self action:@selector(closeViewController) forControlEvents:UIControlEventTouchUpInside];
        [self addSubview:mbtnClose];
    }
    return self;
}

- (void)show{
    UIViewController *topVC = [self appRootViewController];
    [topVC.view addSubview:self];
}


- (UIViewController *)appRootViewController{
    UIViewController *appRootVC = [UIApplication sharedApplication].keyWindow.rootViewController;
    UIViewController *topVC = appRootVC;
    while (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    return topVC;
}


- (void)removeFromSuperview{
    [_mviewBackRoot removeFromSuperview];
    _mviewBackRoot = nil;
    [super removeFromSuperview];
}


- (void)willMoveToSuperview:(UIView *)newSuperview{
    if (newSuperview == nil) {
        return;
    }
    UIViewController *topVC = [self appRootViewController];
    
    if (!_mviewBackRoot) {
        _mviewBackRoot = [[UIView alloc] initWithFrame:topVC.view.bounds];
        _mviewBackRoot.backgroundColor=[UIColor whiteColor];
        
    }
    [topVC.view addSubview:_mviewBackRoot];
}

-(void)closeViewController{
    [self removeFromSuperview];
}


@end
