package com.gl.mmmiinfo_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
	}
	
	public void onStartButton(View v) {
		Intent intent = new Intent(v.getContext(), ButtonActivity.class);
		v.getContext().startActivity(intent);
    }
	
	public void onTiltButton(View v) {
		Intent intent = new Intent(v.getContext(), TiltActivity.class);
		startActivity(intent);
    }
	
	public void onCamButton(View v) {
		Intent intent = new Intent(v.getContext(), CameraActivity.class);
		startActivity(intent);
    }
}
