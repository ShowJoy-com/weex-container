//
//  SHWXNetworkDefaultlmpl.m
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import "SHWXNetworkDefaultlmpl.h"
#import "SHWeexManager.h"
@implementation SHWXNetworkDefaultlmpl


/**
   这里可以针对weex网络请求的URL进行拦截修改
 */
- (NSURL *)rewriteURL:(NSString *)url
     withResourceType:(WXResourceType)resourceType
         withInstance:(WXSDKInstance *)instance
{
    NSURL *completeURL = [NSURL URLWithString:url];
    if ([completeURL isFileURL]) {
        return completeURL;
    } else {
        return [instance completeURL:url];
    }
}


@end
