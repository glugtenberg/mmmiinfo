package com.gl.mmmiinfo_2;

import java.util.ArrayList;

import android.util.Log;

import com.threed.jpct.Matrix;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

public class CubeSpawner {
	public float t = 0;
	public float interval = 1.0f;
	
	public ArrayList<ShootingCube> cubes;
	public ArrayList<SimpleVector> spawnSlots; 
	
	public CubeSpawner(ArrayList<SimpleVector> spawnSlots, World world) {
		Log.d("DEBUG", "CubeSpawner");
		cubes = new ArrayList<ShootingCube>();
		this.spawnSlots = new ArrayList<SimpleVector>();
		this.spawnSlots.addAll(spawnSlots);
		
		buildPool(20, world);
	}
	
	public void update(Game game, float dt) {
		if  (t <= 0) {
			int slotIdx = (int)(spawnSlots.size() * Math.random());
			spawnCube(spawnSlots.get(slotIdx));
			t = interval; 
		} else {
			t -= dt; 
		}
		
		for (ShootingCube cube : cubes) {
			cube.update(dt);
		}
	}
	
	private void buildPool(int num, World world) {
		for (int i = 0; i < num; ++i) {
			ShootingCube cube = new ShootingCube(Primitives.getCube(5));
			
			cube.calcTextureWrapSpherical();
			cube.setTexture("texture");
			cube.strip();
			cube.build();
			
			cube.rotateY((float) (0.25f * Math.PI));
			cube.setActive(false);
			
			SimpleVector c = cube.getTransformedCenter();
		
			world.addObject(cube);
			cubes.add(cube);
		}
	}
	
	public void spawnCube(SimpleVector pos) {
		for (ShootingCube cube : cubes) {
			if (!cube.active) {
				cube.setActive(true);
				cube.clearTranslation();
				cube.translate(pos);
				return; 
			}
		}
	}
}
