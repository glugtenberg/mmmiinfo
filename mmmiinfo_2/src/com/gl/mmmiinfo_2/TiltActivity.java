package com.gl.mmmiinfo_2;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;


public class TiltActivity extends GameActivity implements SensorEventListener {
	
	private SensorManager sensorManager;

    private static final float tiltThreshold = 0.09f; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(this,  sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
		
		inputType = "INPUT_TILT: "; 
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
		if (orientationValues[axisIdx] < -tiltThreshold) {
			game.player.moveToLeftSide();
		} else if (orientationValues[axisIdx] > tiltThreshold) {
			game.player.moveToRightSide();
		} else {
			game.player.moveToCenter();
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

}
