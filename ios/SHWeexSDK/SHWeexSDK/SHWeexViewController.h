//
//  SHWXView.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/9.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SHWeexViewDelegate <NSObject>



@end

@interface SHWeexViewController : UIViewController

-(instancetype)initWithFrame:(CGRect)frame;
/**
 加载weex页面

 @param data 传入字典
 @param ISDebug 是否测试模式 YES是
 */
-(void)SHloadWeexPageWithData:(NSDictionary *)data withDebug:(BOOL)ISDebug withController:(UIViewController *)controller;
@property (nonatomic, assign) id <SHWeexViewDelegate> delegate;

@end
