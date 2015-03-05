package com.gl.mmmiinfo_2;

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


class Renderer implements GLSurfaceView.Renderer {

	private static Activity master = null;
	private RGBColor background = new RGBColor(50, 50, 100);
	
	private FrameBuffer fb = null;
	private World world = null;
	private long time = System.currentTimeMillis();

	private ShootingCube cube = null;
	private int fps = 0;
	private boolean gl2 = true;
	
	private Camera cam = null;
	private Light sun = null;

	public Renderer() {
		
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

			// Create a texture out of the icon...:-)
			//Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.icon)), 64, 64));
			Texture texture = new Texture(64, 64, new RGBColor(255,0,0));
			TextureManager.getInstance().addTexture("texture", texture);

			cube = new ShootingCube(Primitives.getCube(5));
			cube.calcTextureWrapSpherical();
			cube.setTexture("texture");
			cube.strip();
			cube.build();

			world.addObject(cube);

			cam = world.getCamera();
			cam.moveCamera(Camera.CAMERA_MOVEOUT, 50);
			cam.lookAt(cube.getTransformedCenter());

			SimpleVector sv = new SimpleVector();
			sv.set(cube.getTransformedCenter());
			sv.y -= 100;
			sv.z -= 100;
			sun.setPosition(sv);
			MemoryHelper.compact();

			if (master == null) {
				Logger.log("Saving master Activity!");
				//master = HelloWorld.this;
			}
		}
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	}

	public void onDrawFrame(GL10 gl) {
		/*if (touchTurn != 0) {
			cube.rotateY(touchTurn);
			touchTurn = 0;
		}

		if (touchTurnUp != 0) {
			cube.rotateX(touchTurnUp);
			touchTurnUp = 0;
		}*/
		long elapsedTime = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		cube.update(elapsedTime);

		fb.clear(background);
		world.renderScene(fb);
		world.draw(fb);
		fb.display();

	}
}