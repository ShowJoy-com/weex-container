//
//  SHSaveData.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SHSaveData : NSObject
/**
 获取数据
 */
+(id)SHGetDataWithKey:(NSString *)key;
/**
 保存数据
 */
+(void)SHSavaDataWithKey:(NSString *)key withData:(id)data;
/**
 删除数据
 */
+(void)SHDeleteDataWithKey:(NSString *)key;

@end
