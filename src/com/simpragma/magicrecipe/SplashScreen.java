package com.simpragma.magicrecipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		Thread background = new Thread() {
			public void run() {
				try {
					sleep(3 * 1000);
					Intent i = new Intent(getBaseContext(), MainActivity.class);
					startActivity(i);
					finish();

				} catch (Exception e) {

				}
			}
		};
		background.start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
