//
//  SHWXAComponent.m
//  SHWeexSDK
//
//  Created by guo on 2017/6/6.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import "SHWXAComponent.h"

@interface SHWXAComponent()

@property (nonatomic, strong) UITapGestureRecognizer *tap;
@property (nonatomic, strong) NSString *href;

@end

@implementation SHWXAComponent
// 重写父类方法
- (instancetype)initWithRef:(NSString *)ref type:(NSString *)type styles:(NSDictionary *)styles attributes:(NSDictionary *)attributes events:(NSArray *)events weexInstance:(WXSDKInstance *)weexInstance
{
    self = [super initWithRef:ref type:type styles:styles attributes:attributes events:events weexInstance:weexInstance];
    if (self) {
        _tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(openURL)];
        _tap.delegate = self;
        if (attributes[@"href"]) {
            _href = attributes[@"href"];
        }
    }
    return self;
}

- (void)viewDidLoad
{
    [self.view addGestureRecognizer:_tap];
}

- (void)dealloc
{
    if (_tap.delegate) {
        _tap.delegate = nil;
    }
}


- (void)updateAttributes:(NSDictionary *)attributes
{
    if (attributes[@"href"]) {
        _href = attributes[@"href"];
    }
}
#pragma mark Action
/**
 打开链接
 */
- (void)openURL
{
    if (_href && [_href length] > 0) {
        
    }
}


#pragma mark - methods
/**
 获取当前的NavigationController
 @return NavigationController
 */
-(UINavigationController *)getCurrentNavigationController{
    return [self.weexInstance.viewController navigationController];
}

/**
 获取当前的ViewController
 @return ViewController
 */
-(UIViewController *)getCurrentViewController{
    return self.weexInstance.viewController;
}

#pragma mark gesture delegate

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer
{
    if ([gestureRecognizer isKindOfClass:[UITapGestureRecognizer class]] && [otherGestureRecognizer isKindOfClass:[UITapGestureRecognizer class]]) {
        return YES;
    }
    
    return NO;
}


@end
