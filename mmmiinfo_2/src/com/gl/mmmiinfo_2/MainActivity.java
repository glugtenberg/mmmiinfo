package com.gl.mmmiinfo_2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

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
		
		EditText h = (EditText)findViewById(R.id.hueText);
		EditText s = (EditText)findViewById(R.id.saturationText);
		EditText vv = (EditText)findViewById(R.id.valueText);
		
		intent.putExtra("HUE_THRESHOLD", Integer.parseInt(h.getText().toString()));
		intent.putExtra("SATURATION_THRESHOLD", Integer.parseInt(s.getText().toString()));
		intent.putExtra("VALUE_THRESHOLD", Integer.parseInt(vv.getText().toString()));
		
		startActivity(intent);
    }
}
