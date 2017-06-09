//
//  SHWXManagement.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SHWXManagement : NSObject

+(SHWXManagement *)shareManagement;
/**
 下载weex文件
 @param marrWeexPages weex数据
 */
-(void)SHDownloadFileOfWeex:(NSArray *)marrWeexPages;

@end
