//
//  CocosViewController.m
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 13/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import "CocosViewController.h"

#include "GameHomeScene.h"

#pragma mark - CocosViewController

@interface CocosViewController()

@end

@implementation CocosViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self blockRotation];
    
    [self loadCocos2dx];
}

- (void)viewDidAppear:(BOOL)animated {
    NSNumber *value = [NSNumber numberWithInt:UIInterfaceOrientationLandscapeRight];
    [[UIDevice currentDevice] setValue:value forKey:@"orientation"];
}

- (void) blockRotation {
    appDelegate = [HPAppDelegate sharedAppDelegate];
    appDelegate.blockRotation = YES;
}

- (void) freeRotation {
    appDelegate = [HPAppDelegate sharedAppDelegate];
    appDelegate.blockRotation = NO;
}

- (void) loadCocos2dx{
    app = cocos2d::Application::getInstance();
    app->initGLContextAttrs();
    cocos2d::GLViewImpl::convertAttrs();
    
    // Init the CCEAGLView
    CGRect bounds = [self boundsForOrientation:UIInterfaceOrientationLandscapeRight];
    
    eaglView = [CCEAGLView viewWithFrame: bounds
                             pixelFormat: (NSString*)cocos2d::GLViewImpl::_pixelFormat
                             depthFormat: cocos2d::GLViewImpl::_depthFormat
                      preserveBackbuffer: NO
                              sharegroup: nil
                           multiSampling: NO
                         numberOfSamples: 0 ];
    
    [self.view addSubview:eaglView];
    
    // Enable or disable multiple touches
    [eaglView setMultipleTouchEnabled:NO];
    
    cocos2d::Director::getInstance()->setOpenGLView(cocos2d::GLViewImpl::createWithEAGLView(eaglView));
    
    app->run();
    
    [eaglView retain];
}

- (void)didReceiveMemoryWarning {
    cocos2d::Director::getInstance()->purgeCachedData();
    [super didReceiveMemoryWarning];
}

-(void)viewWillDisappear:(BOOL)animated{
    [self freeRotation];
}

-(void)viewDidDisappear:(BOOL)animated {
    //cocos2d::Director::getInstance()->end();
}

- (void)viewDidUnload {
    [eaglView release];
    [super viewDidUnload];
}

- (void)dealloc {
    [super dealloc];
}

#pragma mark - Utils

- (CGRect)boundsForOrientation:(UIInterfaceOrientation)orientation {
    
    CGFloat width   = [[UIScreen mainScreen] bounds].size.width;
    CGFloat height  = [[UIScreen mainScreen] bounds].size.height;
    
    CGRect bounds = CGRectZero;
    
    if (UIInterfaceOrientationIsLandscape(orientation)) {
        bounds.size = CGSizeMake(MAX(width, height), MIN(width, height));
    } else {
        bounds.size = CGSizeMake(MIN(width, height), MAX(width, height));
    }
    
    return bounds;
}

#pragma mark - Navigation


@end
