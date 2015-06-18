package com.hybridplay.bluetoothle;

import java.util.ArrayList;
import java.util.List;

import com.hybridplay.shared.SharedFunctions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class HP_BTSensor {
	
	Context activity;
	
	//////////////////////////////////////////////////////BLUETOOTH
	private final static int REQUEST_ENABLE_BT = 1;
	String BLUETOOTH_CONNECTING = "SENSOR.BLUETOOTH.CONNECTION";
	String BLUETOOTH_RECEIVING = "SENSOR.BLUETOOTH.RECEIVING";
	
	BluetoothManager btManager;
	BluetoothAdapter btAdapter;
	public BluetoothLeService bluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private static BluetoothGattCharacteristic mSCharacteristic, mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;
	
	private boolean mBTScanning;
	public String mBTDeviceName;
	public String mBTDeviceAddress;
    
    private int mBTBaudrate=115200;	//set the default baud rate to 115200
	private String mBTPassword="AT+PASSWOR=DFRobot\r\n";
	private String mBTBaudrateBuffer = "AT+CURRUART="+mBTBaudrate+"\r\n";
	public enum connectionStateEnum{isNull, isScanning, isToScan, isConnecting , isConnected, isDisconnecting};
	public connectionStateEnum mConnectionState = connectionStateEnum.isNull;
	
	public Handler mBTHandler = new Handler();
	public boolean mBTConnected = false;

    // Stops scanning after 10 seconds.
    public long BT_SCAN_PERIOD = 10000;
    public static final String SerialPortUUID="0000dfb1-0000-1000-8000-00805f9b34fb";
	public static final String CommandUUID="0000dfb2-0000-1000-8000-00805f9b34fb";
    public static final String ModelNumberStringUUID="00002a24-0000-1000-8000-00805f9b34fb";
    
    //////////////////////////////////////////////////////SENSOR READINGS
    char HEADER = 'H';
    char FOOTER = 'F';
    int accX, accY, accZ, bat, IR;

    ////////////////////////////////////////////////////// CALIBRATION
    SharedPreferences prefs;
	SharedPreferences.Editor prefsEditor;
	int calibAX, calibAY, calibAZ, calibIR;
	boolean triggerXL, triggerXR, triggerYL, triggerYR, triggerZL, triggerZR, triggerIR;
	int minIR = 4;
	int maxIR = 60;
    
    public HP_BTSensor(Context context){
    	this.activity = context;
    	
    	prefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefsEditor = prefs.edit();
    }
    
    public void applySensorCalibration(){
		String sp;
		if(!prefs.getString(SharedFunctions.HP_PREF_SENSOR_POSITION,"").isEmpty()){
			sp = prefs.getString(SharedFunctions.HP_PREF_SENSOR_POSITION,"");
		}else{
			sp = "0";
		}
		
		int actualCenterX = prefs.getInt(SharedFunctions.HP_PREF_SENSOR_X_CALIB,0);
		int actualCenterY = prefs.getInt(SharedFunctions.HP_PREF_SENSOR_Y_CALIB,0);
		int actualCenterZ = prefs.getInt(SharedFunctions.HP_PREF_SENSOR_Z_CALIB,0);
		
		// MAPPING/CALIBRATING SENSOR READINGS
		
		// endless positive only 0 - 360 range
		int centeredIN = 0;
		
		// X
		if(Integer.parseInt(sp) == 0 || Integer.parseInt(sp) == 3){
			centeredIN = 0;
		}else if(Integer.parseInt(sp) == 4 || Integer.parseInt(sp) == 7){
			centeredIN = 270;
		}else if(Integer.parseInt(sp) == 1 || Integer.parseInt(sp) == 2){
			centeredIN = 180;
		}else if(Integer.parseInt(sp) == 5 || Integer.parseInt(sp) == 6){
			centeredIN = 90;
		}
		if((getAccX()-(actualCenterX-centeredIN)) < 0){
			calibAX = getAccX()-(actualCenterX-centeredIN) + 360;
		}else{
			calibAX = getAccX()-(actualCenterX-centeredIN);
		}
		
		// Y
		centeredIN = 0;
		if((getAccY()-(actualCenterY-centeredIN)) < 0){
			calibAY = getAccY()-(actualCenterY-centeredIN) + 360;
		}else{
			calibAY = getAccY()-(actualCenterY-centeredIN);
		}
		
		// Z
		if(Integer.parseInt(sp) == 2){
			centeredIN = 180;
		}else{
			centeredIN = 0;
		}
		if((getAccZ()-(actualCenterZ-centeredIN)) < 0){
			calibAZ = getAccZ()-(actualCenterZ-centeredIN) + 360;
		}else{
			calibAZ = getAccZ()-(actualCenterZ-centeredIN);
		}
		
		
		// X TRIGGERS
		switch (Integer.parseInt(sp)){
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
		if(Integer.parseInt(sp) == 0 || Integer.parseInt(sp) == 1 || Integer.parseInt(sp) == 4 || Integer.parseInt(sp) == 5){
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
		}else if(Integer.parseInt(sp) == 2 || Integer.parseInt(sp) == 3 || Integer.parseInt(sp) == 6 || Integer.parseInt(sp) == 7){
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
		if(Integer.parseInt(sp) == 0 || Integer.parseInt(sp) == 1 ||Integer.parseInt(sp) == 3){ // (CENTER IN 0, RANGE 270 - 90)
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
		}else if(Integer.parseInt(sp) == 2){ // (CENTER IN 180, RANGE 90 - 270)
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
		calibIR = getIR();
		if(calibIR > minIR && calibIR < maxIR){
			triggerIR = true;
		}else{
			triggerIR = false;
		}
		
	}
    
	boolean initiateBT(){
    	// verify BLE device support
    	if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
    	    return false;
    	}
		
		// Initializes a BLUETOOTH adapter. For API level 18 and above, get a
		// reference to BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
		btAdapter = bluetoothManager.getAdapter();
	
		// Checks if BLUETOOTH is supported on the device.
		if (btAdapter == null) {
			return false;
		}
		return true;
	}
    
    public void connectSensorBLUETOOTH(){
    	if(!initiateBT()){
    		SharedFunctions.showToast(activity,"Your device do not support Bluetooth Low Energy devices!");
			((Activity) activity).finish();
		}
    	
    	Intent gattServiceIntent = new Intent(activity, BluetoothLeService.class);
    	activity.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    	
    	serialBegin(115200); //set the Uart Baudrate on BLE chip to 115200
    }
    
    public void autoConnectBluetooth(BluetoothDevice device){
		if (device == null){
			return;
		}
		scanLeDevice(false);

		System.out.println("Device Name:"+device.getName() + "   " + "Device Name:" + device.getAddress());

		mBTDeviceName=device.getName().toString();
		mBTDeviceAddress=device.getAddress().toString();

		if(mBTDeviceName.equals("No Device Available") && mBTDeviceAddress.equals("No Address Available")){
			mConnectionState = connectionStateEnum.isToScan;
			onConnectionStateChange(mConnectionState);
		}else{
			if (bluetoothLeService.connect(mBTDeviceAddress)) {
				Log.d(BLUETOOTH_CONNECTING, "Connect request success");
				mConnectionState = connectionStateEnum.isConnecting;
				onConnectionStateChange(mConnectionState);
				mBTHandler.postDelayed(mConnectingOverTimeRunnable, BT_SCAN_PERIOD);
			}else{
				Log.d(BLUETOOTH_CONNECTING, "Connect request fail");
				mConnectionState = connectionStateEnum.isToScan;
				onConnectionStateChange(mConnectionState);
			}
		}
    }
    
    public void scanLeDevice(final boolean enable) {
		if(enable){
			if(!mBTScanning){
				mBTScanning = true;
				btAdapter.startLeScan(leScanCallback);
			}
		}else{
			if(mBTScanning){
				mBTScanning = false;
				btAdapter.stopLeScan(leScanCallback);
			}
		}
	}
    
    // AUTO CONNECT on scan
 	private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
 	    @Override
 	    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
 	    	((Activity) activity).runOnUiThread(new Runnable() {
 	            @Override
 	            public void run() {
 	                if(device.getName().equals(SharedFunctions.HP_BT_SENSOR_SSID)){
 	                	autoConnectBluetooth(device);
 	                }
 	            }
 	    	});
 	    }
 	};
    
 	// Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            System.out.println("mServiceConnection onServiceConnected");
        	bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                Log.e(BLUETOOTH_CONNECTING, "Unable to initialize Bluetooth");
            }else{
            	Log.e(BLUETOOTH_CONNECTING, "Bluetooth Service Initialized");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        	System.out.println("mServiceConnection onServiceDisconnected");
            bluetoothLeService = null;
        }
    };
    
    public Runnable mConnectingOverTimeRunnable=new Runnable(){

		@Override
		public void run() {
			if(mConnectionState==connectionStateEnum.isConnecting){
				mConnectionState=connectionStateEnum.isToScan;
			}
			onConnectionStateChange(mConnectionState);
			bluetoothLeService.close();
		}
	};
	
	public Runnable mDisonnectingOverTimeRunnable=new Runnable(){

		@Override
		public void run() {
			if(mConnectionState==connectionStateEnum.isDisconnecting){
				mConnectionState=connectionStateEnum.isToScan;
			}
			onConnectionStateChange(mConnectionState);
			bluetoothLeService.close();
		}
	};
	
	public void onConnectionStateChange(connectionStateEnum theConnectionState) { // Once connection state changes, this function will be called
		switch (theConnectionState) {
		case isConnected:
			Log.d(BLUETOOTH_CONNECTING,"CONNECT");
			break;
		case isConnecting:
			Log.d(BLUETOOTH_CONNECTING,"CONNECTING");
			break;
		case isToScan:
			Log.d(BLUETOOTH_CONNECTING,"SCAN");
			break;
		case isScanning:
			Log.d(BLUETOOTH_CONNECTING,"SCANNING");
			break;
		case isDisconnecting:
			Log.d(BLUETOOTH_CONNECTING,"DISCONNECTING");
			break;
		default:
			break;
		}
	}
	
	// Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("DefaultLocale")
		@Override
        public void onReceive(Context context, Intent intent) {
        	final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mBTConnected = true;
            	mBTHandler.removeCallbacks(mConnectingOverTimeRunnable);
            }else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mBTConnected = false;
                mConnectionState = connectionStateEnum.isToScan;
                onConnectionStateChange(mConnectionState);
            	mBTHandler.removeCallbacks(mDisonnectingOverTimeRunnable);
            	bluetoothLeService.close();
            }else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            	getGattServices(bluetoothLeService.getSupportedGattServices());
            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	if(mSCharacteristic==mModelNumberCharacteristic){
            		if(intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toUpperCase().startsWith("DF BLUNO")) {
						bluetoothLeService.setCharacteristicNotification(mSCharacteristic, false);
						mSCharacteristic=mCommandCharacteristic;
						mSCharacteristic.setValue(mBTPassword);
						bluetoothLeService.writeCharacteristic(mSCharacteristic);
						mSCharacteristic.setValue(mBTBaudrateBuffer);
						bluetoothLeService.writeCharacteristic(mSCharacteristic);
						mSCharacteristic=mSerialPortCharacteristic;
						bluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
						mConnectionState = connectionStateEnum.isConnected;
						onConnectionStateChange(mConnectionState);
					}else{
            			Toast.makeText(context, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
                        mConnectionState = connectionStateEnum.isToScan;
                        onConnectionStateChange(mConnectionState);
					}
            	}else if (mSCharacteristic==mSerialPortCharacteristic) {
            		//onSerialReceived(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            		onSerialReceived(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA_BYTE));
				}
            	
            }
        }
    };
    
    private void getGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        mModelNumberCharacteristic=null;
        mSerialPortCharacteristic=null;
        mCommandCharacteristic=null;
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();
            
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                uuid = gattCharacteristic.getUuid().toString();
                if(uuid.equals(ModelNumberStringUUID)){
                	mModelNumberCharacteristic=gattCharacteristic;
                	//System.out.println("mModelNumberCharacteristic  "+mModelNumberCharacteristic.getUuid().toString());
                }else if(uuid.equals(SerialPortUUID)){
                	mSerialPortCharacteristic = gattCharacteristic;
                }else if(uuid.equals(CommandUUID)){
                	mCommandCharacteristic = gattCharacteristic;
                }
            }
            mGattCharacteristics.add(charas);
        }
        
        if (mModelNumberCharacteristic==null || mSerialPortCharacteristic==null || mCommandCharacteristic==null) {
			Toast.makeText(activity, "Please select DFRobot devices",Toast.LENGTH_SHORT).show();
            mConnectionState = connectionStateEnum.isToScan;
            onConnectionStateChange(mConnectionState);
		}
        else {
        	mSCharacteristic=mModelNumberCharacteristic;
        	bluetoothLeService.setCharacteristicNotification(mSCharacteristic, true);
        	bluetoothLeService.readCharacteristic(mSCharacteristic);
		}
        
    }
	
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    
    public void serialBegin(int baud){
		mBTBaudrate=baud;
		mBTBaudrateBuffer = "AT+CURRUART="+mBTBaudrate+"\r\n";
	}
    
    public void serialSend(String theString){
		if (mConnectionState == connectionStateEnum.isConnected) {
			mSCharacteristic.setValue(theString);
			bluetoothLeService.writeCharacteristic(mSCharacteristic);
		}
	}
    
    public void onSerialReceived(byte[] data) {   	
    	// 10 bytes from Sensor (1 HEADER, 2 ACCX, 2 ACCY, 2 ACCZ, 2 IR, 1 FOOTER)
    	if(data != null){
    		byte[] byteData = data; // data.getBytes(Charset.forName("UTF-8"));
    		if(byteData.length >= 10){
	    		if(byteData[0] == HEADER){
					accX = readArduinoBinaryInt(byteData[1],byteData[2]);
					accY = readArduinoBinaryInt(byteData[3],byteData[4]);
					accZ = readArduinoBinaryInt(byteData[5],byteData[6]);
					IR = readArduinoBinaryInt(byteData[7],byteData[8]);
					
					applySensorCalibration();
	    		}
    		}
    	}
    			
	}
    
    public int readArduinoBinaryInt(byte least, byte most){
		int val;
		val = least;
		val = most*256 + val;
		return val;
	}
    
    public int getAccX(){
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
    
    public int getCalibAccX(){
    	return calibAX;
    }
	
	public int getAccY(){
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
	
	public int getCalibAccY(){
    	return calibAY;
    }

	public int getAccZ(){
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
	
	public int getCalibAccZ(){
    	return calibAZ;
    }
	
	public int getIR(){
		return IR;
	}
	
	public int getCalibIR(){
    	return calibIR;
    }
	
	public boolean isTXR(){
		return triggerXR;
	}
	
	public boolean isTXL(){
		return triggerXL;
	}
	
	public boolean isTYR(){
		return triggerYR;
	}
	
	public boolean isTYL(){
		return triggerYL;
	}
	
	public boolean isTZR(){
		return triggerZR;
	}
	
	public boolean isTZL(){
		return triggerZL;
	}
	
	public boolean isTIR(){
		return triggerIR;
	}
	
	public void onResume(Context activity){
		// Ensures BLUETOOTH is enabled on the device. If BLUETOOTH is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (!btAdapter.isEnabled()) {
			if (!btAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				((Activity) activity).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}


		activity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
	}
	
	public void onActivityResult(Context activity,int requestCode, int resultCode, Intent data){
		// User chose not to enable BLUETOOTH.
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			((Activity) activity).finish();
			return;
		}
	}
	
	public void onPause(Context activity){
		scanLeDevice(false);
		activity.unregisterReceiver(mGattUpdateReceiver);
    	mConnectionState=connectionStateEnum.isToScan;
    	onConnectionStateChange(mConnectionState);
		if(bluetoothLeService!=null){
			bluetoothLeService.disconnect();
            mBTHandler.postDelayed(mDisonnectingOverTimeRunnable, BT_SCAN_PERIOD);
		}
		mSCharacteristic = null;
	}
	
	public void onStop(){
		if(bluetoothLeService!=null){
        	mBTHandler.removeCallbacks(mDisonnectingOverTimeRunnable);
			bluetoothLeService.close();
		}
		mSCharacteristic = null;
	}

	public void onDestroy(Context activity){
		activity.unbindService(mServiceConnection);
        bluetoothLeService = null;
	}

}
