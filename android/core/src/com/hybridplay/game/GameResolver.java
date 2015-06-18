package com.hybridplay.game;

public interface GameResolver {
	
	// BLUETOOTH
	public void saveCalibrationData();
	public void initSensorPositionListAdapter();
	public boolean sensorConnected();
	public boolean sensorTXL();
	public boolean sensorTXR();
	public boolean sensorTYL();
	public boolean sensorTYR();
	public boolean sensorTZL();
	public boolean sensorTZR();
	public boolean sensorTIR();
	
	// UTILS
	public void showShortToast(final CharSequence toastMessage);
	public void showLongToast(final CharSequence toastMessage);
	public void showAlertBox(final String alertBoxTitle, final String alertBoxMessage, final String alertBoxButtonText);
	public void openUri(String uri);
	public void openInstructionsActivity();
	
}
