package com.gl.mmmiinfo_2;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;


class Game implements GLSurfaceView.Renderer {

	private static Activity master = null;
	private RGBColor background = new RGBColor(50, 50, 100);
	
	private FrameBuffer fb = null;
	public World world = null;
	private long time = System.currentTimeMillis();

	public CubeSpawner spawner = null;
	public Player player = null; 
	
	private ShootingCube cube = null;
	private boolean gl2 = true;
	
	public Camera cam = null;
	private Light sun = null;

	public Game() {
		
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		if (fb != null) {
			fb.dispose();
		}

		if (gl2) {
			fb = new FrameBuffer(w, h); // OpenGL ES 2.0 constructor
		} else {
			fb = new FrameBuffer(gl, w, h); // OpenGL ES 1.x constructor
		}

		if (master == null) {

			world = new World();
			world.setAmbientLight(20, 20, 20);

			sun = new Light(world);
			sun.setIntensity(250, 250, 250);

			
			Texture texture = new Texture(64, 64, new RGBColor(255,0,0));
			TextureManager.getInstance().addTexture("texture", texture);
			
			ArrayList<SimpleVector> spawnSlots = new ArrayList<SimpleVector>();
			spawnSlots.add(new SimpleVector(-10 , 0, 100));
			spawnSlots.add(new SimpleVector(0 , 0, 100));
			spawnSlots.add(new SimpleVector(10 , 0, 100));
			spawner = new CubeSpawner(spawnSlots, world); 
			
			player = new Player();
			
			cam = world.getCamera();
			cam.setPosition(0, 0, 0);
			cam.lookAt(new SimpleVector(0, 0, 1));

			SimpleVector sv = new SimpleVector();
			sv.set(new SimpleVector(0, 0, -1));
			sv.y -= 100;
			sv.z -= 100;
			sun.setPosition(sv);
			MemoryHelper.compact();

		}
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	}

	public void onDrawFrame(GL10 gl) {
		long elapsedTime = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		
		spawner.update(this , elapsedTime / 1000.0f);
		player.update(this, elapsedTime / 1000.0f);
				
		fb.clear(background);
		world.renderScene(fb);
		world.draw(fb);
		fb.display();

	}
}