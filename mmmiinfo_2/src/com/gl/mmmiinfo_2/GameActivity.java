package com.gl.mmmiinfo_2;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG;

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
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class GameActivity extends Activity implements SensorEventListener, CvCameraViewListener2{
	
	private Game game = null;
	
	private SensorManager sensorManager;
    
    private boolean tiltFlag = true; 
    private static final float flagThreshold = 0.1f;
    private static final float tiltThreshold = 0.15f; 
    
    private CameraBridgeViewBase mOpenCvCameraView;
	
	private BackgroundSubtractorMOG backgroundSubMOG;
	private Mat background;
	private Mat diff;
	private Mat curr;
	
	static boolean backgroundCaptured = false;
	static int counter = 0;
	private final int cooldown = 20;
	
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
		
		//front cam capture
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.frontCam);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);
		mOpenCvCameraView.setMaxFrameSize(600, 300);
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
	
	@Override
	 public void onPause()
	 {
	     super.onPause();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	 }

	 public void onDestroy() {
	     super.onDestroy();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	 }
	 
	 public void onCameraViewStarted(int width, int height) {
		 backgroundSubMOG = new BackgroundSubtractorMOG();
		 
		 background = new Mat(width, height, CvType.CV_8U);
         diff = new Mat(width, height, CvType.CV_8U);
         curr = new Mat(width, height, CvType.CV_8U);
	 }

	 public void onCameraViewStopped() {
	 }

	 public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		 inputFrame.gray().copyTo(curr);
		 
		 if(counter < 10 && !backgroundCaptured){
			 counter++;
			 return curr;
		 }else if(!backgroundCaptured){
			 backgroundCaptured = true;
			 inputFrame.gray().copyTo(background);
		 }
		 
		 Core.absdiff(curr, background, diff);
		 Imgproc.threshold(diff, diff, 50, 255, Imgproc.THRESH_BINARY);
		 
		 //Set LEFT region
		 int x = 0, y = 0, width = Math.round(curr.width() / 10), height = curr.height();
		 Rect leftRegion = new Rect(x,y,width,height);
		 
		 //Set RIGHT region
		 x = curr.width() - Math.round(curr.width() / 10);
		 Rect rightRegion = new Rect(x,y,width,height);

		 //Define motion
		 double Threshold = 0.4; //40% of region must be occuluded
		 double nzeLeft = Core.countNonZero(diff.submat(leftRegion));
		 double nzeRight = Core.countNonZero(diff.submat(rightRegion));
		 
		 Log.i("GL", "Non zero left: " + nzeLeft + " Non zero right: " + nzeRight);
		 
		 if ((nzeLeft/(width*height)) > Threshold){
			 game.player.moveRight();
		 }else if ((nzeRight/(width*height)) > Threshold){
			 game.player.moveLeft();
		 }else{
			 game.player.slotIdx = 1; //TODO: reset to middle track when no movement captured.
		 }
		 
		 return diff;
	 }

	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	            {
	                Log.i("GL", "OpenCV loaded successfully");
	                mOpenCvCameraView.enableView();
	                
	            } break;
	            default:
	            {
	                super.onManagerConnected(status);
	            } break;
	        }
	    }
	};

	@Override
	public void onResume()
	{
	    super.onResume();
	    OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mLoaderCallback);
	}
}
