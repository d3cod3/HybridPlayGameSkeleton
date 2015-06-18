//
//  CalibrationViewController.m
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 21/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import "CalibrationViewController.h"
#import "SWRevealViewController.h"

#import "SensorPositionList.h"

@interface CalibrationViewController ()

@end

@implementation CalibrationViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupNavBar];
    
    [self setupSensorUI];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)setupNavBar{
    SWRevealViewController *revealViewController = self.parentViewController.revealViewController;
    if( revealViewController ){
        
        UIButton *menuButton =  [UIButton buttonWithType:UIButtonTypeCustom];
        [menuButton setImage:[UIImage imageNamed:@"ic_launcher.png"] forState:UIControlStateNormal];
        [menuButton addTarget:self.parentViewController.revealViewController action:@selector( revealToggle: ) forControlEvents:UIControlEventTouchUpInside];
        [menuButton setFrame:CGRectMake(0, 0, 32, 32)];
        
        revealButtonItem = [[UIBarButtonItem alloc] initWithCustomView:menuButton];
        
        NSArray *actionLeftButtonItems = @[revealButtonItem];
        self.parentViewController.navigationItem.leftBarButtonItems = actionLeftButtonItems;
        
        [self.parentViewController.navigationController.navigationBar addGestureRecognizer: self.parentViewController.revealViewController.panGestureRecognizer];
    }
    
    [[UITabBar appearance] setTintColor:[UIColor whiteColor]];
    
}

#pragma mark - SENSOR
- (void)setupSensorUI{
    
    nc = [NSNotificationCenter defaultCenter];
    defaults = [NSUserDefaults standardUserDefaults];
    
    sensorPositionArray = @[@"HORIZONTAL BUT. LEFT",@"HORIZONTAL BUT. RIGHT",@"HORIZONTAL INVERTED BUT. LEFT",@"HORIZONTAL INVERTED BUT. RIGHT",@"VERTICAL BUT. DOWN",@"VERTICAL BUT. UP",@"VERTICAL INVERTED BUT. UP",@"VERTICAL INVERTED BUT. DOWN"];
    UINib *nib = [UINib nibWithNibName:@"SensorPositionList" bundle:nil];
    [self.sensorPositionsTableView registerNib:nib forCellReuseIdentifier:@"SensorPositionCell"];
    
    [self.sensorPositionsTableView setSeparatorStyle:UITableViewCellSeparatorStyleNone];
    self.sensorPositionsTableView.rowHeight = UITableViewAutomaticDimension;
    self.sensorPositionsTableView.estimatedRowHeight = 100.0;
    
    [self.sensorPositionsTableView reloadData];
    
    UIColor *redColor = [UIColor colorWithRed:255.0/255.0 green:0.0/255.0 blue:0.0/255.0 alpha:1.0];
    UIColor *blueColor = [UIColor colorWithRed:51.0/255.0 green:153.0/255.0 blue:255.0/255.0 alpha:1.0];
    UIColor *greenColor = [UIColor colorWithRed:160.0/255.0 green:191.0/255.0 blue:54.0/255.0 alpha:1.0];
    
    sectorX = [SAMultisectorSector sectorWithColor:redColor maxValue:360.0];
    sectorY = [SAMultisectorSector sectorWithColor:blueColor maxValue:360.0];
    sectorZ = [SAMultisectorSector sectorWithColor:greenColor maxValue:360.0];
    
    sectorX.tag = 0;
    sectorY.tag = 1;
    sectorZ.tag = 2;
    
    sectorX.startValue = 0.0;
    sectorX.endValue = 360.0;
    sectorY.startValue = 0.0;
    sectorY.endValue = 360.0;
    sectorZ.startValue = 0.0;
    sectorZ.endValue = 360.0;
    
    [self.xSensor addSector:sectorX];
    [self.ySensor addSector:sectorY];
    [self.zSensor addSector:sectorZ];
    
    [nc addObserver:self selector:@selector(updateSensorAX:) name:@"sensorAX" object:nil];
    [nc addObserver:self selector:@selector(updateSensorAY:) name:@"sensorAY" object:nil];
    [nc addObserver:self selector:@selector(updateSensorAZ:) name:@"sensorAZ" object:nil];
    [nc addObserver:self selector:@selector(updateSensorIR:) name:@"sensorIR" object:nil];
    
    [nc addObserver:self selector:@selector(updateTXL:) name:@"sensorTXL" object:nil];
    [nc addObserver:self selector:@selector(updateTXR:) name:@"sensorTXR" object:nil];
    [nc addObserver:self selector:@selector(updateTYL:) name:@"sensorTYL" object:nil];
    [nc addObserver:self selector:@selector(updateTYR:) name:@"sensorTYR" object:nil];
    [nc addObserver:self selector:@selector(updateTZL:) name:@"sensorTZL" object:nil];
    [nc addObserver:self selector:@selector(updateTZR:) name:@"sensorTZR" object:nil];
}

-(void)updateSensorAX:(NSNotification *)notification{
    NSString* sAX = notification.object;
    calibAX = [sAX intValue];
    
    [self.xSensor setEndValue:calibAX atSector:self.xSensor.sectors[0]];
    
}

-(void)updateSensorAY:(NSNotification *)notification{
    NSString* sAY = notification.object;
    calibAY = [sAY intValue];
    
    [self.ySensor setEndValue:calibAY atSector:self.ySensor.sectors[0]];
}

-(void)updateSensorAZ:(NSNotification *)notification{
    NSString* sAZ = notification.object;
    calibAZ = [sAZ intValue];
    
    [self.zSensor setEndValue:calibAZ atSector:self.zSensor.sectors[0]];
}

-(void)updateSensorIR:(NSNotification *)notification{
    NSString* ssIR = notification.object;
    calibIR = [ssIR intValue];
    
    [self.sIR setValue:calibIR animated:YES];
}

-(void)updateTXL:(NSNotification *)notification{
    NSString* data = notification.object;
    
    self.XTL.highlighted = [data boolValue];
}

-(void)updateTXR:(NSNotification *)notification{
    NSString* data = notification.object;
    
    self.XTR.highlighted = [data boolValue];
}

-(void)updateTYL:(NSNotification *)notification{
    NSString* data = notification.object;
    
    self.YTL.highlighted = [data boolValue];
}

-(void)updateTYR:(NSNotification *)notification{
    NSString* data = notification.object;
    
    self.YTR.highlighted = [data boolValue];
}

-(void)updateTZL:(NSNotification *)notification{
    NSString* data = notification.object;
    
    self.ZTL.highlighted = [data boolValue];
}

-(void)updateTZR:(NSNotification *)notification{
    NSString* data = notification.object;
    
    self.ZTR.highlighted = [data boolValue];
}

#pragma mark - Actions

- (IBAction)saveCalibrationData:(UIButton *)sender{
    [defaults setObject:[NSNumber numberWithInt:calibAX] forKey:@"hpg_sensor_x_calib"];
    [defaults setObject:[NSNumber numberWithInt:calibAY] forKey:@"hpg_sensor_y_calib"];
    [defaults setObject:[NSNumber numberWithInt:calibAZ] forKey:@"hpg_sensor_z_calib"];
    [defaults setObject:[NSNumber numberWithInt:calibIR] forKey:@"hpg_sensor_ir_calib"];
    
    [defaults synchronize];
    
    NSString *message = @"HybridPlay Sensor calibration saved!";
    
    [self showToast:message forSeconds:2];
}

- (IBAction)saveSensorPosition:(UIButton *)sender{
    
    self.sensorPositionsView.hidden = NO;
    
}

#pragma mark- TableViewDataSource

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    NSInteger nCount = [sensorPositionArray count];
    return nCount;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    static NSString *MyIdentifier = @"SensorPositionCell";
    SensorPositionList *cell = (SensorPositionList*)[tableView dequeueReusableCellWithIdentifier:MyIdentifier];
    
    cell.sensorPosLabel.text = [NSString stringWithFormat:@"%@", [sensorPositionArray objectAtIndex:indexPath.row]];
    
    if(indexPath.row == [[defaults objectForKey:@"hpg_sensor_position"] intValue]){
        [tableView selectRowAtIndexPath:indexPath animated:FALSE scrollPosition:UITableViewScrollPositionNone];
        [[tableView delegate] tableView:tableView didSelectRowAtIndexPath:indexPath];
        
    }
    
    return cell;
    
}


#pragma mark- TableView Delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    actualSensorPosition = (int)indexPath.row;
     
    [defaults setObject:[NSNumber numberWithInt:actualSensorPosition] forKey:@"hpg_sensor_position"];
    [defaults synchronize];
    
    self.sensorPositionsView.hidden = YES;
}

#pragma mark - Utils

- (void)showToast:(NSString*)message forSeconds:(int)sec {
    UIAlertView *toast = [[UIAlertView alloc] initWithTitle:nil
                                                    message:message
                                                   delegate:nil
                                          cancelButtonTitle:nil
                                          otherButtonTitles:nil, nil];
    [toast show];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, sec * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
        [toast dismissWithClickedButtonIndex:0 animated:YES];
    });
}

- (CGRect)getScreenFrameForCurrentOrientation {
    return [self getScreenFrameForOrientation:[UIApplication sharedApplication].statusBarOrientation];
}

- (CGRect)getScreenFrameForOrientation:(UIInterfaceOrientation)orientation {
    
    UIScreen *screen = [UIScreen mainScreen];
    CGRect fullScreenRect = screen.bounds;
    BOOL statusBarHidden = [UIApplication sharedApplication].statusBarHidden;
    
    //implicitly in Portrait orientation.
    if(orientation == UIInterfaceOrientationLandscapeRight || orientation == UIInterfaceOrientationLandscapeLeft){
        CGRect temp = CGRectZero;
        temp.size.width = fullScreenRect.size.height;
        temp.size.height = fullScreenRect.size.width;
        fullScreenRect = temp;
    }
    
    if(!statusBarHidden){
        CGFloat statusBarHeight = 20;//Needs a better solution, FYI statusBarFrame reports wrong in some cases..
        fullScreenRect.size.height -= statusBarHeight;
    }
    
    return fullScreenRect;
}

#pragma mark - Navigation



@end
