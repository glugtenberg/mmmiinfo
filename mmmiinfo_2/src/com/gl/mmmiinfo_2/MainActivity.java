package com.gl.mmmiinfo_2;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import com.threed.jpct.Camera;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends Activity {
	
	private Game game = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLSurfaceView mGLView = new GLSurfaceView(getApplication());

		mGLView.setEGLContextClientVersion(2);

		game = new Game();
		mGLView.setRenderer(game);
		
		setContentView(mGLView);
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		View gui = inflater.inflate(R.layout.main, null);
		
		addContentView(gui, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
	
	public void onLeftBtn(View v)
    {
        game.cam.moveCamera(Camera.CAMERA_MOVELEFT, 10);
    } 
	
	public void onRightBtn(View v)
    {
		game.cam.moveCamera(Camera.CAMERA_MOVERIGHT, 10);
    }
}
