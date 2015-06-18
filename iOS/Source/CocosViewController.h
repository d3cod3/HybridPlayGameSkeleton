//
//  CocosViewController.h
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 13/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "HPAppDelegate.h"

#include "cocos2d.h"
#import "platform/ios/CCEAGLView-ios.h"

@interface CocosViewController : UIViewController {
    
    HPAppDelegate                 *appDelegate;
    
    cocos2d::Application *app;
    CCEAGLView *eaglView;
    
}



@end
