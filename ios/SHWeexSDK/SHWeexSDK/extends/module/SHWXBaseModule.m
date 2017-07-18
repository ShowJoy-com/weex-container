//
//  SHWXBaseModule.m
//  SHWeexSDK
//
//  Created by guo on 2017/6/6.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import "SHWXBaseModule.h"
#import "SHWeexManager.h"
#import "SHWeexViewController.h"
@implementation SHWXBaseModule

@synthesize weexInstance;

WX_EXPORT_METHOD(@selector(loadPage:))
WX_EXPORT_METHOD(@selector(close:))
WX_EXPORT_METHOD(@selector(showTitleBar:))
WX_EXPORT_METHOD(@selector(setTitle:))
WX_EXPORT_METHOD(@selector(showLoading:))
WX_EXPORT_METHOD(@selector(showLoadingOnlyText:))
WX_EXPORT_METHOD(@selector(hideLoading))
WX_EXPORT_METHOD(@selector(fireGlobalEvent: data: callback:))
/**
 打开页面 weex或者webview
 @param mstrUrl 链接地址
 */
-(void)loadPage:(NSString *)mstrUrl{
    if (mstrUrl.length) {
        mstrUrl = [[SHWeexManager shareManagement] checkDomain:mstrUrl];
        //协议跳转
        if ([mstrUrl containsString:@"http://"] || [mstrUrl containsString:@"https://"]) {
            [[[SHWeexManager shareManagement] weexService] openUrl:[self getCurrentViewController] url:mstrUrl force:NO];
            return;
        }
        //其他跳转
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:mstrUrl]];
    }
}

/**
 打开连接
 @param controller 类名
 @param mstrUrl 连接
 @param force 强制用h5打开，不会转成weex 或者native
 */
-(void)openUrl:(UIViewController *)controller url:(NSString *)mstrUrl force:(BOOL)force{
    if (mstrUrl.length) {
        mstrUrl = [[SHWeexManager shareManagement] checkDomain:mstrUrl];
        //协议跳转
        if ([mstrUrl containsString:@"http://"] || [mstrUrl containsString:@"https://"]) {
            [[[SHWeexManager shareManagement] weexService] openUrl:[self getCurrentViewController] url:mstrUrl force:NO];
            return;
        }
        //其他跳转
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:mstrUrl]];
    }
    
}
/**
 关闭当前controller，返回到上一级页面
 @param strJson 可传值（暂无用）
 */
- (void)close:(NSString *)strJson
{
    [[self getCurrentNavigationController] popViewControllerAnimated:YES];
}
/**
 控制是否显示顶部TitleBar
 @param showTitleBar YES显示 NO隐藏
 */
- (void)showTitleBar:(BOOL)showTitleBar
{
    if (showTitleBar==YES) {
        [[self getCurrentNavigationController] setNavigationBarHidden:NO animated:YES];
    }else{
        [[self getCurrentNavigationController] setNavigationBarHidden:YES animated:YES];
    }
}

/**
 设置TitleBar标题
 @param strTitle 标题
 */
- (void)setTitle:(NSString *)strTitle
{
    //[[self getCurrentViewController] setTitle:strTitle];
}
/**
 显示Loading 仅是文案
 @param text 文案
 */
-(void)showLoadingOnlyText:(NSString *)text{
    [[[SHWeexManager shareManagement] weexService] showLoadingOnlyText:text view:[[self getCurrentViewController] view]];
}
/**
 显示Loading
 @param text 自定义loading文案
 */
-(void)showLoading:(NSString *)text{
    [[[SHWeexManager shareManagement] weexService] showLoading:text view:[[self getCurrentViewController] view]];
}
/**
 隐藏Loading
 */
-(void)hideLoading{
    [[[SHWeexManager shareManagement] weexService] hideLoading:[[self getCurrentViewController] view]];
}

/**
 实现反向传值功能 原理是把数据存起来 到达某个页面时 再取出来
 @param name 页面名称
 @param data 传值数据
 @param callback 回调通知weex页面
 */
-(void)fireGlobalEvent:(id)name data:(id)data callback:(WXModuleCallback)callback{
    NSDictionary * mdicValue = [NSDictionary dictionaryWithObjectsAndKeys:name,@"name",data,@"data", nil];
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:@"refreshFromeWeex"];
    [[NSUserDefaults standardUserDefaults] setValue:mdicValue forKeyPath:@"refreshFromeWeex"];
    [[NSUserDefaults standardUserDefaults]synchronize];
    callback(@"");
}

#pragma mark - methods
/**
 获取当前的NavigationController
 @return NavigationController
 */
-(UINavigationController *)getCurrentNavigationController{
    return [weexInstance.viewController navigationController];
}

/**
  获取当前的ViewController
 @return ViewController
 */
-(UIViewController *)getCurrentViewController{
    return weexInstance.viewController;
}

@end
