//
//  SHWXView.h
//  SHWeexSDK
//
//  Created by guo on 2017/6/9.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol SHWXViewDelegate <NSObject>



@end

@interface SHWXView : UIView

- (instancetype)initWithFrame:(CGRect)frame withData:(NSDictionary *)data;
@property (nonatomic, assign) id <SHWXViewDelegate> delegate;
@property (nonatomic, strong) UIViewController * currentViewController;

@end
