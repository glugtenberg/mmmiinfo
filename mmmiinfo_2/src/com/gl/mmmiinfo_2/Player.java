package com.gl.mmmiinfo_2;

import java.util.ArrayList;

import android.util.Log;

import com.threed.jpct.SimpleVector;

public class Player {
	
	private ArrayList<SimpleVector> slots;
	private int slotIdx;
	
	public Player() {
		slots = new ArrayList<SimpleVector>();
		slots.add(new SimpleVector(-10, 0, 0));
		slots.add(new SimpleVector(0, 0, 0));
		slots.add(new SimpleVector(10, 0, 0));
		slotIdx = 1; 
	}
	
	public void update(Game game, float dt) {
		game.world.getCamera().setPosition(slots.get(slotIdx));
		
		for (ShootingCube cube : game.spawner.cubes) {
			if (cube.active && testCollision(cube)) {
				Log.d("DEBUG", "HIT HIT HIT");
				cube.setActive(false); 
			}
		}
	}
	
	boolean testCollision(ShootingCube cube) {
		//TODO: get the halfSize of the cubes instead of the magic 2.5
		SimpleVector center = cube.getTransformedCenter();
		return center.z > -2.5f && center.z < 2.5f &&
			   center.x > slots.get(slotIdx).x - 2.5f && center.x < slots.get(slotIdx).x + 2.5;
	}
	
	public void moveLeft() {
		slotIdx = slotIdx > 0 ? slotIdx - 1 : slotIdx; 
	}
	
	public void moveRight() {
		slotIdx = slotIdx < slots.size() - 1 ? slotIdx + 1 : slotIdx;
	}
}
