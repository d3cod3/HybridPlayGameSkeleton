package com.hybridplay.game.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.hybridplay.bluetoothle.HP_BTSensor;
import com.hybridplay.bluetoothle.HP_BTSensor.connectionStateEnum;
import com.hybridplay.game.HPGame;

public class GameLauncher extends FragmentActivity implements AndroidFragmentApplication.Callbacks {
	
	GameResolverAndroid gameResolver;
	GameLauncher		activity;
	
	////////////////////////////////////////////////////// BLUETOOTH
	HP_BTSensor		btSensor;
	Handler 		btConnectedHandler = new Handler();
	Handler 		btReadingsHandler = new Handler();
	Runnable		btConnectedRunnable;
	Runnable		btReadingsRunnable;
	int				SENSOR_READINGS_MILLIS = 40;
	//////////////////////////////////////////////////////BLUETOOTH
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activity = this;
		gameResolver = new GameResolverAndroid(this);
		
		initHPSensor();
		
		GameFragment fragment = new GameFragment();
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.replace(android.R.id.content, fragment);
		trans.commit();
	}
	
	public void initHPSensor(){
		btSensor = new HP_BTSensor(this);
		
		btSensor.connectSensorBLUETOOTH();
		initBTConnectionHandler();
		// AUTOMATIC INVISIBLE SENSOR CONNECT
		btSensor.mConnectionState=connectionStateEnum.isScanning;
		btSensor.onConnectionStateChange(btSensor.mConnectionState);
		btSensor.scanLeDevice(true);
		
		gameResolver.btSensor = btSensor;
		
	}
	
	public void initBTConnectionHandler(){
		btConnectedRunnable = new Runnable(){
		    public void run(){
		    	if(btSensor.mConnectionState==connectionStateEnum.isConnected){
		    		initBTReadingsHandler();
		    	}else{
		    		btConnectedHandler.postDelayed(this, 1000);
		    	}
		    }
		};
		btConnectedHandler.postDelayed(btConnectedRunnable, 1000);
	}
	
	public void initBTReadingsHandler(){
		btReadingsRunnable = new Runnable(){
		    public void run(){
		    	// obtain readings from sensor
		    	btReadingsHandler.postDelayed(this, SENSOR_READINGS_MILLIS);
		    }
		};
		btReadingsHandler.postDelayed(btReadingsRunnable, SENSOR_READINGS_MILLIS);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		btSensor.onResume(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		btSensor.onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		btSensor.onPause(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		btConnectedHandler.removeCallbacks(btConnectedRunnable);
		btReadingsHandler.removeCallbacks(btReadingsRunnable);
		btSensor.onStop();
	}

	@Override
    protected void onDestroy() {
        super.onDestroy();
		btConnectedHandler.removeCallbacks(btConnectedRunnable);
        btReadingsHandler.removeCallbacks(btReadingsRunnable);
        btSensor.onDestroy(this);
	}
	
	@Override
	public void exit(){
		
	}
	
	private class GameFragment extends AndroidFragmentApplication{
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			return initializeForView(new HPGame(gameResolver));
		}
		
	}

}
