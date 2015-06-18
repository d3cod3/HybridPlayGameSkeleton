
#include "XBridge.h"
#include "HPAppDelegate.h"

#import "SWRevealViewController.h"
#include "MenuViewController.h"
#include "InstructionsViewController.h"

#include "XBridgeTableViewController.h"


void XBridge::goToHPInstructions() {
    id sth = [[UIApplication sharedApplication] delegate];
    if ([sth isKindOfClass:[HPAppDelegate class]]) {
        HPAppDelegate *controller = (HPAppDelegate *)sth;
        
        UIStoryboard *mainStoryboard = [UIStoryboard storyboardWithName:@"Main" bundle: nil];
        
        InstructionsViewController *vc = (InstructionsViewController*)[mainStoryboard instantiateViewControllerWithIdentifier:@"InstructionsViewController"];
        UINavigationController *frontNavigationController=[[UINavigationController alloc] initWithRootViewController:vc];
        
        MenuViewController *rearViewController = (MenuViewController*)[mainStoryboard instantiateViewControllerWithIdentifier: @"MenuViewController"];
        
        SWRevealViewController *mainRevealController = [[SWRevealViewController alloc]  init];
        
        mainRevealController.rearViewController = rearViewController;
        mainRevealController.frontViewController= frontNavigationController;
        
        controller.window.rootViewController =nil;
        controller.window.rootViewController = mainRevealController;
        [controller.window makeKeyAndVisible];
        
    }
    
}

void XBridge::changeSensorPosition(){
    id sth = [[UIApplication sharedApplication] delegate];
    if ([sth isKindOfClass:[HPAppDelegate class]]) {
        
        XBridgeTableViewController *tempTableViewController = [[XBridgeTableViewController alloc] initWithNibName:nil bundle:nil];
        tempTableViewController.tableView.frame = CGRectMake(100, 100, 200, 100);
        
        // ADD THE TABLE VIEW AS SUBVIEW TO COCOS2D VIEWCONTROLLER ----- TODO
        //[viewController.view addSubview:tempTableViewController.tableView];
        
    }
}

void XBridge::saveCalibrationData(){
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    int calibAX = [[defaults objectForKey:@"hpg_sensor_actual_calib_x"] intValue];
    int calibAY = [[defaults objectForKey:@"hpg_sensor_actual_calib_y"] intValue];
    int calibAZ = [[defaults objectForKey:@"hpg_sensor_actual_calib_z"] intValue];
    int calibIR = [[defaults objectForKey:@"hpg_sensor_actual_ir"] intValue];
    
    [defaults setObject:[NSNumber numberWithInt:calibAX] forKey:@"hpg_sensor_x_calib"];
    [defaults setObject:[NSNumber numberWithInt:calibAY] forKey:@"hpg_sensor_y_calib"];
    [defaults setObject:[NSNumber numberWithInt:calibAZ] forKey:@"hpg_sensor_z_calib"];
    [defaults setObject:[NSNumber numberWithInt:calibIR] forKey:@"hpg_sensor_ir_calib"];
    
    [defaults synchronize];
    
    NSString *message = @"HybridPlay Sensor calibration saved!";
    
    UIAlertView *toast = [[UIAlertView alloc] initWithTitle:nil
                                                    message:message
                                                   delegate:nil
                                          cancelButtonTitle:nil
                                          otherButtonTitles:nil, nil];
    [toast show];
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 2 * NSEC_PER_SEC), dispatch_get_main_queue(), ^{
        [toast dismissWithClickedButtonIndex:0 animated:YES];
    });
}

bool XBridge::getTXL() {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if([[defaults objectForKey:@"hpg_sensor_txl"] intValue] == 0){
        return false;
    }else{
        return true;
    }
}

bool XBridge::getTXR() {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if([[defaults objectForKey:@"hpg_sensor_txr"] intValue] == 0){
        return false;
    }else{
        return true;
    }
}

bool XBridge::getTYL() {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if([[defaults objectForKey:@"hpg_sensor_tyl"] intValue] == 0){
        return false;
    }else{
        return true;
    }
}

bool XBridge::getTYR() {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if([[defaults objectForKey:@"hpg_sensor_tyr"] intValue] == 0){
        return false;
    }else{
        return true;
    }
}

bool XBridge::getTZL() {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if([[defaults objectForKey:@"hpg_sensor_txl"] intValue] == 0){
        return false;
    }else{
        return true;
    }
}

bool XBridge::getTZR() {
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    if([[defaults objectForKey:@"hpg_sensor_tzr"] intValue] == 0){
        return false;
    }else{
        return true;
    }
}

int XBridge::getIR(){
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    
    return [[defaults objectForKey:@"hpg_sensor_actual_ir"] intValue];
}
