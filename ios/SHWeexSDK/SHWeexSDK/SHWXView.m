//
//  SHWXView.m
//  SHWeexSDK
//
//  Created by guo on 2017/6/9.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import "SHWXView.h"
#import "SHWXConfig.h"
@interface SHWXView ()

@property (nonatomic, strong) UIView * mviewWeexBack;
@property (nonatomic, strong) NSMutableDictionary* mdicResult;
@property (nonatomic, strong) WXSDKInstance *instance;
@property (nonatomic, strong) NSMutableDictionary *mdicWeexViews;
@property (nonatomic, strong) UIView * mviewCurrentWeex;

@end

@implementation SHWXView

- (instancetype)initWithFrame:(CGRect)frame withData:(NSDictionary *)data
{
    self = [super initWithFrame:frame];
    if (self) {
        self.frame = frame;
        [self initviewWeexBack];
        [self renderWeexViewWithDict:[self SHDetermineLoadType:data]];
    }
    return self;
}
#pragma mark - init -
/**
 初始化背景
 */
-(void)initviewWeexBack{
    self.mviewWeexBack = [[UIView alloc] initWithFrame:CGRectMake(0, 0, CGRectGetWidth(self.frame), CGRectGetHeight(self.frame))];
    [self addSubview:self.mviewWeexBack];
}
#pragma mark - Methods -
/**
 判断加载类型
 @param mdicValueSent weexpage
 */
-(NSMutableDictionary *)SHDetermineLoadType:(NSDictionary *)mdicValueSent{
     NSMutableArray * marrWeexPages = [NSMutableArray arrayWithArray:[SHSaveData SHGetDataWithKey:WEEXPAGES]];
    self.mdicResult = [NSMutableDictionary dictionary];
    __weak __typeof__(self) weakSelf = self;
    [marrWeexPages enumerateObjectsUsingBlock:^(id  _Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
        NSDictionary * mdicValue = [NSDictionary dictionaryWithDictionary:[marrWeexPages objectAtIndex:idx]];
        if ([[mdicValue objectForKey:@"page"] isEqualToString:[mdicValueSent objectForKey:@"page"]]) {
            NSString * mstrFileName = [NSString stringWithFormat:@"weex_%@.js",[mdicValue objectForKey:@"page"]];
            // 检查本地是否存在该文件 存在加载本地文件 不存在加载远端连接
            if ([SHFileProcessing SHCheckFileISThere:mstrFileName]) {
                // 判断文件最后修改时间 防止被篡改
                NSString * mstrLastTime = [NSString stringWithFormat:@"%@",[mdicValue objectForKey:@"modtime"]];
                if ([mstrLastTime isEqualToString:[SHFileProcessing SHGetFileThelastModifyTime:mstrFileName]]) {
                    weakSelf.mdicResult = [NSMutableDictionary dictionaryWithObjectsAndKeys:[SHFileProcessing SHGetFilePathWithFileName:mstrFileName],@"url",[mdicValue objectForKey:@"h5"],@"h5", nil];
                }
            }else{
                weakSelf.mdicResult = [NSMutableDictionary dictionaryWithObjectsAndKeys:[mdicValue objectForKey:@"url"],@"url",[mdicValue objectForKey:@"h5"],@"h5", nil];
            }
        }
    }];
    return weakSelf.mdicResult;
}

/**
 渲染weex页面

 @param dict 页面连接
 */
-(void)renderWeexViewWithDict:(NSMutableDictionary *)dict{
    NSString * mstrURL = [dict objectForKey:@"url"];
    NSString * mstrH5URL = [dict objectForKey:@"h5"];
    if ([self.mdicWeexViews objectForKey:mstrURL] && [[self.mdicWeexViews objectForKey:mstrURL] isKindOfClass:[UIView class]]) {
        [self SHloadWeexViewForKey:mstrURL];
    }else{
        _instance = [[WXSDKInstance alloc] init];
        _instance.viewController = self.currentViewController;
        CGFloat width = self.frame.size.width;
        _instance.frame = CGRectMake(self.frame.size.width-width, 0, width, self.frame.size.height);
        __weak typeof(self) weakSelf = self;
        _instance.onCreate = ^(UIView *view) {
            [weakSelf.mdicWeexViews setValue:view forKey:mstrURL];
            [weakSelf SHloadWeexViewForKey:mstrURL];
        };
        _instance.onFailed = ^(NSError *error) {
           
        };
        
        _instance.renderFinish = ^(UIView *view) {
          
            
        };
        _instance.updateFinish = ^(UIView *view) {
           
        };
        if (![mstrURL hasPrefix:@"http"]) {
            mstrURL = [NSString stringWithFormat:@"file://%@",mstrURL];
        }
        // options 传参
        [_instance renderWithURL:[NSURL URLWithString:mstrURL] options:[self SHWeexOptionsWithH5URL:mstrH5URL withURL:mstrURL] data:nil];
    }

}

/**
 weex页面传参
 @param mstrH5URL H5
 @param mstrURL weex连接
 @return 传参的数据
 */
-(NSMutableDictionary *)SHWeexOptionsWithH5URL:(NSString *)mstrH5URL withURL:(NSString *)mstrURL{
    NSString * mstrVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"];
    NSMutableDictionary * mdicValueOptions = [NSMutableDictionary dictionaryWithDictionary:[SHUniversalTool SHDictionaryWithURLQuery:mstrH5URL]];
    [mdicValueOptions setValue:mstrURL forKey:@"bundleUrl"];
    [mdicValueOptions setValue:@"ios" forKey:@"platform"];
    [mdicValueOptions setValue:mstrVersion forKey:@"version"];
    [mdicValueOptions removeObjectForKey:@"url"];
    return mdicValueOptions;
}

/**
 加载weex页面 通过key
 @param key 页面连接
 */
-(void)SHloadWeexViewForKey:(NSString *)key{
    self.mviewCurrentWeex = [_mdicWeexViews objectForKey:key];
    [self.mviewWeexBack insertSubview:self.mviewCurrentWeex atIndex:0];
    UIAccessibilityPostNotification(UIAccessibilityScreenChangedNotification, self.mviewCurrentWeex);
    for (int i=0; i<self.mviewWeexBack.subviews.count; i++) {
        UIView * mview = [self.mviewWeexBack.subviews objectAtIndex:i];
        if (i==0) {
            mview.hidden=NO;
        }else{
            mview.hidden=YES;
        }
    }
}


@end
