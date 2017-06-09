//
//  SHWXPageModel.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/8.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SHWXPageModel : NSObject

@property (nonatomic,strong) NSString      *page; // 页面名称
@property (nonatomic,strong) NSString      *url;  // weex连接
@property (nonatomic,strong) NSString      *v;    // 最低支持版本号
@property (nonatomic,strong) NSString      *md5;  // weex文件MD5
@property (nonatomic,strong) NSString      *h5;   // weex对应的H5页面
@property (nonatomic,strong) NSString      *modtime; // weex文件最后修改时间

@end
