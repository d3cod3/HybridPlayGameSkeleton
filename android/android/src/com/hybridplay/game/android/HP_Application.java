package com.hybridplay.game.android;

import android.app.Application;
import android.content.Context;

public class HP_Application extends Application {
	
	public static Context context;
	
	@Override
	public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

}
