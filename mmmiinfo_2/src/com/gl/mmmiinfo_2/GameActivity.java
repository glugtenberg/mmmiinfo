package com.gl.mmmiinfo_2;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class GameActivity extends Activity {
	
	protected Game game = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLSurfaceView mGLView = new GLSurfaceView(getApplication());

		mGLView.setEGLContextClientVersion(2);

		game = new Game();
		mGLView.setRenderer(game);
		
		setContentView(mGLView);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	public void onLeftBtn(View v) {
		game.player.moveLeft();
    } 
	
	public void onRightBtn(View v) {
		game.player.moveRight();
    }
	
	public void onDestroy() {
	    super.onDestroy();
	    game.clear();
	    game = null;
	}
	
	public void endGame(View v) throws IOException{
		//string STRINGVALUE = id - mode - score
		String str = "kaas";

		FileOutputStream fos = openFileOutput("name.txt", MODE_PRIVATE);
		fos.write(str.getBytes());
		fos.close();
	}
}
