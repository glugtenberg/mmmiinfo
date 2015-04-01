package com.gl.mmmiinfo_2;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;


public class CameraActivity extends GameActivity implements CvCameraViewListener2 {
	
	//constants
	private static final double REGION_THRESHOLD = 0.3; //percentage of region occupied by ON pixels
	private static final int REGION_SCREEN_RATIO_WIDTH = 4; //1/x of width of screen filled by regions
	private static final int REGION_SCREEN_RATIO_HEIGHT = 2; //1/x of height of screen filled by regions
	
	private static int HUE_THRESHOLD = 41;//0;
	private static int SATURATION_THRESHOLD = 0;//53;
	private static int VALUE_THRESHOLD = 80;//129;

	private CameraBridgeViewBase mOpenCvCameraView;
	
    private Mat background;
	private List<Mat> bgChannels;
	private Mat diff;
	private Mat curr;
	
	static boolean backgroundCaptured = false;
	static int counter = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		View gui = inflater.inflate(R.layout.camera, null);
		
		addContentView(gui, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//front cam capture
		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.frontCam);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);

		mOpenCvCameraView.setMaxFrameSize(600, 400);
		
		inputType = "INPUT_CAMERA: "; 

		mOpenCvCameraView.setMaxFrameSize(800, 500);
		
		HUE_THRESHOLD = getIntent().getExtras().getInt("HUE_THRESHOLD");
		SATURATION_THRESHOLD = getIntent().getExtras().getInt("SATURATION_THRESHOLD");
		VALUE_THRESHOLD = getIntent().getExtras().getInt("VALUE_THRESHOLD");
		
	}
	
	@Override
	 public void onPause()
	 {
		Log.d("DEBUG:", "PAUSE PAUSE PAUSE !!!!");
	     super.onPause();
	     if (mOpenCvCameraView != null)
	         mOpenCvCameraView.disableView();
	     if (game != null && game.world != null) {
	    	 game.clear();
	     }
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
	    game.active = false;
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
			 game.player.moveToLeftSide();
		 }else if ((nzeRight/(width*height)) > REGION_THRESHOLD){
			 game.player.moveToRightSide();
		 }else{
			 game.player.moveToCenter();
		 }
		 
		 Core.rectangle(diff, leftRegion.br(), leftRegion.tl(), new Scalar(255,0,0), 3);
		 Core.rectangle(diff, rightRegion.br(), rightRegion.tl(), new Scalar(255,0,0), 3);
		 
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
	
	public void onScreenClick(View v){
		game.active = true;
		Button b = (Button)v;
		b.setVisibility(View.GONE);
	}

}
