package com.hybridplay.game.android;

import com.hybridplay.shared.SharedFunctions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class HP_SplashScreen extends Activity implements OnClickListener {
	
	Handler handler = new Handler();
	private ImageView splashScreen;
	boolean isManualEnter = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);
		
		// splash screen
		splashScreen = (ImageView) findViewById(R.id.LogoImageView);
        splashScreen.setOnClickListener(this);
        
        if(SharedFunctions.BTorWIFI == 0){
			SharedFunctions.initBTLE(this);
		}else if(SharedFunctions.BTorWIFI == 1){
			SharedFunctions.setWIFI(this, true);
		}
		
        handler.postDelayed(new Runnable() {
            public void run() {
            	if(!isManualEnter){
            		openHPInstructions();
            	}
            }
        }, 3000);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.LogoImageView:
			isManualEnter = true;
			openHPInstructions();
            break;
		
		}
    }
	
	public void openHPInstructions(){
    	Intent i = new Intent(this, HP_Instructions.class);
    	startActivity(i);
    	finish();
    }

}
