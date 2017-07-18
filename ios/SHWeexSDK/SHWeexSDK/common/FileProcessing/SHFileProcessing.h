//
//  SHFileProcessing.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <SystemConfiguration/CaptiveNetwork.h>
#include <CommonCrypto/CommonDigest.h>
@interface SHFileProcessing : NSObject

/**
 获取文件最后一次的修改时间
 @param mstrPath 文件路径
 @return 时间
 */
+(NSString *)SHGetFileThelastModifyTime:(NSString *)mstrPath;
/**
 通过文件路径获取文件的MD5值
 @param path 文件路径
 @return MD5值
 */
+(NSString *)SHGetFileMD5WithPath:(NSString*)path;
/**
 通过文件名获取文件的MD5值
 @param fileName 文件名
 @return MD5值
 */
+(NSString *)SHGetFileMD5WithFileName:(NSString*)fileName;

/**
 检查文件是否存在

 @param fileName 文件名
 @return YES存在 NO不存在
 */
+(BOOL)SHCheckFileISThere:(NSString *)fileName;

/**
 通过文件名移除本地文件

 @param fileName 文件名
 */
+(void)SHRemoveLocalFileWithFileName:(NSString *)fileName;

/**
 通过文件名获取文件路径

 @param fileName 文件名
 @return 文件路径
 */
+(NSString *)SHGetFilePathWithFileName:(NSString *)fileName;
/**
 将文件原存储的的地址 ，转换成新的存储地址
 */
+(void)SHMoveItemAtURL:(NSURL *)oldUrl toURLWithFileName:(NSString *)fileName;

@end
