//
//  ISHWeexService.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/16.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
typedef void(^ISHWeexRequestCallback)(id);
@interface ISHWeexService : NSObject
/**
 打开连接
 @param controller 类名
 @param url 连接
 @param force 强制用h5打开，不会转成weex 或者native
 */
-(void)openUrl:(UIViewController *)controller url:(NSString *)url force:(BOOL)force;
/**
 获取weex配置信息
 @return weex配置信息
 */
-(NSArray*)requestWeexConfig;
/**
 判断是否是release，正式包
 @return YES是正式包
 */
-(BOOL)isRelease;
/**
 获取app版本，用于判断weex 配置是否生效
 weex的配置可是设置某个版本以上才支持
 @return 版本号
 */
-(NSString *)getVersion;
/**
 获取默认的域名，用于支持相对地址
 @return 传入域名
 */
-(NSString *)getDefaultHost;
/**
 是否支持https
 @return YES支持
 */
-(BOOL)isSupportHttps;
/**
 埋点数据
 @param key 埋点名
 @param params 埋点传输数据
 */
-(void)onEvent:(NSString *)key params:(NSDictionary *)params;
/**
 显示Loading
 @param text 自定义loading文案
 */
-(void)showLoading:(NSString *)text view:(UIView *)view;
/**
 显示Loading 无文案
 */
-(void)showLoading:(UIView *)view;
/**
 只有文案展示
 
 @param text 文案
 @param view 显示view
 */
-(void)showLoadingOnlyText:(NSString *)text view:(UIView *)view;
/**
 隐藏Loading
 */
-(void)hideLoading:(UIView *)view;


@end
