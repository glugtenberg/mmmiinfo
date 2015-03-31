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
		
		//getFragmentManager().beginTransaction()
		//	.replace(R.id.container, FrontCam.newInstance()).commit();
	}
	
	public void onStartButton(View v) {
		Intent intent = new Intent(v.getContext(), GameActivity.class);
		v.getContext().startActivity(intent);
    }
}
