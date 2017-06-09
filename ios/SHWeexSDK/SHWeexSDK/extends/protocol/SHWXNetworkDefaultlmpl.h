//
//  SHWXNetworkDefaultlmpl.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//


/**
 针对weex网络请求的URL进行拦截
 */

#import <Foundation/Foundation.h>
#import <WeexSDK/WXModuleProtocol.h>
#import <WeexSDK/WXURLRewriteProtocol.h>

@interface SHWXNetworkDefaultlmpl : NSObject<WXURLRewriteProtocol,WXModuleProtocol>

@end
