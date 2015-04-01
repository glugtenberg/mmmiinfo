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
	
	private int tableDim;
	public float[][] transitionTable; 
	public int prevSlotIdx;

	public float timeLeft = 0;
	public float maxTimeLeft;
	
	public CubeSpawner(ArrayList<SimpleVector> slots, float depth, float[][] transitionTable, int tableDim, World world) {
		Log.d("DEBUG", "CubeSpawner");
		cubes = new ArrayList<ShootingCube>();
		this.spawnSlots = new ArrayList<SimpleVector>();
		for (int i = 0; i < slots.size(); ++i) {
			SimpleVector v = new SimpleVector(slots.get(i));
			v.z += depth;
			spawnSlots.add(v);
		}
		
		this.tableDim = tableDim;
		this.transitionTable = transitionTable; 
		this.prevSlotIdx = tableDim / 2; 
		
		buildPool(40, world);
	}
	
	public void reset() {
		for (int i = 0; i < cubes.size(); ++i) {
			cubes.get(i).setActive(false); 
		}
		timeLeft = maxTimeLeft; 
	}
	
	private int getFreeSlotIdx() {
		float[] transitions = new float[tableDim];
		for (int i = 0; i < tableDim; ++i) {
			transitions[i] = transitionTable[prevSlotIdx][i]; 
		}
		
		float W = (float)Math.random(); 
		float w = 0;
		int idx = 0; 
		while (w < W && idx < tableDim) {
			w += transitions[idx++];
		}
		
		return idx - 1;
	}
	
	public void update(Game game, float dt) {
		if (timeLeft > 0) {
			if  (t <= 0) { 
				int freeSlotIdx = getFreeSlotIdx();
				for (int i = 0; i < spawnSlots.size(); ++i) {
					if (i != freeSlotIdx &&  (i == prevSlotIdx || Math.random() < 0.75)) {
						spawnCube(spawnSlots.get(i));
					} 
				}
				
				prevSlotIdx = freeSlotIdx;
				t = interval; 
			} else {
				t -= dt; 
			}
			
			timeLeft -= dt * 1000;
		}
		
		for (ShootingCube cube : cubes) {
			cube.update(dt);
		}
		 
	}
	
	private void buildPool(int num, World world) {
		for (int i = 0; i < num; ++i) {
			ShootingCube cube = new ShootingCube(Primitives.getCube(5));
			
			cube.calcTextureWrapSpherical();
			cube.setTexture("whiteTexture");
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
