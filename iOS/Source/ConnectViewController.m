//
//  ConnectViewController.m
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 20/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#import <SystemConfiguration/CaptiveNetwork.h>
#import <CoreLocation/CoreLocation.h>

#import "ConnectViewController.h"
#import "SWRevealViewController.h"

@interface ConnectViewController ()

@end

@implementation ConnectViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setupDefaults];
    [self getContext];
    
    wifiORBT = 0;
    
    /*if(wifiORBT == 1){
        [self checkWIFI];
    }else{
        [self initBLUETOOTH];
    }*/
    
    [self setupNavBar];
    [self setupUI];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Core Data

- (void)getContext{
    appDelegate = [HPAppDelegate sharedAppDelegate];
    context = appDelegate.managedObjectContext;
    
    isSensorConnected = appDelegate.isSensorConnected;
    
    self.wifiSSID.text = appDelegate.actualSSID;
    self.wifiBSSID.text = appDelegate.actualBSSID;
    
    actualSSID = appDelegate.actualSSID;
    actualBSSID = appDelegate.actualBSSID;
    signalStrengthVal = appDelegate.signalStrengthVal;
    
}

- (void) setupDefaults {
    nc = [NSNotificationCenter defaultCenter];
    defaults = [NSUserDefaults standardUserDefaults];
    
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

- (void)setupUI{
    
    if(isSensorConnected){
        self.sensorConnected.text = @"SENSOR CONNECTED";
        self.sensorConnected.textColor = [UIColor greenColor];
        self.sensorImage.alpha= 0.0f;
        self.connectButton.alpha = 0.0f;
        
        self.wifiSSID.alpha = 1.0f;
        self.wifiBSSID.alpha = 1.0f;
        
        self.signalStrength.hidden = false;
        self.signalStrengthLabel.hidden = false;
        self.signalStrengthLabel.alpha = 0.0f;
        
        CGRect screenRect = [[UIScreen mainScreen] bounds];
        CGFloat screenWidth = screenRect.size.width;
        
        self.signalStrength.translatesAutoresizingMaskIntoConstraints = YES;
        
        int maxViewWidth = screenWidth-32;
        
        [UIView animateWithDuration:.7 delay:0.5 options:UIViewAnimationOptionCurveEaseOut animations:^{
            
            if( ((screenWidth-32) * (signalStrengthVal/100.0)) <= maxViewWidth ){
                self.signalStrength.frame  = CGRectMake(16, 117,(screenWidth-32) * (signalStrengthVal/100.0),40);
            }else{
                self.signalStrength.frame  = CGRectMake(16, 117,maxViewWidth,40);
            }
            
            self.signalStrengthLabel.alpha = 1.0f;
            
            
        } completion:^(BOOL finished) {
            float red, green, blue;
            blue = 0.0;
            if(signalStrengthVal >= 100){
                signalStrengthVal = 99;
            }
            
            if(signalStrengthVal > 50){
                // green to yellow
                green = 1.0;
                red = ((50.0 - signalStrengthVal % 50) / 50.0);
                
            }else{
                // yellow to red
                red = 1.0;
                green = signalStrengthVal / 50.0;
            }
            [self.signalStrength setBackgroundColor: [UIColor colorWithRed:red green:green blue:blue alpha:1]];
        }];
        
    }else{
        self.sensorConnected.text = @"SENSOR DISCONNECTED";
        self.sensorConnected.textColor = [UIColor redColor];
        self.sensorImage.alpha= 1.0f;
        self.connectButton.alpha = 1.0f;
        
        self.wifiSSID.alpha = 0.0f;
        self.wifiBSSID.alpha = 0.0f;
    }
}

#pragma mark - WIFI

- (void) checkWIFI{
    isSensorConnected = false;
    actualSSID = [self currentWifiSSID];
    actualBSSID = [self currentWifiBSSID];
    
    NSLog(@"%@ - %@",actualSSID,actualBSSID);
    
    self.wifiSSID.text = actualSSID;
    self.wifiBSSID.text = actualBSSID;
    
    if([actualSSID isEqualToString:@"HYBRIDPLAY"]){
        isSensorConnected = true;
    }
    
    
}

- (NSString *)currentWifiBSSID {
    // Does not work on the simulator.
    NSString *bssid = nil;
    NSArray *ifs = (__bridge_transfer id)CNCopySupportedInterfaces();
    for (NSString *ifnam in ifs) {
        NSDictionary *info = (__bridge_transfer id)CNCopyCurrentNetworkInfo((__bridge CFStringRef)ifnam);
        if (info[@"BSSID"]) {
            bssid = info[@"BSSID"];
        }
    }
    return bssid;
}

- (NSString *)currentWifiSSID {
    // Does not work on the simulator.
    NSString *ssid = nil;
    NSArray *ifs = (__bridge_transfer id)CNCopySupportedInterfaces();
    for (NSString *ifnam in ifs) {
        NSDictionary *info = (__bridge_transfer id)CNCopyCurrentNetworkInfo((__bridge CFStringRef)ifnam);
        if (info[@"SSID"]) {
            ssid = info[@"SSID"];
        }
    }
    return ssid;
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex{
    if (alertView.tag == 121 && buttonIndex == 1){
        //code for opening settings app in iOS 8
        [[UIApplication sharedApplication] openURL:[NSURL  URLWithString:UIApplicationOpenSettingsURLString]];
    }
}

#pragma mark - Navigation


@end
