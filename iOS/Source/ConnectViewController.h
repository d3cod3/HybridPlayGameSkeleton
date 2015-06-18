//
//  ConnectViewController.h
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 20/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "HPAppDelegate.h"

@interface ConnectViewController : UIViewController{
    
    HPAppDelegate                 *appDelegate;
    NSManagedObjectContext        *context;
    BOOL                          isSensorConnected;
    
    NSNotificationCenter          *nc;
    NSUserDefaults                *defaults;
    
    UIBarButtonItem               *revealButtonItem;
    
    NSString        *actualSSID;
    NSString        *actualBSSID;
    int             signalStrengthVal;
    
    BOOL            wifiORBT; // 0 - BLUETOOTH, 1 - WIFI
    
}

@property (assign,nonatomic) IBOutlet UIActivityIndicatorView *SearchIndicator;

@property (assign,nonatomic) IBOutlet UILabel         *sensorConnected;
@property (assign,nonatomic) IBOutlet UIImageView     *sensorImage;
@property (assign,nonatomic) IBOutlet UIButton        *connectButton;
@property (assign,nonatomic) IBOutlet UILabel         *wifiSSID;
@property (assign,nonatomic) IBOutlet UILabel         *wifiBSSID;
@property (assign,nonatomic) IBOutlet UIView          *signalStrength;
@property (assign,nonatomic) IBOutlet UILabel         *signalStrengthLabel;

// -----------------------------------------------------------------------------


@end
