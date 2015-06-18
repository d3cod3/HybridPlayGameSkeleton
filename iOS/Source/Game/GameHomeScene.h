//
//  GameHomeScene.h
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 14/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#ifndef HybridPlayGameSkeleton_GameHomeScene_h
#define HybridPlayGameSkeleton_GameHomeScene_h

#include "cocos2d.h"

class GameHomeScene : public cocos2d::Layer
{
public:
    
    static GameHomeScene* create();
    
    static cocos2d::Scene* createScene();
    
    virtual bool init();
    
    virtual void update(float dt);
    
    // callbacks
    void menuCloseCallback(cocos2d::Ref* pSender);
    void changeSensorPosition(cocos2d::Ref* pSender);
    void saveSensorCalibration(cocos2d::Ref* pSender);
    

    // VARIABLES
    
    cocos2d::Sprite *sTXL, *sTXR, *sTYL, *sTYR;
    cocos2d::Sprite *sTXLON, *sTXRON, *sTYLON, *sTYRON;
};

#endif
