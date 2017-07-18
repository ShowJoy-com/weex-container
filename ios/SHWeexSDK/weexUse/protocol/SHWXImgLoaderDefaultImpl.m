//
//  SHWXImgLoaderDefaultImpl.m
//  SHWeexSDK
//
//  Created by guo on 2017/6/7.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import "SHWXImgLoaderDefaultImpl.h"
#import <PINRemoteImage/PINRemoteImageManager.h>

@implementation SHWXImgLoaderDefaultImpl

- (id<WXImageOperationProtocol>)downloadImageWithURL:(NSString *)url imageFrame:(CGRect)imageFrame userInfo:(NSDictionary *)userInfo completed:(void(^)(UIImage *image,  NSError *error, BOOL finished))completedBlock
{
    if ([url hasPrefix:@"//"]) {
        url = [@"http:" stringByAppendingString:url];
    }
    /*
     使用PINRemoteImageManager
     */
    __block SHWXImgLoaderDefaultImplProtocol *operation = [SHWXImgLoaderDefaultImplProtocol new];
    [[PINRemoteImageManager sharedImageManager] downloadImageWithURL:[NSURL URLWithString:url] completion:^(PINRemoteImageManagerResult * _Nonnull result) {
        if (completedBlock) {
            completedBlock(result.image, result.error, YES);
        }
    }];
    return (id<WXImageOperationProtocol>)operation;
    
    /*
     //使用SDWebImage
     return (id<WXImageOperationProtocol>)[[SDWebImageManager sharedManager] downloadImageWithURL:[NSURL URLWithString:url] options:0 progress:^(NSInteger receivedSize, NSInteger expectedSize) {
     
     } completed:^(UIImage *image, NSError *error, SDImageCacheType cacheType, BOOL finished, NSURL *imageURL) {
     if (completedBlock) {
     completedBlock(image, error, finished);
     }
     }];
     */
}


@end
