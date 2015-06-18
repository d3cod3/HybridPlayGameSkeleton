package com.hybridplay.game.android;

import java.util.List;

import com.hybridplay.bluetoothle.HP_BTSensor;
import com.hybridplay.bluetoothle.HP_BTSensor.connectionStateEnum;
import com.hybridplay.shared.SharedFunctions;
import com.philjay.valuebar.ValueBar;
import com.philjay.valuebar.colors.RedToGreenFormatter;
import com.triggertrap.seekarc.SeekArc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class HP_Connect extends HP_Drawer{
	
	HP_Connect activity;

	////////////////////////////////////////////////////// WIFI
	WifiManager wifiManager;
	WifiConfiguration wifiConfig;
	boolean wifiReceiverRegistered = false;
	String WIFI_CONNECTING = "SENSOR.WIFI.CONNECTION";

	// WIFI OFF GUI
	TextView connectionText;
	ImageView connectionImage;
	Button connectionButton;
	Button refreshButton;
	
	// WIFI ON GUI
	ValueBar wifiStrength;
	TextView wifiSSID;
	TextView wifiBSSID;
	TextView ipAssigned;
	
	////////////////////////////////////////////////////// BLUETOOTH
	HP_BTSensor		btSensor;
	Handler 		btConnectedHandler = new Handler();
	Handler 		btReadingsHandler = new Handler();
	Runnable		btConnectedRunnable;
	Runnable		btReadingsRunnable;
	int				SENSOR_READINGS_MILLIS = 40;
	
	int fakeAX, fakeAY, fakeAZ, fakeIR;
	
	SharedPreferences prefs;
	SharedPreferences.Editor prefsEditor;
	
	AlertDialog sensorPositionChooser;
	SensorPositionsListAdapter sensorPositionsListAdapter = null;
	
	////////////////////////////////////////////////////// CALIBRATION GUI
	SeekArc btSensorX;
	SeekArc btSensorY;
	SeekArc btSensorZ;
	SeekBar btSensorIR;
	
	TextView sensorAngleX;
	TextView sensorAngleY;
	TextView sensorAngleZ;
	TextView sensorDistanceIR;
	
	ImageView sensorTXL, sensorTXR;
	ImageView sensorTYL, sensorTYR;
	ImageView sensorTZL, sensorTZR;
	
	Button	 sensorPositionBut;
	Button	 sensorAutoCalibrationBut;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hp_connect);
		
		activity = this;
		
		loadPreferences();
		
		btSensor = new HP_BTSensor(activity);

		if(SharedFunctions.BTorWIFI == 0){
			initBLUETOOTHGUI();
		}else if(SharedFunctions.BTorWIFI == 1){
			initWIFIGUI();
		}

		initTabs();
		initCalibrationGUI();

		if(SharedFunctions.BTorWIFI == 0){
			btSensor.connectSensorBLUETOOTH();
			initBTConnectionHandler();
		}else if(SharedFunctions.BTorWIFI == 1){
			connectSensorWIFI();
		}
		
	}
	
	public void loadPreferences(){
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefsEditor = prefs.edit();
	}
	
	public void initBTConnectionHandler(){
		btConnectedRunnable = new Runnable(){
		    public void run(){
		        //do something
		    	if(btSensor.mConnectionState==connectionStateEnum.isConnected){
		    		initBTReadingsHandler();
		    		onConnectionStateChange(btSensor.mConnectionState);
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
		    	updateCalibrationGUI();
		    	btReadingsHandler.postDelayed(this, SENSOR_READINGS_MILLIS);
		    }
		};
		btReadingsHandler.postDelayed(btReadingsRunnable, SENSOR_READINGS_MILLIS);
	}
	
	public void initBLUETOOTHGUI(){
		connectionText = (TextView)findViewById(R.id.connectionText);
		connectionImage = (ImageView)findViewById(R.id.connectionImage);
		
		connectionButton = (Button)findViewById(R.id.connectButton);
		connectionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	buttonScanOnClickProcess();
            }
        });
		
		wifiStrength = (ValueBar)findViewById(R.id.wifiStrength);
		wifiStrength.setMinMax(0, 100);
		wifiStrength.setInterval(1f);
		wifiStrength.setDrawBorder(false);
		wifiStrength.setTouchEnabled(false);
		wifiStrength.setDrawValueText(false);
		wifiStrength.setDrawMinMaxText(false);
		wifiStrength.setColorFormatter(new RedToGreenFormatter());
		
		wifiSSID = (TextView)findViewById(R.id.wifiSSID);
		wifiBSSID = (TextView)findViewById(R.id.wifiBSSID);
		ipAssigned = (TextView)findViewById(R.id.ipAssigned);
		
		updateSelectedItem(SharedFunctions.CONNECT_MENU_INDEX);
	}
	
	public void initWIFIGUI(){
		SharedFunctions.setWIFI(activity, true);
		
		connectionText = (TextView)findViewById(R.id.connectionText);
		connectionImage = (ImageView)findViewById(R.id.connectionImage);
		
		connectionButton = (Button)findViewById(R.id.connectButton);
		connectionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	wifiManager.startScan();
            }
        });
		
		refreshButton = (Button)findViewById(R.id.refreshButton);
		refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(activity, HP_Connect.class);
            	startActivity(intent);
            	finish();
            }
        });
		
		wifiStrength = (ValueBar)findViewById(R.id.wifiStrength);
		wifiStrength.setMinMax(0, 100);
		wifiStrength.setInterval(1f);
		wifiStrength.setDrawBorder(false);
		wifiStrength.setTouchEnabled(false);
		wifiStrength.setDrawValueText(false);
		wifiStrength.setDrawMinMaxText(false);
		wifiStrength.setColorFormatter(new RedToGreenFormatter());
		
		wifiSSID = (TextView)findViewById(R.id.wifiSSID);
		wifiBSSID = (TextView)findViewById(R.id.wifiBSSID);
		ipAssigned = (TextView)findViewById(R.id.ipAssigned);
		
		updateSelectedItem(SharedFunctions.CONNECT_MENU_INDEX);
	}
	
	public void initSensorPositionListAdapter(){
		sensorPositionsListAdapter = new SensorPositionsListAdapter(activity);
		
		for(int i=0;i<SharedFunctions.sensorPossiblePositions.length;i++){
			sensorPositionsListAdapter.addPosition(SharedFunctions.sensorPossiblePositions[i]);
		}
		sensorPositionsListAdapter.notifyDataSetChanged();
		
		sensorPositionChooser = new AlertDialog.Builder(activity)
		.setTitle("SELECT HYBRIDPLAY SENSOR POSITION...").setAdapter(sensorPositionsListAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which){
				// SAVE SENSOR POSITION FOR CALIBRATION
				prefsEditor.putString(SharedFunctions.HP_PREF_SENSOR_POSITION, String.valueOf(which));
				prefsEditor.commit();
				
				String actualSensorPos = sensorPositionsListAdapter.getPosition(which);
				SharedFunctions.showToast(activity, "You have selected "+actualSensorPos+" for HybridPlay sensor position.\nPlease verify is the correct one (in the real world!)");
			}
		})
		.setOnCancelListener(new DialogInterface.OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				sensorPositionChooser.dismiss();
			}
		})
		.create();
		
		sensorPositionChooser.show();
		
	}
	
	public void initCalibrationGUI(){
		btSensorX = (SeekArc) findViewById(R.id.sensorX);
		btSensorY = (SeekArc) findViewById(R.id.sensorY);
		btSensorZ = (SeekArc) findViewById(R.id.sensorZ);
		btSensorIR = (SeekBar) findViewById(R.id.sensorIR);
		
		sensorAngleX = (TextView) findViewById(R.id.sensorXReadings);
		sensorAngleY = (TextView) findViewById(R.id.sensorYReadings);
		sensorAngleZ = (TextView) findViewById(R.id.sensorZReadings);
		sensorDistanceIR = (TextView) findViewById(R.id.sensorIRReadings);
		
		sensorTXL = (ImageView) findViewById(R.id.triggerXL);
		sensorTXR = (ImageView) findViewById(R.id.triggerXR);
		sensorTYL = (ImageView) findViewById(R.id.triggerYL);
		sensorTYR = (ImageView) findViewById(R.id.triggerYR);
		sensorTZL = (ImageView) findViewById(R.id.triggerZL);
		sensorTZR = (ImageView) findViewById(R.id.triggerZR);
		
		sensorPositionBut = (Button) findViewById(R.id.sensorPositionButton);
		sensorAutoCalibrationBut = (Button) findViewById(R.id.autoCalibrateButton);
		
		sensorPositionBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initSensorPositionListAdapter();
			}
		});
		
		sensorAutoCalibrationBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveCalibrationData();
			}
		});
		
	}
	
	public void updateCalibrationGUI(){
		// ACCELEROMETER
		if(fakeAX != btSensor.getCalibAccX() || fakeAY != btSensor.getCalibAccY() || fakeAZ != btSensor.getCalibAccZ()){
			btSensorX.setProgress(fakeAX);
			btSensorY.setProgress(fakeAY);
			btSensorZ.setProgress(fakeAZ);
			
			sensorAngleX.setText(String.valueOf(fakeAX));
			sensorAngleY.setText(String.valueOf(fakeAY));
			sensorAngleZ.setText(String.valueOf(fakeAZ));
		}
		
		// IR
		btSensorIR.setProgress(fakeIR);
		sensorDistanceIR.setText(String.valueOf(fakeIR+" cm."));
		
		////////////////////////////////// TRIGGERS
		// X
		if(btSensor.isTXL()){
			sensorTXL.setImageResource(R.drawable.izquierda_on);
		}else{
			sensorTXL.setImageResource(R.drawable.izquierda_off);
		}
		if(btSensor.isTXR()){
			sensorTXR.setImageResource(R.drawable.derecha_on);
		}else{
			sensorTXR.setImageResource(R.drawable.derecha_off);
		}
		// Y
		if(btSensor.isTYL()){
			sensorTYL.setImageResource(R.drawable.izquierda_on);
		}else{
			sensorTYL.setImageResource(R.drawable.izquierda_off);
		}
		if(btSensor.isTYR()){
			sensorTYR.setImageResource(R.drawable.derecha_on);
		}else{
			sensorTYR.setImageResource(R.drawable.derecha_off);
		}
		// Z
		if(btSensor.isTZL()){
			sensorTZL.setImageResource(R.drawable.izquierda_on);
		}else{
			sensorTZL.setImageResource(R.drawable.izquierda_off);
		}
		if(btSensor.isTZR()){
			sensorTZR.setImageResource(R.drawable.derecha_on);
		}else{
			sensorTZR.setImageResource(R.drawable.derecha_off);
		}
		
		// UPDATE VALUES
		fakeAX = btSensor.getCalibAccX();
		fakeAY = btSensor.getCalibAccY();
		fakeAZ = btSensor.getCalibAccZ();
		fakeIR = btSensor.getCalibIR();
		
	}
	
	public void saveCalibrationData(){
		prefsEditor.putInt(SharedFunctions.HP_PREF_SENSOR_X_CALIB, btSensor.getAccX());
		prefsEditor.putInt(SharedFunctions.HP_PREF_SENSOR_Y_CALIB, btSensor.getAccY());
		prefsEditor.putInt(SharedFunctions.HP_PREF_SENSOR_Z_CALIB, btSensor.getAccZ());
		prefsEditor.putInt(SharedFunctions.HP_PREF_SENSOR_IR_CALIB, btSensor.getIR());
		prefsEditor.commit();
	}

	public void initTabs(){

		TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
		tabHost.setup();
		
		setNewTab(activity,tabHost,"Connect Tab","Connect",R.id.tab1);
		setNewTab(activity,tabHost,"Calibration Tab","Calibration",R.id.tab2);

		tabHost.setCurrentTab(0);

		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				
			}
		});

	}
	
	private void setNewTab(Context context, TabHost tabHost, String tag, String title, int contentID ){
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(getTabIndicator(tabHost.getContext(), title)); // new function to inject our own tab layout
        tabSpec.setContent(contentID);
        tabHost.addTab(tabSpec);
    }

	@SuppressLint("InflateParams")
    private View getTabIndicator(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);
        return view;
    }

	public void connectSensorWIFI(){
		
		IntentFilter WiFiFilters = new IntentFilter();
		WiFiFilters.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mWifiScanReceiver,WiFiFilters);
		wifiReceiverRegistered = true;

		wifiConfig = new WifiConfiguration();
		
		wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		
		wifiConfig.SSID = "\"" + SharedFunctions.HP_SENSOR_SSID + "\"";
		wifiConfig.preSharedKey = "\""+ SharedFunctions.HP_SENSOR_PASSWD +"\"";

		wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		wifiManager.startScan();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if(SharedFunctions.BTorWIFI == 0){
			btSensor.onResume(activity);
		}else if(SharedFunctions.BTorWIFI == 1){
			IntentFilter WiFiFilters = new IntentFilter();
			WiFiFilters.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			registerReceiver(mWifiScanReceiver,WiFiFilters);
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		btSensor.onActivityResult(activity, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(SharedFunctions.BTorWIFI == 0){
			btSensor.onPause(activity);
		}else if(SharedFunctions.BTorWIFI == 1){
			if(wifiReceiverRegistered){
				unregisterReceiver(mWifiScanReceiver);
			}
		}
		
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
        btSensor.onDestroy(activity);
    }
	
	// BLUETOOTH SCAN	
	void buttonScanOnClickProcess(){
    	switch (btSensor.mConnectionState) {
		case isNull:
			btSensor.mConnectionState = connectionStateEnum.isScanning;
			onConnectionStateChange(btSensor.mConnectionState);
			btSensor.onConnectionStateChange(btSensor.mConnectionState);
			btSensor.scanLeDevice(true);
			break;
		case isToScan:
			btSensor.mConnectionState=connectionStateEnum.isScanning;
			onConnectionStateChange(btSensor.mConnectionState);
			btSensor.onConnectionStateChange(btSensor.mConnectionState);
			btSensor.scanLeDevice(true);
			break;
		case isScanning:
			
			break;

		case isConnecting:
			
			break;
		case isConnected:
			// REFRESH
			btSensor.bluetoothLeService.disconnect();
			btSensor.mBTHandler.postDelayed(btSensor.mDisonnectingOverTimeRunnable, btSensor.BT_SCAN_PERIOD);
			
			initBTConnectionHandler();
            
			btSensor.mConnectionState=connectionStateEnum.isScanning;
			onConnectionStateChange(btSensor.mConnectionState);
			btSensor.onConnectionStateChange(btSensor.mConnectionState);
			btSensor.scanLeDevice(true);
			break;
		case isDisconnecting:
			
			break;

		default:
			break;
		}
    }
    
    // WIFI SCAN
	private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context c, Intent intent) {
			String action = intent.getAction();
			
			String actualSSID = "";
			boolean alreadyConnected = false;
			
			if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
				WifiInfo info = wifiManager.getConnectionInfo();
				if(info!=null){
	                if(info.getSSID() != null){
	                	actualSSID = info.getSSID();
	                	if(actualSSID.equals(SharedFunctions.HP_SENSOR_SSID)){
	                		
	                		String ipAss = SharedFunctions.wifiIpAddress(activity);
	                		Log.d(WIFI_CONNECTING,"HYBRIDPLAY SENSOR ALREADY CONNECTED");
	                		
	                		int level = WifiManager.calculateSignalLevel(info.getRssi(), 100) + 1;
	                		findViewById(R.id.signalLayout).setVisibility(View.VISIBLE);
	                		findViewById(R.id.wifiDataLayout).setVisibility(View.VISIBLE);
	                		wifiSSID.setText(info.getSSID());
					        wifiBSSID.setText(info.getBSSID());
					        ipAssigned.setText(ipAss);
	                		
	                		wifiStrength.animate(0, level, 1000);
	                		
	                		alreadyConnected = true;
	                		connectionText.setText("SENSOR CONNECTED");
					        connectionText.setTextColor(getResources().getColor(R.color.hp_green));

					        connectionImage.setVisibility(View.GONE);
					        connectionButton.setVisibility(View.GONE);
					        refreshButton.setVisibility(View.VISIBLE);
					        refreshButton.setText("REFRESH");
					        
					        unregisterReceiver(mWifiScanReceiver);
					        wifiReceiverRegistered = false;
	                	}
	                }
				}
				
				if(!alreadyConnected) {
					List<ScanResult> mScanResults = wifiManager.getScanResults();
					
					for(int i = 0; i < mScanResults.size(); i++){
						if(mScanResults.get(i).SSID.equals(SharedFunctions.HP_SENSOR_SSID) && !actualSSID.equals(SharedFunctions.HP_SENSOR_SSID)){
							
							Log.d(WIFI_CONNECTING,"CONNECTING HYBRIDPLAY SENSOR");
							
							int netID = wifiManager.addNetwork(wifiConfig);
							wifiManager.disconnect();
					        wifiManager.enableNetwork(netID, true);
					        wifiManager.reconnect();

					        connectionText.setText("SENSOR CONNECTED");
					        connectionText.setTextColor(getResources().getColor(R.color.hp_green));

					        connectionImage.setVisibility(View.GONE);
					        connectionButton.setVisibility(View.GONE);
					        refreshButton.setVisibility(View.VISIBLE);
					        refreshButton.setText("REFRESH");
					        
					        int level = WifiManager.calculateSignalLevel(mScanResults.get(i).level, 100) + 1;
					        
					        findViewById(R.id.signalLayout).setVisibility(View.VISIBLE);
					        findViewById(R.id.wifiDataLayout).setVisibility(View.VISIBLE);
					        wifiSSID.setText(mScanResults.get(i).SSID);
					        wifiBSSID.setText(mScanResults.get(i).BSSID);
					        ipAssigned.setText("Assigning IP...");
					        
					        wifiStrength.animate(0, level, 1000);
					        
					        Log.d(WIFI_CONNECTING,mScanResults.get(i).BSSID+" - "+mScanResults.get(i).SSID);
					        
					        unregisterReceiver(mWifiScanReceiver);
					        wifiReceiverRegistered = false;
					        
					        break;
						}
					}
				}
			}
		}
	};

	
    // SERIAL COMUNICATION
    public void onConnectionStateChange(connectionStateEnum theConnectionState) { // Once connection state changes, this function will be called
		switch (theConnectionState) {
		case isConnected:
			connectionButton.setText("REFRESH");
			
			int level = WifiManager.calculateSignalLevel(btSensor.bluetoothLeService.mConnectionStrength, 100) + 1;
			findViewById(R.id.signalLayout).setVisibility(View.VISIBLE);
			findViewById(R.id.wifiDataLayout).setVisibility(View.VISIBLE);
	        wifiSSID.setText(btSensor.mBTDeviceName);
	        wifiBSSID.setText(btSensor.mBTDeviceAddress);
	        ipAssigned.setText("");
			
			wifiStrength.animate(0,level, 1000);
			
			connectionText.setText("SENSOR CONNECTED");
	        connectionText.setTextColor(getResources().getColor(R.color.hp_green));
	        connectionImage.setVisibility(View.GONE);
			
			break;
		case isConnecting:
			connectionButton.setText("CONNECTING");
			break;
		case isToScan:
			connectionButton.setText("SCAN");
			break;
		case isScanning:
			connectionButton.setText("SCANNING");
			break;
		case isDisconnecting:
			connectionButton.setText("DISCONNECTING");
			
			findViewById(R.id.signalLayout).setVisibility(View.GONE);
			findViewById(R.id.wifiDataLayout).setVisibility(View.GONE);
			
			connectionText.setText("SENSOR DISCONNECTED");
	        connectionText.setTextColor(getResources().getColor(R.color.hp_red));
	        connectionImage.setVisibility(View.VISIBLE);
			
			break;
		default:
			break;
		}
	}
    
    

}
