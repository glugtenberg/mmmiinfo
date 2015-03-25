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
	private Sensor accelerometer;
	private Sensor magneticField; 
	
	private float[] magnetValues = new float[3];
    private float[] accValues = new float[3];
    
    private boolean tiltFlag = true; 
    private static final float flagThreshold = 0.1f;
    private static final float tiltThreshold = 0.2f; 
	
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
		
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
        	magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        	sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
        }
	}
	
	public void onLeftBtn(View v)
    {
		game.player.moveLeft();
    } 
	
	public void onRightBtn(View v)
    {
		game.player.moveRight();
    }

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (game == null) return; 
		
		if (event.sensor.getType() ==  Sensor.TYPE_ACCELEROMETER) {
            for (int i = 0; i < 3; ++i) accValues[i] = event.values[i];
		} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) { 
            for (int i = 0; i < 3; ++i) magnetValues[i] = event.values[i];      
		}
		
		float[] rotationMatrix    = new float[9];
		float[] orientationValues = new float[3];
		SensorManager.getRotationMatrix(rotationMatrix, null, accValues, magnetValues);
        SensorManager.getOrientation(rotationMatrix, orientationValues);
      
		//Log.d("DEBUG", "theta0: " + mValuesOrientation[0]);
		//Log.d("DEBUG", "theta1: " + mValuesOrientation[1]);
        
        if (tiltFlag) {
        	if (orientationValues[2] < -tiltThreshold) {
        		game.player.moveLeft();
        		tiltFlag = false; 
        	}
        	
        	if (orientationValues[2] > tiltThreshold) {
        		game.player.moveRight();
        		tiltFlag = false; 
        	}
        } else {
        	if (orientationValues[2] >= -flagThreshold && orientationValues[2] <= flagThreshold) tiltFlag = true; 
        }
        
		Log.d("DEBUG", "theta2: " + orientationValues[2]);
		Log.d("DEBUG", "---------");	
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}
