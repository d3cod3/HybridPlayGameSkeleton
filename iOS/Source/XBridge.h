
#include "cocos2d.h"

class XBridge {
    
public:
    static void goToHPInstructions();
    
    static void changeSensorPosition();
    static void saveCalibrationData();
    
    static bool getTXL();
    static bool getTXR();
    static bool getTYL();
    static bool getTYR();
    static bool getTZL();
    static bool getTZR();
    static int getIR();
    
};

