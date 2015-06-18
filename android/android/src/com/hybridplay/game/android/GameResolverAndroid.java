package com.hybridplay.game.android;

import com.hybridplay.bluetoothle.HP_BTSensor;
import com.hybridplay.game.GameResolver;
import com.hybridplay.shared.SharedFunctions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class GameResolverAndroid implements GameResolver {
	
	////////////////////////////////////////////////////// BLUETOOTH 
	HP_BTSensor		btSensor;
	////////////////////////////////////////////////////// BLUETOOTH 
	
	////////////////////////////////////////////////////// CALIBRATION
	SharedPreferences prefs;
	SharedPreferences.Editor prefsEditor;
	
	SensorPositionsListAdapter sensorPositionsListAdapter = null;
	////////////////////////////////////////////////////// CALIBRATION

	Handler 		uiThread;
	Context 		appContext;

	public GameResolverAndroid(Context appContext) {
		uiThread = new Handler();
		this.appContext = appContext;
		
		prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
	}
	
	public boolean sensorConnected(){
		return btSensor.mBTConnected;
	}
	
	public boolean sensorTXL(){
		return btSensor.isTXL();
	}
	
	public boolean sensorTXR(){
		return btSensor.isTXR();
	}
	
	public boolean sensorTYL(){
		return btSensor.isTYL();
	}
	
	public boolean sensorTYR(){
		return btSensor.isTYR();
	}
	
	public boolean sensorTZL(){
		return btSensor.isTZL();
	}
	
	public boolean sensorTZR(){
		return btSensor.isTZR();
	}
	
	public boolean sensorTIR(){
		return btSensor.isTIR();
	}
	
	public void saveCalibrationData(){
		prefsEditor = prefs.edit();
		prefsEditor.putInt(SharedFunctions.HP_PREF_SENSOR_X_CALIB, btSensor.getAccX());
		prefsEditor.putInt(SharedFunctions.HP_PREF_SENSOR_Y_CALIB, btSensor.getAccY());
		prefsEditor.putInt(SharedFunctions.HP_PREF_SENSOR_Z_CALIB, btSensor.getAccZ());
		prefsEditor.putInt(SharedFunctions.HP_PREF_SENSOR_IR_CALIB, btSensor.getIR());
		prefsEditor.commit();
		showShortToast("HybridPlay Sensor Calibration UPDATED!");
	}
	
	public void initSensorPositionListAdapter(){
		sensorPositionsListAdapter = new SensorPositionsListAdapter(appContext);

		for(int i=0;i<SharedFunctions.sensorPossiblePositions.length;i++){
			sensorPositionsListAdapter.addPosition(SharedFunctions.sensorPossiblePositions[i]);
		}
		sensorPositionsListAdapter.notifyDataSetChanged();
		
		uiThread.post(new Runnable() {
			public void run() {
				new AlertDialog.Builder(appContext)
				.setTitle("SELECT HYBRIDPLAY SENSOR POSITION...").setAdapter(sensorPositionsListAdapter, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which){
						// SAVE SENSOR POSITION FOR CALIBRATION
						prefsEditor = prefs.edit();
						prefsEditor.putString(SharedFunctions.HP_PREF_SENSOR_POSITION, String.valueOf(which));
						prefsEditor.commit();
						showShortToast("HybridPlay Sensor position changed to: "+SharedFunctions.sensorPossiblePositions[which]);
					}
				})
				.create().show();
			}
		});
		
	}

	public void showShortToast(final CharSequence toastMessage) {
		uiThread.post(new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(appContext, toastMessage, Toast.LENGTH_SHORT);
				toast.getView().setBackgroundColor(appContext.getResources().getColor(R.color.hp_green));
				toast.show();
			}
		});
	}


	public void showLongToast(final CharSequence toastMessage) {
		uiThread.post(new Runnable() {
			public void run() {
				Toast toast = Toast.makeText(appContext, toastMessage, Toast.LENGTH_LONG);
				toast.getView().setBackgroundColor(appContext.getResources().getColor(R.color.hp_green));
				toast.show();
			}
		});
	}


	public void showAlertBox(final String alertBoxTitle, final String alertBoxMessage, final String alertBoxButtonText) {
		uiThread.post(new Runnable() {
			public void run() {
				new AlertDialog.Builder(appContext)
				.setTitle(alertBoxTitle)
				.setMessage(alertBoxMessage)
				.setNeutralButton(alertBoxButtonText,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int whichButton) {
						
					}
				}).create().show();
			}
		});
	}


	public void openUri(String uri) {
		Uri myUri = Uri.parse(uri);
		Intent intent = new Intent(Intent.ACTION_VIEW, myUri);
		appContext.startActivity(intent);
	}
	
	public void openInstructionsActivity() {
		Intent intent;
		
		intent = new Intent(appContext,HP_Instructions.class);
		appContext.startActivity(intent);
	}

}
