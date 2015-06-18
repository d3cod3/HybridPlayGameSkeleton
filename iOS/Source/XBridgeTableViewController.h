//
//  XBridgeTableViewController.h
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 10/6/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface XBridgeTableViewController : UITableViewController {
    
    NSUserDefaults              *defaults;
    
    NSArray *                   sensorPositionArray;
    int                         actualSensorPosition;
    
}



@end
