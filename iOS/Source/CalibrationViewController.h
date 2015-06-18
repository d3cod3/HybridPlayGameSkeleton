//
//  CalibrationViewController.h
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 21/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SAMultisectorControl.h"

@interface CalibrationViewController : UIViewController{
    
    NSUserDefaults          *defaults;
    NSNotificationCenter    *nc;
    
    UIBarButtonItem         *revealButtonItem;
    
    SAMultisectorSector     *sectorX;
    SAMultisectorSector     *sectorY;
    SAMultisectorSector     *sectorZ;
    
    int             actualSensorPosition;
    int             calibAX, calibAY, calibAZ, calibIR;
    
    NSArray *                   sensorPositionArray;
    
}

// -----------------------------------------------------------------------------
@property (assign, nonatomic) IBOutlet SAMultisectorControl *xSensor;
@property (assign, nonatomic) IBOutlet SAMultisectorControl *ySensor;
@property (assign, nonatomic) IBOutlet SAMultisectorControl *zSensor;

@property (assign, nonatomic) IBOutlet UIImageView *XTL;
@property (assign, nonatomic) IBOutlet UIImageView *XTR;
@property (assign, nonatomic) IBOutlet UIImageView *YTL;
@property (assign, nonatomic) IBOutlet UIImageView *YTR;
@property (assign, nonatomic) IBOutlet UIImageView *ZTL;
@property (assign, nonatomic) IBOutlet UIImageView *ZTR;

@property (assign, nonatomic) IBOutlet UISlider *sIR;

@property (assign, nonatomic) IBOutlet UIView *sensorPositionsView;
@property (assign, nonatomic) IBOutlet UITableView *sensorPositionsTableView;
@property (assign, nonatomic) IBOutlet UITableViewCell *sensorPositionsTableCell;


- (IBAction)saveCalibrationData:(id)sender;

@end
