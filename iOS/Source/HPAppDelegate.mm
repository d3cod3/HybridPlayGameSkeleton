
#import "HPAppDelegate.h"

#import "AppDelegate.h"
// cocos2d application instance
static AppDelegate s_sharedApplication;


@interface HPAppDelegate()

@end

@implementation HPAppDelegate

#pragma mark - Class Methods

+ (void)initialize{
    
}

#pragma mark - UIApplicationDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions{
    
    [self setupDefaults];
    [self initBLUETOOTH];
    
    // Override point for customization after application launch.
    UIPageControl *pageControl = [UIPageControl appearance];
    pageControl.pageIndicatorTintColor = [UIColor lightGrayColor];
    pageControl.currentPageIndicatorTintColor = [UIColor blackColor];
    pageControl.backgroundColor = [UIColor colorWithRed:127.0f/255.0f green:127.0f/255.0f blue:127.0f/255.0f alpha:0.0f];
    pageControl.alpha = 0;
    
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    // Saves changes in the application's managed object context before the application terminates.
    [self saveContext];
}

-(NSUInteger)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window{
    if (self.blockRotation) {
        return UIInterfaceOrientationMaskLandscape;
    }
    return UIInterfaceOrientationMaskAll;
}

#pragma mark state preservation / restoration

- (BOOL)application:(UIApplication *)application shouldSaveApplicationState:(NSCoder *)coder
{
    return NO;
}

- (BOOL)application:(UIApplication *)application shouldRestoreApplicationState:(NSCoder *)coder
{
    return NO;
}

- (BOOL)application:(UIApplication *)application willFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    return NO;
}

#pragma mark -
#pragma mark Memory management

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application {
    /*
     Free up as much memory as possible by purging cached data objects that can be recreated (or reloaded from disk) later.
     */
}

- (void)dealloc {
    [super dealloc];
}

#pragma mark - Core Data stack

@synthesize managedObjectContext = _managedObjectContext;
@synthesize managedObjectModel = _managedObjectModel;
@synthesize persistentStoreCoordinator = _persistentStoreCoordinator;

- (NSURL *)applicationDocumentsDirectory {
    // The directory the application uses to store the Core Data store file. This code uses a directory named "com.hybridplay.HybridPlayCenter" in the application's documents directory.
    return [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
}

- (NSManagedObjectModel *)managedObjectModel {
    // The managed object model for the application. It is a fatal error for the application not to be able to find and load its model.
    if (_managedObjectModel != nil) {
        return _managedObjectModel;
    }
    NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"HybridPlayGameSkeleton" withExtension:@"momd"];
    _managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
    return _managedObjectModel;
}

- (NSPersistentStoreCoordinator *)persistentStoreCoordinator {
    // The persistent store coordinator for the application. This implementation creates and return a coordinator, having added the store for the application to it.
    if (_persistentStoreCoordinator != nil) {
        return _persistentStoreCoordinator;
    }
    
    // Create the coordinator and store
    
    _persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];
    NSURL *storeURL = [[self applicationDocumentsDirectory] URLByAppendingPathComponent:@"HybridPlayGameSkeleton.sqlite"];
    NSError *error = nil;
    NSString *failureReason = @"There was an error creating or loading the application's saved data.";
    if (![_persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:nil error:&error]) {
        // Report any error we got.
        NSMutableDictionary *dict = [NSMutableDictionary dictionary];
        dict[NSLocalizedDescriptionKey] = @"Failed to initialize the application's saved data";
        dict[NSLocalizedFailureReasonErrorKey] = failureReason;
        dict[NSUnderlyingErrorKey] = error;
        error = [NSError errorWithDomain:@"YOUR_ERROR_DOMAIN" code:9999 userInfo:dict];
        // Replace this with code to handle the error appropriately.
        // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
        NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
        abort();
    }
    
    return _persistentStoreCoordinator;
}


- (NSManagedObjectContext *)managedObjectContext {
    // Returns the managed object context for the application (which is already bound to the persistent store coordinator for the application.)
    if (_managedObjectContext != nil) {
        return _managedObjectContext;
    }
    
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if (!coordinator) {
        return nil;
    }
    _managedObjectContext = [[NSManagedObjectContext alloc] init];
    [_managedObjectContext setPersistentStoreCoordinator:coordinator];
    return _managedObjectContext;
}

#pragma mark - Core Data Saving support

- (void)saveContext {
    NSManagedObjectContext *managedObjectContext = self.managedObjectContext;
    if (managedObjectContext != nil) {
        NSError *error = nil;
        if ([managedObjectContext hasChanges] && ![managedObjectContext save:&error]) {
            // Replace this implementation with code to handle the error appropriately.
            // abort() causes the application to generate a crash log and terminate. You should not use this function in a shipping application, although it may be useful during development.
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
            abort();
        }
    }
}

#pragma mark - BLUETOOTH

- (void) setupDefaults {
    nc = [NSNotificationCenter defaultCenter];
    defaults = [NSUserDefaults standardUserDefaults];
    
}

- (void) initBLUETOOTH {
    self.blunoManager = [DFBlunoManager sharedInstance];
    self.blunoManager.delegate = self;
    self.aryDevices = [[NSMutableArray alloc] init];
    
    HEADER = 'H';
    FOOTER = 'F';
    
    minIR = 4;
    maxIR = 60;
}

#pragma mark- Actions

- (IBAction)connectHybridPlaySensor{
    [self.aryDevices removeAllObjects];
    
    [self.blunoManager scan];
}

- (IBAction)actionSend:(id)sender{
    //[self.txtSendMsg resignFirstResponder];
    if (self.blunoDev.bReadyToWrite){
        //NSString* strTemp = self.txtSendMsg.text;
        //NSData* data = [strTemp dataUsingEncoding:NSUTF8StringEncoding];
        //[self.blunoManager writeDataToDevice:data Device:self.blunoDev];
    }
}

- (IBAction)actionDidEnd:(id)sender{
    //[self.txtSendMsg resignFirstResponder];
}

#pragma mark- DFBlunoDelegate

-(void)bleDidUpdateState:(BOOL)bleSupported{
    if(bleSupported){
        [self.blunoManager scan];
    }
}
-(void)didDiscoverDevice:(DFBlunoDevice*)dev{
    BOOL bRepeat = NO;
    for(DFBlunoDevice* bleDevice in self.aryDevices){
        if ([bleDevice isEqual:dev]){
            bRepeat = YES;
            break;
        }
    }
    if (!bRepeat){
        [self.aryDevices addObject:dev];
        
        if([dev.name isEqualToString:@"hybridPLAY"]){
            
            if (self.blunoDev == nil){
                self.blunoDev = dev;
                [self.blunoManager connectToDevice:self.blunoDev];
            }else if ([dev isEqual:self.blunoDev]){
                if (!self.blunoDev.bReadyToWrite){
                    [self.blunoManager connectToDevice:self.blunoDev];
                }
            }else{
                if (self.blunoDev.bReadyToWrite){
                    [self.blunoManager disconnectToDevice:self.blunoDev];
                    self.blunoDev = nil;
                }
                [self.blunoManager connectToDevice:dev];
            }
            [self.blunoManager stop];
            
            self.actualSSID = dev.name;
            self.actualBSSID = dev.identifier;
            
            self.signalStrengthVal = 2 * (int)(dev.rssi.integerValue + 100); // 0 - 100
            
            self.isSensorConnected = true;
        }
        
    }
}
- (void)readyToCommunicate:(DFBlunoDevice*)dev{
    self.blunoDev = dev;
    self.isSensorConnected = true;
}
- (void)didDisconnectDevice:(DFBlunoDevice*)dev{
    self.isSensorConnected = false;
}
- (void)didWriteData:(DFBlunoDevice*)dev{
    
}
- (void)didReceiveData:(NSData*)data Device:(DFBlunoDevice*)dev{
    unsigned char * bytePtr = (unsigned char  * )[data bytes];
    
    NSInteger totalData = [data length] / sizeof(unsigned char);
    
    if(totalData >= 10){
        if(bytePtr[0] == HEADER){
            accX = [self readArduinoBinaryInt:bytePtr[1] andLeast:bytePtr[2]];
            accY = [self readArduinoBinaryInt:bytePtr[3] andLeast:bytePtr[4]];
            accZ = [self readArduinoBinaryInt:bytePtr[5] andLeast:bytePtr[6]];
            IR   = [self readArduinoBinaryInt:bytePtr[7] andLeast:bytePtr[8]];
            
            [self applySensorCalibration];
        }
    }
    
    
    [defaults setObject:[NSString stringWithFormat:@"%i",[self isTXL]] forKey:@"hpg_sensor_txl"];
    [defaults setObject:[NSString stringWithFormat:@"%i",[self isTXR]] forKey:@"hpg_sensor_txr"];
    [defaults setObject:[NSString stringWithFormat:@"%i",[self isTYL]] forKey:@"hpg_sensor_tyl"];
    [defaults setObject:[NSString stringWithFormat:@"%i",[self isTYR]] forKey:@"hpg_sensor_tyr"];
    [defaults setObject:[NSString stringWithFormat:@"%i",[self isTZL]] forKey:@"hpg_sensor_tzl"];
    [defaults setObject:[NSString stringWithFormat:@"%i",[self isTZR]] forKey:@"hpg_sensor_tzr"];
    [defaults setObject:[NSString stringWithFormat:@"%i",[self getCalibAccX]] forKey:@"hpg_sensor_actual_calib_x"];
    [defaults setObject:[NSString stringWithFormat:@"%i",[self getCalibAccY]] forKey:@"hpg_sensor_actual_calib_y"];
    [defaults setObject:[NSString stringWithFormat:@"%i",[self getCalibAccZ]] forKey:@"hpg_sensor_actual_calib_z"];
    [defaults setObject:[NSString stringWithFormat:@"%i",[self getCalibIR]] forKey:@"hpg_sensor_actual_ir"];
    [defaults synchronize];
    
    [nc postNotificationName:@"sensorAX" object:[NSString stringWithFormat:@"%i",[self getCalibAccX]]];
    [nc postNotificationName:@"sensorAY" object:[NSString stringWithFormat:@"%i",[self getCalibAccY]]];
    [nc postNotificationName:@"sensorAZ" object:[NSString stringWithFormat:@"%i",[self getCalibAccZ]]];
    [nc postNotificationName:@"sensorIR" object:[NSString stringWithFormat:@"%i",[self getCalibIR]]];
    
    [nc postNotificationName:@"sensorTXL" object:[NSString stringWithFormat:@"%i",[self isTXL]]];
    [nc postNotificationName:@"sensorTXR" object:[NSString stringWithFormat:@"%i",[self isTXR]]];
    [nc postNotificationName:@"sensorTYL" object:[NSString stringWithFormat:@"%i",[self isTYL]]];
    [nc postNotificationName:@"sensorTYR" object:[NSString stringWithFormat:@"%i",[self isTYR]]];
    [nc postNotificationName:@"sensorTZL" object:[NSString stringWithFormat:@"%i",[self isTZL]]];
    [nc postNotificationName:@"sensorTZR" object:[NSString stringWithFormat:@"%i",[self isTZR]]];
    
    //NSLog(@"%x - %x",bytePtr[1],bytePtr[2]);
    //NSLog(@"%i - %i - %i - %i",[self getAccX],[self getAccY],[self getAccZ],[self getIR]);
    
}

- (void) applySensorCalibration{
    
    int sp = 0;
    
    // GET sensor position from NSUserDefaults
    sp = [[defaults objectForKey:@"hpg_sensor_position"] intValue];
    
    int actualCenterX = [[defaults objectForKey:@"hpg_sensor_x_calib"] intValue];
    int actualCenterY = [[defaults objectForKey:@"hpg_sensor_y_calib"] intValue];
    int actualCenterZ = [[defaults objectForKey:@"hpg_sensor_z_calib"] intValue];
    
    // MAPPING/CALIBRATING SENSOR READINGS
    
    // endless positive only 0 - 360 range
    int centeredIN = 0;
    
    // X
    if(sp == 0 || sp == 3){
        centeredIN = 0;
    }else if(sp == 4 || sp == 7){
        centeredIN = 270;
    }else if(sp == 1 || sp == 2){
        centeredIN = 180;
    }else if(sp == 5 || sp == 6){
        centeredIN = 90;
    }
    if(([self getAccX]-(actualCenterX-centeredIN)) < 0){
        calibAX = [self getAccX] - (actualCenterX-centeredIN) + 360;
    }else{
        calibAX = [self getAccX] - (actualCenterX-centeredIN);
    }
    
    // Y
    centeredIN = 0;
    if(([self getAccY]-(actualCenterY-centeredIN)) < 0){
        calibAY = [self getAccY]-(actualCenterY-centeredIN) + 360;
    }else{
        calibAY = [self getAccY]-(actualCenterY-centeredIN);
    }
    
    // Z
    if(sp == 2){
        centeredIN = 180;
    }else{
        centeredIN = 0;
    }
    if(([self getAccZ]-(actualCenterZ-centeredIN)) < 0){
        calibAZ = [self getAccZ]-(actualCenterZ-centeredIN) + 360;
    }else{
        calibAZ = [self getAccZ]-(actualCenterZ-centeredIN);
    }
    
    // X TRIGGERS
    switch (sp){
        case 0: // HORIZONTAL BUT. LEFT (CENTER IN 0, RANGE 270 - 90)
            if(calibAX > 270 && calibAX < 340){
                triggerXL = true;
            }else{
                triggerXL = false;
            }
            
            if(calibAX > 20 && calibAX < 90){
                triggerXR = true;
            }else{
                triggerXR = false;
            }
            break;
        case 1: // HORIZONTAL BUT. RIGHT (CENTER IN 180, RANGE 90 - 270)
            if(calibAX > 90 && calibAX < 160){
                triggerXL = true;
            }else{
                triggerXL = false;
            }
            
            if(calibAX > 200 && calibAX < 270){
                triggerXR = true;
            }else{
                triggerXR = false;
            }
            break;
        case 2: // HORIZONTAL INVERTED BUT. LEFT (CENTER IN 180, RANGE 270 - 90)
            if(calibAX > 200 && calibAX < 270){
                triggerXL = true;
            }else{
                triggerXL = false;
            }
            
            if(calibAX > 90 && calibAX < 160){
                triggerXR = true;
            }else{
                triggerXR = false;
            }
            break;
        case 3: // HORIZONTAL INVERTED BUT. RIGHT (CENTER IN 0, RANGE 270 - 90)
            if(calibAX > 20 && calibAX < 90){
                triggerXL = true;
            }else{
                triggerXL = false;
            }
            
            if(calibAX > 270 && calibAX < 340){
                triggerXR = true;
            }else{
                triggerXR = false;
            }
            break;
        case 4: // VERTICAL BUT. DOWN (CENTER IN 270, RANGE 180 - 360)
            if(calibAX > 180 && calibAX < 250){
                triggerXL = true;
            }else{
                triggerXL = false;
            }
            
            if(calibAX > 290 && calibAX < 360){
                triggerXR = true;
            }else{
                triggerXR = false;
            }
            break;
        case 5: // VERTICAL BUT. UP (CENTER IN 90, RANGE 0 - 180)
            if(calibAX > 0 && calibAX < 70){
                triggerXL = true;
            }else{
                triggerXL = false;
            }
            
            if(calibAX > 110 && calibAX < 180){
                triggerXR = true;
            }else{
                triggerXR = false;
            }
            break;
        case 6: // VERTICAL INVERTED BUT. UP (CENTER IN 90, RANGE 0 - 180)
            if(calibAX > 110 && calibAX < 180){
                triggerXL = true;
            }else{
                triggerXL = false;
            }
            
            if(calibAX > 0 && calibAX < 70){
                triggerXR = true;
            }else{
                triggerXR = false;
            }
            break;
        case 7: // VERTICAL INVERTED BUT. DOWN (CENTER IN 270, RANGE 180 - 360)
            if(calibAX > 290 && calibAX < 360){
                triggerXL = true;
            }else{
                triggerXL = false;
            }
            
            if(calibAX > 180 && calibAX < 250){
                triggerXR = true;
            }else{
                triggerXR = false;
            }
            break;
    }
    
    // Y TRIGGERS
    if(sp == 0 || sp == 1 || sp == 4 || sp == 5){
        if(calibAY > 270 && calibAY < 340){
            triggerYL = true;
        }else{
            triggerYL = false;
        }
        
        if(calibAY > 20 && calibAY < 90){
            triggerYR = true;
        }else{
            triggerYR = false;
        }
    }else if(sp == 2 || sp == 3 || sp == 6 || sp == 7){
        if(calibAY > 270 && calibAY < 340){
            triggerYR = true;
        }else{
            triggerYR = false;
        }
        
        if(calibAY > 20 && calibAY < 90){
            triggerYL = true;
        }else{
            triggerYL = false;
        }
    }
    
    // Z TRIGGERS (Z AXE WILL NOT WORK WITH SENSOR IN VERTICAL POSITIONS)
    if(sp == 0 || sp == 1 || sp == 3){ // (CENTER IN 0, RANGE 270 - 90)
        if(calibAZ > 270 && calibAZ < 340){
            triggerZL = true;
        }else{
            triggerZL = false;
        }
        
        if(calibAZ > 20 && calibAZ < 90){
            triggerZR = true;
        }else{
            triggerZR = false;
        }
    }else if(sp == 2){ // (CENTER IN 180, RANGE 90 - 270)
        if(calibAZ > 90 && calibAZ < 160){
            triggerZL = true;
        }else{
            triggerZL = false;
        }
        
        if(calibAZ > 200 && calibAZ < 270){
            triggerZR = true;
        }else{
            triggerZR = false;
        }
    }
    
    // IR TRIGGERS
    calibIR = [self getIR];
    if(calibIR > minIR && calibIR < maxIR){
        triggerIR = true;
    }else{
        triggerIR = false;
    }
    
}

- (int)readArduinoBinaryInt:(unsigned char)least andLeast:(unsigned char)most{
    int val,endVal;
    val = least;
    endVal = most;
    if(endVal == 0){
        val = least;
    }else{
        val = ((endVal/255) * 180) + least - 75;
    }
    return val;
}

- (int)getAccX{
    if(accX >= -128 && accX <= -77){ // de 128 a 178
        return accX+255;
    }else if(accX >= -179 && accX <= -129){ // de 178 a 227
        return accX+179+178;
    }else if(accX >= -385 && accX <= -257){ // de 228 a 360
        return accX+385+227;
    }else{ // de 0 a 127
        return accX;
    }
}

- (int)getCalibAccX{
    return calibAX;
}

- (int)getAccY{
    if(accY >= -128 && accY <= -77){ // de 128 a 178
        return accY+255;
    }else if(accY >= -179 && accY <= -129){ // de 178 a 227
        return accY+179+178;
    }else if(accY >= -385 && accY <= -257){ // de 228 a 360
        return accY+385+227;
    }else{ // de 0 a 127
        return accY;
    }
}

- (int)getCalibAccY{
    return calibAY;
}

- (int)getAccZ{
    if(accZ >= -128 && accZ <= -77){ // de 128 a 178
        return accZ+255;
    }else if(accZ >= -179 && accZ <= -129){ // de 178 a 227
        return accZ+179+178;
    }else if(accZ >= -385 && accZ <= -257){ // de 228 a 360
        return accZ+385+227;
    }else{ // de 0 a 127
        return accZ;
    }
    
}

- (int)getCalibAccZ{
    return calibAZ;
}

- (int)getIR{
    return IR;
}

- (int)getCalibIR{
    return calibIR;
}

- (BOOL)isTXR{
    return triggerXR;
}

- (BOOL)isTXL{
    return triggerXL;
}

- (BOOL)isTYR{
    return triggerYR;
}

- (BOOL)isTYL{
    return triggerYL;
}

- (BOOL)isTZR{
    return triggerZR;
}

- (BOOL)isTZL{
    return triggerZL;
}

- (BOOL)isTIR{
    return triggerIR;
}

#pragma mark - App Delegate Utils

+ (HPAppDelegate *)sharedAppDelegate{
    return (HPAppDelegate *)[[UIApplication sharedApplication] delegate];
}

+ (BOOL)haveInternetConnection{
    NSURL *scriptUrl = [NSURL URLWithString:@"http://games.hybridplay.com/empty.html"];
    NSData *data = [NSData dataWithContentsOfURL:scriptUrl];
    if(data){
        NSLog(@"Device is connected to the internet");
        return YES;
    }else{
        NSLog(@"Device is not connected to the internet");
        return NO;
    }
}

@end
