//
//  InstructionsViewController.h
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 20/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "InstructionSlideViewController.h"

@interface InstructionsViewController : UIViewController <UIPageViewControllerDataSource>{
    
    UIBarButtonItem *revealButtonItem;
    
}

// -----------------------------------------------------------------------------
@property (strong, nonatomic) UIPageViewController *pageViewController;
@property (strong, nonatomic) NSArray *pageImages;

@end
