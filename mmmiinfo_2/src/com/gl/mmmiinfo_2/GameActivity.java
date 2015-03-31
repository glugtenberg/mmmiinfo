package com.gl.mmmiinfo_2;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

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
    
    private CameraBridgeViewBase mOpenCvCameraView;
	
    private Mat background;
	private List<Mat> bgChannels;
	private Mat diff;
	private Mat curr;
	
	static boolean backgroundCaptured = false;
	static int counter = 0;
	
	//constants
	private static final float flagThreshold = 0.1f;
    private static final float tiltThreshold = 0.15f; 
	private static final double REGION_THRESHOLD = 0.6; //percentage of region occupied by ON pixels
	private static final int REGION_SCREEN_RATIO_WIDTH = 4; //1/x of width of screen filled by regions
	private static final int REGION_SCREEN_RATIO_HEIGHT = 2; //1/x of height of screen filled by regions
	private static final int HUE_THRESHOLD = 0;
	private static final int SATURATION_THRESHOLD = 53;
	private static final int VALUE_THRESHOLD = 129;

	
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
		 
		 background = new Mat(width, height, CvType.CV_8U);
		 bgChannels = new ArrayList<Mat>();
         diff = new Mat(width, height, CvType.CV_8U);
         curr = new Mat(width, height, CvType.CV_8U);
	 }

	 public void onCameraViewStopped() {
	 }

	 public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		 inputFrame.rgba().copyTo(curr);
		 
		 if(counter < 10 && !backgroundCaptured){
			 counter++;
			 return curr;
		 }else if(!backgroundCaptured){
			 backgroundCaptured = true;
			 inputFrame.rgba().copyTo(background);
			 Imgproc.cvtColor(background, background, Imgproc.COLOR_RGB2HSV, 3);
			 Core.split(background, bgChannels);
		 }
		 //get HSV model from RGB
		 Imgproc.cvtColor(curr, curr, Imgproc.COLOR_RGB2HSV, 3);
		 
		 //split HSV into channels
		 List<Mat> channels = new ArrayList<Mat>();
		 Core.split(curr, channels);
		 
		 Mat H = channels.get(0);
		 Mat S = channels.get(1);
		 Mat V = channels.get(2);
		 
		 //init used matrices
		 Mat tmp = new Mat(background.width(), background.height(), CvType.CV_8U);
		 Mat bg = new Mat(background.width(), background.height(), CvType.CV_8U);
		 
		 //background subtraction H
		 Core.absdiff(H, bgChannels.get(0), tmp);
		 Imgproc.threshold(tmp, diff, HUE_THRESHOLD, 255, Imgproc.THRESH_BINARY);
		 
		 //background subtraction S
		 Core.absdiff(S, bgChannels.get(1), tmp);
		 Imgproc.threshold(tmp, bg, SATURATION_THRESHOLD, 255, Imgproc.THRESH_BINARY);
		 Core.bitwise_and(diff, bg, diff);
		 
		 //background subtraction V
		 Core.absdiff(V, bgChannels.get(2), tmp);
		 Imgproc.threshold(tmp, bg, VALUE_THRESHOLD, 255, Imgproc.THRESH_BINARY);
		 Core.bitwise_or(diff, bg, diff);
		 
		 //flip output
		 Core.flip(diff, diff, 0);
		 
		 //enhance by erode and dillate
		 Imgproc.erode(diff, diff, new Mat(), new Point(-1, -1), 1);
		 Imgproc.dilate(diff, diff, new Mat(), new Point(-1, -1), 2);
		 Imgproc.erode(diff, diff, new Mat(), new Point(-1, -1), 1);
		 
		 //Set LEFT region
		 int x = 0, y = 0, width = Math.round(curr.width() / REGION_SCREEN_RATIO_WIDTH), height = Math.round(curr.height() / REGION_SCREEN_RATIO_HEIGHT);
		 Rect leftRegion = new Rect(x,y,width,height);
		 
		 //Set RIGHT region
		 x = curr.width() - Math.round(curr.width() / REGION_SCREEN_RATIO_WIDTH);
		 Rect rightRegion = new Rect(x,y,width,height);

		 //Define motion
		 double nzeLeft = Core.countNonZero(diff.submat(leftRegion));
		 double nzeRight = Core.countNonZero(diff.submat(rightRegion));
		 
		 Log.i("GL", "Non zero left: " + nzeLeft + " Non zero right: " + nzeRight);
		 
		 if ((nzeLeft/(width*height)) > REGION_THRESHOLD){
			 game.player.moveLeft();
		 }else if ((nzeRight/(width*height)) > REGION_THRESHOLD){
			 game.player.moveRight();
		 }else{
			 game.player.slotIdx = 1; //TODO: reset to middle track when no movement captured.
		 }
		 
		 Core.rectangle(diff, leftRegion.br(), leftRegion.tl(), new Scalar(255,0,0), 3);
		 Core.rectangle(diff, rightRegion.br(), rightRegion.tl(), new Scalar(255,0,0), 3);
		 
		 //Core.putText(diff, Integer.toString(HThresh), new Point(10, 50), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255), 1);
		 
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
