package com.gl.mmmiinfo_2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		try {
			writeToOutput();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeToOutput() throws IOException{
		Time now = new Time();
		now.setToNow();
		
		String data = "New session at " + now.format("dd-MM hh:mm:ss"); 
		
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath() + "/");

		File file = new File(dir, "score.txt");
		
		try {
			FileWriter fw = new FileWriter(file,true); 
			fw.write(data);
			fw.close();  
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
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
