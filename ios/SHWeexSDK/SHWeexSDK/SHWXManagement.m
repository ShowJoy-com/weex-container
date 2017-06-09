//
//  SHWXManagement.m
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import "SHWXManagement.h"
#import "SHWXConfig.h"
@implementation SHWXManagement

+(SHWXManagement *)shareManagement{
    static SHWXManagement *shareManagement = nil;
    static dispatch_once_t predicate;
    dispatch_once(&predicate, ^{
        shareManagement = [[self alloc] init];
    });
    return shareManagement;
}
#pragma mark - Public Methods -
/**
 下载weex文件
 @param marrWeexPages weex数据
 */
-(void)SHDownloadFileOfWeex:(NSArray *)marrWeexPages{
    NSMutableArray * marrWeexPagesAvailable = [NSMutableArray arrayWithArray:[self SHFilterWeexPages:marrWeexPages]];
    if (marrWeexPagesAvailable.count>0) {
        // 每次下载前保存最新的weexPages
        [SHSaveData SHSavaDataWithKey:WEEXPAGES withData:marrWeexPagesAvailable];
        // 遍历下载weexpages
        [marrWeexPages enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSDictionary * mdicValue = [NSDictionary dictionaryWithDictionary:[marrWeexPagesAvailable objectAtIndex:idx]];
            if ([self SHCheckISDownLoadJswithDic:mdicValue]) {
                // 下载weex文件
                [self downLoadWeexFile:mdicValue];
            }
        }];
    }
}

#pragma mark - Request

/**
 下载weex文件

 @param dicValue weexpage
 */
- (void)downLoadWeexFile:(NSDictionary *)dicValue{
    // 下载的weex链接
    NSString * url = [NSString stringWithFormat:@"%@",[dicValue objectForKey:@"url"]];
    NSString * mstrRemoteMD5 = [NSString stringWithFormat:@"%@",[dicValue objectForKey:@"md5"]];
    NSString * mstrRemotePage = [NSString stringWithFormat:@"%@",[dicValue objectForKey:@"page"]];
    NSURL *URL = [NSURL URLWithString:url];
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    AFURLSessionManager *manager = [[AFURLSessionManager alloc] initWithSessionConfiguration:configuration];
    NSURLRequest *request = [NSURLRequest requestWithURL:URL];
    NSURLSessionDownloadTask * downloadTask = [manager downloadTaskWithRequest:request progress:^(NSProgress * _Nonnull downloadProgress) {
    } destination:^NSURL * _Nonnull(NSURL * _Nonnull targetPath, NSURLResponse * _Nonnull response) {
        // 返回下载后的保存路径 下载前先清除已存在的该文件
        NSString * mstrFileName = [NSString stringWithFormat:@"weex_%@.js",[dicValue objectForKey:@"page"]];
        [SHFileProcessing SHRemoveLocalFileWithFileName:mstrFileName];
        NSString * path = [SHFileProcessing SHGetFilePathWithFileName:mstrFileName];
        return [NSURL fileURLWithPath:path];
    } completionHandler:^(NSURLResponse * _Nonnull response, NSURL * _Nullable filePath, NSError * _Nullable error) {
        NSString * mstrPath = filePath.path;
        if (mstrPath) {
            // 校验文件MD5和远程MD5是否相同
            NSString * fileMd5 = [SHFileProcessing SHGetFileMD5WithPath:mstrPath];
            if (mstrRemoteMD5.length>0) {
                if ([fileMd5 isEqualToString:mstrRemoteMD5]) {
                    [self SHRecordFileThelastModifyTimeWithPath:mstrPath withDic:dicValue];
                }else{
                    NSString * mstrFileName = [NSString stringWithFormat:@"weex_%@.js",mstrRemotePage];
                    // MD5校验，如果不成功把已下载的JS文件删除
                    [SHFileProcessing SHRemoveLocalFileWithFileName:mstrFileName];
                    
                }
            }else{
                [self SHRecordFileThelastModifyTimeWithPath:mstrPath withDic:dicValue];
            }
        }
    }];
    [downloadTask resume];
}


#pragma mark - methods
/**
 过滤不符合要求的weex文件
 @param marrWeexPages weex文件
 @return 合格的weex文件
 */
-(NSMutableArray *)SHFilterWeexPages:(NSArray *)marrWeexPages{
    NSMutableArray * marrPages = [NSMutableArray array];
    /*
     判断一：判断本地是否已经保存WeexPages，对比新的WeexPages，内容相同返回空数组，内容不同进入下一步判断
     */
    NSMutableArray * marrPagesLocal = [NSMutableArray arrayWithArray:[SHSaveData SHGetDataWithKey:WEEXPAGES]];
    if (marrPagesLocal.count==0 || [marrPagesLocal isEqualToArray:marrWeexPages]) {
        return marrPages;
    }
    /*
     判断二：判断软件版本是否支持该weex页面
     */
    [marrWeexPages enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NSDictionary * mdicValue = [NSDictionary dictionaryWithDictionary:[marrPages objectAtIndex:idx]];
        if ([self SHComparedVersionIsSuport:mdicValue]==YES) {
            [marrPages addObject:mdicValue];
        }
    }];
    return marrPages;
}
/**
 判断该weex页面软件版本是否支持
 @param mdic weexpage
 @return YES支持 NO不支持
 */
-(BOOL)SHComparedVersionIsSuport:(NSDictionary *)mdic{
    NSString * mstrV = [mdic objectForKey:@"v"];
    return [SHUniversalTool SHComparedWithTheCurrentVersion:mstrV];
}

/**
 检查是否需要下载该文件

 @param dicvalue weexpage
 @return YES需要下载 NO不需要下载
 */
-(BOOL)SHCheckISDownLoadJswithDic:(NSDictionary *)dicvalue{
    NSString * mstrFileName = [NSString stringWithFormat:@"weex_%@.js",[dicvalue objectForKey:@"page"]];
    /* 
     检查沙盒中是否有该文件 有该文件验证MD5是否失效 没有直接下载
     */
    if ([SHFileProcessing SHCheckFileISThere:mstrFileName]) {
        NSString * fileMD5 = [SHFileProcessing SHGetFileMD5WithFileName:mstrFileName];
        NSString * remoteMD5 = [NSString stringWithFormat:@"%@",[dicvalue objectForKey:@"md5"]];
        /* 如果没录入远程MD5也可以下载 可以用于测试时不用录入MD5值 */
        if (remoteMD5.length==0) {
            return YES;
        }
        /*
         比较远程MD5和本地MD5是否一样 一样的话说明文件是最新的 不需要下载 反之下载
         */
        if ([fileMD5 isEqualToString:remoteMD5]) {
            return NO;
        }else{
            return YES;
        }
    }else{
        return YES;
    }
}
/**
 修改存储数据，添加文件最后修改时间
 
 @param mstrPath 文件路径
 @param dicValue weexpage
 */
-(void)SHRecordFileThelastModifyTimeWithPath:(NSString *)mstrPath withDic:(NSDictionary *)dicValue{
    NSMutableArray * marrWeexPages = [SHSaveData SHGetDataWithKey:WEEXPAGES];
    [marrWeexPages enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NSMutableDictionary * mdicValueChange = [NSMutableDictionary dictionaryWithDictionary:[marrWeexPages objectAtIndex:idx]];
        if ([mdicValueChange isEqual:dicValue]) {
            [mdicValueChange setValue:[SHFileProcessing SHGetFileThelastModifyTime:mstrPath] forKey:@"modtime"];
            [marrWeexPages replaceObjectAtIndex:idx withObject:mdicValueChange];
        }
        if (stop) {
            [SHSaveData SHSavaDataWithKey:WEEXPAGES withData:marrWeexPages];
        }
    }];
}


@end
