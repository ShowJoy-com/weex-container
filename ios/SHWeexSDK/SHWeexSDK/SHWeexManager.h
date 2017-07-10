//
//  SHWXManagement.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ISHWeexService.h"
#import <UIKit/UIKit.h>
@interface SHWeexManager : NSObject

@property (nonatomic,strong) ISHWeexService *weexService;


+(SHWeexManager *)shareManagement;

/**
 初始化weex
 @param applocation UIApplication
 @param weexService ISHWeexService
 */
-(void)init:(UIApplication *)applocation weexService:(ISHWeexService *)weexService;
/**
 下载weex文件
 @param marrWeexPages weex数据
 */
-(void)SHDownloadFileOfWeex:(NSArray *)marrWeexPages;
/**
 通过Url获取weex数据
 @param mstrUrl 传入的weexURL
 @return 返回weex相关的字典数据
 */
-(NSDictionary *)SHGetDataPushToWeexController:(NSString *)mstrUrl;
/**
 打开二维码扫描
 */
-(void)SHOpenQRCodeScanning:(UIViewController *)controller;

@end
