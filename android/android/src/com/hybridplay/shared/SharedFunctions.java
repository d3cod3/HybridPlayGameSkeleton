package com.hybridplay.shared;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.apache.http.util.ByteArrayBuffer;

import com.hybridplay.game.android.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class SharedFunctions {

	public static final int GAMES_MENU_INDEX = 0;
	public static final int CONNECT_MENU_INDEX = 1;
	public static final int INSTRUCTIONS_MENU_INDEX = 2;
	public static final int CREDITS_MENU_INDEX = 3;

	public static final String HP_PREF_WEBSITE = "hpg_website";
	public static final String HP_PREF_USER = "hpg_user";
	public static final String HP_PREF_PASS = "hpg_pass";
	
	public static final String HP_PREF_SENSOR_POSITION = "hpg_sensor_position";
	public static final String HP_PREF_SENSOR_X_CALIB = "hpg_sensor_x_calib";
	public static final String HP_PREF_SENSOR_Y_CALIB = "hpg_sensor_y_calib";
	public static final String HP_PREF_SENSOR_Z_CALIB = "hpg_sensor_z_calib";
	public static final String HP_PREF_SENSOR_IR_CALIB = "hpg_sensor_ir_calib";
	
	public static int BTorWIFI = 0; // 0 - BLUETOOTH, 1 - WIFI
	
	public static final String HP_SENSOR_SSID = "HYBRIDPLAY";
	public static final String HP_SENSOR_PASSWD = "HYBRIDPLAY";
	public static final String HP_SENSOR_IP = "192.168.1.99";
	public static final int    HP_SENSOR_PORT = 9999;
	public static final String HP_BT_SENSOR_SSID = "hybridPLAY";
	public static final String HP_SENSOR_MODE = "hpg_sensor_mode"; // single player, multi player
	

	public static final String HP_GAMES_FLAG = "HYBRIDPLAY GAME SKELETON";
	public static final String BASE_APP_DIR = "/hybridplayGameSkeleton";
	
	public static final String sensorPossiblePositions[] ={"HORIZONTAL BUT. LEFT","HORIZONTAL BUT. RIGHT","HORIZONTAL INVERTED BUT. LEFT","HORIZONTAL INVERTED BUT. RIGHT","VERTICAL BUT. DOWN","VERTICAL BUT. UP","VERTICAL INVERTED BUT. UP","VERTICAL INVERTED BUT. DOWN"};
	
	public static String wifiIpAddress(Context context) {
	    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

	    // Convert little-endian to big-endianif needed
	    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
	        ipAddress = Integer.reverseBytes(ipAddress);
	    }

	    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

	    String ipAddressString;
	    try {
	        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
	    } catch (UnknownHostException ex) {
	        Log.e("WIFIIP", "Unable to get host address.");
	        ipAddressString = null;
	    }

	    return ipAddressString;
	}
	
	public static void showToast(Context context,CharSequence toastMessage) {
		Toast toast = Toast.makeText(context, toastMessage, Toast.LENGTH_LONG);
		toast.getView().setBackgroundColor(context.getResources().getColor(R.color.hp_green));
		toast.show();
	}

	public static Calendar getTime(int timezone){
		// get the supported ids for the requested timezone
		String[] ids = TimeZone.getAvailableIDs(timezone * 60 * 60 * 1000);
		// if no ids were returned, something is wrong. get out.
		if (ids.length == 0){
			return null;
		}

		SimpleTimeZone pdt = new SimpleTimeZone(timezone * 60 * 60 * 1000, ids[0]);
		// set up rules for daylight savings time
		pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

		// create a GregorianCalendar with the Pacific Daylight time zone
		// and the current date and time
		Calendar calendar = new GregorianCalendar(pdt);
		Date trialTime = new Date();
		calendar.setTime(trialTime);

		return calendar;
	}

	@SuppressLint("SimpleDateFormat")
	public static int timeZone(){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),Locale.getDefault());
		Date currentLocalTime = calendar.getTime();
		DateFormat date = new SimpleDateFormat("Z");
		String localTime = date.format(currentLocalTime);

		if(localTime.substring(0,1).equals("-")){
			return Integer.parseInt(localTime.substring(2, 3))*-1;
		}else{
			return Integer.parseInt(localTime.substring(2, 3));
		}
	}

	public static Bitmap getBitmapFromSDCard(Context context,String fileName) {
		String _path = Environment.getExternalStorageDirectory()+fileName;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap myBitmap = BitmapFactory.decodeFile(_path, options);

		if (options.outWidth != -1 && options.outHeight != -1) {
			return myBitmap;
		}else {
			myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo);
			return myBitmap;
		}


	}

	public static void getBitmapFromURL(String fileUrl,final ImageView imgView) {
		AsyncTask<String, Object, String> task = new AsyncTask<String, Object, String>() {

			@Override
			protected String doInBackground(String... params) {
				URL myFileUrl = null;
				try {
					myFileUrl = new URL(params[0]);
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}
				try {
					HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
					conn.setDoInput(true);
					conn.setUseCaches(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);

					Bitmap bmImg = BitmapFactory.decodeStream(bis);
					bis.close();
					is.close();

					imgView.setImageBitmap(bmImg);
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}

				return null;
			}
		};
		task.execute(fileUrl);
	}

	public static void initHPStorage(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					File root = android.os.Environment.getExternalStorageDirectory();

					File dir = new File (root.getAbsolutePath() + BASE_APP_DIR);
					if(!dir.exists()) {
						dir.mkdirs();
						Log.d("HPG STORAGE MKDIR","BASE DIR CREATED");
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	public static void DownloadFromUrl(String DownloadUrl, String fileName) {

		final String du = DownloadUrl;
		final String fn = fileName;

		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					try {
						File root = android.os.Environment.getExternalStorageDirectory();

						File baseAppDir = new File (root.getAbsolutePath() + BASE_APP_DIR);
						if(!baseAppDir.exists()) {
							baseAppDir.mkdirs();
							Log.d("TEST MKDIR","BASE DIR CREATED");
						}

						File dir = new File (root.getAbsolutePath());
						if(!dir.exists()) {
							dir.mkdirs();
							Log.d("TEST MKDIR","GAMES IMG DIR CREATED");
						}

						// Create a URL for the desired page
						URL url = new URL(du); //you can write here any link
						File file = new File(dir, fn);

						if(!file.exists()){
							Log.d("DownloadManager", "download url:" + url);
							Log.d("DownloadManager", "downloaded file name:" + fn);

							/* Open a connection to that URL. */
							URLConnection ucon = url.openConnection();
							InputStream is = ucon.getInputStream();
							BufferedInputStream bis = new BufferedInputStream(is);

							ByteArrayBuffer baf = new ByteArrayBuffer(50);
							int current = 0;
							while ((current = bis.read()) != -1) {
								baf.append((byte) current);
							}

							/* Convert the Bytes read to a String. */
							FileOutputStream fos = new FileOutputStream(file);
							fos.write(baf.toByteArray());
							fos.close();

						}

					} catch (IOException e) {
						Log.d("DownloadManager", "Error: " + e);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}).start();
	}

	public static boolean isInternetOn(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
	
	public static void setWIFI(Context context, boolean state){
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifi.setWifiEnabled(state);
	}
	
	public static boolean setBluetooth(boolean enable) {
	    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    boolean isEnabled = bluetoothAdapter.isEnabled();
	    if (enable && !isEnabled) {
	        return bluetoothAdapter.enable(); 
	    }
	    else if(!enable && isEnabled) {
	        return bluetoothAdapter.disable();
	    }
	    // No need to change bluetooth state
	    return true;
	}
	
	public static boolean initBTLE(Context activityContext){
		// verify BLE device support
    	if (!activityContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
    	    return false;
    	}
		
		// Initializes a BLUETOOTH adapter. For API level 18 and above, get a
		// reference to BluetoothAdapter through BluetoothManager.
		final BluetoothManager bluetoothManager = (BluetoothManager) activityContext.getSystemService(Context.BLUETOOTH_SERVICE);
		BluetoothAdapter btAdapter = bluetoothManager.getAdapter();
	
		// Checks if BLUETOOTH is supported on the device.
		if (btAdapter == null) {
			return false;
		}
		
		/*if (btAdapter.isEnabled()) {
			btAdapter.disable();
		}
		
		btAdapter.enable();*/
		
		return true;
	}

	public static boolean isTabletDevice(Context activityContext) {
		// Verifies if the Generalized Size of the device is XLARGE to be
		// considered a Tablet
		boolean xlarge = ((activityContext.getResources().getConfiguration().screenLayout & 
				Configuration.SCREENLAYOUT_SIZE_MASK) == 
				Configuration.SCREENLAYOUT_SIZE_XLARGE);

		// If XLarge, checks if the Generalized Density is at least MDPI
		// (160dpi)
		if (xlarge) {
			DisplayMetrics metrics = new DisplayMetrics();
			Activity activity = (Activity) activityContext;
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			// MDPI=160, DEFAULT=160, DENSITY_HIGH=240, DENSITY_MEDIUM=160,
			// DENSITY_TV=213, DENSITY_XHIGH=320
			if (metrics.densityDpi == DisplayMetrics.DENSITY_DEFAULT
					|| metrics.densityDpi == DisplayMetrics.DENSITY_HIGH
					|| metrics.densityDpi == DisplayMetrics.DENSITY_MEDIUM
					|| metrics.densityDpi == DisplayMetrics.DENSITY_TV
					|| metrics.densityDpi == DisplayMetrics.DENSITY_XHIGH) {

				// Yes, this is a tablet!
				return true;
			}
		}

		// No, this is not a tablet!
		return false;
	}

}
