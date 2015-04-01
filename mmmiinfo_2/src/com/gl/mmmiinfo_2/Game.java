package com.gl.mmmiinfo_2;

import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;

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
	private RGBColor background = new RGBColor(40, 40, 40);
	
	private FrameBuffer fb = null;
	public World world = null;
	private long time = System.currentTimeMillis();

	public CubeSpawner spawner = null;
	public Player player = null; 
	
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
			init();
		}
	}

	public void clear() {
		world.removeAll(); 
   	 	//fb.dispose();
   	 	TextureManager.getInstance().flush();
	}
	
	public void init() {
		world = new World();
		world.setAmbientLight(50, 50, 50);
		
		sun = new Light(world);
		sun.setIntensity(250, 250, 250);

		Texture whiteTexture = new Texture(64, 64, new RGBColor(255,255,255));
		TextureManager.getInstance().addTexture("whiteTexture", whiteTexture);
		
		Texture greyTexture = new Texture(64, 64, new RGBColor(140,140,140));
		TextureManager.getInstance().addTexture("greyTexture", greyTexture);
		
		ArrayList<SimpleVector> slots = new ArrayList<SimpleVector>();
		slots.add(new SimpleVector(-11 , 0, 0));
		slots.add(new SimpleVector(0 , 0, 0));
		slots.add(new SimpleVector(11 , 0, 0));
		
		float[][] transitionTable = new float[3][3];
		transitionTable[0][0] = 0.0f;
		transitionTable[0][1] = 0.7f;
		transitionTable[0][2] = 0.3f;
		
		transitionTable[1][0] = 0.5f;
		transitionTable[1][1] = 0.0f;
		transitionTable[1][2] = 0.5f;
		
		transitionTable[2][0] = 0.3f;
		transitionTable[2][1] = 0.7f;
		transitionTable[2][2] = 0.0f;
				
		spawner = new CubeSpawner(slots, 100, transitionTable, 3, world); 
		player = new Player(slots);
		
		createBackground();
		
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
	
	private void createBackground() {
		addBackgroundBox(new SimpleVector(-11, 5, 0));
		addBackgroundBox(new SimpleVector(0, 5, 0));
		addBackgroundBox(new SimpleVector(11, 5, 0));
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
	
	private void addBackgroundBox(SimpleVector pos) {
		Object3D box = new Object3D(12);
	    
	    float w = 5;
	    float h = 0.25f;
	    float d = 200;
	    
	    SimpleVector upperLeftFront=new SimpleVector(-w,-h,-d);
	    SimpleVector upperRightFront=new SimpleVector(w,-h,-d);
	    SimpleVector lowerLeftFront=new SimpleVector(-w,h,-d);
	    SimpleVector lowerRightFront=new SimpleVector(w,h,-d);
	    
	    SimpleVector upperLeftBack = new SimpleVector( -w, -h, d);
	    SimpleVector upperRightBack = new SimpleVector(w, -h, d);
	    SimpleVector lowerLeftBack = new SimpleVector( -w, h, d);
	    SimpleVector lowerRightBack = new SimpleVector(w, h, d);
	    
	    // Front
	    box.addTriangle(upperLeftFront,0,0, lowerLeftFront,0,1, upperRightFront,1,0);
	    box.addTriangle(upperRightFront,1,0, lowerLeftFront,0,1, lowerRightFront,1,1);
	    
	    // Back
	    box.addTriangle(upperLeftBack,0,0, upperRightBack,1,0, lowerLeftBack,0,1);
	    box.addTriangle(upperRightBack,1,0, lowerRightBack,1,1, lowerLeftBack,0,1);
	    
	    // Upper
	    box.addTriangle(upperLeftBack,0,0, upperLeftFront,0,1, upperRightBack,1,0);
	    box.addTriangle(upperRightBack,1,0, upperLeftFront,0,1, upperRightFront,1,1);
	    
	    // Lower
	    box.addTriangle(lowerLeftBack,0,0, lowerRightBack,1,0, lowerLeftFront,0,1);
	    box.addTriangle(lowerRightBack,1,0, lowerRightFront,1,1, lowerLeftFront,0,1);
	    
	    // Left
	    box.addTriangle(upperLeftFront,0,0, upperLeftBack,1,0, lowerLeftFront,0,1);
	    box.addTriangle(upperLeftBack,1,0, lowerLeftBack,1,1, lowerLeftFront,0,1);
	    
	    // Right
	    box.addTriangle(upperRightFront,0,0, lowerRightFront,0,1, upperRightBack,1,0);
	    box.addTriangle(upperRightBack,1,0, lowerRightFront, 0,1, lowerRightBack,1,1);
	    
	    box.calcTextureWrapSpherical();
		box.setTexture("greyTexture");
		box.strip();
		box.build();
		
		box.clearTranslation();
		box.translate(pos);
		box.translate(0, h, 0);
		
		world.addObject(box);
	}

}