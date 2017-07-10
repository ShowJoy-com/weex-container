//
//  SHUniversalTool.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

/*
 一些通用的方法供全局调用
 */

#import <Foundation/Foundation.h>

@interface SHUniversalTool : NSObject

/**
 比较版本号大小
 @param version 版本号
 @param currentVersion 系统版本号
 @return YES 传入版本号大于等于当前版本号 NO 传入版本号小于等于当前版本号
 */
+(BOOL)SHComparedVersion:(NSString *)version withTheCurrentVersion:(NSString *)currentVersion;
/**
 *  @brief  将url参数转换成NSDictionary
 *
 *  @param query url参数
 *
 *  @return NSDictionary
 */
+(NSDictionary *)SHDictionaryWithURLQuery:(NSString *)query;
@end
