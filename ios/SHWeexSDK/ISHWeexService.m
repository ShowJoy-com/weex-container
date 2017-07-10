//
//  ISHWeexService.m
//  SHWeexSDK
//
//  Created by guo on 2017/6/16.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import "ISHWeexService.h"
#import "SHUniversalTool.h"
#import "SHWeexManager.h"
#import "SHWeexViewController.h"
@implementation ISHWeexService

/**
 打开连接
 @param controller 类名
 @param url 连接
 @param force 强制用h5打开，不会转成weex 或者native
 */
-(void)openUrl:(UIViewController *)controller url:(NSString *)url force:(BOOL)force{
    
    NSMutableDictionary * mdicSentValue = [NSMutableDictionary dictionaryWithDictionary:[SHUniversalTool SHDictionaryWithURLQuery:url]];
    NSString * mstrUrlhttp = [mdicSentValue objectForKey:@"url"];
    if (![mstrUrlhttp containsString:@"http"]) {
         mstrUrlhttp = [NSString stringWithFormat:@"%@%@",[[[SHWeexManager shareManagement] weexService] getDefaultHost],mstrUrlhttp];
        [mdicSentValue setValue:mstrUrlhttp forKey:@"url"];
    }
    SHWeexViewController * mweexVC = [[SHWeexViewController alloc] initWithFrame:CGRectMake(0, 0,[[UIScreen mainScreen] bounds].size.width , [[UIScreen mainScreen] bounds].size.height)];
    [mweexVC SHloadWeexPageWithData:mdicSentValue withDebug:NO withController:controller];
    [controller.navigationController pushViewController:mweexVC animated:YES];
    
}
/**
 获取weex配置信息
 @return weex配置信息
 */
-(NSArray*)requestWeexConfig{
    NSDictionary * mdicvalue = [NSDictionary dictionaryWithObjectsAndKeys:@"",@"h5",@"http://ocr4ojfnd.bkt.clouddn.com/foo.js",@"url",@"test",@"page",@"1.0.0",@"v",@"",@"md5",@"false",@"hideTitleBar", nil];
    return [NSArray arrayWithObjects:mdicvalue, nil];
}
/**
 判断是否是release，正式包
 @return YES是正式包
 */
-(BOOL)isRelease{
    return NO;
}
/**
 获取app版本，用于判断weex 配置是否生效
 weex的配置可是设置某个版本以上才支持
 @return 版本号
 */
-(NSString *)getVersion{
    NSString *mstrCurrentVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    return mstrCurrentVersion;
}
/**
 获取默认的域名，用于支持相对地址
 @return 传入域名
 */
-(NSString *)getDefaultHost{
    return @"http://www.showjoy.com";
}
/**
 是否支持https
 @return YES支持
 */
-(BOOL)isSupportHttps{
    return YES;
}
/**
 埋点数据
 @param key 埋点名
 @param params 埋点传输数据
 */
-(void)onEvent:(NSString *)key params:(NSDictionary *)params{
    
}
/**
 显示Loading
 @param text 自定义loading文案
 */
-(void)showLoading:(NSString *)text{
    
}
/**
 显示Loading 无文案
 */
-(void)showLoading{
    
    
}
/**
 隐藏Loading
 */
-(void)hideLoading{
    
}

@end
