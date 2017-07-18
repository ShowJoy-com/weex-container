//
//  SHWXImgLoaderDefaultImpl.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

/*
 重写图片加载，这里使用的是PINRemoteImage 可以根据自己需要换成其他的 这里给出两种想法一种 PINRemoteImage 一种SDwebImage
 */

#import <Foundation/Foundation.h>
#import <WeexSDK/WXModuleProtocol.h>
#import <WeexSDK/WXImgLoaderProtocol.h>
#import "SHWXImgLoaderDefaultImplProtocol.h"

@interface SHWXImgLoaderDefaultImpl : NSObject<WXImgLoaderProtocol, WXModuleProtocol>

@end
