
#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>

#import "DFBlunoManager.h"

@interface HPAppDelegate : NSObject < UIApplicationDelegate , DFBlunoDelegate>{
    
    NSNotificationCenter          *nc;
    NSUserDefaults                *defaults;
 
    unsigned char   HEADER;
    unsigned char   FOOTER;
    int             accX, accY, accZ, bat, IR;
    int             calibAX, calibAY, calibAZ, calibIR;
    BOOL            triggerXL, triggerXR, triggerYL, triggerYR, triggerZL, triggerZR, triggerIR;
    int             minIR;
    int             maxIR;
    
}

@property (nonatomic, strong) UIWindow*	window;
@property (nonatomic, assign) bool blockRotation;

@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;

// SENSOR
@property(strong,nonatomic) DFBlunoManager* blunoManager;
@property(strong,nonatomic) DFBlunoDevice* blunoDev;
@property(strong,nonatomic) NSMutableArray* aryDevices;
@property(assign,nonatomic) BOOL isSensorConnected;
@property(assign,nonatomic) NSString        *actualSSID;
@property(assign,nonatomic) NSString        *actualBSSID;
@property(assign,nonatomic) int             signalStrengthVal;

- (void)saveContext;
- (NSURL *)applicationDocumentsDirectory;

+ (HPAppDelegate *)sharedAppDelegate;

+ (BOOL)haveInternetConnection;

- (IBAction)actionSend:(id)sender;
- (IBAction)actionDidEnd:(id)sender;

- (int)getAccX;
- (int)getAccY;
- (int)getAccZ;
- (int)getIR;

- (int)getCalibAccX;
- (int)getCalibAccY;
- (int)getCalibAccZ;
- (int)getCalibIR;

@end
