//
//  MenuViewController.h
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 13/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "HPAppDelegate.h"

@interface SWUITableViewCell : UITableViewCell
@property (assign,nonatomic) IBOutlet UILabel *label;
@end

@interface MenuViewController : UITableViewController {
    
    HPAppDelegate*                appDelegate;
    NSManagedObjectContext        *context;
    NSNotificationCenter          *nc;
    
}

// -----------------------------------------------------------------------------

@end
