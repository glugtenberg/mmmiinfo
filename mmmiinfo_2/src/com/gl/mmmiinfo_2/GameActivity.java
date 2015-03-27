package com.gl.mmmiinfo_2;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class GameActivity extends Activity implements SensorEventListener{
	
	private Game game = null;
	
	private SensorManager sensorManager;
    
    private boolean tiltFlag = true; 
    private static final float flagThreshold = 0.1f;
    private static final float tiltThreshold = 0.15f; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLSurfaceView mGLView = new GLSurfaceView(getApplication());

		mGLView.setEGLContextClientVersion(2);

		game = new Game();
		mGLView.setRenderer(game);
		
		setContentView(mGLView);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		View gui = inflater.inflate(R.layout.game, null);
		
		addContentView(gui, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this,  sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void onLeftBtn(View v) {
		game.player.moveLeft();
    } 
	
	public void onRightBtn(View v) {
		game.player.moveRight();
    }

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {
			handleTilting(event);
		} 
	}

	private void handleTilting(SensorEvent event) {
		if (game == null || game.player == null) return; 
		
		float[] orientationValues = new float[3];
		for (int i = 0; i < 3; ++i) orientationValues[i] = event.values[i];
		
		int axisIdx = 1; 
	        
        if (tiltFlag) {
        	if (orientationValues[axisIdx] < -tiltThreshold) {
        		game.player.moveLeft();
        		tiltFlag = false; 
        	}
        	
        	if (orientationValues[axisIdx] > tiltThreshold) {
        		game.player.moveRight();
        		tiltFlag = false; 
        	}
        } else {
        	if (orientationValues[axisIdx] >= -flagThreshold && orientationValues[axisIdx] <= flagThreshold) tiltFlag = true; 
        }
        /*
        Log.d("DEBUG", "v0: " + orientationValues[0]);
        Log.d("DEBUG", "v1: " + orientationValues[1]);
		Log.d("DEBUG", "v2: " + orientationValues[2]);
		Log.d("DEBUG", "---------");	
		*/
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}
