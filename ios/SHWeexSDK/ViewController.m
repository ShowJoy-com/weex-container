//
//  ViewController.m
//  SHWeexSDK
//
//  Created by guo on 2017/6/5.
//  Copyright © 2017年 YunRuo. All rights reserved.
//

#import "ViewController.h"
#import "SHWeexViewController.h"
#import "SHWeexManager.h"
@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    NSArray * marrTitles = [NSArray arrayWithObjects:@"weex页面",@"扫码测试", nil];
    for (int i=0; i<marrTitles.count; i++) {
        UIButton * mbtnWeex = [UIButton buttonWithType:UIButtonTypeSystem];
        mbtnWeex.frame=CGRectMake(100, 64+50*i, 100 , 50);
        [mbtnWeex setTitle:[marrTitles objectAtIndex:i] forState:UIControlStateNormal];
        [mbtnWeex addTarget:self action:@selector(ClickWeex:) forControlEvents:UIControlEventTouchUpInside];
        mbtnWeex.tag=i;
        [self.view addSubview:mbtnWeex];
    }
}

-(void)ClickWeex:(UIButton *)btn{
    if (btn.tag==0) {
        SHWeexViewController * vc = [[SHWeexViewController alloc] initWithFrame:CGRectMake(0, 64,[[UIScreen mainScreen] bounds].size.width , [[UIScreen mainScreen] bounds].size.height-64)];
        NSDictionary * mdicSentValue = [NSDictionary dictionaryWithObjectsAndKeys:@"http://ocr4ojfnd.bkt.clouddn.com/foo.js",@"url",@"",@"h5",@"login",@"page", nil];
          [vc SHloadWeexPageWithData:mdicSentValue withRelease:NO withController:self];
        [self.navigationController pushViewController:vc animated:YES];
    }else{
        [[SHWeexManager shareManagement] SHOpenQRCodeScanning:self];
    }
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
