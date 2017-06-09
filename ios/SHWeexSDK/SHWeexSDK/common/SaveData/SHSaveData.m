//
//  SHSaveData.m
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import "SHSaveData.h"

@implementation SHSaveData
/**
 获取数据
 */
+(id)SHGetDataWithKey:(NSString *)key{
    return [[NSUserDefaults standardUserDefaults]valueForKey:key];
}
/**
 保存数据
 */
+(void)SHSavaDataWithKey:(NSString *)key withData:(id)data{
    [[NSUserDefaults standardUserDefaults]setValue:data forKeyPath:key];
    [[NSUserDefaults standardUserDefaults]synchronize];
}
/**
 删除数据
 */
+(void)SHDeleteDataWithKey:(NSString *)key{
    [[NSUserDefaults standardUserDefaults] removeObjectForKey:key];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

@end
