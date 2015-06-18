//
//  GameHomeScene.cpp
//  HybridPlayGameSkeleton
//
//  Created by n3m3da on 14/5/15.
//  Copyright (c) 2015 Apportable. All rights reserved.
//

#include "GameHomeScene.h"

#include "XBridge.h"

USING_NS_CC;

// ---------------------------------------------------------------
GameHomeScene* GameHomeScene::create(){
    GameHomeScene *hpscene = new GameHomeScene();
    if (hpscene && hpscene->init()){
        hpscene->autorelease();
        return hpscene;
    }
    CC_SAFE_DELETE(hpscene);
    return NULL;
}

// ---------------------------------------------------------------
Scene* GameHomeScene::createScene(){
    
    auto scene = Scene::create();
    
    // 'layer' is an autorelease object
    auto layer = GameHomeScene::create();
    
    // add layer as a child to scene
    scene->addChild(layer);
    
    // return the scene
    return scene;
}

// ---------------------------------------------------------------
bool GameHomeScene::init(){
    // super init first
    if ( !Layer::init() ){
        return false;
    }
    
    this->scheduleUpdate();
    
    Size visibleSize = Director::getInstance()->getVisibleSize();
    Vec2 origin = Director::getInstance()->getVisibleOrigin();
    
    // add a "close" icon to exit the progress.
    auto closeItem = MenuItemImage::create("CloseNormal.png","CloseSelected.png",CC_CALLBACK_1(GameHomeScene::menuCloseCallback, this));
    
    closeItem->setPosition(Vec2(origin.x + visibleSize.width - closeItem->getContentSize().width/2 , origin.y + closeItem->getContentSize().height/2));
    
    // MENU
    auto menu = Menu::create(closeItem, NULL);
    menu->setPosition(Vec2::ZERO);
    this->addChild(menu, 1);
    
    // SENSOR
    sTXL = Sprite::create("izquierda_off.png");
    sTXR = Sprite::create("derecha_off.png");
    sTYL = Sprite::create("arriba_off.png");
    sTYR = Sprite::create("abajo_off.png");
    sTXLON = Sprite::create("izquierda_on.png");
    sTXRON = Sprite::create("derecha_on.png");
    sTYLON = Sprite::create("arriba_on.png");
    sTYRON = Sprite::create("abajo_on.png");
    
    sTXL->setPosition( Vec2(28, visibleSize.height-90) );
    sTXR->setPosition( Vec2(120, visibleSize.height-90) );
    sTYL->setPosition( Vec2(74, visibleSize.height-42) );
    sTYR->setPosition( Vec2(74, visibleSize.height-136) );
    
    sTXLON->setPosition( Vec2(28, visibleSize.height-90) );
    sTXRON->setPosition( Vec2(120, visibleSize.height-90) );
    sTYLON->setPosition( Vec2(74, visibleSize.height-42) );
    sTYRON->setPosition( Vec2(74, visibleSize.height-136) );
    
    this->addChild(sTXL,0);
    this->addChild(sTXR,0);
    this->addChild(sTYL,0);
    this->addChild(sTYR,0);
    
    this->addChild(sTXLON,0);
    this->addChild(sTXRON,0);
    this->addChild(sTYLON,0);
    this->addChild(sTYRON,0);
    
    // add a background
    auto pSprite = Sprite::create("logo.png");
    pSprite->setPosition(Vec2(visibleSize.width/2 + origin.x, visibleSize.height/2 + origin.y));
    this->addChild(pSprite, 0);
    
    return true;
}

// ---------------------------------------------------------------
void GameHomeScene::update(float dt){
    //cocos2d::log( "update: %f", dt );
    if(XBridge::getTXL()){
        sTXLON->setVisible(true);
    }else{
        sTXLON->setVisible(false);
    }
    if(XBridge::getTXR()){
        sTXRON->setVisible(true);
    }else{
        sTXRON->setVisible(false);
    }
    if(XBridge::getTYL()){
        sTYLON->setVisible(true);
    }else{
        sTYLON->setVisible(false);
    }
    if(XBridge::getTYR()){
        sTYRON->setVisible(true);
    }else{
        sTYRON->setVisible(false);
    }
}

// ---------------------------------------------------------------
void GameHomeScene::changeSensorPosition(Ref* pSender){
    
}

// ---------------------------------------------------------------
void GameHomeScene::saveSensorCalibration(Ref* pSender){
    XBridge::saveCalibrationData();
}

// ---------------------------------------------------------------
void GameHomeScene::menuCloseCallback(Ref* pSender){
    Director::getInstance()->end();
    
    XBridge::goToHPInstructions();
}
