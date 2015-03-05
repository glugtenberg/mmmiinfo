package com.gl.mmmiinfo_2;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLSurfaceView mGLView = new GLSurfaceView(getApplication());

		mGLView.setEGLContextClientVersion(2);

		Renderer renderer = new Renderer();
		mGLView.setRenderer(renderer);
		
		setContentView(mGLView);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		View gui = inflater.inflate(R.layout.main, null);
		
		addContentView(gui, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
	
	public void onLeftBtn(View v)
    {
        //cam.moveCamera(Camera.CAMERA_MOVELEFT, 10);
    } 
	
	public void onRightBtn(View v)
    {
        //cam.moveCamera(Camera.CAMERA_MOVERIGHT, 10);
    }
}
