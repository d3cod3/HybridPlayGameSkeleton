package com.hybridplay.game.android;

import android.os.Bundle;

public class HP_Credits extends HP_Drawer {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hp_credits);
		
		updateSelectedItem(3);
	}

}
